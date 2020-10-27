package com.example.pruebamenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pruebamenu.Models.ciudadesModel;
import com.example.pruebamenu.Models.userModel;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryData extends AppCompatActivity {
    private EditText edtname, edtemail, edtphone, edtdir, edtcp;
    private TextInputEditText edtpass;
    private Spinner spnCity;
    private Button btnsave;
    private CheckBox checkData;
    private LinearLayout linearData;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference documentReference, documentReferenceDirec;
    private boolean isExpanded = false, isChekced = false, saveData;
    private List<ciudadesModel> cdmodel = new ArrayList<>();
    private ArrayList<String> ciudadesList = new ArrayList<>();
    private List<userModel> userModel = new ArrayList<>();
    private String email, password, phone, name, userID, direc, locationID, locationName, CP;
    private ProgressBar progressBar;
    private boolean predet = true;
    private int option;
    private boolean status = true;
    private boolean savedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_data);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.deliverytext);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        option = intent.getIntExtra("OPTION", 0);
        edtname = findViewById(R.id.edtRegName);
        edtemail = findViewById(R.id.edtRegEmail);
        edtpass = findViewById(R.id.edtRegPass);
        edtdir = findViewById(R.id.edtRegDir);
        edtphone = findViewById(R.id.edtRegPhone);
        edtcp = findViewById(R.id.edtRegCP2);
        btnsave = findViewById(R.id.btnSaveAndContinue);
        spnCity = findViewById(R.id.spinnerRegCiudad);
        progressBar = findViewById(R.id.progressnewreg);
        linearData = findViewById(R.id.linearDatosRegistro);
        checkData = findViewById(R.id.checkSaveData);
        checkData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                showMore();
            }
        });
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                validateInputs(isChekced);
            }
        });
        getCityData();
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
                Toast.makeText(DeliveryData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        spnCity.setAdapter(adapter);
    }

    public void showMore() {
        isExpanded = !isExpanded;
        if (isExpanded) {
            isChekced = true;
            linearData.setVisibility(View.VISIBLE);
            btnsave.setText(getResources().getString(R.string.guardar_y_continuar));
            Toast.makeText(this, "Completa los datos faltantes", Toast.LENGTH_SHORT).show();
        } else {
            isChekced = false;
            btnsave.setText(getResources().getString(R.string.continuar));
            linearData.setVisibility(View.GONE);
        }
    }

    public void validateInputs(boolean ischeck) {
        saveData = ischeck;
        email = edtemail.getText().toString().trim();
        password = edtpass.getText().toString().trim();
        name = edtname.getText().toString().trim();
        phone = edtphone.getText().toString().trim();
        direc = edtdir.getText().toString().trim();
        CP = edtcp.getText().toString().trim();
        locationID = String.valueOf(spnCity.getSelectedItemId());
        locationName = spnCity.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            edtname.setError("Campo requerido");
            errorToast();
            return;
        }

        if (ischeck) {
            if (TextUtils.isEmpty(email)) {
                edtemail.setError("Campo requerido");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                edtpass.setError("Campo requerido");
                errorToast();
                return;
            }
            if (password.length() < 8) {
                edtpass.setError("La contraseña debe ser mínimo de 8 caracteres");
                errorToast();
                return;
            }
        }

        if (phone.length() < 10) {
            edtphone.setError("Ingrese un número a 10 dígitos");
            errorToast();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            edtphone.setError("Campo requerido");
            errorToast();
            return;
        }

        if (TextUtils.isEmpty(direc)) {
            edtdir.setError("Campo requerido");
            errorToast();
            return;
        }
 /*       if (TextUtils.isEmpty(CP)) {
            edtcp.setError("Campo requerido");
            errorToast();
            return;
        }*/
        //Toast.makeText(this, "Todo bien", Toast.LENGTH_SHORT).show();
        saveData(saveData);
    }

    public void errorToast(){
        Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
    }

    public void saveData(boolean savedata) {
        if (savedata) {
            btnsave.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        userID = mAuth.getCurrentUser().getUid();
                        documentReference = db.collection("users").document(userID);
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("id", userID);
                        userData.put("name", name);
                        userData.put("phone", phone);
                        userData.put("email", email);
                        createUsersFirestore(documentReference, userData);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    btnsave.setEnabled(true);
                    Toast.makeText(DeliveryData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            saveNoUserData();
            //Toast.makeText(this, "Todavia no lo programo", Toast.LENGTH_SHORT).show();
        }
    }

    public void createUsersFirestore(DocumentReference docRef, Map<String, Object> data) {
        docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DeliveryData.this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                documentReferenceDirec = db.collection("users").document(userID)
                        .collection("direcciones").document(locationID);
                Map<String, Object> userDirec = new HashMap<>();
                userDirec.put("id", locationID);
                userDirec.put("direc", direc);
                userDirec.put("ciudad", locationName);
                userDirec.put("predet", predet);
                userDirec.put("cp", CP);
                saveUserDirection(documentReferenceDirec, userDirec);
                btnsave.setEnabled(true);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                btnsave.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeliveryData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveUserDirection(DocumentReference docRefDir, Map<String, Object> data) {
        docRefDir.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                savedUser = true;
                initActivityOption(savedUser);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                btnsave.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DeliveryData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveNoUserData() {
        clearSharedPreferencesUser();
        savedUser = false;
        String userid = "nouser";
        userModel model = new userModel(
                userid,
                name,
                phone,
                direc,
                locationName,
                CP,
                locationID,
                status
                );
        userModel.add(model);
        SharedPreferences sharedPreferences = getSharedPreferences("userdatapref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userModel);
        editor.putString("userdata", json);
        editor.apply();
        initActivityOption(savedUser);
    }

    public void clearSharedPreferencesUser() {
        userModel.clear();
    }

    public void initActivityOption(boolean savedUser){
        if (option == 2) {
            if(savedUser){
                startActivity(new Intent(DeliveryData.this, CompleteMandado.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
            else{
                startActivity(new Intent(DeliveryData.this, CompleteMandado.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        } else {
            if(savedUser){
                startActivity(new Intent(DeliveryData.this, FinishOrder.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
            else{
                startActivity(new Intent(DeliveryData.this, FinishOrder.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}