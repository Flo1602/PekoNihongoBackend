package at.primetshofer.pekoNihongoBackend.enums;

public enum MoneyReward {
    DAILY_QUESTS_COMPLETE(50),
    EXERCISE_REWARD(1),
    WEEKLY_QUEST_COMPLETE(50);

    public final int amount;

    MoneyReward(int amount) {
        this.amount = amount;
    }
}
