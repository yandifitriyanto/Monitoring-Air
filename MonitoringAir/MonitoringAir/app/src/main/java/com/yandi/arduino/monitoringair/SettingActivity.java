package com.yandi.arduino.monitoringair;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText textMaksimal;
    private EditText textMinimal;
    private Button buttonSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        textMaksimal = (EditText) findViewById(R.id.textMaksimal);
        textMinimal = (EditText) findViewById(R.id.textMinimal);
        buttonSimpan = (Button) findViewById(R.id.buttonSimpan);

        buttonSimpan.setOnClickListener(this);

        getSettingKetinggian();
    }

    private void simpanSettingKetinggian() {
        textMaksimal.setError(null);
        textMinimal.setError(null);

        final String maksimal = textMaksimal.getText().toString().trim();
        final String minimal = textMinimal.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(maksimal)) {
            textMaksimal.setError(getString(R.string.error_field_required));
            focusView = textMaksimal;
            cancel = true;
        }

        if (TextUtils.isEmpty(minimal)) {
            textMinimal.setError(getString(R.string.error_field_required));
            focusView = textMinimal;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            SettingKetinggianRequest settingKetinggianRequest = new SettingKetinggianRequest(
                    SettingActivity.this,
                    maksimal,
                    minimal,
                    true,
                    new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {}
            });
            settingKetinggianRequest.execute();
            saveSharedPreferences(maksimal);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSimpan) simpanSettingKetinggian();
    }

    public void getSettingKetinggian() {
        final String maksimal = textMaksimal.getText().toString().trim();
        final String minimal = textMinimal.getText().toString().trim();

        SettingKetinggianRequest settingKetinggianRequest = new SettingKetinggianRequest(
                SettingActivity.this,
                maksimal,
                minimal,
                false,
                new AsyncResponse() {
                    @Override
                    public void processFinish(String json) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            textMaksimal.setText(jsonObject.getString("maksimal"));
                            textMinimal.setText(jsonObject.getString("minimal"));

                            saveSharedPreferences(jsonObject.getString("maksimal"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        settingKetinggianRequest.execute();
    }

    private void saveSharedPreferences(String maksimal) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("maksimal", maksimal);
        editor.commit();
    }
}
