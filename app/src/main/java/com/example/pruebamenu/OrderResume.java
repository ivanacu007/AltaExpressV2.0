package com.example.pruebamenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebamenu.AdapterOrderRes.CustomOrderResAdapter;
import com.example.pruebamenu.Models.cartModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OrderResume extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private CustomOrderResAdapter adapter;
    private List<cartModel> cartModelList;
    private EditText edt;
    private TextView tvcount;
    private String id, note;
    private FirebaseAuth mAuth;
    private FloatingActionButton btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_resume);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.resumeOr);
        mAuth = FirebaseAuth.getInstance();
//        Intent intent = getIntent();
//        id = intent.getExtras().getString("ID");
        getCartData();
        btnContinuar = findViewById(R.id.continuarCompra);
        tvcount = findViewById(R.id.countline);
        edt = findViewById(R.id.edtNote);
        note = String.valueOf(edt.getText());
        mRecyclerView = findViewById(R.id.rcvResume);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        adapter = new CustomOrderResAdapter(OrderResume.this, cartModelList);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
        //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser();
            }
        });

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = edt.length();
                String convert = String.valueOf(length);
                tvcount.setText(convert + "/250");
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void validateUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(OrderResume.this, FinishOrder.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        } else {
            startActivity(new Intent(OrderResume.this, DeliveryData.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void getCartData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("mylist", null);
        Type type = new TypeToken<ArrayList<cartModel>>() {
        }.getType();
        cartModelList = gson.fromJson(json, type);

        if (cartModelList == null) {
            cartModelList = new ArrayList<>();
        }
        //getTotal();
    }

    public void saveCartData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartModelList);
        editor.putString("mylist", json);
        editor.apply();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
