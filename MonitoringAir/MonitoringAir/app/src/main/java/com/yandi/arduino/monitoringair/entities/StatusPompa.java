package com.yandi.arduino.monitoringair.entities;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Yandi on 26/08/2017.
 */

@IgnoreExtraProperties
public class StatusPompa {
    public String status;

    public StatusPompa() {}

    public StatusPompa(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
