package com.yandi.arduino.monitoringair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Yandi on 25/08/2017.
 */

public class MonitoringPengisianRequest extends AsyncTask<Void, Void, String> {
    private Context context;
    private Integer status;
    private Integer action;
    ProgressDialog loading;

    public static Integer ActionPengisianAir = 1;
    public static Integer ActionSimpanStatusPompa = 2;
    public static Integer ActionStatusPompa = 3;

    public AsyncResponse delegate = null;

    public MonitoringPengisianRequest(Context context, Integer status, Integer action) {
        this.context = context;
        this.status = status;
        this.action = action;
    }

    public MonitoringPengisianRequest(Context context, Integer action, AsyncResponse delegate) {
        this.context = context;
        this.action = action;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String titleDialog = null;
        if (action.equals(ActionSimpanStatusPompa)) {
            if (status != null && status == 0) {
                titleDialog = "Proses mematikan pompa";
            } else {
                titleDialog = "Proses menyalakan pompa";
            }
            loading = ProgressDialog.show(context, titleDialog, "Harap tunggu...", false, false);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (action.equals(ActionSimpanStatusPompa)) {
            loading.dismiss();
        } else {
            delegate.processFinish(s);
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        RequestHandler requestHandler = new RequestHandler();
        String res = null;
        if (action.equals(ActionPengisianAir)) {
            res = requestHandler.sendGetRequest(Config.UrlPengisianAir);
        } else if (action.equals(ActionSimpanStatusPompa)) {
            res = requestHandler.sendGetRequest(Config.UrlSimpanStatusPompa + "/?status=" + status);
        } else if(action.equals(ActionStatusPompa)) {
            res = requestHandler.sendGetRequest(Config.UrlStatusPompa);
        }

        return res;
    }
}
