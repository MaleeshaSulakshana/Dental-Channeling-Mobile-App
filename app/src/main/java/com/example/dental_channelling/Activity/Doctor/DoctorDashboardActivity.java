package com.example.dental_channelling.Activity.Doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dental_channelling.LoginActivity;
import com.example.dental_channelling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DoctorDashboardActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private Button btnExitYes, btnExitNo, btnSignOut;
    private Dialog exitDialog;
    private TextView doctorName;

    private LinearLayout appointments,profile;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_dashboard);

//        Get layouts
        exitDialog = new Dialog(DoctorDashboardActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        btnSignOut = (Button) findViewById(R.id.btnSignOut);

        doctorName = (TextView) findViewById(R.id.doctorName);

        appointments = (LinearLayout) findViewById(R.id.appointments);
        profile = (LinearLayout) findViewById(R.id.profile);

        firebaseAuth = FirebaseAuth.getInstance();

//        Set doctor name
        showDoctorName();

//        Set onclick commands to view appointments
        appointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAppointments();
            }
        });

//        Set onclick commands to view profile
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDoctorProfile();
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

//    Method for open doctor profile view activity
    private void navigateToAppointments()
    {
        Intent intent = new Intent(DoctorDashboardActivity.this, DoctorViewAppointmentsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open doctor profile view activity
    private void navigateToDoctorProfile()
    {
        Intent intent = new Intent(DoctorDashboardActivity.this, DoctorProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open login activity
    private void navigateToLogin()
    {
        Intent login = new Intent(DoctorDashboardActivity.this, LoginActivity.class);
        startActivity(login);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show doctor name
    private void showDoctorName()
    {
//       Get user id
        String userId = firebaseAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text view
                    doctorName.setText("Hi "+ snapshot.child("name").getValue().toString());

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