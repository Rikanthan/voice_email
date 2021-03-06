package com.example.i_email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.i_email.Home;
import java.util.ArrayList;
import java.util.Locale;

public class Contacts extends AppCompatActivity implements View.OnClickListener {
    ArrayList<String> contactsList = new ArrayList<>();
    ArrayList<String> contactsId = new ArrayList<>();
    ImageButton previous, next;
    DatabaseReference userReff;
    TextToSpeech speechText;
    String sender;
    Home home;
    TextView textView;
    int position = 0;
    String uid;
    Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
         uid= firebaseUser.getUid();
        home = new Home();
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        textView = findViewById(R.id.contact);
        previous = (ImageButton) findViewById(R.id.pre);
        next = (ImageButton) findViewById(R.id.next);

        previous.setOnClickListener(
                v -> {
                    previous();
                }
        );
        next.setOnClickListener(
                v -> {
                    next();
                }
        );
        speechText = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = speechText.setLanguage(Locale.ENGLISH);
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
        userReff = FirebaseDatabase.getInstance().getReference().child("UserID");
        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                    if(snapshot1.exists())
                    {
                        String val = snapshot1.getKey();
                        boolean itsMe = false;
                        if(uid.contains(snapshot1.getValue().toString()))
                        {
                            itsMe = true;
                            sender = val;
                            contactsList.add("Me");
                            contactsId.add(snapshot1.getValue().toString());
                            if(position == 0)
                            {
                                textView.setText(contactsList.get(0));
                                speak(contactsList.get(0));
                            }
                        }
                        else if(!itsMe)
                        {
                            contactsList.add(val);
                            contactsId.add(snapshot1.getValue().toString());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    previous();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    next();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public void previous()
    {
        vibrator.vibrate(150);
        position--;
        if(position < 0)
        {
            position = 0;
        }
        try{
            textView.setText(contactsList.get(position));
            speak(contactsList.get(position));
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    public void next()
    {
        vibrator.vibrate(150);
        position++;
        if(position > contactsList.size())
        {
            position = contactsList.size();
        }
        try{
            textView.setText(contactsList.get(position));
            speak(contactsList.get(position));
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    public void selectContact(View v)
    {
        vibrator.vibrate(150);
        try{
            Intent intent = new Intent(this,Home.class);
            intent.putExtra("sender",sender);
            intent.putExtra("receiver",contactsList.get(position));
            intent.putExtra("receiverId",contactsId.get(position));
            speak("You are directing to write message");
            startActivity(intent);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    private void speak(String text) {
        speechText.setPitch(0.8f);
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

    @Override
    public void onClick(View v) {

    }
}