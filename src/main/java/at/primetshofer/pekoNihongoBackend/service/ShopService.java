package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.dto.ShopItemDto;
import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.enums.EffectType;
import at.primetshofer.pekoNihongoBackend.enums.ShopItemType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShopService {

    private final StatsService statsService;
    private final MoneyService moneyService;
    private final QuestService questService;
    private final EffectsService effectsService;

    public ShopService(StatsService statsService, MoneyService moneyService, QuestService questService, EffectsService effectsService) {
        this.statsService = statsService;
        this.moneyService = moneyService;
        this.questService = questService;
        this.effectsService = effectsService;
    }

    public List<ShopItemDto> getAllItems(Long userId) {
        ShopItemType[] allItems = ShopItemType.values();
        List<ShopItemDto> shopItems = new ArrayList<>(allItems.length);

        for (ShopItemType item : allItems) {
            boolean available = checkItemAvailability(userId, item);
            LocalDateTime activeTill = null;

            if(!available){
                activeTill = checkActiveTill(userId, item);
            }

            shopItems.add(new ShopItemDto(item, available, activeTill));
        }

        return shopItems;
    }

    @Transactional
    public boolean buyItem(User user, ShopItemType item) {
        boolean paid = moneyService.pay(user.getId(), item.price);
        boolean success = false;

        if(!paid){
            return success;
        }

        switch (item) {
            case STREAK_EXTENDER -> success = statsService.increaseDailyQuestStreak(user.getId());
            case CHALLENGE_QUEST -> success = questService.createRandomChallengeQuest(user);
            case DAILY_QUEST_EDIT_15_MIN -> success = effectsService.applyEffect(user, EffectType.ALLOW_DAILY_QUESTS_EDIT, Duration.ofMinutes(15));
            case STREAK_REPAIR -> success = statsService.repairStreak(user);
        }

        if(!success){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return success;
    }

    @Transactional
    public int gambleMoney(Long userId, ShopItemType item, int amount){
        double multiplier = 0;
        int gambleAmount = (item.price == -1) ? amount : item.price;

        if(item == ShopItemType.MONEY_GAMBLE){
            multiplier = gambleLowRisk();
        } else if(item == ShopItemType.MONEY_GAMBLE_HIGH_RISK){
            multiplier = gambleHighRisk();
        }

        int win = (int)Math.ceil(gambleAmount * multiplier);

        moneyService.pay(userId, gambleAmount);
        moneyService.addMoneyReward(userId, win);

        return win;
    }

    private boolean checkItemAvailability(Long userId, ShopItemType item) {
        switch (item) {
            case STREAK_EXTENDER -> {
                return !statsService.isStreakExtended(userId);
            }
            case CHALLENGE_QUEST -> {
                return !questService.hasActiveChallengeQuest(userId);
            }
            case DAILY_QUEST_EDIT_15_MIN -> {
                return !effectsService.hasEffect(userId, EffectType.ALLOW_DAILY_QUESTS_EDIT);
            }
            case MONEY_GAMBLE, MONEY_GAMBLE_HIGH_RISK, STREAK_REPAIR -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private LocalDateTime checkActiveTill(Long userId, ShopItemType item) {
        if (item == ShopItemType.DAILY_QUEST_EDIT_15_MIN) {
            return effectsService.getActiveEffect(userId, EffectType.ALLOW_DAILY_QUESTS_EDIT).getExpirationDateTime();
        }
        return null;
    }

    private double gambleLowRisk(){
        SecureRandom random = new SecureRandom();
        int rand = random.nextInt(100);
        double multiplier;

        if(rand < 1){
            multiplier = 6;
        } else if(rand < 6){
            multiplier = 3;
        } else if(rand < 26){
            multiplier = 1.8;
        } else if(rand < 58){
            multiplier = 1.15;
        } else {
            multiplier = 0;
        }

        return multiplier;
    }

    private double gambleHighRisk(){
        SecureRandom random = new SecureRandom();
        double rand = random.nextDouble(100);
        double multiplier;

        if(rand < 0.5){
            multiplier = 30;
        } else if(rand < 3){
            multiplier = 10;
        } else if(rand < 11){
            multiplier = 4;
        } else if(rand < 25){
            multiplier = 1.5;
        } else {
            multiplier = 0;
        }

        return multiplier;
    }
}
