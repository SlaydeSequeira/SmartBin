package com.example.smartbin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smartbin.adapter.MessageAdapter;
import com.example.smartbin.model.Chat;
import com.example.smartbin.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smartbin.adapter.MessageAdapter;
import com.example.smartbin.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {

    TextView username;
    ImageView imageView;

    RecyclerView recyclerView;
    EditText msg_editText;
    ImageButton sendBtn;

    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    ValueEventListener seenListener;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#FBFAEC"));
        }
        Objects.requireNonNull(getSupportActionBar()).hide();

        imageView = findViewById(R.id.imageview_profile);
        username = findViewById(R.id.usernamey);
        sendBtn = findViewById(R.id.btn_send);
        msg_editText = findViewById(R.id.text_send);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Use a single chat room for all users (e.g., "GlobalChatRoom")
        final String chatRoomId = "GlobalChatRoom";

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        // Assuming you have a Users model class for user information
        DatabaseReference userRef = reference.child("MyUsers").child(fuser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                if (user != null) {
                    username.setText(user.getUsername());
                    // Check if imageURL is not null before using equals
                    if (user.getImageURL() != null && "default".equals(user.getImageURL())) {
                        imageView.setImageResource(R.drawable.draw);
                    } else {
                        // Load user image using Glide or your preferred image loading library
                        if (user.getImageURL() != null) {
                            Glide.with(getApplicationContext()).load(user.getImageURL()).into(imageView);
                        }
                    }
                } else {
                    // Handle the case where the user data is null
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if necessary
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msg_editText.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), chatRoomId, msg);
                } else {
                    Toast.makeText(MessageActivity.this, "Please send a non-empty message", Toast.LENGTH_SHORT).show();
                }
                msg_editText.setText("");
            }
        });

        SeenMessage(chatRoomId);
        readMessages(fuser.getUid(), chatRoomId);
    }

    private void SeenMessage(final String chatRoomId) {
        reference = FirebaseDatabase.getInstance().getReference("Chats").child(chatRoomId);

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat != null && chat.getReceiver().equals(fuser.getUid())) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if necessary
            }
        });
    }

    private void sendMessage(String sender, String chatRoomId, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(chatRoomId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.push().setValue(hashMap);
    }

    private void readMessages(final String myId, final String chatRoomId) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats").child(chatRoomId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat != null) {
                        mchat.add(chat);
                    }
                }

                // Move the messageAdapter creation and setting outside the loop
                messageAdapter = new MessageAdapter(MessageActivity.this, mchat);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if necessary
            }
        });
    }

    private void CheckStatus(String status) {
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        CheckStatus("Offline");
    }
}

