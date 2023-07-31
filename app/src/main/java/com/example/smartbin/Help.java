package com.example.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Help extends AppCompatActivity {

    private EditText editText1;
    private EditText editText2;
    private Spinner spinner;
    private Button submitButton;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        editText1 = findViewById(R.id.edittext1);
        editText2 = findViewById(R.id.edittext2);
        spinner = findViewById(R.id.action_bar_spinner);
        submitButton = findViewById(R.id.button);
        firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myRef= firebaseDatabase.getReference("Complaints");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, R.layout.spinner_item_layout);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setDropDownVerticalOffset(spinner.getHeight());
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // Set up the OnClickListener for the "Submit" button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the values from the EditText fields
                String a = editText1.getText().toString();
                String b = editText2.getText().toString();

                // Get the selected item from the Spinner
                int selectedItemPosition = spinner.getSelectedItemPosition();
                HashMap<Object,Object> hashMap = new HashMap<>();
                hashMap.put(a,b);
                String c = String.valueOf(selectedItemPosition);
                myRef.child(c).setValue(hashMap);
                Toast.makeText(Help.this,"Successfully updated",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
