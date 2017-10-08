package com.yandi.arduino.monitoringair;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yandi.arduino.monitoringair.entities.PengisianAir;
import com.yandi.arduino.monitoringair.entities.StatusPompa;

import org.json.JSONException;
import org.json.JSONObject;

public class MonitoringPengisianActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MonitoringPengisianActivity.class.getSimpleName();
    private boolean statusPenuh = false;

    private ProgressBar progressBarPengisianAir;
    private TextView textViewProgress;
    private TextView textViewStatusPompa;
    private Button buttonPompaAir;
    private SwipeRefreshLayout swipeRefreshLayout;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private StatusPompa statusPompa;
    private PengisianAir pengisianAir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_pengisian);

        progressBarPengisianAir = (ProgressBar) findViewById(R.id.progressBarPengisianAir);
        textViewProgress = (TextView) findViewById(R.id.textViewProgress);
        textViewStatusPompa = (TextView) findViewById(R.id.textViewStatusPompa);
        buttonPompaAir = (Button) findViewById(R.id.buttonPompaAir);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        buttonPompaAir.setOnClickListener(this);

        // get sharedPreferences maksimal, set di SettingActivity
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String maksimal = sharedPreferences.getString("maksimal", null);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStatusPompa();
                getPengisianAir(maksimal);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        if (Config.REALTIME == true) {
            getStatusPompaRealtime();
            getPengisianAirRealtime(maksimal);
        } else {
            getStatusPompa();
            getPengisianAir(maksimal);
        }
    }

    private void getStatusPompa() {
        MonitoringPengisianRequest monitoringPengisianRequest = new MonitoringPengisianRequest(
                MonitoringPengisianActivity.this,
                MonitoringPengisianRequest.ActionStatusPompa,
                new AsyncResponse() {
                    @Override
                    public void processFinish(String json) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            Integer statusPompaJson = jsonObject.getInt("status_pompa");
                            statusPompa = new StatusPompa(String.valueOf(statusPompaJson));
                            if (statusPompaJson == 1) {
                                textViewStatusPompa.setText("Pompa Air Sedang Menyala");
                                buttonPompaAir.setText("Matikan");
                            } else {
                                textViewStatusPompa.setText("Pompa Air Sedang Mati");
                                buttonPompaAir.setText("Hidupkan");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        monitoringPengisianRequest.execute();
    }

    private void getPengisianAir(final String maksimal) {
        MonitoringPengisianRequest monitoringPengisianRequest = new MonitoringPengisianRequest(
                MonitoringPengisianActivity.this,
                MonitoringPengisianRequest.ActionPengisianAir,
                new AsyncResponse() {
                    @Override
                    public void processFinish(String json) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            Integer ketinggian = jsonObject.getInt("jumlah");
                            Integer maksimalKetinggian = Integer.parseInt(maksimal);
                            Integer sisaKetinggian = maksimalKetinggian - ketinggian;
                            if(sisaKetinggian < 0) {
                                maksimalKetinggian = ketinggian;
                            }

                            Double percent = (double) ketinggian / maksimalKetinggian * 100;

                            progressBarPengisianAir.setProgress(percent.intValue());
                            textViewProgress.setText(percent.intValue() + "%");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        monitoringPengisianRequest.execute();
    }

    private void getStatusPompaRealtime() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("StatusPompa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                statusPompa = dataSnapshot.getValue(StatusPompa.class);
               if (Integer.parseInt(statusPompa.status) == 1) {
                   textViewStatusPompa.setText("Pompa Air Sedang Menyala");
                   buttonPompaAir.setText("Matikan");
               } else {
                   textViewStatusPompa.setText("Pompa Air Sedang Mati");
                   buttonPompaAir.setText("Hidupkan");
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadNote:onCancelled", databaseError.toException());
            }
        });
    }

    private void getPengisianAirRealtime(final String maksimal) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("PengisianAir").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pengisianAir = dataSnapshot.getValue(PengisianAir.class);
                Integer ketinggian = Integer.parseInt(pengisianAir.jumlah);
                Integer maksimalKetinggian = Integer.parseInt(maksimal);
                Integer sisaKetinggian = maksimalKetinggian - ketinggian;
                if (sisaKetinggian == 0) {
                    if (statusPenuh == false) {
                        maksimalKetinggian = maksimalKetinggian + 1;
                    }
                } else if(sisaKetinggian < 0) {
                    maksimalKetinggian = ketinggian;
                    statusPenuh = true;
                } else {
                    statusPenuh = false;
                }

                Double percent = (double) ketinggian / maksimalKetinggian * 100;

                progressBarPengisianAir.setProgress(percent.intValue());
                textViewProgress.setText(percent.intValue() + "%");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadNote:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonPompaAir) {
            if (Integer.parseInt(statusPompa.status) == 1) {
                if (Config.REALTIME == false) {
                    textViewStatusPompa.setText("Pompa Air Sedang Mati");
                    buttonPompaAir.setText("Hidupkan");
                    statusPompa = new StatusPompa("0");
                }
                simpanStatusPompa(0);
            } else {
                if (Config.REALTIME == false) {
                    textViewStatusPompa.setText("Pompa Air Sedang Menyala");
                    buttonPompaAir.setText("Matikan");
                    statusPompa = new StatusPompa("1");
                }
                simpanStatusPompa(1);
            }
        }
    }

    private void simpanStatusPompa(Integer status) {
        MonitoringPengisianRequest monitoringPengisianRequest = new MonitoringPengisianRequest(
                MonitoringPengisianActivity.this, status, MonitoringPengisianRequest.ActionSimpanStatusPompa
        );

        monitoringPengisianRequest.execute();
    }
}
