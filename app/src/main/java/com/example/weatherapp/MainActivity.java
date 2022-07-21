package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UTFDataFormatException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView resultTextView;

    public void getWeather(View view)
    {
        try {
            String encodeCityName = URLEncoder.encode(editText.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodeCityName + "&appid=46f831c7dfa00e4a0ef522a89597cc47");
            Log.i("Confirmation: ", "I am working");

            InputMethodManager mngr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mngr.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
        }
    }


    public class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        public String doInBackground(String... urls) {


            URL url;
            String result = "";
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1)
                {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch(Exception e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT ).show();
                    }
                });
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
//                String tempInfo = jsonObject.getString("main");
                Log.i("Weather Content" , weatherInfo);



                JSONArray arr = new JSONArray(weatherInfo);
                ;
                String meassage = "";

                String main = "";
                String description = "";
                for (int i=0 ; i<arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")){
                        meassage += main + ": " + description + "\r\n";
                    }
                }

                if (!meassage.equals("")){
                    resultTextView.setText(meassage);
                    resultTextView.setVisibility(View.VISIBLE);

                }
                else
                    Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT ).show();
            }catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find weather :(",Toast.LENGTH_SHORT ).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         editText = findViewById(R.id.editText);
         resultTextView = findViewById(R.id.resultTextview);


    }
}