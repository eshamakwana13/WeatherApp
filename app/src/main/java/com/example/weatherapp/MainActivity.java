package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    EditText cityView;
    String city;
    String lat, lng;
    String temp, humid, wind, precip;
    TextView t, h, w, p;

    String GOOGLE_API_KEY = "";
    String DARK_SKI_API_KEY = "";

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityView = findViewById(R.id.address);
        t = findViewById(R.id.tempValue);
        h = findViewById(R.id.humidityValue);
        w = findViewById(R.id.windValue);
        p = findViewById(R.id.precipValue);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void getWeather(View v) {
        city = cityView.getText().toString().replaceAll(" ", "+");
        System.out.println("Made it");
        new URLRequest().execute();
        System.out.println("Made it");
    }

    private class URLRequest extends AsyncTask<Void,Void,Void> {

        Exception ex;

        @Override
        protected Void doInBackground(Void... voids) {

            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + city + "&key=" + GOOGLE_API_KEY;

            String response = request(url);

            try {
                parseGoogleResult(response);
            } catch (Exception e) {
                System.out.println(e);
            }

            System.out.println(city);
            System.out.println(response);

            url = "https://api.darksky.net/forecast/" +DARK_SKI_API_KEY + "/" + lat + "," + lng;

            response = request(url);

            try {
                parseDarkSkyResult(response);
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println(response);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    t.setText(temp);
                    h.setText(humid);
                    w.setText(wind);
                    p.setText(precip);

                    LatLng loc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    mMap.addMarker(new MarkerOptions().position(loc));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                }
            });

            return null;
        }

        protected String request(String url) {
            try {
                URL request = new URL(url);
                HttpURLConnection con = (HttpURLConnection) request.openConnection();

                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                int code = con.getResponseCode();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer response = new StringBuffer();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            } catch (Exception e) {
                this.ex = e;
                System.out.println(e);
            }

            return null;
        }
    }

    public void parseGoogleResult(String response) throws Exception {
        JSONObject obj = new JSONObject(response);
        JSONArray results = obj.getJSONArray("results");
        JSONObject info = results.getJSONObject(0);
        JSONObject geometry = info.getJSONObject("geometry");
        JSONObject location = geometry.getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");

        System.out.println(lat + ", " + lng);
    }

    public void parseDarkSkyResult(String response) throws Exception {
        JSONObject obj = new JSONObject(response);
        JSONObject current = obj.getJSONObject("currently");
        temp = current.getString("temperature");
        humid = current.getString("humidity");
        wind = current.getString("windSpeed");
        precip = current.getString("precipProbability");

        System.out.println(temp + ", " + humid + ", " + wind + ", " + precip);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
