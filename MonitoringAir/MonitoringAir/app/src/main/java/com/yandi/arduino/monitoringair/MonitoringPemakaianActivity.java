package com.yandi.arduino.monitoringair;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MonitoringPemakaianActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private TextView textViewJumlahPemakaian;
    private Button buttonFilter;
    private PopupWindow popupWindow;
    private LinearLayout linearLayout;
    private String tanggalDariFilter;
    private String tanggalSampaiFilter;

    private Context context;
    private Activity activity;

    ArrayList<HashMap<String, String>> pemakaianAirJsonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_pemakaian);

        context = getApplicationContext();
        activity = MonitoringPemakaianActivity.this;

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        listView = (ListView) findViewById(R.id.listViewPemakaian);
        textViewJumlahPemakaian = (TextView) findViewById(R.id.textViewJumlahPemakaian);
        buttonFilter = (Button) findViewById(R.id.buttonFilter);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dateNow = sdf.format(calendar.getTime());

        tanggalDariFilter = dateNow;
        tanggalSampaiFilter = dateNow;

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            tanggalDariFilter = extra.getString("tanggal_dari", dateNow);
            tanggalSampaiFilter = extra.getString("tanggal_sampai", dateNow);
        }

        Log.d("tanggal_dari_main", tanggalDariFilter);
        Log.d("tanggal_sampai_main", tanggalSampaiFilter);
        buttonFilter.setOnClickListener(this);
        pemakaianAirJsonList = new ArrayList<>();
        getPemakaianAir();
    }

    private void getPemakaianAir() {
        final String tanggalDari = tanggalDariFilter;
        final String tanggalSampai = tanggalSampaiFilter;

        Log.d("tanggal_dari", tanggalDari);
        Log.d("tanggal_sampai", tanggalSampai);
        MonitoringPemakaianRequest monitoringPemakaianRequest = new MonitoringPemakaianRequest(
                MonitoringPemakaianActivity.this, tanggalDari, tanggalSampai, new AsyncResponse() {
            @Override
            public void processFinish(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String total = jsonObject.getString("total");
                    String textViewJumlah = "Jumlah Pemakaian Air : " + total + Config.satuan;
                    textViewJumlahPemakaian.setText(textViewJumlah);
                    //detail pemakaian
                    JSONArray jsonArray = jsonObject.getJSONArray("detail_pemakaian");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject detailPemakaian = jsonArray.getJSONObject(i);
                        String tanggal = detailPemakaian.getString("tanggal");
                        String jumlah = detailPemakaian.getString("jumlah");
                        // tmp hash map for single contact
                        HashMap<String, String> pemakaianAir = new HashMap<>();
                        pemakaianAir.put("tanggal", tanggal);
                        pemakaianAir.put("jumlah", jumlah + " ml");
                        pemakaianAirJsonList.add(pemakaianAir);
                    }

                    if (pemakaianAirJsonList.size() > 0) {
                        listViewPemakaianAir(pemakaianAirJsonList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        );

        monitoringPemakaianRequest.execute();
    }

    private void listViewPemakaianAir(ArrayList<HashMap<String, String>> pemakaianAir) {
        /**
         * Updating parsed JSON data into ListView
         * */
        ListAdapter adapter = new SimpleAdapter(
                MonitoringPemakaianActivity.this, pemakaianAir,
                R.layout.list_item_pemakaian, new String[]{"tanggal", "jumlah"}, new int[]{R.id.tanggalPemakaian, R.id.jumlahPemakaian});

        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view == buttonFilter) {
            // Initialize a new instance of LayoutInflater service
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

            // Inflate the custom layout/view
            View customView = layoutInflater.inflate(R.layout.popup_filter,null);

            popupWindow = new PopupWindow(customView, 1000, 800);
            final Calendar myCalendar = Calendar.getInstance();

            final EditText tanggalDari = (EditText) customView.findViewById(R.id.etTanggalDari);
            final EditText tanggalSampai = (EditText) customView.findViewById(R.id.etTanggalSampai);
            final Button buttonLihat = (Button) customView.findViewById(R.id.buttonLihat);

            final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "dd-MM-yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    tanggalDari.setText(sdf.format(myCalendar.getTime()));
                }
            };

            tanggalDari.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(MonitoringPemakaianActivity.this, startDate, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    String myFormat = "dd-MM-yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                    tanggalSampai.setText(sdf.format(myCalendar.getTime()));
                }
            };

            tanggalSampai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(MonitoringPemakaianActivity.this, endDate, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            buttonLihat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MonitoringPemakaianActivity.this, MonitoringPemakaianActivity.class);
                    intent.putExtra("tanggal_dari", tanggalDari.getText().toString().trim());
                    intent.putExtra("tanggal_sampai", tanggalSampai.getText().toString().trim());
                    startActivity(intent);
                }
            });

            popupWindow.showAtLocation(linearLayout, Gravity.CENTER,0,0);
            popupWindow.setFocusable(true);
            popupWindow.update();
            popupWindow.setOutsideTouchable(false);

            View container = (View) popupWindow.getContentView().getParent();
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
            p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            p.dimAmount = 0.3f;
            wm.updateViewLayout(container, p);
        }
    }
}
