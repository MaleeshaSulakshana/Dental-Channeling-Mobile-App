package com.example.dental_channelling.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dental_channelling.Adapters.AppointmentAdapter;
import com.example.dental_channelling.Adapters.PatientAdapter;
import com.example.dental_channelling.Constructors.Appointment;
import com.example.dental_channelling.Constructors.Doctors;
import com.example.dental_channelling.Constructors.Patients;
import com.example.dental_channelling.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminViewPatientsActivity extends AppCompatActivity {

    private ListView patientsViewList;
    private ArrayList<Patients> patientsArrayList = new ArrayList<>();

    private DatabaseReference ref;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_patients);

//        Show patients on list
        showPatientsOnList();

        patientsViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent detailsPage = new Intent(AdminViewPatientsActivity.this, AdminViewPatientDetailsActivity.class);
                detailsPage.putExtra("patientID", patientsArrayList.get(i).getPatientId());
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
        Intent intent = new Intent(AdminViewPatientsActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show patients on list
    private void showPatientsOnList()
    {
//        Show patients data on list view
        patientsViewList = (ListView) findViewById(R.id.patientsViewList);
        PatientAdapter patientAdapter = new PatientAdapter(this, R.layout.view_patients_row, patientsArrayList);
        patientsViewList.setAdapter(patientAdapter);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ref = FirebaseDatabase.getInstance().getReference("users/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    String accountType = snapshot1.child("accountType").getValue().toString();
                    Integer type = Integer.parseInt(accountType);

                    if (type.equals(3)) {

                        String patientProfilePicture = "";
                        if (snapshot1.child("profileImage").exists()) {
                            patientProfilePicture = snapshot1.child("profileImage").getValue().toString();
                        }

                        patientsArrayList.add(new Patients(snapshot1.child("name").getValue().toString(),
                                snapshot1.getKey(), patientProfilePicture));
                        patientAdapter.notifyDataSetChanged();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}