package com.example.dental_channelling.Activity.Doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.dental_channelling.Activity.Admin.AdminViewDoctorDetailsActivity;
import com.example.dental_channelling.Activity.Admin.AdminViewDoctorsActivity;
import com.example.dental_channelling.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DoctorViewPatientProfileActivity extends AppCompatActivity {

    private TextInputLayout txtFieldName, txtFieldEmail, txtFieldPhone, txtFieldAddress;
    private String patientId, appointmentId;
    private ImageView profileIcon;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_view_patient_profile);

        //        Get put extra value
        Intent patient = getIntent();
        patientId = patient.getStringExtra("patientID");
        appointmentId = patient.getStringExtra("appointmentId");

        txtFieldName = (TextInputLayout) this.findViewById(R.id.txtFieldName);
        txtFieldEmail = (TextInputLayout) this.findViewById(R.id.txtFieldEmail);
        txtFieldPhone = (TextInputLayout) this.findViewById(R.id.txtFieldPhone);
        txtFieldAddress = (TextInputLayout) this.findViewById(R.id.txtFieldAddress);

        profileIcon = (ImageView) this.findViewById(R.id.profileIcon);

        firebaseAuth = FirebaseAuth.getInstance();

//        Show patient profile details
        showPatientProfileDetails(patientId);

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
        goBack();
    }

    private void goBack()
    {
        Intent intent = new Intent(DoctorViewPatientProfileActivity.this, DoctorViewAppointmentsDetailsActivity.class);
        intent.putExtra("appointmentID", appointmentId);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Get patient profile details
    private void  showPatientProfileDetails(String userId)
    {

        ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text boxes
                    txtFieldName.getEditText().setText(snapshot.child("name").getValue().toString());
                    txtFieldEmail.getEditText().setText(snapshot.child("email").getValue().toString());
                    txtFieldPhone.getEditText().setText(snapshot.child("phone").getValue().toString());
                    txtFieldAddress.getEditText().setText(snapshot.child("address").getValue().toString());

                    if (snapshot.child("profileImage").exists()) {
                        if (!snapshot.child("profileImage").getValue().equals("")){
                            Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(profileIcon);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

}