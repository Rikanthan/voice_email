package com.example.i_email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ShowSentbox extends AppCompatActivity implements SentboxHolder.OnItemClickListener,View.OnClickListener{
    RecyclerView recyclerView;
    DatabaseReference databaseReference,userReff;
    LinearLayoutManager linearLayoutManager;
    ImageButton previous, next;
    FirebaseAuth firebaseAuth;
    String fuser,uid,sender;
    TextToSpeech speechText;
    SentboxHolder mAdapter;
    List<Sentbox> newcartlist;
    int pos = 0;
    int position = 0;
    int readPosition = 0;
    ArrayList<String> contactsList = new ArrayList<>();
    ArrayList<String> contactsId = new ArrayList<>();
    Toolbar toolbar;
    TextView textView;
    Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sentbox);
        toolbar = findViewById(R.id.sentboxtoolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.show_sentbox);
        recyclerView.setHasFixedSize(true);
        textView = findViewById(R.id.contact);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        newcartlist = new ArrayList<>();
        previous = (ImageButton) findViewById(R.id.pre);
        next = (ImageButton) findViewById(R.id.next);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        fuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        speechText = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = speechText.setLanguage(Locale.ENGLISH);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {
                    Log.e("TTS","Initialization is successfully");
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
        filter();
        show();
        previous.setOnClickListener(
                v -> {
                    pre();
                  read(position);
                }
        );
        next.setOnClickListener(
                v -> {
                    next();
                }
        );
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    @Override
    public void onItemClick(int position) {
            vibrator.vibrate(150);
            Sentbox mySentbox = newcartlist.get(position);
            String msg = mySentbox.getMsg();
            String receiver = mySentbox.getReceiver();
            String receiveDate = mySentbox.getDate();
            String receiveTime = mySentbox.getTime();
            speak("Message is"+msg +" received by"+receiver+" at "+receiveDate+ "    "+ receiveTime);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;
            case R.id.sentbox_icon:
                Intent i1 = new Intent(this, ShowSentbox.class);
                startActivity(i1);
                return true;
            case R.id.inbox_icon:
                Intent i2 = new Intent(this, ShowInbox.class);
                startActivity(i2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void read(int readPosition)
    {
        vibrator.vibrate(150);
        Sentbox mySentbox = newcartlist.get(readPosition);
        String msg = mySentbox.getMsg();
        String receiver = mySentbox.getReceiver();
        String receiveDate = mySentbox.getDate();
        String receiveTime = mySentbox.getTime();
        speak("Message is"+msg +" received by"+receiver+" at "+receiveDate+ "    "+ receiveTime);
    }

    public void show()
    {
        newcartlist.clear();
        databaseReference= FirebaseDatabase.getInstance().getReference("User").child(fuser).child("Sentbox");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Sentbox mySentbox = dataSnapshot.getValue(Sentbox.class);
                    String receiver = mySentbox.getReceiver();
                    String msg = mySentbox.getMsg();
                    String receiveDate = mySentbox.getDate();
                    String receiveTime = mySentbox.getTime();

                    if(receiver != null && receiver.equals(contactsList.get(pos)))
                    {
                        newcartlist.add(mySentbox);
                        position++;
                    }
                    if(position == snapshot.getChildrenCount())
                    {
                        speak("Your recent sent message "+msg +" receive by"+receiver+" at "+receiveDate+ "    "+ receiveTime);
                    }

                }
                Collections.reverse(newcartlist);
                mAdapter = new SentboxHolder(ShowSentbox.this,newcartlist );
                mAdapter.setOnItemClickListener(ShowSentbox.this);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {

            }
        });
    }
    public void filter()
    {
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
                            if(pos == 0)
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
                    try {
                        position++;
                        if(position > newcartlist.size())
                        {
                            position = newcartlist.size();
                        }
                        read(position);
                    }
                    catch (Exception e)
                    {
                        System.out.println(e);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    try {
                        position--;
                        if(position < 0)
                        {
                            position = 0;
                        }
                        read(position);
                    }
                    catch (Exception e)
                    {
                        System.out.println(e);
                    }
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    public void pre()
    {
        vibrator.vibrate(150);
        pos--;
        if(pos < 0)
        {
            pos = 0;
        }
        try{
            textView.setText(contactsList.get(pos));
            speak(contactsList.get(pos));
            show();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    public void next()
    {
        vibrator.vibrate(150);
        pos++;
        if(pos > contactsList.size())
        {
            pos = contactsList.size();
        }
        try{
            textView.setText(contactsList.get(pos));
            speak(contactsList.get(pos));
            show();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    @Override
    public void onClick(View v) {

    }
}