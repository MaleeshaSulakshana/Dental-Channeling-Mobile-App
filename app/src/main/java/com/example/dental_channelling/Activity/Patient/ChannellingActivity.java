package com.example.dental_channelling.Activity.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dental_channelling.Constructors.ChannelDoctor;
import com.example.dental_channelling.Constructors.Registration;
import com.example.dental_channelling.PayPalConfig.PayPalConfig;
import com.example.dental_channelling.R;
import com.example.dental_channelling.RegistrationActivity;
import com.example.dental_channelling.Utiles.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class ChannellingActivity extends AppCompatActivity {

    private TextInputLayout txtFieldName, txtFieldEmail,
            txtFieldCharges, txtFieldPhone, txtFiledDateSelector, txtFieldDoctorName;
    private EditText txtFieldDate;
    private Button btnChannel;

    final Calendar calendar = Calendar.getInstance();

    String doctorId = "", mon="no", tue="no", wen="no", thu="no",
            fri="no", sat="no", sun="no", todayDate="", keyDate="", channellingCharges="",
            keyId="", name="", email=""
            , phone="", selectedDate="";
    int randomNumber = 00000000;
    Date dateState;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;

    private static final String TAG = "Dental-Channelling";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    private static final String CONFIG_CLIENT_ID = PayPalConfig.PAYPAL_CLIENT_ID;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channelling);

//        Get put extra value
        Intent patient = getIntent();
        doctorId = patient.getStringExtra("doctorId");

//        Start paypal services
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

//        Get ui components
        txtFieldDate = (EditText) findViewById(R.id.txtFieldSelectDate);

        txtFieldDoctorName = (TextInputLayout) findViewById(R.id.txtFieldDoctorName);
        txtFieldCharges = (TextInputLayout) findViewById(R.id.txtFieldCharges);
        txtFieldName = (TextInputLayout) findViewById(R.id.txtFieldName);
        txtFieldEmail = (TextInputLayout) findViewById(R.id.txtFieldEmail);
        txtFieldPhone = (TextInputLayout) findViewById(R.id.txtFieldPhone);
        txtFiledDateSelector = (TextInputLayout) findViewById(R.id.txtFiledDateSelector);

        btnChannel = (Button) findViewById(R.id.btnChannel);

//        Get today date
        dateState = new Date();

        firebaseAuth = FirebaseAuth.getInstance();

        showDoctorDetails(doctorId);

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

//        Set some option to text layouts
        txtFieldDate.setEnabled(true);
        txtFieldDate.setTextIsSelectable(true);
        txtFieldDate.setFocusable(false);
        txtFieldDate.setFocusableInTouchMode(false);

//        Onclick for show date picker
        txtFieldDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ChannellingActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        Command for channelling
        btnChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                channelDoctor();
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
        Intent intent = new Intent(ChannellingActivity.this, PatientViewDoctorDetailsActivity.class);
        intent.putExtra("doctorID", doctorId);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show date on text box
    private void updateDate() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtFieldDate.setText(sdf.format(calendar.getTime()));
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
                    txtFieldDoctorName.getEditText().setText("Doctor Name : "+snapshot.child("name").getValue().toString());
                    txtFieldCharges.getEditText().setText("Charges : Rs. "+snapshot.child("charges").getValue().toString());
                    channellingCharges = snapshot.child("charges").getValue().toString();

                    mon = snapshot.child("mon").getValue().toString();
                    tue = snapshot.child("tue").getValue().toString();
                    wen = snapshot.child("wen").getValue().toString();
                    thu = snapshot.child("thu").getValue().toString();
                    fri = snapshot.child("fri").getValue().toString();
                    sat = snapshot.child("sat").getValue().toString();
                    sun = snapshot.child("sun").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Method for channel doctor
    private void channelDoctor()
    {
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateState);
        keyDate = new SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault()).format(dateState);

//            Generate random number
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            randomNumber = ThreadLocalRandom.current().nextInt(100000000, 999999999);
        }

        keyId = keyDate+randomNumber;

        name = txtFieldName.getEditText().getText().toString();
        email = txtFieldEmail.getEditText().getText().toString();
        phone = txtFieldPhone.getEditText().getText().toString();
        selectedDate = txtFiledDateSelector.getEditText().getText().toString();

//        Check fields empty
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || selectedDate.isEmpty())
        {
            Toast.makeText(ChannellingActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

//        Validate email
        } else if (!EmailValidator.isValidEmail(email)) {
            Toast.makeText(ChannellingActivity.this, "Please check your email pattern!", Toast.LENGTH_SHORT).show();

//        Validate phone number
        } else if (phone.length() != 10) {
            Toast.makeText(ChannellingActivity.this, "Please check your mobile number!", Toast.LENGTH_SHORT).show();

        } else {

            if (selectedDate.compareTo(todayDate) > 0 || selectedDate.equals(todayDate)) {

                String dayName = "";

                SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                try {
                    Date parseSelectedDate = inFormat.parse(selectedDate);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.US);
                    dayName = simpleDateFormat.format(parseSelectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String dateOk = "no";
                if (dayName.equals("Monday")) {
                    if (mon.equals("yes")) {
                        dateOk = "yes";
                    }
                } else if (dayName.equals("Tuesday")) {
                    if (tue.equals("yes")) {
                        dateOk = "yes";
                    }
                } else if (dayName.equals("Wednesday")) {
                    if (wen.equals("yes")) {
                        dateOk = "yes";
                    }
                } else if (dayName.equals("Thursday")) {
                    if (thu.equals("yes")) {
                        dateOk = "yes";
                    }
                } else if (dayName.equals("Friday")) {
                    if (fri.equals("yes")) {
                        dateOk = "yes";
                    }
                } else if (dayName.equals("Saturday")) {
                    if (sat.equals("yes")) {
                        dateOk = "yes";
                    }
                } else if (dayName.equals("Sunday")) {
                    if (sun.equals("yes")) {
                        dateOk = "yes";
                    }
                }

                if (dateOk.equals("yes")){

//                    Call payment method
                    getPayment();

                } else {
                    Toast.makeText(ChannellingActivity.this, "Doctor not available on "+dayName+"!", Toast.LENGTH_SHORT).show();
                }

            }

        }

    }

    private void getPayment() {

        if (channellingCharges.equals("")){
            Toast.makeText(ChannellingActivity.this, "Waiting for channelling charges!", Toast.LENGTH_SHORT).show();
        } else {

            PayPalPayment thingToBuy = new PayPalPayment(new BigDecimal(String.valueOf(channellingCharges)),
                    "USD", "Dental Channelling Payment",
                    PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(ChannellingActivity.this, PaymentActivity.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
            startActivityForResult(intent, REQUEST_CODE_PAYMENT);

        }

    }

    private void insertChannellingDetails()
    {
//            Get user id
       String  userId = firebaseAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("appointments/"+keyId);

        ChannelDoctor channelDoctor = new ChannelDoctor(keyId, userId, name, email
                , phone, selectedDate, doctorId, "pending");
        ref.setValue(channelDoctor).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ChannellingActivity.this, "Channelling Successful!", Toast.LENGTH_SHORT).show();
                goBack();
            }
        });
    }

    protected void displayResultText(String result) {
        Toast.makeText(
                getApplicationContext(),
                result, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        insertChannellingDetails();

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                displayResultText("Payment canceled!");

            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        displayResultText("Future Payment code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");

            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");

            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        displayResultText("Profile Sharing code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) { }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

}