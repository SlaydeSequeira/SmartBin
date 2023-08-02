package com.example.smartbin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {
    int checkflag=0;
    EditText userETLogin, passETlogin;
    Button loginBtn, RegisterBtn;
    ImageView image;
    // Firebase:
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        image = findViewById(R.id.img);
        userETLogin = findViewById(R.id.edittext1);
        passETlogin = findViewById(R.id.edittext2);
        loginBtn    = findViewById(R.id.button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your web client ID from Firebase console
                .requestEmail()
                .build();

        // Build the GoogleSignInClient with the options above
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Firebase Auth:
        auth = FirebaseAuth.getInstance();


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        // Login Button:
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_text = userETLogin.getText().toString();
                String pass_text  = passETlogin.getText().toString();


                // Checking if it is empty:
                if (TextUtils.isEmpty(email_text) || TextUtils.isEmpty(pass_text)){
                    Toast.makeText(LoginActivity.this, "Please fill the Fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.signInWithEmailAndPassword(email_text, pass_text)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Intent i = new Intent(LoginActivity.this,HomePage.class);
                                        startActivity(i);
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });



    }
    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // onActivityResult to handle the result of Google Sign-In
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    // Method to handle Google Sign-In result
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                firebaseAuthWithGoogle(account);
            }
        } else {
            // Google Sign-In failed, handle error if needed
        }
    }

    // Firebase authentication with Google credentials
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign-in success, update user info into the Realtime Database
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                String username = firebaseUser.getDisplayName();
                                String email = firebaseUser.getEmail();
                                // ... Get other user info from the FirebaseUser object if needed

                                // Add your code to update user info into the Realtime Database
                                updateUserInfoToDatabase(userId, username, email);
                            }
                        } else {
                            // Sign-in failed, handle error if needed
                        }
                    }
                });
    }

    // Method to update user info into the Realtime Database
    private void updateUserInfoToDatabase(String userId, String username, String email) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User already exists, proceed to home page
                    Intent intent = new Intent(LoginActivity.this, HomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // User doesn't exist, update user info
                    HashMap<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", userId);
                    userInfo.put("username", username);
                    userInfo.put("email", email);
                    userInfo.put("admin", 0);
                    userInfo.put("imageURL", "default");
                    Check();

                    usersRef.setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // User info updated successfully
                                // Proceed to the next activity or perform any other action
                                Intent intent = new Intent(LoginActivity.this, HomePage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                // Error occurred while updating user info, handle it if needed
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event if needed
            }
        });
    }

    private void Check() {
        int t= RandomGenerator();
        final int[] flag = {0};
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("tokens");
        if(checkflag==0) {
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    StringBuilder childrenKeys = new StringBuilder();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String childKey = childSnapshot.getKey();
                        childrenKeys.append(childKey);
                        int a = Integer.parseInt(childKey);
                        if (a == t) {
                            Check();
                            flag[0] = 1;
                            break;
                        }
                    }
                    if (flag[0] != 1) {
                        String token = String.valueOf(t);
                        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = fuser.getUid();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(token, uid);
                        myRef.updateChildren(map);
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("MyUsers");
                        HashMap<String,Object> hashmap = new HashMap<>();
                        hashmap.put("token",token);
                        myRef.child(uid).updateChildren(hashmap);
                        checkflag = 1;

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled event if needed
                }
            });
        }
    }

    private int RandomGenerator() {
        Random r = new Random();
        int token=r.nextInt(1000000);
        return token;
    }
}