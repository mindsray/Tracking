package com.example.tracking;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Message extends AppCompatActivity {
    private EditText Message;
    private Button SentMessage , history;
    String text;
    TextView textViewTime;
    private TextView EmailUser;
    private DatabaseReference reference , reference2 , reference3;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    Map<String, Object> hashMap ;
    Map<String, String> hashmapHistory;
    ArrayList<String> myArrList = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    public int count;
    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Message = findViewById(R.id.editTextMessage);
        SentMessage = findViewById(R.id.button_sentMessage);
        history = findViewById(R.id.button_History);
        textViewTime = findViewById(R.id.textViewTime);
        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

         time = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        textViewTime.setText(time);

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Message.this, HistoryMessage.class));

            }
        });
        SentMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                text = Message.getText().toString();
                if (text.isEmpty()) {
                    Toast.makeText(Message.this, "กรุณากรอกข้อความ", Toast.LENGTH_SHORT).show();
                }
              else{

                    SentMessageToFirebase();
                    addTime();
                    addMessageHistory();
                    myArrList.add(text);
//                    Intent intent = new Intent(getBaseContext(), HistoryMessage.class);
//                    intent.putExtra("Mymessage", myArrList);
//                    startActivity(intent);


                }
            }
        });
    }



//    private  void showmessage (){
//        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,R.layout.activity_message,myArrList);
//        final ListView listView =(ListView)findViewById(R.id.windowList);
//        myArrList.add(text);
//        System.out.println(myArrList);
//        listView.setAdapter(arrayAdapter);
//    }

    private void SentMessageToFirebase () {
        reference = FirebaseDatabase.getInstance().getReference().child("Location").child(firebaseUser.getUid()); //ใช้Uid ที่firebase genขึ้นมา
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                hashMap = new HashMap<>();
                hashMap.put("Message", text);
                reference.updateChildren(hashMap);
                Toast.makeText(Message.this, "ส่งข้อความเสร็จสิ้น", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void addTime () {
        reference = FirebaseDatabase.getInstance().getReference().child("Location").child(firebaseUser.getUid()).child("Message"); //ใช้Uid ที่firebase genขึ้นมา
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashMap = new HashMap<>();
                hashMap.put("Time",time);
                reference.updateChildren(hashMap);
                System.out.println(time);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void addMessageHistory(){
        reference2 = FirebaseDatabase.getInstance().getReference().child("MessageHistory").child(firebaseUser.getUid());
        reference2.addListenerForSingleValueEvent(
                    new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashmapHistory = new HashMap<String, String>();
                hashmapHistory.put("text",text);
                count = (int) dataSnapshot.getChildrenCount();
                System.out.println(count);
                reference2.child(String.valueOf(count)).setValue(text);
                System.out.println(text);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    }


