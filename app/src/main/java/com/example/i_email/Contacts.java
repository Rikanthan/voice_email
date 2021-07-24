package com.example.i_email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
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

public class Contacts extends AppCompatActivity {
    ArrayList<String> contactsList = new ArrayList<>();
    ArrayList<String> contactsId = new ArrayList<>();
    DatabaseReference userReff;
    TextToSpeech speechText;
    Home home;
    TextView textView;
    int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid= firebaseUser.getUid();
        home = new Home();
        textView = findViewById(R.id.contact);
        speechText = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
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
               // adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void previous(View v)
    {
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
    public void next(View v)
    {
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

    }
    private void speak(String text) {
        // String text = "Welcome";//to voice-email application. please press mic button and speak few words to send a message.";
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
}