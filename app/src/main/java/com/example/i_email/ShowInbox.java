package com.example.i_email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowInbox extends AppCompatActivity implements InboxHolder.OnItemClickListener{
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    LinearLayoutManager linearLayoutManager;
    FirebaseAuth firebaseAuth;
    String fuser;
    Button deletebutton;
    TextToSpeech speechText;
    InboxHolder mAdapter;
    List<Inbox> newcartlist;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_inbox);
        recyclerView = findViewById(R.id.show_inbox);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        newcartlist=new ArrayList<>();
        firebaseAuth=FirebaseAuth.getInstance();
        fuser=FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        databaseReference= FirebaseDatabase.getInstance().getReference("User").child(fuser).child("Inbox");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Inbox mycart=dataSnapshot.getValue(Inbox.class);
                    String msg = mycart.getMsg();
                    String sender = mycart.getSender();
                    String receiveDate = mycart.getDate();
                    String receiveTime = mycart.getTime();

                    mycart.setMsg(msg);
                    mycart.setSender(sender);
                    mycart.setDate(receiveDate);
                    mycart.setTime(receiveTime);
                    Inbox showcart=new Inbox(msg,sender,receiveDate,receiveTime);
                    newcartlist.add(showcart);
                }
                mAdapter = new InboxHolder(ShowInbox.this, newcartlist);
                mAdapter.setOnItemClickListener(ShowInbox.this);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
    databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            for(DataSnapshot dataSnapshot:snapshot.getChildren())
            {

                if(i == position)
                {
                    Inbox mycart1=dataSnapshot.getValue(Inbox.class);
                    String msg = mycart1.getMsg();
                    String sender = mycart1.getSender();
                    String receiveDate = mycart1.getDate();
                    String receiveTime = mycart1.getTime();
                    speak("Message is"+msg +" send by"+sender+" at "+receiveDate+ "    "+ receiveTime);
                    i = 0;
                    break;
                }
                i++;
            }
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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