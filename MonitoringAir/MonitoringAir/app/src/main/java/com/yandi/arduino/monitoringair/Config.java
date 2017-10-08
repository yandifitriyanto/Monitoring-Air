package com.yandi.arduino.monitoringair;

/**
 * Created by Yandi on 23/08/2017.
 */

public class Config {
    // End point REST API
    public static final String BaseUrl = "http://192.168.43.104/api/";
    public static final String UrlSimpanSettingKetinggian = BaseUrl + "simpan-setting-ketinggian-air";
    public static final String UrlSettingKetinggian = BaseUrl + "setting-ketinggian-air";
    public static final String UrlPengisianAir = BaseUrl + "pengisian-air";
    public static final String UrlSimpanStatusPompa = BaseUrl + "simpan-status-pompa-air";
    public static final String UrlStatusPompa = BaseUrl + "status-pompa-air";
    public static final String UrlPemakaianAir = BaseUrl + "pemakaian-air";

    //satuan
    public static final String satuan = "ml";

    //realtime
    public static final Boolean REALTIME = false;
}
