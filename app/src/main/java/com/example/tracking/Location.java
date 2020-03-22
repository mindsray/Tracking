package com.example.tracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.app.ProgressDialog;
import android.view.ContextMenu;
import android.view.Menu;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.DoublePredicate;
import java.util.jar.Attributes;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.cache.DiskLruCache;

import static com.example.tracking.R.id.toolbarrrr;

public class Location extends AppCompatActivity {
    private static final boolean USER_IS_GOING_TO_EXIT = true ;
    private double Latitude_current;
    private double Longitude_current;
    private TextView textView;
    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    public static final String TAG = "YOUR-TAG-NAME";
    private DatabaseReference reference , databaseReference;
    private FirebaseAuth firebaseAuth;
    Button SendMessage;
    Button Logout ,test;
    TextView textViewSuccess , EmailUser , Userid ,Name ;
    CircleImageView imageview;
    private FirebaseUser firebaseUser;
    User user;
    Map<String, Object> hashMap;
    String name ;
    String link ;
    private int SELECT_IMAGE = 1001;
    private int CROP_IMAGE = 2001;
    Toolbar toolbar;
    String uid;
    ProgressDialog pd;
    private long backPressedTime;
    private Toast backToast;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;



    //    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public static void setStatusBarGradiant(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window   window = activity.getWindow();
//            //Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.name, anotherTheme);
//            Drawable background = ContextCompat.getDrawable(activity, R.drawable.bg_gradient);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(ContextCompat.getColor(activity,android.R.color.transparent));
//            window.setNavigationBarColor(ContextCompat.getColor(activity,android.R.color.transparent));
//            window.setBackgroundDrawable(background);
//        }
//    }
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1; // 1 minute




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        pd = new ProgressDialog(Location.this);
        pd.setMessage("กำลังส่งตำแหน่ง");
        pd.show();
        SendMessage = findViewById(R.id.Button_SendMessage);
        textViewSuccess = findViewById(R.id.textview_Success);
        EmailUser = findViewById(R.id.textview_name);
        firebaseAuth = firebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        imageview = findViewById(R.id.imageViewProfile);
        Name = findViewById(R.id.textview_UserName);
        Toolbar toolbar = (Toolbar) findViewById(toolbarrrr);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.baseline_more_vert_black_18dp);
        toolbar.setOverflowIcon(drawable);

        EmailUser.setText(firebaseUser.getEmail());
        uid = firebaseUser.getUid();
        String uid = firebaseUser.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("User").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("Name").getValue(String.class);
                link = dataSnapshot.child("imageUrl").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);


        textViewSuccess.setText("กำลังส่งตำแหน่ง...");


        SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Location.this, Message.class));
            }
        });

        locationManager = (LocationManager) Location.this.getSystemService(Context.LOCATION_SERVICE);
        System.out.println("+ ON CREATE +");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option,menu);
//        System.out.println("menuuuu");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_Profile){
            Intent profileIntent = new Intent(Location.this , ShowProfile.class);
            startActivity(profileIntent);
        }
        if (id == R.id.action_ChangePassword){
            Intent ChangePWIntent = new Intent(Location.this , ChangePassword.class);
            startActivity(ChangePWIntent);
        }
        if (id == R.id.action_Logout){
            FirebaseAuth.getInstance().signOut();
            Logout();
        }
