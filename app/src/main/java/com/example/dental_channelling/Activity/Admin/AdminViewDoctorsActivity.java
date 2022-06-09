package com.example.dental_channelling.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.dental_channelling.Activity.Patient.PatientViewDoctorDetailsActivity;
import com.example.dental_channelling.Activity.Patient.PatientViewDoctorsActivity;
import com.example.dental_channelling.Adapters.DoctorAdapter;
import com.example.dental_channelling.Constructors.Doctors;
import com.example.dental_channelling.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminViewDoctorsActivity extends AppCompatActivity {

    private ListView doctorsViewList;
    private ArrayList<Doctors> arrayList = new ArrayList<>();

    private Button btnAddDoctor;

    private DatabaseReference ref;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_doctors);

//        Show doctors on list
        showDoctorsOnList();

        btnAddDoctor = (Button) findViewById(R.id.btnAddDoctor);

//        Set onclick commands for navigate to add doctor activity
        btnAddDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddDoctorActivity();
            }
        });

        doctorsViewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent detailsPage = new Intent(AdminViewDoctorsActivity.this, AdminViewDoctorDetailsActivity.class);
                detailsPage.putExtra("doctorID", arrayList.get(i).getDoctorId());
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
        Intent intent = new Intent(AdminViewDoctorsActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show doctors on list
    private void showDoctorsOnList()
    {
//        Show doctors data on list view
        doctorsViewList = (ListView) findViewById(R.id.doctorsViewList);
        DoctorAdapter doctorAdapter = new DoctorAdapter(this, R.layout.view_doctors_row, arrayList);
        doctorsViewList.setAdapter(doctorAdapter);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ref = FirebaseDatabase.getInstance().getReference("users/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    String accountType = snapshot1.child("accountType").getValue().toString();
                    Integer type = Integer.parseInt(accountType);

                    if (type.equals(2)) {

                        String profileImage = "";

                        if (snapshot1.child("profileImage").exists()) {
                            profileImage = snapshot1.child("profileImage").getValue().toString();
                        }

                        arrayList.add(new Doctors(snapshot1.child("name").getValue().toString(),
                                snapshot1.getKey(),
                                snapshot1.child("details").getValue().toString(), profileImage));
                        doctorAdapter.notifyDataSetChanged();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    Method for open add doctor activity
    private void navigateToAddDoctorActivity()
    {
        Intent intent = new Intent(AdminViewDoctorsActivity.this, AdminAddDoctorActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}