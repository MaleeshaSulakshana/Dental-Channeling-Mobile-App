package com.example.dental_channelling.Activity.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.dental_channelling.Activity.Admin.AdminViewDoctorDetailsActivity;
import com.example.dental_channelling.Activity.Admin.AdminViewDoctorsActivity;
import com.example.dental_channelling.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PatientViewDoctorDetailsActivity extends AppCompatActivity {

    private Button btnChannel;
    private String doctorId;
    private TextInputLayout txtFieldName, txtFieldEmail,
            txtFieldCharges, txtFieldPhone, txtFieldDetails, txtFieldHours;
    private ImageView profileIcon;
    private ChipGroup chipGroup;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_doctor_details);

//        Get put extra value
        Intent patient = getIntent();
        doctorId = patient.getStringExtra("doctorID");

        txtFieldName = (TextInputLayout) this.findViewById(R.id.txtFieldName);
        txtFieldEmail = (TextInputLayout) this.findViewById(R.id.txtFieldEmail);
        txtFieldPhone = (TextInputLayout) this.findViewById(R.id.txtFieldPhone);
        txtFieldCharges = (TextInputLayout) this.findViewById(R.id.txtFieldCharges);
        txtFieldDetails = (TextInputLayout) this.findViewById(R.id.txtFieldDetails);
        txtFieldHours = (TextInputLayout) this.findViewById(R.id.txtFieldHours);

        chipGroup = (ChipGroup) findViewById(R.id.daysSelector);

        profileIcon = (ImageView) this.findViewById(R.id.profileIcon);

        btnChannel = (Button) this.findViewById(R.id.btnChannel);

        firebaseAuth = FirebaseAuth.getInstance();

//        Show patients details
        showDoctorDetails(doctorId);

//        Command for navigate channelling page
        btnChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToChannellingActivity();
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
        goBack();
    }

    private void goBack()
    {
        Intent intent = new Intent(PatientViewDoctorDetailsActivity.this, PatientViewDoctorsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open channelling activity
    private void navigateToChannellingActivity()
    {
        Intent intent = new Intent(PatientViewDoctorDetailsActivity.this, ChannellingActivity.class);
        intent.putExtra("doctorId", doctorId);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Show doctor details
    private void  showDoctorDetails(String doctorId)
    {
        ref = FirebaseDatabase.getInstance().getReference("users/"+doctorId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text boxes
                    txtFieldName.getEditText().setText(snapshot.child("name").getValue().toString());
                    txtFieldEmail.getEditText().setText(snapshot.child("email").getValue().toString());
                    txtFieldPhone.getEditText().setText(snapshot.child("phone").getValue().toString());
                    txtFieldCharges.getEditText().setText("Rs. "+snapshot.child("charges").getValue().toString());
                    txtFieldDetails.getEditText().setText(snapshot.child("details").getValue().toString());
                    txtFieldHours.getEditText().setText(snapshot.child("hours").getValue().toString());

                    String mon = snapshot.child("mon").getValue().toString();
                    String tue = snapshot.child("tue").getValue().toString();
                    String wen = snapshot.child("wen").getValue().toString();
                    String thu = snapshot.child("thu").getValue().toString();
                    String fri = snapshot.child("fri").getValue().toString();
                    String sat = snapshot.child("sat").getValue().toString();
                    String sun = snapshot.child("sun").getValue().toString();

                    for (int i=0; i<chipGroup.getChildCount();i++){
                        Chip chip = (Chip)chipGroup.getChildAt(i);

                        String chipValue = chip.getText().toString().toLowerCase();
                        if (chipValue.equals("mon")) {
                            if (mon.equals("yes")){
                                chip.setChecked(true);
                            }
                        } else if (chipValue.equals("tue")) {
                            if (tue.equals("yes")){
                                chip.setChecked(true);
                            }
                        } else if (chipValue.equals("wen")) {
                            if (wen.equals("yes")){
                                chip.setChecked(true);
                            }
                        } else if (chipValue.equals("thu")) {
                            if (thu.equals("yes")){
                                chip.setChecked(true);
                            }
                        } else if (chipValue.equals("fri")) {
                            if (fri.equals("yes")){
                                chip.setChecked(true);
                            }
                        } else if (chipValue.equals("sat")) {
                            if (sat.equals("yes")){
                                chip.setChecked(true);
                            }
                        } else if (chipValue.equals("sun")) {
                            if (sun.equals("yes")){
                                chip.setChecked(true);
                            }
                        }

                    }

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