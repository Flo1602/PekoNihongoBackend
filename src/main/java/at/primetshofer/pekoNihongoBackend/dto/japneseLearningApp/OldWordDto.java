package at.primetshofer.pekoNihongoBackend.dto.japneseLearningApp;

public record OldWordDto(String japanese, String english, String kana, OldProgressDto[] progress) {
}
