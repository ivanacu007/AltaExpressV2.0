package com.example.pruebamenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebamenu.AdapterResFinishOrder.AdapterResFinishOrder;
import com.example.pruebamenu.Models.cartModel;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinishOrder extends AppCompatActivity {
    private List<cartModel> cartModelList;
    private List<pedidosIdModel> idmodel = new ArrayList<>();
    private List<userModel> userModelList;
    private TextView txvTotal, txvCantidad, txvEnvio;
    private Button btnFinish;
    private String idUser, idNeg, nameNeg, idNegServer, code, pedidoID, itemID, cdID, direc, ciudad, userPhone, userName;
    private int totalArt = 0;
    private double pagoTotal = 0, tarifa = 0;
    private boolean estado = false;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference collectionReference;
    private DocumentReference docRef;
    private RecyclerView mRecy;
    private AdapterResFinishOrder adapter;
    private ImageView imgEx;
    private CardView cardRes;
    private LinearLayout layoutEx, linearcontent;
    private ProgressBar pgf, pgbF;
    private boolean isExpanded;
    private boolean predet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_order);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.pagar);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //idNeg = new Intent().getExtras().getString("idN");
        pgbF = findViewById(R.id.progressBarF);
        linearcontent = findViewById(R.id.linearcontent);
        pgbF.setVisibility(View.VISIBLE);
        linearcontent.setVisibility(View.GONE);
        cardRes = findViewById(R.id.cadrResFinish);
        mRecy = findViewById(R.id.recResFinish);
        txvEnvio = findViewById(R.id.txvEnvio);
        txvCantidad = findViewById(R.id.txvCantidad);
        txvTotal = findViewById(R.id.txvTotalPagar);
        btnFinish = findViewById(R.id.btnFinish);
        layoutEx = findViewById(R.id.linearRes);
        imgEx = findViewById(R.id.imgExpand);
        //pgf = findViewById(R.id.progressFinish);
        cardRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandResume();
            }
        });
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pgf.setVisibility(View.VISIBLE);
                saveData();
            }
        });
        getList();
        getNegocioData();
        getResume();
        getPedidosID();
    }

