package at.primetshofer.pekoNihongoBackend.enums;

public enum ShopItemType {
    STREAK_EXTENDER(300),
    MONEY_GAMBLE(-1),
    MONEY_GAMBLE_HIGH_RISK(-1),
    CHALLENGE_QUEST(150),
    DAILY_QUEST_EDIT_15_MIN(1000),
    STREAK_REPAIR(2500);

    public final int price;
    ShopItemType(int price) {
        this.price = price;
    }
}
