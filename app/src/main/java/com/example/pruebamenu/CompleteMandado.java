package com.example.pruebamenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebamenu.Models.pedidosIdModel;
import com.example.pruebamenu.Models.userModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompleteMandado extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference collectionReference;
    private List<pedidosIdModel> idmodel = new ArrayList<>();
    private List<userModel> userModelList;
    private boolean estado = false;
    private double tarifa;
    private boolean predet;
    private boolean isRegistered;
    private String idUser, mandado_contenido, code, pedidoID, cdID, user_direc, ciudad, userPhone, userName;
    private CardView cardEdit;
    private TextView content, name, direc, ubicacion, costo, phone;
    private ProgressBar pgb1, pgb2;
    private LinearLayout linearCardEnvio;
    private Button saveMandado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_mandado);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.completarmandado);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        content = findViewById(R.id.contenido);
        cardEdit = findViewById(R.id.cardEditDir);
        name = findViewById(R.id.user_name);
        direc = findViewById(R.id.mandadodirec);
        ubicacion = findViewById(R.id.mandadocity);
        costo = findViewById(R.id.mandadotarifa);
        phone = findViewById(R.id.user_phone);
        pgb1 = findViewById(R.id.progressBarCard1);
        pgb2 = findViewById(R.id.progressBarCard2);
        saveMandado = findViewById(R.id.btnSaveMandado);
        linearCardEnvio = findViewById(R.id.linearMandado2);
        pgb2.setVisibility(View.VISIBLE);
        linearCardEnvio.setVisibility(View.GONE);

        cardEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRegistered){
                    int option = 2;
                    Intent i = new Intent(CompleteMandado.this, EditUserData.class);
                    i.putExtra("OP", option);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                }
                else{
                 onBackPressed();
                }
            }
        });

        saveMandado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveData();
            }
        });
        getMandadoContent();
        getUserData();
        getMandadosID();
    }

    public boolean validateUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            isRegistered = true;
            idUser = mAuth.getCurrentUser().getUid();
            return true;
        } else {
            isRegistered = false;
            idUser = "nouser";
            return false;
        }
    }

    public void getUserData(){
        if(validateUser()){
            db.collection("users").document(idUser).collection("direcciones")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        predet = doc.getBoolean("predet");
                        if (predet) {
                            cdID = doc.getString("id");
                            user_direc = doc.getString("direc");
                            ciudad = doc.getString("ciudad");
                            direc.setText(user_direc);
                            ubicacion.setText(ciudad);
                            getDirectionData(cdID);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CompleteMandado.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            getRegisteredUserData(idUser);
        }
        else{
            getNoUserSavedData();
        }
    }

    public void getRegisteredUserData(String id){
        db.collection("users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userName = documentSnapshot.getString("name");
                    userPhone = documentSnapshot.getString("phone");
                    name.setText(userName);
                    phone.setText(userPhone);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CompleteMandado.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveData() {
        validateID();
        //String
        Map<String, Object> mandado = new HashMap<>();
        mandado.put("id", pedidoID);
        mandado.put("user_id", idUser);
        mandado.put("user_name", userName);
        mandado.put("user_phone", userPhone);
        mandado.put("fecha_orden", new Timestamp(new Date()));
        mandado.put("status", estado);
        mandado.put("direccion", user_direc);
        mandado.put("ciudad", ciudad);
        mandado.put("contenido", content);

        db.collection("mandados").document(pedidoID)
                .set(mandado)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CompleteMandado.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getMandadosID() {
        db.collection("mandados").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    pedidosIdModel model = new pedidosIdModel(
                            doc.getString("id")
                    );
                    idmodel.add(model);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CompleteMandado.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void validateID() {
        code = String.valueOf(genCode());
        if (idmodel.size() > 0) {
            for (int i = 0; i < idmodel.size(); i++) {
                String aux = idmodel.get(i).getPedidoID();
                if (code.equals(aux) || code.length() < 5) {
                    code = String.valueOf(genCode());
                } else {
                    pedidoID = code;
                }
            }
        } else {
            if (code.length() < 5) {
                code = String.valueOf(genCode());
            } else {
                pedidoID = code;
            }
        }
    }

    public void getNoUserSavedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("userdatapref", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("userdata", null);
        Type type = new TypeToken<ArrayList<userModel>>() {
        }.getType();
        userModelList = gson.fromJson(json, type);

        if (userModelList == null) {
            userModelList = new ArrayList<>();
        }
        for (int i = 0; i < userModelList.size(); i++) {
            cdID = userModelList.get(i).getCityID();
            user_direc = userModelList.get(i).getUserDir();
            ciudad = userModelList.get(i).getUserCity();
            userPhone = userModelList.get(i).getUserPhone();
            userName = userModelList.get(i).getUserName();
        }
        direc.setText(user_direc);
        ubicacion.setText(ciudad);
        name.setText(userName);
        phone.setText(userPhone);
        getDirectionData(cdID);
        //Toast.makeText(this, String.valueOf(userModelList.size()), Toast.LENGTH_SHORT).show();
        //getTotal();
    }

    public void getDirectionData(final String id) {
        final int cID = 99;
        db.collection("tarifas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    int tID = Integer.parseInt(doc.getString("id"));
                    if (cID == tID) {
                        tarifa = doc.getDouble("tarifa");
                        costo.setText(String.format("$%.2f", tarifa));
                        pgb2.setVisibility(View.GONE);
                        linearCardEnvio.setVisibility(View.VISIBLE);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CompleteMandado.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

 /*   public void finishOrder() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), OrderComplete.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                pgbSave.setVisibility(View.INVISIBLE);
            }
        }, 3000);
    }*/

    public static int genCode() {
        return (int) (100000 * Math.random());
    }

    public void getMandadoContent() {
        SharedPreferences sharedPreferences = getSharedPreferences("mandadocontent", MODE_PRIVATE);
        String mcontent = sharedPreferences.getString("content", null);
        content.setText(mcontent);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserData();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}