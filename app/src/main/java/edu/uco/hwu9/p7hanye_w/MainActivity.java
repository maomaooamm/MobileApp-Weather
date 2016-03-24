package edu.uco.hwu9.p7hanye_w;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    private LatLng cityPosition;
    private Double temperature;
    private String city;
    private EditText txtCity;
    private TextView tCity, tWeather, tTemp, tWind;
    private Button btnSubmit;
    private Button btnLocate;
    Handler handler;

    static String API_KEY;

    public MainActivity() {
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCity = (EditText) findViewById(R.id.txtCity);
        tCity = (TextView) findViewById(R.id.tCity);
        tWeather = (TextView) findViewById(R.id.tWeather);
        tTemp = (TextView) findViewById(R.id.tTemp);
        tWind = (TextView) findViewById(R.id.tWind);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnLocate = (Button) findViewById(R.id.btnLocate);
        API_KEY = getString(R.string.API_KEY);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetWeather().execute(txtCity.getText().toString());
            }
        });

        btnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double a = cityPosition.latitude;
                double b = cityPosition.longitude;
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("city", city)
                        .putExtra("temperature", temperature)
                        .putExtra("a", a).putExtra("b", b);
                startActivity(intent);
            }
        });

    }

    private class GetWeather extends AsyncTask<String, Void, JSONObject> {

        private static final String API_URL =
                "http://api.openweathermap.org/data/2.5/weather?";
        private final String TAG = "HttpGetWeather";

        @Override
        protected JSONObject doInBackground(String... params) {
            InputStream in = null;
            HttpURLConnection con = null;
            JSONObject result = null;
            try {
                Uri uri = Uri.parse(API_URL).buildUpon()
                        .appendQueryParameter("q", params[0] + ",us")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("appid", API_KEY)
                        .build();

                URL url = new URL(uri.toString());
                con = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(con.getInputStream());
                String data = readStream(in);
                result = new JSONObject(data);

                if(result.getInt("cod") != 200){
                    return null;
                }

            } catch (Exception e) {
                return null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            if (result == null) {
                Toast.makeText(MainActivity.this, "Error! City not found!",
                        Toast.LENGTH_LONG).show();
            } else {

                renderWeather(result);
            }
        }
    }

    private void renderWeather(JSONObject result) {
        try {
            JSONObject loc = result.getJSONObject("coord");
            cityPosition = new LatLng(loc.getDouble("lat"), loc.getDouble("lon"));

            city = result.getString("name");
            tCity.setText("City: " + city);

            JSONObject weatherObject =
                    result.getJSONArray("weather").getJSONObject(0);
            String description = weatherObject.getString("description");
            tWeather.setText("Weather: " + description);

            JSONObject main = result.getJSONObject("main");
            temperature = main.getDouble("temp");
            tTemp.setText(String.format("Temperature: "
                    + "%.2f", main.getDouble("temp")) + " °C");

            JSONObject wind = result.getJSONObject("wind");
            tWind.setText("Wind: " + wind.getDouble("speed") + " km/h");
        } catch (Exception e) {
            Log.e("GetJSONWeather", "Data is not found");
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer data = new StringBuffer("");
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            Log.e("ReadStream", "IOException");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }
}


