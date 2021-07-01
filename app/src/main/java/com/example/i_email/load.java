package com.example.i_email;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;


import java.util.Locale;

public class load extends AppCompatActivity {

    private TextToSpeech speechText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);


        checkconnection();

        speechText = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = speechText.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                       Log.e("TTS","Initialization is successfull");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });



    }
    public void checkconnection(){

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if(null!=activeNetwork){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                //  Toast.makeText(this,"Connection Enable",Toast.LENGTH_SHORT).show();

            }
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                // Toast.makeText(this,"Data Network Enable",Toast.LENGTH_SHORT).show();

            }

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    speak();
                    Intent intent = new Intent(load.this, MainActivity.class);
                    startActivity(intent);

                }
            }, 2000);


        }
        else {
            Toast.makeText(this,"Please check your Internet Connection",Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(load.this, load.class);
                    startActivity(intent);
                }
            }, 10000);
        }
    }
    private void speak() {
        String text = "I-Email seyalikku ungalai varavetkirom";
        speechText.setPitch(0.7f);
        speechText.setSpeechRate(0.7f);
        speechText.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    @Override
    protected void onDestroy() {
        if (speechText != null) {
            speechText.stop();
            speechText.shutdown();
        }
        super.onDestroy();
    }

}



