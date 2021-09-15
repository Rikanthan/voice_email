package com.example.i_email;

import androidx.annotation.NonNull;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class Authenticaton extends AppCompatActivity {

    TextToSpeech speechText;
    TextView actionText;
    Locale lang;
    Float speed, pitch;
    String uid;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticaton);
        actionText = findViewById(R.id.seeActions);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(uid);

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
                    databaseReference.child("passcode").addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                     String key = snapshot.getValue(String.class);
                                        if(matches.get(0).contains(key))
                                        {
                                            speak("Your password is correct");
                                            Intent i1 = new Intent(Authenticaton.this,Actions.class);
                                            startActivity(i1);
                                            speak("Welcome to voice-email application.  If you want go to see inbox tell us inbox.  If you want go to see sentbox tell us sentbox. If you want to write a message tell us write");
                                        }
                                        else
                                        {
                                            speak("Please tell correct passcode");
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            }
                    );


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