package com.yandi.arduino.monitoringair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Yandi on 27/08/2017.
 */

public class MonitoringPemakaianRequest extends AsyncTask<Void, Void, String> {
    private Context context;
    private String tanggalDari;
    private String tanggalSampai;
    ProgressDialog loading;

    public AsyncResponse delegate = null;

    public MonitoringPemakaianRequest(Context context, String tanggalDari, String tanggalSampai, AsyncResponse delegate) {
        this.context = context;
        this.tanggalDari = tanggalDari;
        this.tanggalSampai = tanggalSampai;
        this.delegate = delegate;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String titleDialog = "Sedang mengambil data";
        loading = ProgressDialog.show(context, titleDialog, "Harap tunggu...", false, false);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        loading.dismiss();
        delegate.processFinish(s);
    }

    @Override
    protected String doInBackground(Void... voids) {
        RequestHandler requestHandler = new RequestHandler();
        String res = requestHandler.sendGetRequest(Config.UrlPemakaianAir + "/?tanggal_dari=" + tanggalDari + "&tanggal_sampai=" + tanggalSampai);
        return res;
    }
}
