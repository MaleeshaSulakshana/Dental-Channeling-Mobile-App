package com.example.dental_channelling.Activity.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

public class DentalAboutActivity extends AppCompatActivity {

    private TextView txtName, txtEmail, txtPhone, txtAddress, txtOpeningDays, txtOpeningHours;
    private DatabaseReference ref;

    private String email="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dental_about);

        txtName = (TextView) this.findViewById(R.id.txtName);
        txtEmail = (TextView) this.findViewById(R.id.txtEmail);
        txtPhone = (TextView) this.findViewById(R.id.txtPhone);
        txtAddress = (TextView) this.findViewById(R.id.txtAddress);
        txtOpeningDays = (TextView) this.findViewById(R.id.txtOpeningDays);
        txtOpeningHours = (TextView) this.findViewById(R.id.txtOpeningHours);

//        Show dental details
        showDetails();

//        Make call
        txtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });

//        Make email
        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEmail();
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
        Intent intent = new Intent(DentalAboutActivity.this, PatientDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
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
                    txtName.setText(snapshot.child("name").getValue().toString());
                    txtEmail.setText(snapshot.child("email").getValue().toString());
                    txtPhone.setText(snapshot.child("phone").getValue().toString());
                    txtAddress.setText(snapshot.child("address").getValue().toString());
                    txtOpeningDays.setText(snapshot.child("days").getValue().toString());
                    txtOpeningHours.setText(snapshot.child("hours").getValue().toString());

                    email = snapshot.child("email").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Method for make phone call
    private void makeCall()
    {
        String number = txtPhone.getText().toString();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+number));
        startActivity(callIntent);
    }


//    Method for make email sender
    private void makeEmail()
    {
        if (!email.equals("")){

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:"+email);
            intent.setData(data);
            startActivity(intent);

        } else {
            Toast.makeText(DentalAboutActivity.this, "Waiting for email address!", Toast.LENGTH_SHORT).show();
        }

    }
}