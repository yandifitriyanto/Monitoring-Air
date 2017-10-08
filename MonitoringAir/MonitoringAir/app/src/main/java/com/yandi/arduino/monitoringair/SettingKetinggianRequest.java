package com.yandi.arduino.monitoringair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yandi on 23/08/2017.
 */

public class SettingKetinggianRequest extends AsyncTask<Void, Void, String> {
    private String _textMaksimal;
    private String _textMinimal;
    private Context context;
    private Boolean isSimpan = true;
    ProgressDialog loading;

    public AsyncResponse delegate = null;

    public SettingKetinggianRequest(Context context, String textMaksimal, String textMinimal, Boolean isSimpan, AsyncResponse delegate) {
        this.context = context;
        _textMaksimal = textMaksimal;
        _textMinimal = textMinimal;
        this.isSimpan = isSimpan;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        String titleDialog = null;
        if (isSimpan == true) {
            titleDialog = "Proses menambah data";
        } else {
            titleDialog = "Sedang mengambil data";
        }
        loading = ProgressDialog.show(context, titleDialog, "Harap tunggu...", false, false);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        loading.dismiss();
        if (isSimpan == true) {
            String message = getMessage(s);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } else {
            delegate.processFinish(s);
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        RequestHandler requestHandler = new RequestHandler();
        String res = null;
        if (isSimpan) {
            res = requestHandler.sendGetRequest(Config.UrlSimpanSettingKetinggian + "/?maksimal=" + _textMaksimal + "&minimal=" + _textMinimal);
        } else {
            res = requestHandler.sendGetRequest(Config.UrlSettingKetinggian);
        }
        return res;
    }

    public String getMessage(String json) {
        String message = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            message = jsonObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }
}
