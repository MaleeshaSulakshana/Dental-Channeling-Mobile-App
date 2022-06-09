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

import com.example.dental_channelling.Activity.Patient.PatientDashboardActivity;
import com.example.dental_channelling.Constructors.Registration;
import com.example.dental_channelling.Utiles.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private Button btnCreateAccount, btnExitYes, btnExitNo;
    private Dialog exitDialog;

    private TextView txtLoginPage;

    private TextInputLayout txtFieldName, txtFieldEmail, txtFieldPhone, txtFieldAddress, txtFieldPsw, txtFieldConfirmPsw;

    private ProgressDialog loadingBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

//        Get layouts
        exitDialog = new Dialog(RegistrationActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_box);

        txtLoginPage = (TextView) this.findViewById(R.id.txtLoginPage);

        txtFieldName = (TextInputLayout) this.findViewById(R.id.txtFieldName);
        txtFieldEmail = (TextInputLayout) this.findViewById(R.id.txtFieldEmail);
        txtFieldPhone = (TextInputLayout) this.findViewById(R.id.txtFieldPhone);
        txtFieldAddress = (TextInputLayout) this.findViewById(R.id.txtFieldAddress);
        txtFieldPsw = (TextInputLayout) this.findViewById(R.id.txtFieldPsw);
        txtFieldConfirmPsw = (TextInputLayout) this.findViewById(R.id.txtFieldConfirmPsw);

        btnCreateAccount = (Button) this.findViewById(R.id.btnCreateAccount);

        firebaseAuth = FirebaseAuth.getInstance();

//        Set onclick commands to text
        txtLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });

//        Set onclick command to registration button
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration();
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

//    Method for open login activity
    private void navigateToLogin()
    {
        Intent login = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(login);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open patient dashboard activity
    private void navigateToPatientDashboard()
    {
        Intent patient = new Intent(RegistrationActivity.this, PatientDashboardActivity.class);
        startActivity(patient);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for registration
    private void registration()
    {

        String name = txtFieldName.getEditText().getText().toString();
        String email = txtFieldEmail.getEditText().getText().toString();
        String phone = txtFieldPhone.getEditText().getText().toString();
        String address = txtFieldAddress.getEditText().getText().toString();
        String psw = txtFieldPsw.getEditText().getText().toString();
        String cpsw = txtFieldConfirmPsw.getEditText().getText().toString();

//        Check fields empty
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || psw.isEmpty() || cpsw.isEmpty())
        {
            Toast.makeText(RegistrationActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

//        Validate email
        } else if (!EmailValidator.isValidEmail(email)) {
            Toast.makeText(RegistrationActivity.this, "Please check your email pattern!", Toast.LENGTH_SHORT).show();

//        Check length of mobile number
        } else if (phone.length() != 10) {
            Toast.makeText(RegistrationActivity.this, "Please check your mobile number!", Toast.LENGTH_SHORT).show();

//        Check psw and confirm psw is equals
        } else if (!psw.equals(cpsw)) {
            Toast.makeText(RegistrationActivity.this, "Please check password and confirm password!", Toast.LENGTH_SHORT).show();

        } else {

//           Start progress bar
            loadingBar = new ProgressDialog(RegistrationActivity.this);
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

//            Create account with firebase authentication
            firebaseAuth.createUserWithEmailAndPassword(email, psw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

//                               Get user id
                                String userId = firebaseAuth.getCurrentUser().getUid();
                                ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

//                               Insert patient data to firebase real time database
                                Registration registration = new Registration(name, email, phone, address, "3", "");
                                ref.setValue(registration);
                                loadingBar.dismiss();

//                               Navigate to patient dashboard
                                navigateToPatientDashboard();

                            } else {

                                String msg = task.getException().toString();
                                Toast.makeText(RegistrationActivity.this, "Error" + msg, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }

                        }
                    });

        }


    }

}