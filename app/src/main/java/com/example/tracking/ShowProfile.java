package com.example.tracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import static com.example.tracking.R.id.toolbarrrr;

public class ShowProfile extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView Name , LastName , Email ,PassWord , CFPassWord;
    CircleImageView imageview;
    private Button EditProfile;
    String name , lastName , email ,passWord , cfPassWord ,link;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        Toolbar toolbar = (Toolbar) findViewById(toolbarrrr);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // perform whatever you want on back arrow click
            }
        });
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_more_vert_black_18dp);
        toolbar.setOverflowIcon(drawable);

        Name = findViewById(R.id.EditTextName);
        LastName = findViewById(R.id.EditText_LastName);
        Email = findViewById(R.id.EditText_Email);
        PassWord = findViewById(R.id.EditText_PassWord);
        CFPassWord = findViewById(R.id.EditText_CFPassWord);
        EditProfile = findViewById(R.id.Button_Register);
        imageview = findViewById(R.id.imageView);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        Name = findViewById(R.id.EditTextName);
        LastName = findViewById(R.id.EditText_LastName);
        Email = findViewById(R.id.EditText_Email);
        PassWord = findViewById(R.id.EditText_PassWord);
        CFPassWord = findViewById(R.id.EditText_CFPassWord);
        EditProfile = findViewById(R.id.Button_Register);
        imageview = findViewById(R.id.imageView);
        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        Email.setText(firebaseUser.getEmail());
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("User").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("Name").getValue(String.class);
                Name.setText(name); //getnameไว้หน้าUI
                System.out.println(name);
                lastName = dataSnapshot.child("Last").getValue(String.class);
                LastName.setText(lastName);
                System.out.println(lastName);
                email = dataSnapshot.child("email").getValue(String.class);
                Email.setText(email);
                System.out.println(email);
                link = dataSnapshot.child("imageUrl").getValue(String.class);
                LoadImageUrl(link);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);


        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowProfile.this, EditProfile.class));
            }
        });


    }

    private void ShowData(){

        DatabaseReference rootRef2 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef2.child("User").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("Name").getValue(String.class);
                Name.setText(name); //getnameไว้หน้าUI
                lastName = dataSnapshot.child("Last").getValue(String.class);
                LastName.setText(lastName);
                email = dataSnapshot.child("email").getValue(String.class);
                Email.setText(email);
                link = dataSnapshot.child("imageUrl").getValue(String.class);
                LoadImageUrl(link);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);


        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowProfile.this, EditProfile.class));
            }
        });


    }

    private  void LoadImageUrl(String link){

        Picasso.get().load(link).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageview, new com.squareup.picasso.Callback(){

                    @Override
                    public void onSuccess() {

                        System.out.println("sssssssssssssssssssss");
                    }
                    @Override
                    public void onError(Exception e) {
                        System.out.println("rrrrrrrrrrrrrrrrrrrr");

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        ShowData();
//        System.out.println(name +"OOOOOOOOOOOO");
        System.out.println("++ ON START ++ ");
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowData();
//        System.out.println(name +"OOOOOOOOOOOO");
        System.out.println("+ ON RESUME +");


    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("-- ON STOP -- ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("- ON DESTROY - ");
    }


}
