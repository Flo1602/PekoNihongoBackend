package at.primetshofer.pekoNihongoBackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.zip.GZIPOutputStream;

@Component
public class BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);

    @Value("${pekoNihongoBackend.resources.location}")
    private String staticResourceLocation;

    private static final String BACKUP_DIR = "/DB_Backup";

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public BackupService(@Value("${spring.datasource.url}") String dbUrl,
                         @Value("${spring.datasource.username}") String dbUser,
                         @Value("${spring.datasource.password}") String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    /** Täglich um 03:30 Uhr (Europe/Vienna). */
    @Scheduled(cron = "0 30 3 * * *", zone = "Europe/Vienna")
    public void doBackup() {
        checkAndBackup();
    }

    /* ------------------------------------------------------------ */

    private void checkAndBackup() {

        final File backupDir = new File(staticResourceLocation + BACKUP_DIR);
        if (!backupDir.exists() && !backupDir.mkdirs()) {
            logger.error("Konnte Backup-Verzeichnis nicht anlegen: {}", staticResourceLocation + BACKUP_DIR);
            return;
        }

        final LocalDate today = LocalDate.now();
        final String backupFileName = "backup-" + today + ".sql.gz";
        final File   backupFile     = new File(backupDir, backupFileName);

        if (backupFile.exists()) {
            logger.warn("Backup für heute existiert bereits: {}", backupFileName);
        } else {
            performBackup(backupFile.getAbsolutePath());
        }

        cleanupBackups();
    }

    /**
     * Ruft mysqldump auf und packt die Ausgabe on-the-fly in GZIP.
     * Das vermeidet temporäre .sql-Dateien.
     */
    private void performBackup(String backupFilePath) {

        DbInfo info;
        try {
            info = parseDbInfo(dbUrl);
        } catch (IllegalArgumentException ex) {
            logger.error("Datasource-URL konnte nicht geparst werden: {}", dbUrl, ex);
            return;
        }

        /*  mysqldump --single-transaction --routines -h HOST -P PORT -u USER -pPASSWORD DB  */
        List<String> cmd = new ArrayList<>(Arrays.asList(
                "mysqldump",
                "--single-transaction",  // konsistenter Dump ohne Write-Lock
                "--routines",            // Prozeduren / Funktionen
                "-h", info.host(),
                "-P", info.port(),
                "-u", dbUser,
                "-p" + dbPassword,       // Achtung: Password steht in ps-Output; ggf. .my.cnf nutzen
                info.db()
        ));

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);  // leitet STDERR nach STDOUT

        try {
            Process process = pb.start();

            try (InputStream in  = process.getInputStream();
                 OutputStream fos = new FileOutputStream(backupFilePath);
                 GZIPOutputStream gz = new GZIPOutputStream(fos)) {

                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) {
                    gz.write(buf, 0, len);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                logger.info("Backup erfolgreich erstellt: {}", backupFilePath);
            } else {
                logger.error("mysqldump lieferte Exit-Code {}", exitCode);
            }

        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            logger.error("Backup fehlgeschlagen: {}", backupFilePath, ex);
        }
    }

    /* ------------------------------------------------------------ */

    private void cleanupBackups() {

        final File backupDir = new File(staticResourceLocation + BACKUP_DIR);
        final File[] files = backupDir.listFiles((dir, name) ->
                name.matches("backup-\\d{4}-\\d{2}-\\d{2}\\.sql\\.gz"));

        if (files == null) return;

        List<BackupEntry> entries = new ArrayList<>();
        for (File f : files) {
            String dateStr = f.getName()
                    .substring("backup-".length(), "backup-YYYY-MM-DD".length());
            try {
                entries.add(new BackupEntry(LocalDate.parse(dateStr), f));
            } catch (DateTimeParseException ignored) { }
        }

        entries.sort(Comparator.comparing(BackupEntry::date).reversed());

        LocalDate today = LocalDate.now();
        Map<String, BackupEntry> weekly = new HashMap<>();
        Map<String, BackupEntry> monthly = new HashMap<>();
        List<File> delete = new ArrayList<>();

        WeekFields wf = WeekFields.of(Locale.getDefault());

        for (BackupEntry e : entries) {
            long days = ChronoUnit.DAYS.between(e.date(), today);

            if (days > 7 && days <= 90) {                    // weekly
                String key = e.date().getYear() + "-" + e.date().get(wf.weekOfYear());
                if (!weekly.putIfAbsent(key, e).equals(e)) delete.add(e.file());
            } else if (days > 90) {                          // monthly
                String key = e.date().getYear() + "-" + e.date().getMonthValue();
                if (!monthly.putIfAbsent(key, e).equals(e)) delete.add(e.file());
            }
        }

        delete.forEach(f -> {
            if (f.delete()) logger.info("Altes Backup gelöscht: {}", f.getName());
            else            logger.warn("Konnte Backup nicht löschen: {}", f.getName());
        });
    }

    /* ------------------------------------------------------------ */

    private static DbInfo parseDbInfo(String url) {
        // jdbc:mysql://host:3306/db?params
        if (!url.startsWith("jdbc:mysql://"))
            throw new IllegalArgumentException("Nicht-unterstützte URL: " + url);

        String stripped = url.substring("jdbc:mysql://".length());
        String[] hostAndRest = stripped.split("/", 2);
        if (hostAndRest.length < 2) throw new IllegalArgumentException("Kein Datenbankname in URL");

        String hostPort = hostAndRest[0];
        String dbAndParams = hostAndRest[1];

        String[] hp = hostPort.split(":", 2);
        String host = hp[0];
        String port = hp.length == 2 ? hp[1] : "3306";

        String db = dbAndParams.contains("?")
                ? dbAndParams.substring(0, dbAndParams.indexOf('?'))
                : dbAndParams;

        return new DbInfo(host, port, db);
    }

    /* ------------------------------------------------------------ */

    private record BackupEntry(LocalDate date, File file) {}
    private record DbInfo(String host, String port, String db)  {}
}