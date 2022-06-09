package com.example.dental_channelling.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dental_channelling.Activity.Doctor.DoctorViewAppointmentsDetailsActivity;
import com.example.dental_channelling.Activity.Doctor.DoctorViewPatientProfileActivity;
import com.example.dental_channelling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminViewAppointmentDetailsActivity extends AppCompatActivity {

    private TextView appointmentDate, doctorName, patientName, phoneNumber, email;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref, usersRef;

    String appointmentId = "", patientId = "", doctorId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_appointment_details);

//        Get put extra value
        Intent patient = getIntent();
        appointmentId = patient.getStringExtra("appointmentID");

        appointmentDate = this.findViewById(R.id.appointmentDate);
        doctorName = this.findViewById(R.id.doctorName);
        patientName = this.findViewById(R.id.patientName);
        phoneNumber = this.findViewById(R.id.phoneNumber);
        email = this.findViewById(R.id.email);

        firebaseAuth = FirebaseAuth.getInstance();

//        Show patients details
        showAppointmentDetails(appointmentId);

//        Navigate to activity
        patientName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPatientDetailsActivity();
            }
        });

        doctorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDoctorDetailsActivity();
            }
        });

//        Make call
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });

//        Make email
        email.setOnClickListener(new View.OnClickListener() {
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
        Intent intent = new Intent(AdminViewAppointmentDetailsActivity.this, AdminViewAppointmentsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Show appointment details
    private void  showAppointmentDetails(String appointmentId)
    {
        usersRef = FirebaseDatabase.getInstance().getReference("users/");
        ref = FirebaseDatabase.getInstance().getReference("appointments/"+appointmentId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text boxes
                    appointmentDate.setText(snapshot.child("date").getValue().toString());
                    patientName.setText(snapshot.child("name").getValue().toString());
                    phoneNumber.setText(snapshot.child("phone").getValue().toString());
                    email.setText(snapshot.child("email").getValue().toString());

                    patientId = snapshot.child("userId").getValue().toString();
                    doctorId = snapshot.child("doctorId").getValue().toString();

                    usersRef.child(snapshot.child("doctorId").getValue().toString())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    doctorName.setText(dataSnapshot.child("name").getValue().toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Method for make phone call
    private void makeCall()
    {
        String number = phoneNumber.getText().toString();
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
            Toast.makeText(AdminViewAppointmentDetailsActivity.this, "Waiting for email address!", Toast.LENGTH_SHORT).show();
        }

    }

    private void navigateToPatientDetailsActivity()
    {
        if (!patientId.equals("")){

            Intent intent = new Intent(AdminViewAppointmentDetailsActivity.this, AdminViewPatientDetailsActivity.class);
            intent.putExtra("patientID", patientId);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } else {
            Toast.makeText(AdminViewAppointmentDetailsActivity.this, "Waiting for patient id!", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDoctorDetailsActivity()
    {
        if (!doctorId.equals("")){

            Intent intent = new Intent(AdminViewAppointmentDetailsActivity.this, AdminViewDoctorDetailsActivity.class);
            intent.putExtra("doctorID", doctorId);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } else {
            Toast.makeText(AdminViewAppointmentDetailsActivity.this, "Waiting for doctor id!", Toast.LENGTH_SHORT).show();
        }
    }

}