package com.example.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailUser, passwordUser, NameUser, LastNameUser, CFPasswordUser;
    private Button reg;
    String Name,lastname ,email,pass,cfpass ="";
    User user = new User();
    private FirebaseDatabase db;
    private DatabaseReference mDatabaseReff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

            NameUser = findViewById(R.id.EditTextName);
            LastNameUser = findViewById(R.id.EditText_LastName);
            emailUser = findViewById(R.id.EditText_Email);
            passwordUser = findViewById(R.id.EditText_PassWord);
            CFPasswordUser = findViewById(R.id.EditText_CFPassWord);
            reg = findViewById(R.id.Button_Register);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name = NameUser.getText().toString();
                lastname = LastNameUser.getText().toString();
                email = emailUser.getText().toString();
                pass= passwordUser.getText().toString();
                cfpass = CFPasswordUser.getText().toString();


                if( Name.isEmpty() || lastname.isEmpty() || email.isEmpty() || pass.isEmpty() || cfpass.isEmpty()  ){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);
                    dialog.setTitle("ลงทะเบียนไม่สำเร็จ");
                    dialog.setMessage("กรุณากรอกข้อมูลให้ครบ");
                    dialog.setPositiveButton("ยืนยัน",null);
                    dialog.show();
                }

                else{
                    if(checkPassword() && checkEmail()){
                        registerUserToFirebase();
                    }
                    else {
                        checkEmail();
                        checkPassword();
                    }

                }
            }
        });
    }

    private void getValue(){
        user.setName(NameUser.getText().toString().trim());
        user.setLastname(LastNameUser.getText().toString().trim());
        user.setEmail(emailUser.getText().toString().trim());
        user.setPass(passwordUser.getText().toString().trim());
    }

    private void registerUserToFirebase(){

        email= emailUser.getText().toString().trim();
        pass = passwordUser.getText().toString().trim();

        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //เรียกดาต้าเบสมาใช้ vvv
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert  firebaseUser != null;
                            final String userid = firebaseUser.getUid();

                            db = FirebaseDatabase.getInstance();
                            mDatabaseReff = db.getReference("User");
                            user = new User();
                            mDatabaseReff.addValueEventListener(new ValueEventListener(){
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    getValue();
                                    mDatabaseReff.child(userid).setValue(user);
                                    Toast.makeText(Register.this,"ลงทะเบียนสำเร็จ",Toast.LENGTH_SHORT).show();
                                    finish(); //ปิดหน้าปัจจุบัน
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });

    }

    private boolean checkPassword(){
        if(pass.length() >= 8 && pass.equals(cfpass)){
            return true;
        }
        else{
            Toast.makeText(Register.this,"รหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean checkEmail(){
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches() ){
            return true;
        }
        else {
            Toast.makeText(Register.this,"รูปแบบอีเมล์ไม่ถูกต้อง",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}


