/*    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.layout_dialog_warning,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText(getResources().getString(R.string.titledialog));
        ((TextView) view.findViewById(R.id.textMessage)).setText(getResources().getString(R.string.dialogmessage));
    }*/

    public void expandResume() {
        isExpanded = !isExpanded;
        if (isExpanded) {
            //Picasso.get().load(R.drawable.ic_up).into(imgEx);
            imgEx.setImageDrawable(null);
            imgEx.setBackgroundResource(R.drawable.ic_up);
            layoutEx.setVisibility(View.VISIBLE);
        } else {
            imgEx.setImageDrawable(null);
            imgEx.setScaleType(ImageView.ScaleType.CENTER);
            imgEx.setBackgroundResource(R.drawable.ic_down);
            layoutEx.setVisibility(View.GONE);
        }
    }

    public boolean validateUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            idUser = mAuth.getCurrentUser().getUid();
            //pgbF.setVisibility(View.GONE);
            return true;
        } else {
            idUser = "nouser";
            //showDialog();
            return false;
        }
    }

    public void getPedidosID() {
        db.collection("pedidos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                Toast.makeText(FinishOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void validateID() {
        code = String.valueOf(genCode());
        if (idmodel.size() > 0){
            for (int i = 0; i < idmodel.size(); i++) {
                String aux = idmodel.get(i).getPedidoID();
                if (code.equals(aux) || code.length() < 5) {
                    code = String.valueOf(genCode());
                } else {
                    pedidoID = code;
                }
            }
        }
        else{
            if (code.length() < 5) {
                code = String.valueOf(genCode());
            }
            else{
                pedidoID = code;
            }
        }

    }

    public void finishOrder() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), OrderComplete.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                //pgf.setVisibility(View.INVISIBLE);
            }
        }, 3000);
    }

    public void getResume() {
        //saveCartData();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecy.setLayoutManager(gridLayoutManager);
        adapter = new AdapterResFinishOrder(FinishOrder.this, cartModelList);
        mRecy.setAdapter(adapter);
    }

    public void saveData() {
        validateID();
        //String
            Map<String, Object> order = new HashMap<>();
        order.put("id", pedidoID);
        order.put("user_id", idUser);
        order.put("user_name", userName);
        order.put("user_phone", userPhone);
        order.put("pago_total", pagoTotal);
        order.put("fecha_orden", new Timestamp(new Date()));
        order.put("status", estado);
        order.put("id_neg", idNeg);
        order.put("name_neg", nameNeg);
        order.put("direccion", direc);
        order.put("ciudad", ciudad);

        db.collection("pedidos").document(pedidoID)
                .set(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (int i = 0; i < cartModelList.size(); i++) {
                            itemID = cartModelList.get(i).getCart_itemID();
                            String name = cartModelList.get(i).getCart_itemName();
                            String ingred = cartModelList.get(i).getCart_itemDesc();
                            double precio = cartModelList.get(i).getCart_itemPrice();
                            int cantidad = cartModelList.get(i).getItem_cantidadTotal();
                            Map<String, Object> items = new HashMap<>();
                            items.put("item_id", itemID);
                            items.put("item_name", name);
                            items.put("item_ingred", ingred);
                            items.put("item_precio_base", precio);
                            items.put("item_cantidad", cantidad);
                            collectionReference = db.document("pedidos/" + pedidoID).collection("items");
                            saveSubItems(collectionReference, items, itemID);
                        }
                        //Toast.makeText(FinishOrder.this, "Todo good", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FinishOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void saveSubItems(CollectionReference Ref, Map<String, Object> items, String itemID) {
        Ref.document(itemID)
                .set(items)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finishOrder();
                        //Toast.makeText(FinishOrder.this, "Todo good x2", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FinishOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void calculoTotal() {
        for (int i = 0; i < cartModelList.size(); i++) {
            totalArt += cartModelList.get(i).getItem_cantidadTotal();
            pagoTotal += cartModelList.get(i).getCartItem_newPrice();
            txvCantidad.setText(String.valueOf(totalArt));
            //Toast.makeText(this, String.valueOf(totalArt), Toast.LENGTH_SHORT).show();
        }
        if(validateUser()){
            db.collection("users").document(idUser).collection("direcciones")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        predet = doc.getBoolean("predet");
                        if (predet) {
                            cdID = doc.getString("id");
                            direc = doc.getString("direc");
                            ciudad = doc.getString("ciudad");
                            getDirectionData(cdID);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FinishOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FinishOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDirectionData(final String id) {
        final int cID = Integer.parseInt(id);
        db.collection("tarifas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    int tID = Integer.parseInt(doc.getString("id"));
                    if (cID == tID) {
                        tarifa = doc.getDouble("tarifa");
                        pagoTotal = pagoTotal + tarifa;
                        txvEnvio.setText(String.format("$%.2f", tarifa));
                        txvTotal.setText(String.format("$%.2f", pagoTotal));
                        pgbF.setVisibility(View.GONE);
                        linearcontent.setVisibility(View.VISIBLE);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FinishOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getList() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("mylist", null);
        Type type = new TypeToken<ArrayList<cartModel>>() {
        }.getType();
        cartModelList = gson.fromJson(json, type);
        if (cartModelList == null) {
            cartModelList = new ArrayList<>();
        }
        calculoTotal();
        //getResume();
    }

    public void getNegocioData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        final String nID = sharedPreferences.getString("negocioid", null);
        db.collection("dbfood").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            idNegServer = doc.getString("id");
                            if (nID.equals(idNegServer)) {
                                idNeg = doc.getString("id");
                                nameNeg = doc.getString("name");
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FinishOrder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        for (int i = 0; i < userModelList.size(); i++){
            cdID = userModelList.get(i).getCityID();
            direc = userModelList.get(i).getUserDir();
            ciudad = userModelList.get(i).getUserCity();
            userPhone = userModelList.get(i).getUserPhone();
            userName = userModelList.get(i).getUserName();
        }
        getDirectionData(cdID);
        Toast.makeText(this, String.valueOf(userModelList.size()), Toast.LENGTH_SHORT).show();
        //getTotal();
    }


    public static int genCode() {
        return (int) (100000 * Math.random());
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
