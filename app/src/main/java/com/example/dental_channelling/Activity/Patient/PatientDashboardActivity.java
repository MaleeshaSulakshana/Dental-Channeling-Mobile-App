package com.example.dental_channelling.Activity.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dental_channelling.Activity.Admin.AdminDashboardActivity;
import com.example.dental_channelling.LoginActivity;
import com.example.dental_channelling.MainActivity;
import com.example.dental_channelling.R;
import com.example.dental_channelling.RegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PatientDashboardActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private Button btnPatientsSignOut, btnExitYes, btnExitNo;
    private Dialog exitDialog;

    private TextView patientName;

    private LinearLayout viewDoctors, viewAppointments, viewProfile, viewAbout;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);

//        Get layouts
        exitDialog = new Dialog(PatientDashboardActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        viewDoctors = (LinearLayout) findViewById(R.id.viewDoctors);
        viewAppointments = (LinearLayout) findViewById(R.id.viewAppointments);
        viewProfile = (LinearLayout) findViewById(R.id.viewProfile);
        viewAbout = (LinearLayout) findViewById(R.id.viewAbout);

        patientName = (TextView) findViewById(R.id.patientName);

        btnPatientsSignOut = (Button) findViewById(R.id.btnPatientsSignOut);

        firebaseAuth = FirebaseAuth.getInstance();

//        Set patient name
        showPatientName();

//        Set onclick commands to view doctors
        viewDoctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToViewDoctors();
            }
        });

//        Set onclick commands to view appointments
        viewAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPatientAppointments();
            }
        });

//        Set onclick commands to profile
        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPatientProfile();
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
        btnPatientsSignOut.setOnClickListener(new View.OnClickListener() {
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

//    Method for open patient profile view activity
    private void navigateToPatientProfile()
    {
        Intent intent = new Intent(PatientDashboardActivity.this, PatientProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open patient view doctors activity
    private void navigateToViewDoctors()
    {
        Intent intent = new Intent(PatientDashboardActivity.this, PatientViewDoctorsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open patient view appointments activity
    private void navigateToPatientAppointments()
    {
        Intent intent = new Intent(PatientDashboardActivity.this, PatientAppointmentsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open patient about activity
    private void navigateToDentalAboutActivity()
    {
        Intent intent = new Intent(PatientDashboardActivity.this, DentalAboutActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open login activity
    private void navigateToLogin()
    {
        Intent login = new Intent(PatientDashboardActivity.this, LoginActivity.class);
        startActivity(login);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show patient name
    private void showPatientName()
    {
//       Get user id
        String userId = firebaseAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text view
                    patientName.setText("Hi "+ snapshot.child("name").getValue().toString());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Method for sign out
    private void signOut()
    {
        firebaseAuth.signOut();
        navigateToLogin();
    }

}