//        System.out.println("menuuuu2");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.option, menu);
//        System.out.println("menuuuu3");
    }

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }

        mBackPressed = System.currentTimeMillis();
    }



    private  void LoadImageUrl(String link){

    Picasso.get().load(link).placeholder(R.mipmap.ic_launcher)
    .error(R.mipmap.ic_launcher)
    .into(imageview , new com.squareup.picasso.Callback(){

        @Override
        public void onSuccess() {

            System.out.println("Upload Image Success");
        }
        @Override
        public void onError(Exception e) {
            System.out.println("Upload Image Error");

        }
    });
}
    private void Logout(){
        firebaseAuth.signOut();
        Intent intent = new Intent(Location.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission
                (Location.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(Location.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else
            {
            android.location.Location location =
                    locationManager.getLastKnownLocation
                            (LocationManager.NETWORK_PROVIDER);
            android.location.Location location1 =
                    locationManager.getLastKnownLocation
                            (LocationManager.GPS_PROVIDER);

            android.location.Location location2 =
                    locationManager.getLastKnownLocation
                            (LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                Latitude_current = latti;
                Longitude_current = longi;

                System.out.println("lo " + Latitude_current + " " + Longitude_current);

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                Latitude_current = latti;
                Longitude_current = longi;

                System.out.println("lo1 " + Latitude_current + " " + Longitude_current);
}
            else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                Latitude_current = latti;
                Longitude_current = longi;

                System.out.println("lo2 " + Latitude_current + " " + Longitude_current);

            }
                   else {

                System.out.println("Unble to Trace your location");

            }


        }


        //    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);

    }


    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("กรูณาเปิด GPS เพื่อเชื่อมต่อ")
                .setCancelable(false)
                .setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

        private void setLocationCurrent() {

        reference = FirebaseDatabase.getInstance().getReference("Location").child(firebaseUser.getUid());

//            reference.child("User").child("name").addValueEventListener();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                pd.dismiss();

                hashMap = new HashMap<>();
                String getemail =  firebaseUser.getEmail();


//                Register value = dataSnapshot.getValue(Register.class);
//                name = value.Name;
                hashMap.put("email",getemail);
                hashMap.put("Name",name);
                hashMap.put("imageUrl",link);

                if (Latitude_current >= 13.817000 && Latitude_current <= 13.830000){
                    if (Longitude_current >= 100.036000 && Longitude_current < 100.0430000)   {
                    hashMap.put("latitude", Latitude_current);
                    hashMap.put("longitude", Longitude_current);
                    hashMap.put("Status", "online");
                }
                }
                else
                    {
                        hashMap.put("Status", "Offline");
                }

                reference.updateChildren(hashMap);

                if (Latitude_current >= 13.817000 && Latitude_current <= 13.83000) {
                    if (Longitude_current >= 100.036000 && Longitude_current < 100.043000) {

                        textViewSuccess.setText("ส่งตำแหน่งเสร็จสิ้น");
                    }
                    else
                    {

                        textViewSuccess.setText("ไม่สามารถส่งตำแหน่งได้เนื่องจากคุณไม่อยู่ในขอบเขต");

                    }
                }
                else
                {

                    textViewSuccess.setText("ไม่สามารถส่งตำแหน่งได้เนื่องจากคุณไม่อยู่ในขอบเขต");

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Can't put data");
            }
        });
            }


    @Override
    public void onStart() {
    System.out.println(firebaseAuth.getAccessToken(true));
        super.onStart();
        pd.show();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("User").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("Name").getValue(String.class);
                Name.setText(name); //getnameไว้หน้าUI
                link = dataSnapshot.child("imageUrl").getValue(String.class);
                System.out.println(link);
                LoadImageUrl(link);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getLocation();
            if (Latitude_current != 0 && Longitude_current != 0) {

            }
        }
        System.out.println("++ ON START ++ ");
        textViewSuccess.setText("กำลังส่งตำแหน่ง...");
    }

    @Override
    public void onResume() {
        super.onResume();
        pd.show();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("User").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("Name").getValue(String.class);
                Name.setText(name); //getnameไว้หน้าUI
                link = dataSnapshot.child("imageUrl").getValue(String.class);
                System.out.println(link);
                LoadImageUrl(link);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(5000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getLocation();
                                setLocationCurrent();

//                                textView.setText(Latitude_current + " " + Longitude_current);

                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }


                }

                textViewSuccess.setText("กำลังส่งตำแหน่ง...");
            }
        };
        t.start();

//               new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//
//                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    getLocation();
//                    setLocationCurrent();
//                } else {
//                    System.out.println("Can't get Location");
//
//                }
//            }
//        }, 0, 5000);//put here time 1000 milliseconds=1 second


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

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    getLocation();
//                    setLocationCurrent();
//                }
//                else
//                {
//
//                    textView.setText("Can't get Location");
//
//
//                }
//
//            }
//        }, 5, 5000);//put here time 1000 milliseconds=1 second
        System.out.println("-- ON STOP -- ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        System.out.println("- ON DESTROY - ");
    }


}