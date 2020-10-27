package com.example.pruebamenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pruebamenu.AdapterPedidosUser.PedidosUserAdapter;
import com.example.pruebamenu.Models.pedidoModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyOrders extends AppCompatActivity {
    private RecyclerView mRecy;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<pedidoModel> modelList = new ArrayList<>();
    private PedidosUserAdapter adapter;
    //private Date dateOrder = new Date();
    private String userID, userPID, pedidoID;
    private String aux1 = "", aux2 = "", aux3 = "", dateOr = "01/01/2020";
    private boolean status = false, expanded = false;
    private ProgressBar progressBar;
    private int checkCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.myorders);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressOrders);
        mRecy = findViewById(R.id.myRecyUser);
        mRecy.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        initRecyclerView();
        getData();
        checkCompra();
    }

    public void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecy.setLayoutManager(gridLayoutManager);
    }

    public void getData() {
        userID = mAuth.getCurrentUser().getUid();
        db.collection("pedidos")
                .orderBy("fecha_orden", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    userPID = doc.getString("user_id");
                    Timestamp dateOrder = doc.getTimestamp("fecha_orden");
                    Date fecha = dateOrder.toDate();
                    String fechaaux = getDate(fecha);
                    //dateOr = dateOrder.toString();
                    //Long t =Long.parseLong(dateOr);
                    //String formatDate = getDate(t);
                    if (userID.equals(userPID)) {
                        pedidoModel model = new pedidoModel(
                                doc.getString("id"),
                                doc.getString("name_neg"),
                                fechaaux,
                                aux2,
                                status,
                                expanded,
                                doc.getDouble("pago_total")
                        );
                        modelList.add(model);
                        adapter = new PedidosUserAdapter(MyOrders.this, modelList);
                        mRecy.setAdapter(adapter);
                    }
                }
                mRecy.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyOrders.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkCompra(){
        SharedPreferences sharedPreferences = getSharedPreferences("compraPref", MODE_PRIVATE);
        int compra = sharedPreferences.getInt("compra", 0);
        if(compra == 1){
            checkCompra = compra;
        }
    }

    private String getDate(Date date) {
        String dateFormated = DateFormat.format("dd/MM/yyyy", date).toString();
        return dateFormated;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_order_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mandados:
                Toast.makeText(this, "Mis mandados", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        if(checkCompra == 1){
            startActivity(new Intent(getApplicationContext(), Dashboard.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
            super.onBackPressed();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}