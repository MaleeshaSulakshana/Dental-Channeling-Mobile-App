package com.example.dental_channelling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dental_channelling.Activity.Admin.AdminDashboardActivity;
import com.example.dental_channelling.Activity.Doctor.DoctorDashboardActivity;
import com.example.dental_channelling.Activity.Patient.PatientDashboardActivity;
import com.example.dental_channelling.Utiles.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class
LoginActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private Button btnExitYes, btnExitNo, btnLogin;
    private Dialog exitDialog;

    private TextView txtCreateAccountPage;
    private TextInputLayout txtFieldEmail, txtFieldPsw;

    private ProgressDialog loadingBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Get layouts
        exitDialog = new Dialog(LoginActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        txtCreateAccountPage = (TextView) this.findViewById(R.id.txtCreateAccountPage);
        btnLogin = (Button) this.findViewById(R.id.btnLogin);

        txtFieldEmail = (TextInputLayout) findViewById(R.id.txtFieldEmail);
        txtFieldPsw = (TextInputLayout) findViewById(R.id.txtFieldPsw);

        firebaseAuth = FirebaseAuth.getInstance();

//        Set onclick commands to text
        txtCreateAccountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });

//        Set onclick commands to login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
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

    //    Tap to close app
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        exitDialog.show();

        btnExitYes = (Button) exitDialog.findViewById(R.id.btnYes);
        btnExitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });

        btnExitNo = (Button) exitDialog.findViewById(R.id.btnNo);
        btnExitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitDialog.dismiss();
            }
        });

    }

//    Method for open register activity
    private void navigateToRegister()
    {
        Intent register = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(register);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open patient dashboard activity
    private void navigateToPatientDashboard()
    {
        Intent patient = new Intent(LoginActivity.this, PatientDashboardActivity.class);
        startActivity(patient);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open doctor dashboard activity
    private void navigateToDoctorDashboard()
    {
        Intent doctor = new Intent(LoginActivity.this, DoctorDashboardActivity.class);
        startActivity(doctor);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open admin dashboard activity
    private void navigateToAdminDashboard()
    {
        Intent admin = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        startActivity(admin);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for login
    private void login()
    {
        String email = txtFieldEmail.getEditText().getText().toString();
        String psw = txtFieldPsw.getEditText().getText().toString();

//        Check text fields is empty
        if (email.isEmpty() || psw.isEmpty()){
            Toast.makeText(LoginActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

//        Validate email
        } else if (!EmailValidator.isValidEmail(email)) {
            Toast.makeText(LoginActivity.this, "Please check your email pattern!", Toast.LENGTH_SHORT).show();

        } else {

//           Start Progress bar
            loadingBar = new ProgressDialog(LoginActivity.this);
            loadingBar.setTitle("Waiting for login");
            loadingBar.setMessage("Please Wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

//           Login
            firebaseAuth.signInWithEmailAndPassword(email, psw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                String userId = firebaseAuth.getCurrentUser().getUid();
                                ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

//                                        Check account type and redirect to dashboard page
                                        String accountType = snapshot.child("accountType").getValue().toString();
                                        Integer type = Integer.parseInt(accountType);
                                        if (type == 1) {
                                            loadingBar.dismiss();
                                            navigateToAdminDashboard();

                                        } else if (type == 2) {
                                            loadingBar.dismiss();
                                            navigateToDoctorDashboard();

                                        } else if (type == 3) {
                                            loadingBar.dismiss();
                                            navigateToPatientDashboard();

                                        } else {
                                            firebaseAuth.signOut();
                                            loadingBar.dismiss();
                                            Toast.makeText(LoginActivity.this, "Your account type is wrong!", Toast.LENGTH_SHORT).show();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                        firebaseAuth.signOut();
                                        loadingBar.dismiss();
                                        Toast.makeText(LoginActivity.this, "Some error occur! Try again", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }

}