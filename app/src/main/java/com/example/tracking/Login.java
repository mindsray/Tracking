package com.example.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.security.MessageDigest;

public class Login extends AppCompatActivity {
    EditText edittext_email, edittext_password;
    TextView register;
    Button login;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseReff;
    private FirebaseDatabase db;
    private String email, pass = "";
    private FirebaseUser firebaseUser;
    String Showpass , Encryptpass;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edittext_email = findViewById(R.id.EditText_Email);
        edittext_password = findViewById(R.id.EditText_PassWord);
        register = findViewById(R.id.TextView_Register);
        login = findViewById(R.id.Button_Login);



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }

        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                email = edittext_email.getText().toString();
                pass = edittext_password.getText().toString();

                if (!email.isEmpty() && !pass.isEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(Login.this, Location.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(Login.this, "อีเมล์หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                } else {
                    Toast.makeText(Login.this, "กรุณากรอกอีเมล์ และ รหัสผ่านให้ครบถ้วน", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public void ShowPassword(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("User").child(firebaseUser.getUid());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Showpass= dataSnapshot.child("Password").getValue(String.class);
                // PasswordCurrent.setText(pass);//getnameไว้หน้าUI
                System.out.println(Showpass);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }
}





