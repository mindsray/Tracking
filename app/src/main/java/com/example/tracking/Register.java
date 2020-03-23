package com.example.tracking;

import
        androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;
import static com.example.tracking.R.id.toolbarrrr;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailUser, passwordUser, NameUser, LastNameUser, CFPasswordUser;
    private Button reg;
    String Name , link;
    public Uri imageUrl;
    UUID id;
    String lastname;
    String email;
    String pass;
    String cfpass ="";
    Task<Uri> downloadUrl;
    String newPassEncry;
    User user = new User();
    private FirebaseDatabase db;
    private DatabaseReference mDatabaseReff , getmDatabaseReff;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private String image_user = "";

    private int SELECT_IMAGE = 1001;
    private int CROP_IMAGE = 2001;
    private StorageReference mStorageRef;
    private StorageManager storageManager;
    private Storage storage;
    private static final int ImageBack=1;

    private FirebaseUser firebaseUser;
    public String U_id;
    HashMap<String,String> hashMap = new HashMap<>();
    CircleImageView imageview;

    public Register() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        id = UUID.randomUUID();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


            NameUser = findViewById(R.id.EditTextName);
            LastNameUser = findViewById(R.id.EditText_LastName);
            emailUser = findViewById(R.id.EditText_Email);
            passwordUser = findViewById(R.id.EditText_PassWord);
            CFPasswordUser = findViewById(R.id.EditText_CFPassWord);
            reg = findViewById(R.id.Button_Register);
            imageview = findViewById(R.id.imageview);

            imageview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                     chooseImage();
                    return false;
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


                if( Name.isEmpty() || lastname.isEmpty() || email.isEmpty() || pass.isEmpty()
                        || cfpass.isEmpty()
                ){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);
                    dialog.setTitle("ลงทะเบียนไม่สำเร็จ");
                    dialog.setMessage("กรุณากรอกข้อมูลให้ครบ");
                    dialog.setPositiveButton("ยืนยัน",null);
                    dialog.show();
                }
                else{
                    if(checkPassword() && checkEmail()){
                        newPassEncry =  EncryptPassword(pass);
                        System.out.println(newPassEncry);
                        registerUserToFirebase();
                        uploadImage11();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Register.this);
                        dialog.setTitle("ลงทะเบียนสำเร็จ");

                    }
                    else {
                        checkEmail();
                        checkPassword();
                    }

                }
              //  startActivity(new Intent(Register.this, Login.class));
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
            imageview.setImageURI(filePath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageview.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(Register.this, "Canceled", Toast.LENGTH_SHORT).show();
        }
    }

private String getExtention(Uri uri){

         ContentResolver cr = getContentResolver();
         MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
         return mimeTypeMap.getMimeTypeFromExtension(cr.getType(uri));

}




//private  void  getimage (){
//    mStorageRef.child("/Images/images").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//        @Override
//        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//            Uri downloadUri = taskSnapshot.getStorage().getDownloadUrl();
//            generatedFilePath = downloadUri.toString();
//        }
//    });
//}


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
                         final String  userid = firebaseUser.getUid();
                            mDatabaseReff = FirebaseDatabase.getInstance().getReference("User").child(userid);
//                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("Name",Name);
                            hashMap.put("Last",lastname);
                            hashMap.put("email",email);
                            hashMap.put("Password",newPassEncry);
//                          user = new User();
                            U_id = userid;
                            System.out.println(U_id);
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

    private void uploadImage11() {
        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            final StorageReference reference = mStorageRef.child("images/" + U_id);
            reference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference imageStroe = FirebaseDatabase.getInstance().getReference().child("User").child(U_id);
                                    hashMap.put("imageUrl", String.valueOf(uri));
                                    System.out.println(uri);
                                    imageStroe.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            System.out.println("Completeddd");
                                        }

                                    });

                                }
                            });

                            Toast.makeText(Register.this, "ลงทะเบียนเสร็จสิ้น", Toast.LENGTH_SHORT).show();
//                            System.out.println( downloadUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, "ลงทะเบียนไม่สำเร็จ " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("กำลังอัพโหลด " + (int) progress + "%");
                        }
                    });


        }
    }
}






















