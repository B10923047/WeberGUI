package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.ui.dashboard.watchHistory;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String JSON_URL = "http://172.20.10.4:3000/api/courses";
    String date, year, month, result;
    int day;
    String dateString;
    ArrayList<HashMap<String, String>> friendsList;
    int week1 = 0;
    int week2 = 0;
    int week3 = 0;
    int week4 = 0;
    int week5 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        com.example.myapplication.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        friendsList = new ArrayList<>();
        GetData getData = new GetData();
        getData.execute();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.navigation_dashboard) {
                Intent intent = new Intent(MainActivity.this, com.example.myapplication.ui.dashboard.calendar.class);
                startActivity(intent);
            }
        });


    }

    public void openAboutHistory(View view) {
        Intent intent = new Intent(this, watchHistory.class);
        startActivity(intent);
    }


    @SuppressLint("StaticFieldLeak")
    public class GetData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... string) {
            String current = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(JSON_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);
                    int data = isr.read();
                    while (data != -1) {
                        current = current.concat(String.valueOf((char) data));
                        data = isr.read();

                    }
                    return current;

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Calendar calendar = Calendar.getInstance();
                String thisMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
                String thisYear = String.valueOf(calendar.get(Calendar.YEAR));
                Log.d("month", thisYear + "/" + thisMonth);

                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("parkinson");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    date = jsonObject1.getString("date");
                    String[] parts = date.split("/");
                    year = parts[0];
                    month = parts[1];
                    day = Integer.parseInt(parts[2]);
                    result = jsonObject1.getString("result");

                    // Hashmap
                    if (result.equals("有異狀") && year.equals(thisYear) && month.equals(thisMonth)) {
                        if (1 <= day && day <= 7) {
                            week1 = week1 + 1;
                        } else if (8 <= day && day <= 14) {
                            week2 = week2 + 1;
                        } else if (15 <= day && day <= 21) {
                            week3 = week3 + 1;
                        } else if (22 <= day && day <= 28) {
                            week4 = week4 + 1;
                        } else if (29 <= day) {
                            week5 = week5 + 1;
                        }

                    }
                }
                LineChart mChart = (LineChart) findViewById(R.id.linechart);
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(false);
                ArrayList<Entry> yValues = new ArrayList<>();
                //int a = 1;
                yValues.add(new Entry(0, week1));
                yValues.add(new Entry(1, week2));
                yValues.add(new Entry(2, week3));
                yValues.add(new Entry(3, week4));
                //yValues.add(new Entry(4,week5));
                LineDataSet set1 = new LineDataSet(yValues, "Data Set 1");
                set1.setFillAlpha(110);
                set1.setColor(Color.RED);
                set1.setDrawFilled(true);
                set1.setLineWidth(3f);
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);
                LineData data = new LineData(dataSets);
                mChart.setData(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}




