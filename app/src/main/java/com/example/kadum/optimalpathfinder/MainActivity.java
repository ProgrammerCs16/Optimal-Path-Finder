package com.example.kadum.optimalpathfinder;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView originEditText, destionationEditText;
    static String origin = "", destinations = "", locations = "";
    int [][] durationMatrix ;

    public void findDurationMatrix(){

        origin = originEditText.getText().toString();
        destinations = destionationEditText.getText().toString();

        locations +=origin;

        String[] splitDestinations = destinations.split(", ");
        int size = splitDestinations.length+1;

        durationMatrix = new int[size][size];

        for(int i=0;i<splitDestinations.length;i++)
            locations +="|"+splitDestinations[i].toString();

        Toast.makeText(this, locations, Toast.LENGTH_SHORT).show();

        Log.i("Locations", locations);


        // Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        // startActivity(intent);

        //New code
        DownloadTask content = new DownloadTask();
        String result = null;

        try {

            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?traffic_model=best_guess&departure_time=now&units=imperial&origins="+locations+"&destinations="+locations+"&key=AIzaSyDf0QEg_0b8pn5fZtrY27lW7VzGYOLMGSw";

            result = content.execute(url).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void go(View view) {

        findDurationMatrix();

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try{

                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while (data != -1){

                    char current = (char) data;

                    result += current;

                    data = inputStreamReader.read();
                }

                return result;

            }catch (Exception e){

                e.printStackTrace();
                return "Failed";
            }

           /* Log.i("URL",strings[0]);
            return "done";*/
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                JSONObject jsonObject = new JSONObject(result);

                String rows = jsonObject.getString("rows");

                JSONArray jsonArray = new JSONArray(rows);
                JSONObject jsonPart = new JSONObject();

                for(int i=0;i<jsonArray.length();i++) {

                    jsonPart = jsonArray.getJSONObject(i);


                    String elements = jsonPart.getString("elements");

                    JSONArray jsonSubArray = new JSONArray(elements);
                    JSONObject jsonSubPart = new JSONObject();

                    JSONObject finalJsonObject;

                    for (int j = 0; j < jsonSubArray.length(); j++) {

                        jsonSubPart = jsonSubArray.getJSONObject(j);
                        String duration = jsonSubPart.getString("duration_in_traffic");

                        finalJsonObject = new JSONObject(duration);

                        int duration_in_traffic = (int) finalJsonObject.get("value");

                        durationMatrix[i][j] = duration_in_traffic;

                        String a = Integer.toString(i);
                        String b = Integer.toString(j);

                        Log.i("durationMatrix "+a+" to "+b,Integer.toString(durationMatrix[i][j]));


                        //Log.i("Duration", Integer.toString(duration_in_traffic));

                    }

                }

            } catch (JSONException e) {

                Log.i("MESSAGE","ERROR IN PARSING JSON");
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        originEditText = (EditText)findViewById(R.id.originEditText);
        destionationEditText = (EditText)findViewById(R.id.destinationEditText);

        /*DownloadTask content = new DownloadTask();
        String result = null;

        try {

            result = content.execute("https://maps.googleapis.com/maps/api/distancematrix/json?traffic_model=best_guess&departure_time=now&units=imperial&origins=IIT+Patna|Bihta|Pasighat&destinations=IIT+Patna|Bihta|Pasighat&key=AIzaSyDf0QEg_0b8pn5fZtrY27lW7VzGYOLMGSw").get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

    }
}
