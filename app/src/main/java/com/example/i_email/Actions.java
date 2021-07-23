package com.example.i_email;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Actions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
    }
    public void Action(View v)
    {
        Intent i = new Intent(this,Home.class);
        startActivity(i);
    }
}