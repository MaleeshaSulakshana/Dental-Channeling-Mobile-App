package com.example.dental_channelling.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.dental_channelling.LoginActivity;
import com.example.dental_channelling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private Button btnExitYes, btnExitNo, btnOpenDental, btnSignOut;
    private Dialog exitDialog;

    private LinearLayout viewDoctors, viewPatients, viewAppointments, viewProfile, viewAbout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

//        Get layouts
        exitDialog = new Dialog(AdminDashboardActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

//        btnOpenDental = (Button) findViewById(R.id.btnOpenDental);
        btnSignOut = (Button) findViewById(R.id.btnSignOut);

        viewDoctors = (LinearLayout) findViewById(R.id.viewAdminDoctors);
        viewPatients = (LinearLayout) findViewById(R.id.viewPatients);
        viewAppointments = (LinearLayout) findViewById(R.id.viewAdminAppointments);
        viewProfile = (LinearLayout) findViewById(R.id.viewAdminProfile);
        viewAbout = (LinearLayout) findViewById(R.id.viewAbout);

        firebaseAuth = FirebaseAuth.getInstance();

//        showStatus();

////        Set onclick commands to set availability
//        btnOpenDental.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setDentalIsOpen();
//            }
//        });

//        Set onclick commands to view doctors
        viewDoctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToViewDoctors();
            }
        });

//        Set onclick commands to view patients
        viewPatients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToViewPatients();
            }
        });

//        Set onclick commands to view appointments
        viewAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAppointments();
            }
        });

//        Set onclick commands to profile
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfile();
            }
        });

//        Set onclick commands to view about
        viewAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDentalAboutActivity();
            }
        });

//        Set onclick command for sign out
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
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

////    Method for show status
//    private void showStatus()
//    {
//        ref = FirebaseDatabase.getInstance().getReference("dentalDetails/");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if (snapshot != null) {
////                   Set value to text boxes
//                    if (snapshot.child("status").exists()){
//                        if (snapshot.child("status").toString().equals("open")) {
//                            btnOpenDental.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
//                            btnOpenDental.setText("CLOSE");
//                        } else {
//                            btnOpenDental.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
//                            btnOpenDental.setText("OPEN");
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });
//    }
//
////    Method for set dental open and close
//    private void setDentalIsOpen()
//    {
//        String availabilityText = btnOpenDental.getText().toString().toLowerCase();
//        if (availabilityText.equals("open")) {
//
//            btnOpenDental.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
//            btnOpenDental.setText("CLOSE");
//            updateStatus("open");
//
//        } else {
//
//            btnOpenDental.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
//            btnOpenDental.setText("OPEN");
//            updateStatus("close");
//        }
//
//    }
//
////    Method for update status
//    private void updateStatus(String status)
//    {
//        ref = FirebaseDatabase.getInstance().getReference("dentalDetails/");
//        ref.child("status").setValue(status);
//    }

//    Method for open admin profile view activity
    private void navigateToProfile()
    {
        Intent intent = new Intent(AdminDashboardActivity.this, AdminProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open admin view doctors activity
    private void navigateToViewDoctors()
    {
        Intent intent = new Intent(AdminDashboardActivity.this, AdminViewDoctorsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open admin view doctors activity
    private void navigateToViewPatients()
    {
        Intent intent = new Intent(AdminDashboardActivity.this, AdminViewPatientsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open admin view appointments activity
    private void navigateToAppointments()
    {
        Intent intent = new Intent(AdminDashboardActivity.this, AdminViewAppointmentsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open admin about activity
    private void navigateToDentalAboutActivity()
    {
        Intent intent = new Intent(AdminDashboardActivity.this, AdminDentalAboutActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open login activity
    private void navigateToLogin()
    {
        Intent login = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        startActivity(login);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for sign out
    private void signOut()
    {
        firebaseAuth.signOut();
        navigateToLogin();
    }

}