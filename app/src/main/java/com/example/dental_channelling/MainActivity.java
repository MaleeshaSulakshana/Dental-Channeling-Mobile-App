package com.example.dental_channelling;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dental_channelling.Activity.Admin.AdminDashboardActivity;
import com.example.dental_channelling.Activity.Doctor.DoctorDashboardActivity;
import com.example.dental_channelling.Activity.Patient.PatientDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    Button btnStart;
    boolean doubleBackToExitPressedOnce = false;
    private Button btnExitYes, btnExitNo;
    private Dialog exitDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    private ProgressDialog loadingBar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Get layouts
        exitDialog = new Dialog(MainActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnStart = (Button) this.findViewById(R.id.btnStart);

        firebaseAuth = FirebaseAuth.getInstance();

////        Get User permissions
//        checkSelfPermission(Manifest.permission.CALL_PHONE);
//        if (checkSelfPermission(Manifest.permission.CALL_PHONE)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},
//                    1);
//            return;
//        }

//        Set onclick commands to btn
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//               Start progress bar
                loadingBar = new ProgressDialog(MainActivity.this);
                loadingBar.setTitle("Checking your account");
                loadingBar.setMessage("Please Wait");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

//                    Check is connect network
                if (isNetworkConnected() == false) {
                    Toast.makeText(MainActivity.this, "Please turn on wifi or mobile data!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

//                    Check internet connection
                } else if (isInternetAvailable() == false){
                    Toast.makeText(MainActivity.this, "Please  check your internet connection!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

//                    Check is logged in or not
                } else {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

//                                Check which user logged in
                                String userId = firebaseAuth.getCurrentUser().getUid();
                                ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

//                                        Check account type and redirect to dashboard page
                                        String accountType = snapshot.child("accountType").getValue().toString();
                                        Integer type = Integer.parseInt(accountType);
                                        loadingBar.dismiss();

                                        if (type == 1) { navigateToAdminDashboard(); }
                                        else if (type == 2) { navigateToDoctorDashboard(); }
                                        else if (type == 3) { navigateToPatientDashboard(); }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        loadingBar.dismiss();

                                        firebaseAuth.signOut();
                                        Toast.makeText(MainActivity.this, "Some error occur! Try again", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        },5);

                    }
                    else {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadingBar.dismiss();
                                navigateToLogin();
                            }
                        },5);

                    }

                }

            }
        });

    }

//    Hide status bar and navigation bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

//    Tap to close app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        exitDialog.show();

        btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }

//    Method for open login activity
    private void navigateToLogin()
    {
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open patient dashboard activity
    private void navigateToPatientDashboard()
    {
        Intent patient = new Intent(MainActivity.this, PatientDashboardActivity.class);
        startActivity(patient);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open doctor dashboard activity
    private void navigateToDoctorDashboard()
    {
        Intent doctor = new Intent(MainActivity.this, DoctorDashboardActivity.class);
        startActivity(doctor);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open admin dashboard activity
    private void navigateToAdminDashboard()
    {
        Intent admin = new Intent(MainActivity.this, AdminDashboardActivity.class);
        startActivity(admin);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Check network connection
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

//    Check internet connection
    private boolean isInternetAvailable() {
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                InetAddress ipAddr = InetAddress.getByName("www.google.com");
                return !ipAddr.equals("");
            }
            else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

}