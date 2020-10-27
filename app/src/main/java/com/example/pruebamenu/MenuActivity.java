package com.example.pruebamenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import com.example.pruebamenu.AdapterMenu.CustomAdapter;
import com.example.pruebamenu.Models.cartModel;
import com.example.pruebamenu.Models.itemModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private List<itemModel> modelList = new ArrayList<>();
    public ArrayList<cartModel> cartModelList = new ArrayList<>();
    private CustomAdapter adapter;
    private RecyclerView mRecyclerView;
    private String name, id, aux;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference collectionReference;
    private CheckBox myCheckMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myCheckMenu = findViewById(R.id.checkItem);

        Intent intent = getIntent();
        name = intent.getExtras().getString("NAME");
        id = intent.getExtras().getString("ID");
        aux = "dbfood/" + id;
        collectionReference = db.document(aux).collection("menu");
        setActionBarTitle(name);
        mRecyclerView = findViewById(R.id.myC);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getMenuData(collectionReference);
        saveNegocioID();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Galería en desarrollo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fabNExt = findViewById(R.id.fabNext);
        fabNExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cartModelList.size() < 1){
                    Snackbar.make(view, "Debes elegir al menos un producto para continuar", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    saveCartData();
                    Intent i = new Intent(MenuActivity.this, OrderResume.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    //i.putExtra("ID", id);
                    startActivity(i);
                }
            }
        });
    }

    public void getMenuData(CollectionReference refColl) {
        try {
            mRecyclerView.removeAllViews();
            refColl.orderBy("id")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        boolean status = doc.getBoolean("status");
                        if (status) {
                            itemModel model = new itemModel(
                                    doc.getString("id"),
                                    doc.getString("name"),
                                    doc.getString("ingred"),
                                    doc.getDouble("price"),
                                    doc.getBoolean("status"));
                            modelList.add(model);
                            adapter = new CustomAdapter(MenuActivity.this, modelList);
                            mRecyclerView.setAdapter(adapter);
                            recyclerViewAnimation(mRecyclerView);
                        }
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){
            Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void saveNegocioID(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("negocioid", id);
        editor.apply();
    }
    public void saveCartData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartModelList);
        editor.putString("mylist", json);
        editor.apply();
    }

    public void setActionBarTitle(String title) {
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(title);
        toolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.textMedioNegro, null));
        toolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.textMedioNegro, null));
        //toolbarLayout.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD);
        toolbarLayout.setExpandedTitleMarginStart(30);
        toolbarLayout.setExpandedTitleMarginBottom(30);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.quicksand_medium_font);
        toolbarLayout.setCollapsedTitleTypeface(typeface);
        toolbarLayout.setExpandedTitleTypeface(typeface);
    }

    private void recyclerViewAnimation(RecyclerView recyclerView){
        Context context = recyclerView.getContext();
        LayoutAnimationController layoutAnimationController = AnimationUtils
                .loadLayoutAnimation(context, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
