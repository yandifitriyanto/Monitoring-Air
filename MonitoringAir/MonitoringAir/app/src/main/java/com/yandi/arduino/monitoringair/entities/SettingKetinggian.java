package com.yandi.arduino.monitoringair.entities;

/**
 * Created by Yandi on 26/08/2017.
 */

public class SettingKetinggian {
    public String maksimal;
    public String minimal;

    public  SettingKetinggian() {}

    public SettingKetinggian(String maksimal, String minimal) {
        this.maksimal = maksimal;
        this.minimal = minimal;
    }

    public String getMaksimal() {
        return maksimal;
    }

    public void setMaksimal(String maksimal) {
        this.maksimal = maksimal;
    }

    public String getMinimal() {
        return minimal;
    }

    public void setMinimal(String minimal) {
        this.minimal = minimal;
    }
}
