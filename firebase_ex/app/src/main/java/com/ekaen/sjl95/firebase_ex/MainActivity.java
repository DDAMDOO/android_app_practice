package com.ekaen.sjl95.firebase_ex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    private Button sendbt;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendbt = (Button) findViewById(R.id.button2);

        sendbt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                databaseReference.child("message").push().setValue("2");
            }
        });
    }
}