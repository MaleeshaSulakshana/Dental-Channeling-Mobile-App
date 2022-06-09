package com.example.dental_channelling.Activity.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dental_channelling.Activity.Admin.AdminViewDoctorDetailsActivity;
import com.example.dental_channelling.Activity.Admin.AdminViewDoctorsActivity;
import com.example.dental_channelling.Activity.Doctor.DoctorViewAppointmentsActivity;
import com.example.dental_channelling.Adapters.AppointmentAdapter;
import com.example.dental_channelling.Adapters.DoctorAdapter;
import com.example.dental_channelling.Constructors.Appointment;
import com.example.dental_channelling.Constructors.Doctors;
import com.example.dental_channelling.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PatientAppointmentsActivity extends AppCompatActivity {

    private Button btnSelectDate;
    private ListView appointmentViewList;
    private ArrayList<Appointment> appointmentArrayList = new ArrayList<>();

    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;

    private String todayDate = "";
    private Date dateState;

    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointments);

        btnSelectDate = (Button) this.findViewById(R.id.btnSelectDate);
        appointmentViewList = (ListView) findViewById(R.id.appointmentViewList);

        firebaseAuth = FirebaseAuth.getInstance();

//        Get today date
        dateState = new Date();
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateState);
        btnSelectDate.setText(todayDate);

//        Show appointment on list
        showAppointmentsOnList(todayDate);

//        Show calender
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

        };

//        Onclick for show date picker
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PatientAppointmentsActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        appointmentViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent detailsPage = new Intent(PatientAppointmentsActivity.this, PatientViewAppointmentDetailsActivity.class);
                detailsPage.putExtra("appointmentID", appointmentArrayList.get(i).getAppointmentId());
                startActivity(detailsPage);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        Intent intent = new Intent(PatientAppointmentsActivity.this, PatientDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show appointment on list
    private void showAppointmentsOnList(String dateSelected)
    {
//        Show appointment data on list view
        appointmentArrayList = new ArrayList<>();
        appointmentViewList.setAdapter(null);
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(this, R.layout.view_appointments_row, appointmentArrayList);
        appointmentViewList.setAdapter(appointmentAdapter);

        String userId = firebaseAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("appointments/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    String appointmentUserId = snapshot1.child("userId").getValue().toString();
                    String status = snapshot1.child("status").getValue().toString();
                    String appointmentDate = snapshot1.child("date").getValue().toString();

                    if (userId.equals(appointmentUserId) && status.equals("pending")
                            && appointmentDate.equals(dateSelected)) {

                        appointmentArrayList.add(new Appointment(snapshot1.child("name").getValue().toString(),
                                snapshot1.child("id").getValue().toString(),
                                snapshot1.child("date").getValue().toString()));

                        appointmentAdapter.notifyDataSetChanged();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    Method for show date on text
    private void updateDate() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String dateValue = sdf.format(calendar.getTime()).toString();
        btnSelectDate.setText(sdf.format(calendar.getTime()));

        showAppointmentsOnList(dateValue);
    }

}