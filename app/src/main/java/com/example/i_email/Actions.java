package com.example.i_email;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class Actions extends AppCompatActivity {
    TextToSpeech speechText;
    TextView actionText;
    Locale lang;
    Float speed, pitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
        actionText = findViewById(R.id.seeActions);
        try{
            pitch = getIntent().getFloatExtra("pitch",0.7f);
            speed = getIntent().getFloatExtra("speed",0.8f);
           lang = Locale.UK;
        }
        catch (Exception e)
        {
            System.out.println(e);
            lang = Locale.UK;
            pitch = 0.7f;
            speed = 0.8f;
        }

        speechText = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = speechText.setLanguage(lang);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {
                    Log.e("TTS","Initialization is successfull");
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                actionText.setText(matches.get(0));
                if(matches != null)
                {
                    if(matches.get(0).contains("inbox"))
                    {
                        speak("You go to inbox page");
                        Intent i1 = new Intent(Actions.this,ShowInbox.class);
                        startActivity(i1);

                    }
                    else if(matches.get(0).contains("send"))
                    {
                        speak("You go to sent box page");
                        Intent i2 = new Intent(Actions.this,ShowSentbox.class);
                        startActivity(i2);

                    }
                    else if(matches.get(0).contains("write")
                            ||  matches.get(0).contains("ght")
                                    ||  matches.get(0).contains("contacts"))
                    {
                        speak("You go to contacts page");
                        Intent i3 = new Intent(Actions.this,Contacts.class);
                        startActivity(i3);

                    }
                    else if(matches.get(0).contains("setting"))
                    {
                        speak("You go to settings page");
                        Intent i4 = new Intent(Actions.this,SettingsActivity.class);
                        startActivity(i4);
                    }
                    //speak("Please tell correct page");
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        findViewById(R.id.action_button).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

                        break;
                }
                return false;
            }
        });
    }
    private void speak(String text) {
        speechText.setPitch(pitch);
        speechText.setSpeechRate(speed);
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