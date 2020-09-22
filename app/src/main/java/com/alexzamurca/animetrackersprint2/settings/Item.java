package com.alexzamurca.animetrackersprint2.settings;

public class Item {
    private String IconName;
    private int IconImage;
    private int IconPrice;

    public Item(String iconName, int iconImage, int iconPrice) {
        IconName = iconName;
        IconImage = iconImage;
        IconPrice = iconPrice;
    }

    public String getIconName() {
        return IconName;
    }

    public int getIconImage() {
        return IconImage;
    }

    public int getIconPrice() {
        return IconPrice;
    }
}
