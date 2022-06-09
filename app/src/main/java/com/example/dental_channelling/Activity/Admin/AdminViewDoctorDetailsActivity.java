package com.example.dental_channelling.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dental_channelling.Activity.Patient.PatientProfileActivity;
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

import java.util.List;

public class AdminViewDoctorDetailsActivity extends AppCompatActivity {

    private Button btnUpdate, btnCancel;
    private String doctorId;
    private TextInputLayout txtFieldName, txtFieldEmail,
            txtFieldPhone, txtFieldDetails, txtFieldHours, txtFieldCharges;
    private ImageView profileIcon;
    private ChipGroup chipGroup;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_doctor_details);

//        Get put extra value
        Intent doctor = getIntent();
        doctorId = doctor.getStringExtra("doctorID");

        txtFieldName = (TextInputLayout) this.findViewById(R.id.txtFieldName);
        txtFieldEmail = (TextInputLayout) this.findViewById(R.id.txtFieldEmail);
        txtFieldPhone = (TextInputLayout) this.findViewById(R.id.txtFieldPhone);
        txtFieldDetails = (TextInputLayout) this.findViewById(R.id.txtFieldDetails);
        txtFieldCharges = (TextInputLayout) this.findViewById(R.id.txtFieldCharges);
        txtFieldHours = (TextInputLayout) this.findViewById(R.id.txtFieldHours);

        chipGroup = (ChipGroup) findViewById(R.id.daysSelector);
        profileIcon = (ImageView) this.findViewById(R.id.profileIcon);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        firebaseAuth = FirebaseAuth.getInstance();

//        Show patients details
        showDoctorDetails(doctorId);

//        Set onclick commands to update profile
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDoctorProfileDetails(doctorId);
            }
        });

//        Set onclick commands to go back
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
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
        Intent intent = new Intent(AdminViewDoctorDetailsActivity.this, AdminViewDoctorsActivity.class);
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
                    txtFieldDetails.getEditText().setText(snapshot.child("details").getValue().toString());
                    txtFieldCharges.getEditText().setText(snapshot.child("charges").getValue().toString());
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

//    Update doctor profile details
    private void updateDoctorProfileDetails(String doctorId)
    {

        String name = txtFieldName.getEditText().getText().toString();
        String phone = txtFieldPhone.getEditText().getText().toString();
        String details = txtFieldDetails.getEditText().getText().toString();
        String charges = txtFieldCharges.getEditText().getText().toString();
        String hours = txtFieldHours.getEditText().getText().toString();

        String mon = "no";
        String tue = "no";
        String wen = "no";
        String thu = "no";
        String fri = "no";
        String sat = "no";
        String sun = "no";

        List<Integer> ids = chipGroup.getCheckedChipIds();
        for (Integer id:ids){
            Chip chip = chipGroup.findViewById(id);

            if (chip.getText().toString().equals("Mon")) {
                mon = "yes";
            }

            if (chip.getText().toString().equals("Tue")) {
                tue = "yes";
            }

            if (chip.getText().toString().equals("Wen")) {
                wen = "yes";
            }

            if (chip.getText().toString().equals("Thu")) {
                thu = "yes";
            }

            if (chip.getText().toString().equals("Fri")) {
                fri = "yes";
            }

            if (chip.getText().toString().equals("Sat")) {
                sat = "yes";
            }

            if (chip.getText().toString().equals("Sun")) {
                sun = "yes";
            }

        }

//        Check fields empty
        if (name.isEmpty() || phone.isEmpty() || details.isEmpty() || charges.isEmpty())
        {
            Toast.makeText(AdminViewDoctorDetailsActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

//        Check length of mobile number
        } else if (mon.equals("no") || tue.equals("no") || wen.equals("no") || thu.equals("no") ||
                fri.equals("no") || sat.equals("no") || sun.equals("no")) {

            if (phone.length() != 10) {
                Toast.makeText(AdminViewDoctorDetailsActivity.this, "Please check your mobile number!", Toast.LENGTH_SHORT).show();

//        Check psw and confirm psw is equals
            } else {

                ref = FirebaseDatabase.getInstance().getReference("users/"+doctorId);

                ref.child("name").setValue(name);
                ref.child("phone").setValue(phone);
                ref.child("details").setValue(details);
                ref.child("charges").setValue(charges);
                ref.child("hours").setValue(hours);

                ref.child("mon").setValue(mon);
                ref.child("tue").setValue(tue);
                ref.child("wen").setValue(wen);
                ref.child("thu").setValue(thu);
                ref.child("fri").setValue(fri);
                ref.child("sat").setValue(sat);
                ref.child("sun").setValue(sun);

                Toast.makeText(AdminViewDoctorDetailsActivity.this, "Doctor profile details updated successful!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(AdminViewDoctorDetailsActivity.this, "Please select days!", Toast.LENGTH_SHORT).show();

        }

    }

}