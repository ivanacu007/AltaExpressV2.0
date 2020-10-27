package com.example.pruebamenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OrderComplete extends AppCompatActivity {
    private ImageButton btnClose;
    private CardView cardPedidos, cardHome;
    private MenuActivity menuActivity = new MenuActivity();
    private OrderResume orderResume = new OrderResume();
    private MainActivity mainActivity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);
//        assert getSupportActionBar() != null;   //null check
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardHome = findViewById(R.id.cardDashboard);
        cardPedidos = findViewById(R.id.cardMisPedidos);
        btnClose = findViewById(R.id.imageButtonClose);
        validateUser();
        cardPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCompraEstatus();
                startActivity(new Intent(getApplicationContext(), MyOrders.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });
        cardHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Dashboard.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Dashboard.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });
    }

    public void validateUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            cardPedidos.setVisibility(View.VISIBLE);
        } else {
            cardPedidos.setVisibility(View.GONE);
        }
    }

    public void saveCompraEstatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("compraPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int compra = 1;
        editor.putInt("compra", compra);
        editor.apply();
    }

//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
}