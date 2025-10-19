package at.primetshofer.pekoNihongoBackend.web;

import at.primetshofer.pekoNihongoBackend.dto.ShopItemDto;
import at.primetshofer.pekoNihongoBackend.enums.ShopItemType;
import at.primetshofer.pekoNihongoBackend.security.authentication.AuthConstants;
import at.primetshofer.pekoNihongoBackend.service.ShopService;
import at.primetshofer.pekoNihongoBackend.utils.WebUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@SecurityRequirement(name = AuthConstants.SECURITY_SCHEME_NAME)
public class ShopController {

    private final WebUtils webUtils;
    private final ShopService shopService;

    public ShopController(WebUtils webUtils, ShopService shopService) {
        this.webUtils = webUtils;
        this.shopService = shopService;
    }

    @GetMapping
    public List<ShopItemDto> getItems() {
        return shopService.getAllItems(webUtils.getCurrentUserId());
    }

    @PostMapping("/buy")
    public boolean buyItem(@RequestBody ShopItemType type) {
        return shopService.buyItem(webUtils.getCurrentUser(), type);
    }

    @PostMapping("/gamble/{amount}")
    public int gamble(@RequestBody ShopItemType type, @PathVariable int amount){
        return shopService.gambleMoney(webUtils.getCurrentUserId(), type, amount);
    }

    @GetMapping("/currMoney")
    public int getMoneyFromUser(){
        return webUtils.getCurrentUser().getMoney();
    }

}
