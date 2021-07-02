package com.example.i_email;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Home extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextToSpeech speechText;
    FirebaseAuth firebaseAuth;
    DatabaseReference reff;
    DatabaseReference userReff;
    Spinner spinner;
    String inboxid = "";
    long sentboxid = 0;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList = new ArrayList<>();
    ArrayList<String> contacts = new ArrayList<>();
    String receiverId = "";
    String selectedUser = "";
    String currentDate = "";
    String currentTime = "";
    Inbox inbox;
    Sentbox sentbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkPermission();
       final EditText  msg = findViewById(R.id.editText);
        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid= firebaseUser.getUid();
         inbox = new Inbox();
         sentbox = new Sentbox();
        System.out.println("current user"+uid);
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
        spinner =(Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        adapter = new ArrayAdapter<String>(Home.this, android.R.layout.simple_spinner_item,spinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        userReff = FirebaseDatabase.getInstance().getReference().child("UserID");
        reff = FirebaseDatabase.getInstance().getReference().child("User");

        // speak();

        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                    if(snapshot1.exists())
                    {
                        String val = snapshot1.getKey();
                        spinnerDataList.add(val);
                        contacts.add(snapshot1.getValue().toString());
//                        boolean itsMe = false;
//                        if(uid.contains(snapshot1.getValue().toString()))
//                        {
//                            itsMe = true;
//                        }
//                        else if(!itsMe)
//                        {
//                            String val = snapshot1.getKey();
//                            spinnerDataList.add(val);
//                            contacts.add(snapshot1.getValue().toString());
//                        }

                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
          adapter.notifyDataSetChanged();

        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());


        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                speak(  "Your message sent successfully");
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches

                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Calendar calendar = Calendar.getInstance();
                //displaying the first match
                if (matches != null)
                    msg.setText(matches.get(0));
                reff.child(uid).child("Sentbox").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            sentboxid = snapshot.getChildrenCount();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

                SimpleDateFormat dateformatter = new SimpleDateFormat("dd MMM, yyyy");
                SimpleDateFormat timeformatter = new SimpleDateFormat("HH:mm:ss a");
                Date date = new Date();
                currentDate = dateformatter.format(calendar.getTime());
                currentTime = timeformatter.format(calendar.getTime());
                    sentbox.setDate(currentDate);
                    sentbox.setTime(currentTime);
                    sentbox.setMsg(msg.getText().toString().trim());
                    sentbox.setReceiver(selectedUser);
                    reff.child(uid).child("Sentbox").child(String.valueOf(++sentboxid)).setValue(sentbox);
                    inbox.setDate(currentDate);
                    inbox.setTime(currentTime);
                    inbox.setMsg(msg.getText().toString().trim());
                    inbox.setSender(spinnerDataList.get(contacts.indexOf(uid)));
                    inboxid = UUID.randomUUID().toString();
                    reff.child(receiverId).child("Inbox").child(inboxid).setValue(inbox);
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        findViewById(R.id.button).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        msg.setHint("You will see input here");
                        speak(  "              Your message sent successfully");
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        msg.setText("");
                        msg.setHint("Listening...");

                        break;
                }
                return false;
            }
        });
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }
    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //txvResult.setText("Hello");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                }
                break;
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),spinnerDataList.get(position), Toast.LENGTH_LONG).show();
        selectedUser = spinnerDataList.get(position);
        receiverId = contacts.get(position);
        speak("you selected "+spinnerDataList.get(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    public void goInbox(View v)
    {
        Intent intent = new Intent(getApplicationContext(), ShowInbox.class);
        startActivity(intent);
    }

}
