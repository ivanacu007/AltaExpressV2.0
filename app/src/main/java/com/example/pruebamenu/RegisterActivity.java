package com.example.pruebamenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebamenu.Models.ciudadesModel;
import com.example.pruebamenu.Models.pedidosIdModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputEditText edtEmail, edtPass, edtNumber, edtName, edtDir, edtCol;
    private String email, password, number, name, userID, direc, locationID, locationName, colonia;
    private Button btnRegister, txYatengo;
    private DocumentReference documentReference, documentReferenceDirec;
    private ProgressBar progressReg;
    private ImageButton btnBack;
    private Spinner spn;
    private boolean predet = true;
    private List<ciudadesModel> cdmodel = new ArrayList<>();
    private ArrayList<String> ciudadesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnBack = findViewById(R.id.backButton2);
        edtEmail = findViewById(R.id.edtEmailReg);
        edtPass = findViewById(R.id.edtPassReg);
        edtNumber = findViewById(R.id.edtNumberReg);
        edtName = findViewById(R.id.edtNameReg);
        edtDir = findViewById(R.id.edtDir);
        edtCol = findViewById(R.id.edtregcp);
        spn = findViewById(R.id.spinnerLocation);
        txYatengo = findViewById(R.id.txYatengo);
        btnRegister = findViewById(R.id.btnReg);
        progressReg = findViewById(R.id.progressReg);
        progressReg.setVisibility(View.GONE);
        getCityData();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationID = String.valueOf(spn.getSelectedItemId());
                locationName = spn.getSelectedItem().toString();
                //Toast.makeText(RegisterActivity.this, String.valueOf(locationID+" - "+locationName), Toast.LENGTH_SHORT).show();
                validateInputs();
                hideKeyboard(v);
            }
        });
        txYatengo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    public void getCityData() {
        db.collection("tarifas").orderBy("id").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    ciudadesModel model = new ciudadesModel(
                            doc.getString("id"),
                            doc.getString("nombre"),
                            doc.getDouble("tarifa")
                    );
                    cdmodel.add(model);
                }
                llenarSpinner();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void llenarSpinner() {
        for (int i = 0; i < cdmodel.size(); i++) {
            int id = Integer.parseInt(cdmodel.get(i).getId());
            if (id < 99) {
                ciudadesList.add(cdmodel.get(i).getNombre());
            }
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ciudadesList);
        spn.setAdapter(adapter);
    }

    public void validateInputs() {
        email = edtEmail.getText().toString().trim();
        password = edtPass.getText().toString().trim();
        name = edtName.getText().toString().trim();
        number = edtNumber.getText().toString().trim();
        direc = edtDir.getText().toString().trim();
        colonia = edtCol.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            edtPass.setError("Campo requerido");
            return;
        }
        if (password.length() < 8) {
            edtPass.setError("La contraseña debe ser mínimo de 8 caracteres");
            return;
        }
        if (number.length() < 10) {
            edtNumber.setError("Ingrese un número a 10 dígitos");
            return;
        }
        if (TextUtils.isEmpty(number)) {
            edtNumber.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(direc)) {
            edtDir.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(colonia)) {
            edtCol.setError("Campo requerido");
            return;
        }

        singUp();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void singUp() {
        btnRegister.setEnabled(false);
        txYatengo.setEnabled(false);
        progressReg.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userID = mAuth.getCurrentUser().getUid();
                    documentReference = db.collection("users").document(userID);
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", userID);
                    userData.put("name", name);
                    userData.put("phone", number);
                    userData.put("email", email);
                    createUsersFirestore(documentReference, userData);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressReg.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                txYatengo.setEnabled(true);
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createUsersFirestore(DocumentReference docRef, Map<String, Object> data) {
        docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegisterActivity.this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                documentReferenceDirec = db.collection("users").document(userID)
                        .collection("direcciones").document(locationID);
                Map<String, Object> userDirec = new HashMap<>();
                userDirec.put("id", locationID);
                userDirec.put("direc", direc);
                userDirec.put("ciudad", locationName);
                userDirec.put("predet", predet);
                userDirec.put("colonia", colonia);
                saveUserDirection(documentReferenceDirec, userDirec);
                btnRegister.setEnabled(true);
                txYatengo.setEnabled(true);
                progressReg.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                btnRegister.setEnabled(true);
                txYatengo.setEnabled(true);
                progressReg.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveUserDirection(DocumentReference docRefDir, Map<String, Object> data) {
        docRefDir.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(getApplicationContext(), Dashboard.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                btnRegister.setEnabled(true);
                txYatengo.setEnabled(true);
                progressReg.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}