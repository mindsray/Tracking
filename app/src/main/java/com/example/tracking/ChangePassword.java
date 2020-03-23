package com.example.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.*;
import com.google.android.material.snackbar.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.CDATASection;

import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import static com.example.tracking.R.id.toolbarrrr;



public class ChangePassword extends AppCompatActivity {
    Button ChangePassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    String pass , passCur , newpass,cfpass;
    String newPassword;
    String uid;
    EditText PasswordCurrent , NewPassword , CFPassword;
    private DatabaseReference reference;
    HashMap hashMap = new HashMap<>();
    String passCurrentEncrypt , NewPassEncrypt , CFPasswordEncrypt ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = findViewById(toolbarrrr);

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

        ChangePassword = findViewById(R.id.Button_ChangePassword);
        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        PasswordCurrent = findViewById(R.id.EditText_CurrentPassword);
        NewPassword = findViewById(R.id.EditText_NewPassword);
        CFPassword = findViewById(R.id.EditText_CFNewPassword);
        uid = firebaseUser.getUid();

         ShowPassword();

         ChangePassword.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 passCur = PasswordCurrent.getText().toString();
                 newpass = NewPassword.getText().toString();
                 cfpass = CFPassword.getText().toString();
//                 System.out.println(pass +"1111111");
                 passCurrentEncrypt =  EncryptPassword(passCur);
//                 System.out.println(passCurrentEncrypt +"222222");
//                buildAlertCFPassword();
                 NewPassEncrypt =  EncryptPassword(newpass);
//                 System.out.println(NewPassEncrypt +"44444444");

                 CFPasswordEncrypt = EncryptPassword(cfpass);
//                 System.out.println(CFPasswordEncrypt + " 55555555555");

                 if (passCurrentEncrypt.equals(pass)){

                     if (NewPassEncrypt.equals(CFPasswordEncrypt)){
                         updateData();
                         updatePasswordFirebaseAuth();
                         Toast.makeText(ChangePassword.this, "เปลี่ยนรหัสผ่านสำเร็จ", Toast.LENGTH_SHORT).show();
                     }

                     else {
                         Toast.makeText(ChangePassword.this, "กรุณากรอกรหัสผ่านใหม่ให้ตรงกัน", Toast.LENGTH_SHORT).show();
                     }


                 }

                 else {

                     Toast.makeText(ChangePassword.this, "กรุณากรอกรหัสผ่านปัจจุบันให้ถูกต้อง", Toast.LENGTH_SHORT).show();
                 }


             }
         });


    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        System.out.println("menuuuu");
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.option,menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_Profile){
//            Intent profileIntent = new Intent(ChangePassword.this , ShowProfile.class);
//            startActivity(profileIntent);
//        }
//        if (id == R.id.action_ChangePassword){
//            Intent ChangePWIntent = new Intent(ChangePassword.this , ChangePassword.class);
//            startActivity(ChangePWIntent);
//        }
//        if (id == R.id.action_Logout){
//            Logout();
//
//        }
////        System.out.println("menuuuu2");
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        getMenuInflater().inflate(R.menu.option, menu);
////        System.out.println("menuuuu3");
//    }


    protected void buildAlertCFPassword() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ยืนยันเปลี่ยนรหัสผ่านใหม่หรือไม่")
                .setCancelable(false)
                .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Toast.makeText(ChangePassword.this, "เปลี่ยนรหัสผ่านสำเร็จ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }
    private void Logout(){
        firebaseAuth.signOut();
        Intent intent = new Intent(ChangePassword.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void ShowPassword(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("User").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               pass= dataSnapshot.child("Password").getValue(String.class);
              // PasswordCurrent.setText(pass);//getnameไว้หน้าUI
                System.out.println(pass);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
    }



    public static String EncryptPassword (String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }


    private void updateData(){
        reference = FirebaseDatabase.getInstance().getReference().child("User").child(firebaseUser.getUid()); //ใช้Uid ที่firebase genขึ้นมา
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashMap = new HashMap<>();
                hashMap.put("Password", NewPassEncrypt);
                System.out.println();
                reference.updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public  void updatePasswordFirebaseAuth(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(NewPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("UpdateEPassword Success in Firebase Authen");
                        }
                        else {
                            System.out.println("UpdateEPassword Fail in Firebase Authen");
                        }
                    }
                });
    }

            private void UpdatePassWord() {
                final FirebaseUser user1;
                user1 = FirebaseAuth.getInstance().getCurrentUser();
                String email = user1.getEmail();
                AuthCredential credential = EmailAuthProvider.getCredential(email, pass);
                user1.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user1.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(ChangePassword.this, "มีบางอย่างผิดปกติ กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();

                                    } else {
//                                        updateData();
                                        Toast.makeText(ChangePassword.this, "เปลี่ยนรหัสผ่านสำเร็จ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {

                            Toast.makeText(ChangePassword.this, "ไม่สามารถเปลี่ยนรหัสผ่านได้", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
}



