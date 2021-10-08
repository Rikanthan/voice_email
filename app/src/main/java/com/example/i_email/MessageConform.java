package com.example.i_email;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessageConform extends AppCompatActivity {
    TextView msgView,title;
    DatabaseReference reff;
    String id = "";
    String receiverId = "";
    String selectedUser = "";
    String currentDate = "";
    String currentTime = "";
    String sender = "";
    String uid = "";
    String message = "";
    Inbox inbox;
    Sentbox sentbox;
    Vibrator vibrator;
    String email,username,phone,passCode;
    boolean isPasscode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_conform);
        msgView = findViewById(R.id.message);
        title = findViewById(R.id.titleconfirm);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        reff = FirebaseDatabase.getInstance().getReference().child("User");
        inbox = new Inbox();
        sentbox = new Sentbox();
        reff = FirebaseDatabase.getInstance().getReference().child("User");
        isPasscode = getIntent().getBooleanExtra("isPasscode",false);
        if(isPasscode)
        {
            email = getIntent().getStringExtra("email");
            username = getIntent().getStringExtra("username");
            phone = getIntent().getStringExtra("phone");
            passCode = getIntent().getStringExtra("passcode");
            msgView.setText(passCode);
            title.setText("Confirm your passcode");
        }
        else
        {
            receiverId = getIntent().getStringExtra("receiverId");
            selectedUser = getIntent().getStringExtra("receiver");
            sender = getIntent().getStringExtra("sender");
            message = getIntent().getStringExtra("message");
            msgView.setText(message);
        }

        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
    }

    public void confirm(View view)
    {
        vibrator.vibrate(150);
        if(isPasscode)
        {
            UserDetails userDetails = new UserDetails();
            userDetails.setEmail(email);
            userDetails.setPhoneNo(phone);
            userDetails.setUsername(username);
            userDetails.setPassCode(passCode);
            reff.child(uid).setValue(userDetails);
            Intent intent = new Intent(MessageConform.this,MainActivity.class);
            startActivity(intent);
        }
        else
        {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateformatter = new SimpleDateFormat("dd MMM, yyyy");
            SimpleDateFormat timeformatter = new SimpleDateFormat("HH:mm:ss a");
            currentDate = dateformatter.format(calendar.getTime());
            currentTime = timeformatter.format(calendar.getTime());
            sentbox.setDate(currentDate);
            sentbox.setTime(currentTime);
            sentbox.setMsg(message);
            sentbox.setReceiver(selectedUser);
            id = currentDate+" "+currentTime;
            reff.child(uid).child("Sentbox").child(id).setValue(sentbox);
            inbox.setDate(currentDate);
            inbox.setTime(currentTime);
            inbox.setMsg(message);
            inbox.setSender(sender);
            reff.child(receiverId).child("Inbox").child(id).setValue(inbox);
        }
    }
    public void close(View view)
    {
        vibrator.vibrate(150);
        if(isPasscode)
        {
            Intent intent = new Intent(MessageConform.this,Authenticaton.class);
            intent.putExtra("email",email);
            intent.putExtra("username",username);
            intent.putExtra("phone",phone);
            intent.putExtra("isLogin",false);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(MessageConform.this,Home.class);
            startActivity(intent);
        }
    }
}