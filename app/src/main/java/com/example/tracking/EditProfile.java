package com.example.tracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.tracking.R.id.toolbarrrr;

public class EditProfile extends AppCompatActivity {
    private static final String TAG = "" ;
    private FirebaseAuth mAuth;
    private TextView Name , LastName , Email ,PassWord , CFPassWord;
    CircleImageView imageviewww;
    private Button UpdateProfile;
    String name , lastName , email ,passWord , cfPassWord ,link;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    private DatabaseReference mDatabaseReff;
    private StorageReference mStorageRef;
    HashMap hashMap1 = new HashMap<>();
    HashMap hashMap = new HashMap<>();
    String uid;
    String UpdateName , Update_LastName , Update_Email ,Update_image;
    private DatabaseReference mDatabase;
    private DatabaseReference reference , referenceUpdate ,reference1;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private int requestCode;
    private int resultCode;
    String u;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(toolbarrrr);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // perform whatever you want on back arrow click
            }
        });
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_more_vert_black_18dp);
        toolbar.setOverflowIcon(drawable);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Name = findViewById(R.id.EditTextName);
        LastName = findViewById(R.id.EditText_LastName);
        Email = findViewById(R.id.EditText_Email);
        CFPassWord = findViewById(R.id.EditText_CFPassWord);
        UpdateProfile = findViewById(R.id.Button_Update);
        imageviewww = findViewById(R.id.imageView);
        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        System.out.println(uid +"MiniMinddddddddddddddddddddddddddddddd");

        imageviewww.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               chooseImage();
//                imageviewww.setImageResource(R.drawable.image_icon_proflie);
                return false;
            }
        });

        ShowUpdateUserFirebase();
//        ShowUpdateLocationUserFirebase();

        UpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateName = Name.getText().toString();
                Update_LastName = LastName.getText().toString();
                Update_Email = Email.getText().toString();
                Update_image = imageviewww.toString();
//                LoadImageUrl( Update_image);
                System.out.println(Update_image);
                if (UpdateName.isEmpty() && Update_LastName.isEmpty() && Update_Email.isEmpty()){
                    Toast.makeText(EditProfile.this, "กรุณากรอกข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                }
              else {

                    if (checkEmail()){
                        updateDataUser();
                        uploadImage11();
                        addEmailtoFirebaseAuth();
                        Toast.makeText(EditProfile.this, "อัพเดตข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        checkEmail();
                    }
//                  updateLocationData();

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
        this.requestCode = requestCode;
        this.resultCode = resultCode;

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            imageviewww.setImageURI(filePath);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageviewww.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(EditProfile.this, "Canceled", Toast.LENGTH_SHORT).show();
        }
    }



    private  void LoadImageUrl(String  Update_image){
        Picasso.get().load( Update_image).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageviewww, new com.squareup.picasso.Callback(){

                    @Override
                    public void onSuccess() {

                        System.out.println("LoadImageUrl Success");
                    }
                    @Override
                    public void onError(Exception e) {
                        System.out.println("LoadImageUrl Error");

                    }
                });
    }




    private void uploadImage11() {
        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            final StorageReference reference = mStorageRef.child("images/" + uid);
            reference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference imageStroe = FirebaseDatabase.getInstance().getReference().child("User").child(firebaseUser.getUid());
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


//                            System.out.println( downloadUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfile.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });


        }
    }

    private void updateDataUser(){
        reference1 = FirebaseDatabase.getInstance().getReference().child("User").child(uid); //ใช้Uid ที่firebase genขึ้นมา
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashMap1 = new HashMap<>();
                hashMap1.put("Name", UpdateName);
                System.out.println(UpdateName +"Name");
                hashMap1.put("Last", Update_LastName);
                System.out.println(Update_LastName+"Name");
                hashMap1.put("email", Update_Email);
                System.out.println(Update_Email+"Name");
                reference1.updateChildren(hashMap1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditProfile.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
    }
//    private void updateLocationData(){
//        reference = FirebaseDatabase.getInstance().getReference().child("Location").child(firebaseUser.getUid()); //ใช้Uid ที่firebase genขึ้นมา
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                hashMap = new HashMap<>();
//                hashMap.put("Name", UpdateName);
//                hashMap.put("email", Update_Email);
//                reference.updateChildren(hashMap);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private boolean checkEmail( ){
        if(Patterns.EMAIL_ADDRESS.matcher(Email.getText().toString()).matches() ){
            return true;
        }
        else {
            Toast.makeText(EditProfile.this,"รูปแบบอีเมล์ไม่ถูกต้อง",Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    private boolean checkE(){

        if(Patterns.EMAIL_ADDRESS.matcher(Email.getText().toString()).matches()) {
            return true;
        }
        else {

            Toast.makeText(this, "Email is INVALID.", Toast.LENGTH_SHORT).show();
            return false;
        }

    }


    private void ShowUpdateUserFirebase(){

        DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference uidRef = rootRef1.child("User").child(firebaseUser.getUid());
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("Name").getValue(String.class);
                Name.setText(name);//getnameไว้หน้าUI
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
    }


    public  void  addEmailtoFirebaseAuth() {

        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        user1.updateEmail(Email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                           System.out.println("UpdateEmail Succes in Firebase Authen");

                        }
                        else {
                            System.out.println("UpdateEmail Fail in Firebase Authen");
                        }
                    }
                });
}

    @Override
    public void onStart() {
        super.onStart();
//        ShowUpdateUserFirebase();

        System.out.println("++ ON START ++ ");
    }

    @Override
    public void onResume() {
        super.onResume();

//        ShowUpdateUserFirebase();

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



