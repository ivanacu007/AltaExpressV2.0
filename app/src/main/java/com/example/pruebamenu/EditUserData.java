package com.example.pruebamenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class EditUserData extends AppCompatActivity {
    private LinearLayout linearPersonD, linearDir;
    private CollectionReference docRef;
    private int option;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userID, userIDir, newName, newPhone, newDir, newCP, locationID, locationName;
    private EditText edtNameEdit, edtPhoneEdit, edtDirEdit, edtCPEdit, edtEmail;
    private Button btnSavePersonD, btnSaveDir;
    private ProgressBar pgb;
    private Spinner spn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_data);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        linearPersonD = findViewById(R.id.layoutPersonData);
        linearDir = findViewById(R.id.layoutDir);
        edtNameEdit = findViewById(R.id.edtUserNameAccountEdit);
        edtPhoneEdit = findViewById(R.id.edtUserPhoneAccountEdit);
        edtDirEdit = findViewById(R.id.edtDirecUserEdit);
        edtCPEdit = findViewById(R.id.edtusercpEdit);
        edtEmail = findViewById(R.id.edtUserEmailAccountE);
        btnSavePersonD = findViewById(R.id.btnSavePerson);
        btnSaveDir = findViewById(R.id.btnSaveDir);
        linearPersonD.setVisibility(View.GONE);
        linearDir.setVisibility(View.GONE);
        pgb = findViewById(R.id.pgE);
        spn = findViewById(R.id.spinnerLocationEdit);
        pgb.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        option = intent.getIntExtra("OP", 0);
        userID = mAuth.getCurrentUser().getUid();
        //Toast.makeText(this, String.valueOf(option), Toast.LENGTH_SHORT).show();
        showOption(option);
        getUserData(userID);

        btnSavePersonD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
        btnSaveDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDir();
            }
        });

        //this.setTitle(R.string.mydata);
    }

    public void showOption(int op) {
        if (op == 1) {
            this.setTitle("Datos personales");
            linearPersonD.setVisibility(View.VISIBLE);
            linearDir.setVisibility(View.GONE);
        }
        if (op == 2) {
            this.setTitle("Datos de envío");
            linearDir.setVisibility(View.VISIBLE);
            linearPersonD.setVisibility(View.GONE);
        }
    }

    private void getUserData(String id) {
        userIDir = id;
        db.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            pgb.setVisibility(View.GONE);
                            edtNameEdit.setText(documentSnapshot.getString("name"));
                            edtEmail.setText(documentSnapshot.getString("email"));
                            edtPhoneEdit.setText(String.valueOf(documentSnapshot.getString("phone")));
                            docRef = db.collection("users").document(userIDir).collection("direcciones");
                            showOption(option);
                            getPredetDirection(docRef);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pgb.setVisibility(View.GONE);
                Toast.makeText(EditUserData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPredetDirection(CollectionReference colDir) {
        colDir.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            boolean predet = doc.getBoolean("predet");
                            if (predet) {
                                pgb.setVisibility(View.GONE);
                                int pos = Integer.parseInt(doc.getString("id"));
                                edtDirEdit.setText(doc.getString("direc"));
                                edtCPEdit.setText(doc.getString("cp"));
                                spn.setSelection(pos);

                                //edtUserLocation.setText(doc.getString("ciudad"));
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pgb.setVisibility(View.GONE);
                Toast.makeText(EditUserData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateData() {
        newName = edtNameEdit.getText().toString().trim();
        newPhone = edtPhoneEdit.getText().toString().trim();
        DocumentReference user = db.collection("users").document(userID);
        user.update("name", newName);
        user.update("phone", newPhone)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditUserData.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditUserData.this, "Ocurrió un error, intentalo más tarde", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUserDir() {
        newDir = edtDirEdit.getText().toString().trim();
        newCP = edtCPEdit.getText().toString().trim();
        locationID = String.valueOf(spn.getSelectedItemId());
        locationName = spn.getSelectedItem().toString();
        final CollectionReference direc = db.collection("users").document(userID).collection("direcciones");
        direc.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            boolean predet = doc.getBoolean("predet");
                            if (predet) {
                                String document = doc.getId();
                                DocumentReference direction = db.collection("users").document(userID).collection("direcciones")
                                        .document(document);
                                direction.update("direc", newDir);
                                direction.update("ciudad", locationName);
                                direction.update("cp", newCP);
                                direction.update("id", locationID)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(EditUserData.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                                                onBackPressed();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditUserData.this, "Ocurrió un error, intentalo más tarde", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditUserData.this, "Ocurrió un error, intentalo más tarde", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}