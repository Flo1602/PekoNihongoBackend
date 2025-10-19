package at.primetshofer.pekoNihongoBackend.dto;

import at.primetshofer.pekoNihongoBackend.enums.ShopItemType;

public record ShopItemDto(ShopItemType type, int price, boolean available) {
    public ShopItemDto(ShopItemType type, boolean available) {
        this(type, type.price, available);
    }
}

