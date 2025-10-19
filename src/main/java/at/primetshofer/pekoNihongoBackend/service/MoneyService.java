package at.primetshofer.pekoNihongoBackend.service;

import at.primetshofer.pekoNihongoBackend.entity.User;
import at.primetshofer.pekoNihongoBackend.enums.MoneyReward;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MoneyService {

    private final UserService userService;

    public MoneyService(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    public boolean pay(Long useId, int amount) {
        User userById = userService.getUserByIdAndLock(useId);

        if(userById == null || userById.getMoney() < amount){
            return false;
        }

        userById.setMoney(userById.getMoney() - amount);

        return true;
    }


    @Transactional
    public void addMoneyReward(Long useId, MoneyReward reward) {
        addMoneyReward(useId, reward.amount);
    }

    @Transactional
    public void addMoneyReward(Long useId, int amount) {
        User userById = userService.getUserByIdAndLock(useId);

        if(userById == null){
            return;
        }

        userById.setMoney(userById.getMoney() + amount);
    }
}
