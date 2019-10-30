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
import java.util.function.DoublePredicate;
public class Location extends AppCompatActivity {

    private double Latitude_current;
    private double Longitude_current;
    private TextView textView;
    private static final int REQUEST_LOCATION = 1;
    private DatabaseReference mDatabase;
    private LocationManager locationManager;
    public static final String TAG = "YOUR-TAG-NAME";
    private DatabaseReference reference;
    private LocationListener locationListener;
    private LocationRequest mLocationRequest;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    Button SendMessage;
    Button Logout;
    TextView textViewSuccess , EmailUser , Userid;
    private FirebaseUser firebaseUser;
    User user;
    Map<String, Object> hashMap;



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

         EmailUser.setText(firebaseUser.getEmail());

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

        locationManager = (LocationManager) Location.this.getSystemService(Context.LOCATION_SERVICE);
        System.out.println("+ ON CREATE +");
    }



    private void getLocation() {

        if (ActivityCompat.checkSelfPermission
                (Location.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            android.location.Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            android.location.Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);


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

            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                Latitude_current = latti;
                Longitude_current = longi;

                System.out.println("lo2 " + Latitude_current + " " + Longitude_current);


            } else {

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
      reference = FirebaseDatabase.getInstance().getReference("Location").child(firebaseUser.getUid()); //ใช้Uid ที่firebase genขึ้นมา
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String getemail =  firebaseUser.getEmail();
                hashMap = new HashMap<>();
                hashMap.put("latitude", Latitude_current);
                hashMap.put("longitude", Longitude_current);
                hashMap.put("Email",getemail);

//                reference.child("Location").setValue(hashMap);
                reference.updateChildren(hashMap);
                if (Latitude_current != 0 && Longitude_current != 0) {
                    textViewSuccess.setText("ส่งตำแหน่งเสร็จสิ้น");
                } else {
                    textViewSuccess.setText("ส่งตำแหน่งไม่สำเร็จ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




             //   reference.child("Location").push().setValue(hashMap);

//           hashMap.put("ID", count);


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
            } else {
                textViewSuccess.setText("ส่งตำแหน่งไม่สำเร็จ");
            }

            //  textView.setText(  Latitude_current +" "+ Longitude_current  );

        } else {
            textViewSuccess.setText("ไม่สามารถตำแหน่งปัจจุบันไม่ได้");
        }
        System.out.println("++ ON START ++ ");
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