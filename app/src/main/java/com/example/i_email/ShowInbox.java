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
import com.google.firebase.database.core.Context;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowInbox extends AppCompatActivity implements InboxHolder.OnItemClickListener,View.OnClickListener{
    RecyclerView recyclerView;
    DatabaseReference databaseReference,userReff;
    LinearLayoutManager linearLayoutManager;
    FirebaseAuth firebaseAuth;
    String fuser,uid,sender;
    int position = 0;
    TextToSpeech speechText;
    InboxHolder mAdapter;
    List<Inbox> newcartlist;
    Toolbar toolbar;
    ImageButton next, previous;
    ArrayList<String> contactsList = new ArrayList<>();
    ArrayList<String> contactsId = new ArrayList<>();
    TextView textView;
    int pos = 0;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_inbox);
        recyclerView = findViewById(R.id.show_inbox);
        recyclerView.setHasFixedSize(true);
        textView = findViewById(R.id.contact);
        linearLayoutManager=new LinearLayoutManager(this);
        toolbar = findViewById(R.id.inboxtoolbar);
        setSupportActionBar(toolbar);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        recyclerView.setLayoutManager(linearLayoutManager);
        previous = (ImageButton)findViewById(R.id.pre);
        next = (ImageButton)findViewById(R.id.next);
        newcartlist = new ArrayList<>();
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        firebaseAuth=FirebaseAuth.getInstance();
        fuser=FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        filter();
        show();

        previous.setOnClickListener(
                v -> {
                    pre();
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
            Inbox myInbox1 = newcartlist.get(position);
            String msg = myInbox1.getMsg();
            String sender = myInbox1.getSender();
            String receiveDate = myInbox1.getDate();
            String receiveTime = myInbox1.getTime();
            speak("Message is"+msg +" send by"+sender+" at "+receiveDate+ "    "+ receiveTime);
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
        switch (item.getItemId()){
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

    public void show()
    {
        newcartlist.clear();
        databaseReference= FirebaseDatabase.getInstance().getReference("User").child(fuser).child("Inbox");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Inbox myInbox=dataSnapshot.getValue(Inbox.class);
                    String msg = myInbox.getMsg();
                    String sender = myInbox.getSender();
                    String receiveDate = myInbox.getDate();
                    String receiveTime = myInbox.getTime();
                    if(sender.equals(contactsList.get(pos)))
                    {
                        newcartlist.add(myInbox);
                        position++;
                    }
                    if(position == snapshot.getChildrenCount())
                    {
                        speak("Your recent message "+msg +" send by"+sender+" at "+receiveDate+ "    "+ receiveTime);
                    }
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
                   pre();
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