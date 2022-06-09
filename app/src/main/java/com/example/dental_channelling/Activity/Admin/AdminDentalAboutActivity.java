package com.example.dental_channelling.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dental_channelling.Activity.Patient.PatientProfileActivity;
import com.example.dental_channelling.R;
import com.example.dental_channelling.RegistrationActivity;
import com.example.dental_channelling.Utiles.EmailValidator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDentalAboutActivity extends AppCompatActivity {

    private Button btnUpdate, btnCancel;
    private TextInputLayout txtFieldName, txtFieldEmail, txtFieldPhone, txtFieldAddress, txtFieldOpeningDays, txtFieldOpeningHours;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dental_about);

        txtFieldName = (TextInputLayout) this.findViewById(R.id.txtFieldName);
        txtFieldEmail = (TextInputLayout) this.findViewById(R.id.txtFieldEmail);
        txtFieldPhone = (TextInputLayout) this.findViewById(R.id.txtFieldPhone);
        txtFieldAddress = (TextInputLayout) this.findViewById(R.id.txtFieldAddress);
        txtFieldOpeningDays = (TextInputLayout) this.findViewById(R.id.txtFieldOpeningDays);
        txtFieldOpeningHours = (TextInputLayout) this.findViewById(R.id.txtFieldOpeningHours);

//        Button click for navigate to view doctor activity
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        firebaseAuth = FirebaseAuth.getInstance();

//        Show dental details
        showDetails();

//        Set onclick commands to view doctors
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDashboardActivity();
            }
        });

//        Set onclick command to update details
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails();
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
        navigateToDashboardActivity();
    }

//    Method for open dashboard activity
    private void navigateToDashboardActivity()
    {
        Intent intent = new Intent(AdminDentalAboutActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

//    Get for show details
    private void  showDetails()
    {

        ref = FirebaseDatabase.getInstance().getReference("dentalDetails");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text boxes
                    txtFieldName.getEditText().setText(snapshot.child("name").getValue().toString());
                    txtFieldEmail.getEditText().setText(snapshot.child("email").getValue().toString());
                    txtFieldPhone.getEditText().setText(snapshot.child("phone").getValue().toString());
                    txtFieldAddress.getEditText().setText(snapshot.child("address").getValue().toString());
                    txtFieldOpeningDays.getEditText().setText(snapshot.child("days").getValue().toString());
                    txtFieldOpeningHours.getEditText().setText(snapshot.child("hours").getValue().toString());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Method for update details
    private void updateDetails()
    {
        String name = txtFieldName.getEditText().getText().toString();
        String email = txtFieldEmail.getEditText().getText().toString();
        String phone = txtFieldPhone.getEditText().getText().toString();
        String address = txtFieldAddress.getEditText().getText().toString();
        String days = txtFieldOpeningDays.getEditText().getText().toString();
        String hours = txtFieldOpeningHours.getEditText().getText().toString();

//        Check fields empty
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || days.isEmpty() || hours.isEmpty()) {
            Toast.makeText(AdminDentalAboutActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

//            Check email is validate
        } else if(!EmailValidator.isValidEmail(email)) {
            Toast.makeText(AdminDentalAboutActivity.this, "Please check your email pattern!", Toast.LENGTH_SHORT).show();

//          Check length of mobile number
        } else if (phone.length() != 10) {
            Toast.makeText(AdminDentalAboutActivity.this, "Please check your mobile number!", Toast.LENGTH_SHORT).show();

//          Update details
        } else {

            ref = FirebaseDatabase.getInstance().getReference("dentalDetails");
            ref.child("name").setValue(name);
            ref.child("email").setValue(email);
            ref.child("phone").setValue(phone);
            ref.child("address").setValue(address);
            ref.child("days").setValue(days);
            ref.child("hours").setValue(hours);

            Toast.makeText(AdminDentalAboutActivity.this, "Dental details updated successful!", Toast.LENGTH_SHORT).show();

        }
    }
}