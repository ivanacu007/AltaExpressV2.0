package com.example.pruebamenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserAccount extends AppCompatActivity {
    private EditText edtName, edtEmail, edtPhone, edtDirec, edtUserCP, edtUserLocation;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userID, userIDir;
    private CollectionReference docRef;
    private CardView cardPersonData, cardDirections;
    private int option = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.mydata);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        edtName = findViewById(R.id.edtUserNameAccount);
        edtEmail = findViewById(R.id.edtUserEmailAccount);
        edtPhone = findViewById(R.id.edtUserPhoneAccount);
        edtDirec = findViewById(R.id.edtDirecUser);
        edtUserCP = findViewById(R.id.edtusercp);
        cardPersonData = findViewById(R.id.cardPersonD);
        cardDirections = findViewById(R.id.cardDir);
        edtUserLocation = findViewById(R.id.edtuserlocation);
        userID = mAuth.getCurrentUser().getUid();
        getUserData(userID);

        cardPersonData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option = 1;
                Intent i = new Intent(UserAccount.this, EditUserData.class);
                i.putExtra("OP", option);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });
        cardDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option = 2;
                Intent i = new Intent(UserAccount.this, EditUserData.class);
                i.putExtra("OP", option);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });
    }

    private void getUserData(String id) {
        userIDir = id;
        db.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            edtName.setText(documentSnapshot.getString("name"));
                            edtEmail.setText(documentSnapshot.getString("email"));
                            edtPhone.setText(String.valueOf(documentSnapshot.getString("phone")));
                            docRef = db.collection("users").document(userIDir).collection("direcciones");
                            getPredetDirection(docRef);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserAccount.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPredetDirection(CollectionReference colDir){
        colDir.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            boolean predet = doc.getBoolean("predet");
                            if(predet){
                                edtDirec.setText(doc.getString("direc"));
                                edtUserCP.setText(doc.getString("cp"));
                                edtUserLocation.setText(doc.getString("ciudad"));
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserAccount.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserData(userID);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}