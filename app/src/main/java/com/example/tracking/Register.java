package com.example.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailUser, passwordUser, NameUser, LastNameUser, CFPasswordUser;
    private Button reg;
    private ImageView imageView;
    String Name,lastname ,email,pass,cfpass ="";
    User user = new User();
    private FirebaseDatabase db;
    private DatabaseReference mDatabaseReff;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private String image_user = "";
    CircleImageView CircleImageViewProfile;


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
            imageView =  findViewById(R.id.imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseImage();

                }
            });


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
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
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

                            mDatabaseReff = FirebaseDatabase.getInstance().getReference("User").child(userid);
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("Name",Name);
                            hashMap.put("Last",lastname);
                            hashMap.put("email",email);
//                            user = new User();
                           mDatabaseReff.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){
                                       Intent intent = new Intent(Register.this,Login.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                       startActivity(intent);
                                       finish();

                                   }

                               }
                           });

                        } else {
                            Toast.makeText(Register.this ,"ลงทะเบียนสำเร็จ" ,Toast.LENGTH_SHORT).show();
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


























