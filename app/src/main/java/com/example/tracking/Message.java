package com.example.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Message extends AppCompatActivity {
    private EditText Message;
    private Button SentMessage;
    String text;
    private TextView EmailUser;
    private DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    Map<String, Object> hashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Message = findViewById(R.id.editTextMessage);
        SentMessage = findViewById(R.id.button_sentMessage);
        EmailUser = findViewById(R.id.textView_check);
        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        EmailUser.setText(firebaseUser.getEmail());
        SentMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = Message.getText().toString();

                if (text.isEmpty()) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(Message.this);
                    dialog.setTitle("กรุณากรอกข้อความ");
                }
              else{
                    SentMessageToFirebase ();
                }

            }
        });


    }

    private void SentMessageToFirebase () {
        reference = FirebaseDatabase.getInstance().getReference().child("Location").child(firebaseUser.getUid()); //ใช้Uid ที่firebase genขึ้นมา
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                hashMap = new HashMap<>();
                hashMap.put("Message", text);
                reference.updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}