package com.example.dental_channelling.Activity.Patient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dental_channelling.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class PatientProfileActivity extends AppCompatActivity {

    private Button btnUpdateProfile, btnCancel, btnChangePsw;
    private TextInputLayout txtFieldName, txtFieldEmail, txtFieldPhone, txtFieldAddress;
    private ImageView profileIcon;

    private static final int PICK_IMAGE = 100;
    public Uri imageUri;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        txtFieldName = (TextInputLayout) this.findViewById(R.id.txtFieldName);
        txtFieldEmail = (TextInputLayout) this.findViewById(R.id.txtFieldEmail);
        txtFieldPhone = (TextInputLayout) this.findViewById(R.id.txtFieldPhone);
        txtFieldAddress = (TextInputLayout) this.findViewById(R.id.txtFieldAddress);

        btnUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnChangePsw = (Button) findViewById(R.id.btnChangePsw);

        profileIcon = (ImageView) findViewById(R.id.profileIcon);

        firebaseAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        Show profile details
        showProfileDetails();

//        Set onclick commands to update profile
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileDetails();
            }
        });

//        Set onclick commands to back to dashboard
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDashboard();
            }
        });

//        Set onclick commands to chang psw
        btnChangePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToChangePsw();
            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
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
        navigateToDashboard();
    }

//    Method for open profile psw change activity
    private void navigateToChangePsw()
    {
        Intent intent = new Intent(PatientProfileActivity.this, PatientPswChangeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for open profile dashboard activity
    private void navigateToDashboard()
    {
        Intent intent = new Intent(PatientProfileActivity.this, PatientDashboardActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Get profile details
    private void  showProfileDetails()
    {

//       Get user id
        String userId = firebaseAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot != null) {
//                   Set value to text boxes
                    txtFieldName.getEditText().setText(snapshot.child("name").getValue().toString());
                    txtFieldEmail.getEditText().setText(snapshot.child("email").getValue().toString());
                    txtFieldPhone.getEditText().setText(snapshot.child("phone").getValue().toString());
                    txtFieldAddress.getEditText().setText(snapshot.child("address").getValue().toString());

                    if (snapshot.child("profileImage").exists()) {
                        if (!snapshot.child("profileImage").getValue().equals("")){
                            Picasso.get().load(snapshot.child("profileImage").getValue().toString()).into(profileIcon);
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

//    Update profile details
    private void updateProfileDetails()
    {

        String name = txtFieldName.getEditText().getText().toString();
        String phone = txtFieldPhone.getEditText().getText().toString();
        String address = txtFieldAddress.getEditText().getText().toString();

//        Check fields empty
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty())
        {
            Toast.makeText(PatientProfileActivity.this, "Text fields are empty!", Toast.LENGTH_SHORT).show();

//        Check length of mobile number
        } else if (phone.length() != 10) {
            Toast.makeText(PatientProfileActivity.this, "Please check your mobile number!", Toast.LENGTH_SHORT).show();

        } else {

//           Get user id
            String userId = firebaseAuth.getCurrentUser().getUid();
            ref = FirebaseDatabase.getInstance().getReference("users/"+userId);

            ref.child("name").setValue(name);
            ref.child("phone").setValue(phone);
            ref.child("address").setValue(address);

            Toast.makeText(PatientProfileActivity.this, "Profile details updated successful!", Toast.LENGTH_SHORT).show();
        }

    }

//    Method for open gallery
    private void openGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

//    Method for upload profile picture
    private void uploadProfilePicture() {

        final ProgressDialog progressDialog = new ProgressDialog(PatientProfileActivity.this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();

        final String userId = firebaseAuth.getCurrentUser().getUid();
        StorageReference riversRef = storageReference.child("Profile_Pictures/" + userId);
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
//                                Get user id
                                String userId = firebaseAuth.getCurrentUser().getUid();
                                ref = FirebaseDatabase.getInstance().getReference("users/"+userId);
                                ref.child("profileImage").setValue(uri.toString());
                            }
                        });
                        progressDialog.dismiss();
                        Toast.makeText(PatientProfileActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PatientProfileActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploading... " + (int) progressPercentage + "%");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            profileIcon.setImageURI(imageUri);
//            Upload to firebase storage
            uploadProfilePicture();
        }

    }

}