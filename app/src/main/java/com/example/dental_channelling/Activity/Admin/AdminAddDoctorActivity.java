package com.example.dental_channelling.Activity.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dental_channelling.Constructors.DoctorRegistration;
import com.example.dental_channelling.Constructors.Registration;
import com.example.dental_channelling.R;
import com.example.dental_channelling.RegistrationActivity;
import com.example.dental_channelling.Utiles.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminAddDoctorActivity extends AppCompatActivity {

    private Button btnCreateAccount, btnCancel;
    private TextInputLayout txtFieldName, txtFieldEmail, txtFieldPhone,
            txtFieldDetails, txtFieldTimes, txtFieldPsw, txtFieldConfirmPsw, txtFieldCharges;
    private ChipGroup chipGroup;
    private String selectedDays = "";

    private ProgressDialog loadingBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_doctor);

        txtFieldName = (TextInputLayout) this.findViewById(R.id.txtFieldName);
        txtFieldCharges = (TextInputLayout) this.findViewById(R.id.txtFieldCharges);
        txtFieldEmail = (TextInputLayout) this.findViewById(R.id.txtFieldEmail);
        txtFieldPhone = (TextInputLayout) this.findViewById(R.id.txtFieldPhone);
        txtFieldDetails = (TextInputLayout) this.findViewById(R.id.txtFieldDetails);
        txtFieldTimes = (TextInputLayout) this.findViewById(R.id.txtFieldHours);
        txtFieldPsw = (TextInputLayout) this.findViewById(R.id.txtFieldPsw);
        txtFieldConfirmPsw = (TextInputLayout) this.findViewById(R.id.txtFieldConfirmPsw);

        chipGroup = (ChipGroup) findViewById(R.id.daysSelector);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);

        firebaseAuth = FirebaseAuth.getInstance();

//        Set onclick commands to go back
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToViewDoctorActivity();
            }
        });

//        Set onclick commands to create new doctor account
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewDoctorAccount();
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
        navigateToViewDoctorActivity();
    }

//    Method for open view doctor activity
    private void navigateToViewDoctorActivity()
    {
        Intent intent = new Intent(AdminAddDoctorActivity.this, AdminViewDoctorsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

//    Method for create new doctor account
    private void createNewDoctorAccount()
    {
        String name = txtFieldName.getEditText().getText().toString();
        String email = txtFieldEmail.getEditText().getText().toString();
        String phone = txtFieldPhone.getEditText().getText().toString();
        String charges = txtFieldCharges.getEditText().getText().toString();
        String details = txtFieldDetails.getEditText().getText().toString();
        String times = txtFieldTimes.getEditText().getText().toString();
        String psw = txtFieldPsw.getEditText().getText().toString();
        String cpsw = txtFieldConfirmPsw.getEditText().getText().toString();

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
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || details.isEmpty()
               || charges.isEmpty() || times.isEmpty() || psw.isEmpty() || cpsw.isEmpty())
        {
            Toast.makeText(AdminAddDoctorActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

//        Validate available days
        } else if (mon.equals("no") || tue.equals("no") || wen.equals("no") || thu.equals("no") ||
                fri.equals("no") || sat.equals("no") || sun.equals("no")) {

//            Validate email
            if (!EmailValidator.isValidEmail(email)) {
                Toast.makeText(AdminAddDoctorActivity.this, "Please check email pattern!", Toast.LENGTH_SHORT).show();

//        Check length of mobile number
            } else if (phone.length() != 10) {
                Toast.makeText(AdminAddDoctorActivity.this, "Please check mobile number!", Toast.LENGTH_SHORT).show();

//        Check psw and confirm psw is equals
            } else if (!psw.equals(cpsw)) {
                Toast.makeText(AdminAddDoctorActivity.this, "Please check password and confirm password!", Toast.LENGTH_SHORT).show();

            } else {

//           Start progress bar
                loadingBar = new ProgressDialog(AdminAddDoctorActivity.this);
                loadingBar.setTitle("Creating New Doctor Account");
                loadingBar.setMessage("Please Wait");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

//            Create account with firebase authentication
                String finalMon = mon;
                String finalTue = tue;
                String finalWen = wen;
                String finalThu = thu;
                String finalFri = fri;
                String finalSat = sat;
                String finalSun = sun;
                firebaseAuth.createUserWithEmailAndPassword(email, psw)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){

//                               Get user id
                                    String userId = firebaseAuth.getCurrentUser().getUid();
                                    ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

//                               Insert doctor data to firebase real time database
                                    DoctorRegistration registration = new DoctorRegistration(name, email, phone, charges, details,
                                            finalMon, finalTue, finalWen, finalThu, finalFri, finalSat, finalSun
                                            , times,"","2");
                                    ref.setValue(registration);
                                    clear();
                                    Toast.makeText(AdminAddDoctorActivity.this, "New doctor added successful!", Toast.LENGTH_SHORT).show();

                                    loadingBar.dismiss();

                                } else {

                                    String msg = task.getException().toString();
                                    Toast.makeText(AdminAddDoctorActivity.this, "Error" + msg, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();

                                }

                            }
                        });

                firebaseAuth.signOut();

            }
        } else {
            Toast.makeText(AdminAddDoctorActivity.this, "Please select days!", Toast.LENGTH_SHORT).show();

        }

    }

    private void clear()
    {
        txtFieldName.getEditText().setText("");
        txtFieldCharges.getEditText().setText("");
        txtFieldEmail.getEditText().setText("");
        txtFieldPhone.getEditText().setText("");
        txtFieldDetails.getEditText().setText("");
        txtFieldTimes.getEditText().setText("");
        txtFieldPsw.getEditText().setText("");
        txtFieldConfirmPsw.getEditText().setText("");
    }

}