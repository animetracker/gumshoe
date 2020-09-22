package com.alexzamurca.animetrackersprint2;

public class Item {
    private String IconName;
    private int IconImage;
    private int IconPrice;

    public Item() {

    }

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

    public void setIconName(String iconName) {
        IconName = iconName;
    }

    public void setIconImage(int iconImage) {
        IconImage = iconImage;
    }

    public void setIconPrice(int iconPrice) {
        IconPrice = iconPrice;
    }
}
