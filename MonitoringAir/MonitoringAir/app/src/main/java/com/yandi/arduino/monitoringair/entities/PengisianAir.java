package com.yandi.arduino.monitoringair.entities;

/**
 * Created by Yandi on 27/08/2017.
 */

public class PengisianAir {
    public String jumlah;

    public  PengisianAir() {}

    public PengisianAir(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }
}
