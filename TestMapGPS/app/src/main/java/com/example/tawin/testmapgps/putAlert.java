package com.example.tawin.testmapgps;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class putAlert extends AppCompatActivity {


    EditText editTextEvent;
    Button addEventButton;
    private static final int REQUEST_VOIC_RECOGINITION = 10001;
    Button voicebutton;
    EditText TitleName;
    String eventVoice;
    String event = null;
    String strDate;
    double lat, lng;
    String lat2,lng2;
    LocationManager lm;
    private static final String URL = "http://192.168.0.3/ProjectJ/insert.php";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_alert);

        voicebutton =  findViewById(R.id.voiceBut2);

        editTextEvent = findViewById(R.id.editIuput);
        addEventButton = findViewById(R.id.addEventButton);


        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
                onButtonClick();
            }
        });


    }

    //voice Recognition
    private void callVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "th-TH");
        startActivityForResult(intent, REQUEST_VOIC_RECOGINITION);
    }
    //voice Recognition
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VOIC_RECOGINITION &&
                resultCode == RESULT_OK &&
                data != null) {
            ArrayList<String> resultList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String textVoice = resultList.get(0);
            editTextEvent.setText(textVoice);
            event = resultList.get(0);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);

            strDate = sdf.format(c.getTime());

            GpsTracker gt = new GpsTracker(getApplicationContext());
            Location l = gt.getLocation();
            lat = l.getLatitude();
            lng = l.getLongitude();

            lat2 = new Double(lat).toString();
            lng2 = new Double(lng).toString();
            onButtonClick();

        }
    }

    public void voiceBut(View view) {
        voicebutton =  findViewById(R.id.voiceBut);
        voicebutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                callVoiceRecognition();

            }
        });
    }



    //add Data to DB
    private void addArtist(){
        if(event == null) {
            event = editTextEvent.getText().toString().trim();
        }

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);

          strDate = sdf.format(c.getTime());

        GpsTracker gt = new GpsTracker(getApplicationContext());
        Location l = gt.getLocation();
        lat = l.getLatitude();
        lng = l.getLongitude();

        lat2 = new Double(lat).toString();
        lng2 = new Double(lng).toString();
    }

//test
private void onButtonClick(){
    if(!event.isEmpty() && !strDate.isEmpty()&& !lat2.isEmpty()&& !lng2.isEmpty()){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("onResponse",response);
                editTextEvent.setText("");

                Toast.makeText(putAlert.this,"เพิ่มข้อมูลแล้วจ้า",Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onError",error.toString());
                Toast.makeText(putAlert.this,"เกิดข้อผิดพลาดโปรดลองอีกครั้ง",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("name",event);
                params.put("lastname",strDate);
                params.put("lat2",lat2);
                params.put("lng2",lng2);

                return params;
            }
        };
        requestQueue.add(request);
    }

}


}
