package at.primetshofer.pekoNihongoBackend.dto;

import java.util.List;

public record WordInfoDto(String word, String link, String jlptInfo, List<KanjiInfoDto> kanjiInfos) {
}

