package com.example.fuelonwheelsapp.profile;

public class ProfileItem {
    private int iconId;
    private String item;

    public ProfileItem(int iconId, String item) {
        this.iconId = iconId;
        this.item = item;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
