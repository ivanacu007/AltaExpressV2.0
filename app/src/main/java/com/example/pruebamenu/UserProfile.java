package com.example.pruebamenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfile extends AppCompatActivity {
    private TextView txFullName;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String fullName, userID;
    private CardView cardMyData, cardMyOrders, cardHelp, cardLog;
    private LinearLayout linearUserData;
    private boolean userLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.config);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        txFullName = findViewById(R.id.txusername);
        cardMyOrders = findViewById(R.id.cardMyOrders);
        cardMyData = findViewById(R.id.cardMyData);
        cardHelp = findViewById(R.id.cardHelp);
        cardLog = findViewById(R.id.cardLog);
        linearUserData = findViewById(R.id.linearUserData);
        validateUser();

        cardLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });

        cardMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MyOrders.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });

        cardMyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserAccount.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });
    }

    private void validateUser() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                linearUserData.setVisibility(View.VISIBLE);
                cardLog.setVisibility(View.GONE);
                userID = mAuth.getCurrentUser().getUid();
                getUserData(userID);
                userLog = true;
                this.setTitle(R.string.profile_text);
            }
            else{
                userLog = false;
                linearUserData.setVisibility(View.GONE);
                cardLog.setVisibility(View.VISIBLE);
                //Toast.makeText(this, "No hay usuario", Toast.LENGTH_SHORT).show();
            }
    }

    public void getUserData(String id){
        db.collection("users").document(id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    fullName = documentSnapshot.getString("name");
                    txFullName.setText(fullName);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_account_menu, menu);
        return userLog;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Dashboard.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        validateUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}