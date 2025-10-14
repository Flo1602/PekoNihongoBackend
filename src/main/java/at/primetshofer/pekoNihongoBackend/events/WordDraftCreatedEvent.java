package at.primetshofer.pekoNihongoBackend.events;

public class WordDraftCreatedEvent {
    private final Long userId;
    private final Long draftId;

    public WordDraftCreatedEvent(Long userId, Long draftId) {
        this.userId = userId;
        this.draftId = draftId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getDraftId() {
        return draftId;
    }
}
