package com.example.tracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.DoublePredicate;
import java.util.jar.Attributes;

import okhttp3.internal.cache.DiskLruCache;

public class Location extends AppCompatActivity {

    private double Latitude_current;
    private double Longitude_current;
    private TextView textView;
    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;
    public static final String TAG = "YOUR-TAG-NAME";
    private DatabaseReference reference , databaseReference;
    private FirebaseAuth firebaseAuth;
    Button SendMessage;
    Button Logout;
    TextView textViewSuccess , EmailUser , Userid ,NameUser ;
    private FirebaseUser firebaseUser;
    User user;
    Map<String, Object> hashMap;
    String name;
    private int SELECT_IMAGE = 1001;
    private int CROP_IMAGE = 2001;



    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1; // 1 minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        textView = findViewById(R.id.textview_location);
        SendMessage = findViewById(R.id.Button_SendMessage);
        Logout = findViewById(R.id.Button_LogOut);
        textViewSuccess = findViewById(R.id.textview_Success);
         EmailUser = findViewById(R.id.textview_name);
         firebaseAuth = firebaseAuth.getInstance();
         firebaseUser = firebaseAuth.getCurrentUser();

         Userid = findViewById(R.id.textview_Uid);
         NameUser = findViewById(R.id.textview_name);

         EmailUser.setText(firebaseUser.getEmail());
         textViewSuccess.setText("กำลังส่งตำแหน่ง...");

//          Userid.setText( firebaseUser.getUid());

        SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Location.this, Message.class));
            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Location.this, Login.class));
            }
        });



//
//        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();
//        mdatabase.child("User").child(firebaseUser.getUid()).child("Name").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Register value = dataSnapshot.getValue(Register.class);
//                name = value.Name;
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


//        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference mUsersRef = mRootRef.child("Users").child(firebaseUser.getUid()).child("Name");
//        databaseReference= FirebaseDatabase.getInstance().getReference().child("User").child(firebaseUser.getUid()).child("Name");
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Register value = snapshot.getValue(Register.class);
//                     name = value.Name;
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        System.out.println(name);


        locationManager = (LocationManager) Location.this.getSystemService(Context.LOCATION_SERVICE);
        System.out.println("+ ON CREATE +");
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
                Toast.makeText(Location.this, "Unble to Trace your location", Toast.LENGTH_SHORT).show();

            }


        }


        //    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);

    }


    protected void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }



        private void setLocationCurrent() {


            String uid = firebaseUser.getUid();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference uidRef = rootRef.child("User").child(uid);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                     name = dataSnapshot.child("Name").getValue(String.class);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            uidRef.addListenerForSingleValueEvent(valueEventListener);



        reference = FirebaseDatabase.getInstance().getReference("Location").child(firebaseUser.getUid());

//            reference.child("User").child("name").addValueEventListener();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                hashMap = new HashMap<>();
                String getemail =  firebaseUser.getEmail();


//                Register value = dataSnapshot.getValue(Register.class);
//                name = value.Name;
                hashMap.put("Email",getemail);
                hashMap.put("Name",name);

                if (Latitude_current >= 13.817000 || Latitude_current <= 13.818888){
                    if (Longitude_current >= 100.036000 || Longitude_current < 100.045000)   {
                    hashMap.put("latitude", Latitude_current);
                    hashMap.put("longitude", Longitude_current);
                    hashMap.put("Status", "Online");
                }
                }
                else
                    {
                        hashMap.put("Status", "Offline");
                }

                reference.updateChildren(hashMap);

                if (Latitude_current >= 13.817000 && Latitude_current <= 13.818888) {
                    if (Longitude_current >= 100.036000 && Longitude_current < 100.045000) {
                        textViewSuccess.setText("ส่งตำแหน่งเสร็จสิ้น");
                    }
                }
                else
                {
                    textViewSuccess.setText("ไม่สามารถส่งตำแหน่งได้เนื่องจากคุณไม่อยู่ในขอบเขต");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Can't put data ");
            }
        });
            }


    @Override
    public void onStart() {

        super.onStart();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getLocation();
            if (Latitude_current != 0 && Longitude_current != 0) {
                setLocationCurrent();
            }
        }
        System.out.println("++ ON START ++ ");
        textViewSuccess.setText("กำลังส่งตำแหน่ง...");
    }

    @Override
    public void onResume() {
        super.onResume();
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

                                textView.setText(Latitude_current + " " + Longitude_current);

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