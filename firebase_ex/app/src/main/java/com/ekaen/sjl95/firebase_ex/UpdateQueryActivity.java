package com.ekaen.sjl95.firebase_ex;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateQueryActivity extends AppCompatActivity {

    private static final String TAG = "ACTIVITY";

    FirebaseDatabase db;
    DatabaseReference ref;

    TextView tvMessage;
    EditText etMessage;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_query);

        tvMessage = findViewById(R.id.tv_message);
        etMessage = findViewById(R.id.et_newData);
        btn = findViewById(R.id.bt_update);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("message : ");

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String newMessage = etMessage.getText().toString();
                ref.setValue(newMessage);
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String val = dataSnapshot.getValue(String.class);

                tvMessage.setText(val);
                Log.d(TAG, "VALUE : "+val);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "fail", databaseError.toException());
            }
        });
    }
}
