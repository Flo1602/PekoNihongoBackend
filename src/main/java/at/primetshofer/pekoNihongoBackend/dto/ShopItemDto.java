package at.primetshofer.pekoNihongoBackend.dto;

import at.primetshofer.pekoNihongoBackend.enums.ShopItemType;

import java.time.LocalDateTime;

public record ShopItemDto(ShopItemType type, int price, boolean available, LocalDateTime activeTill) {
    public ShopItemDto(ShopItemType type, boolean available, LocalDateTime activeTill) {
        this(type, type.price, available, activeTill);
    }
}

