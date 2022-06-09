package com.example.dental_channelling.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dental_channelling.Activity.Patient.PatientDashboardActivity;
import com.example.dental_channelling.Activity.Patient.PatientProfileActivity;
import com.example.dental_channelling.Activity.Patient.PatientPswChangeActivity;
import com.example.dental_channelling.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminProfileActivity extends AppCompatActivity {

    private Button btnChangePsw, btnCancel, btnUpdate;
    private TextInputLayout txtFieldName, txtFieldEmail;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        txtFieldName = (TextInputLayout) this.findViewById(R.id.txtFieldName);
        txtFieldEmail = (TextInputLayout) this.findViewById(R.id.txtFieldEmail);

        btnChangePsw = (Button) findViewById(R.id.btnChangePsw);
        btnUpdate = (Button) findViewById(R.id.btnProfileUpdate);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        firebaseAuth = FirebaseAuth.getInstance();

//        Show profile details
        showProfileDetails();

//        Set onclick commands to chang psw
        btnChangePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToChangePsw();
            }
        });

//        Set onclick commands to back to dashboard
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDashboard();
            }
        });

//        Set onclick commands to update profile
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileDetails();
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

    @Override
    public void onBackPressed() {
        navigateToDashboard();
    }

//    Method for open profile psw change activity
    private void navigateToChangePsw()
    {
        Intent intent = new Intent(AdminProfileActivity.this, AdminPswChangeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open profile dashboard activity
    private void navigateToDashboard()
    {
        Intent intent = new Intent(AdminProfileActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Get profile details
    private void  showProfileDetails()
    {

//       Get user id
        String userId = firebaseAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text boxes
                    txtFieldName.getEditText().setText(snapshot.child("name").getValue().toString());
                    txtFieldEmail.getEditText().setText(snapshot.child("email").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Method for update profile
    private void updateProfileDetails()
    {

        String name = txtFieldName.getEditText().getText().toString();

//        Check fields empty
        if (name.isEmpty())
        {
            Toast.makeText(AdminProfileActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

//        Update profile
        }  else {

//           Get user id
            String userId = firebaseAuth.getCurrentUser().getUid();
            ref = FirebaseDatabase.getInstance().getReference("users/"+userId);
            ref.child("name").setValue(name);

            Toast.makeText(AdminProfileActivity.this, "Profile details updated successful!", Toast.LENGTH_SHORT).show();
        }

    }

}