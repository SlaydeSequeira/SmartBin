package com.example.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AadharVerification extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String verificationId;
    private FirebaseUser fuser;

    private EditText editText1, editText2, editText3;
    private Button button1, button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aadhar_verification);

        // Initialize Firebase Auth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference("MyUsers")
                .child(fuser.getUid());

        // Create a callback for the verification process
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // Auto-retrieval or instant verification of the OTP code.
                // You can automatically sign in the user here if needed.
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // Verification failed. Handle the error here.
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code is sent successfully.
                // Save the verification ID and show the OTP entry UI.
                AadharVerification.this.verificationId = verificationId;
            }
        };

        // Initialize views
        editText1 = findViewById(R.id.edittext1);
        editText2 = findViewById(R.id.edittext2);
        editText3 = findViewById(R.id.edittext3);
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button1);

        // Set click listener for the button to send OTP
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editText2.getText().toString();
                requestPhoneNumberVerification("+91" + phoneNumber); // Replace with the user's phone number
            }
        });

        // Set click listener for the button to save data to Firebase
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aadharData = editText1.getText().toString();
                String phoneNumber = editText2.getText().toString();
                String otp = editText3.getText().toString();

                // Verify the OTP
                if (!TextUtils.isEmpty(verificationId) && !TextUtils.isEmpty(otp)) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                    signInWithPhoneAuthCredential(credential, aadharData, phoneNumber);
                } else {
                    Toast.makeText(AadharVerification.this, "Please enter the OTP first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, String aadharData, String phoneNumber) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Phone authentication is successful, handle signed-in user.
                        FirebaseUser user = task.getResult().getUser();

                        // Save data to Firebase after successful authentication
                        saveDataToFirebase(aadharData, phoneNumber, "verified");
                    } else {
                        // Phone authentication failed.
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid verification code.
                            Toast.makeText(AadharVerification.this, "Invalid OTP entered. Please try again.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Other errors.
                            Toast.makeText(AadharVerification.this, "Error occurred during OTP verification. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveDataToFirebase(String aadharData, String phoneNumber, String otp) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyUsers")
                .child(fuser.getUid());

        // Create a HashMap to store the data
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("aadhar", aadharData);
        dataMap.put("phno", phoneNumber);
        dataMap.put("otp", otp);
        dataMap.put("verified", "true");

        // Push data to Firebase
        reference.updateChildren(dataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AadharVerification.this, "Aadhar Verification Successful", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AadharVerification.this, "Error Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}