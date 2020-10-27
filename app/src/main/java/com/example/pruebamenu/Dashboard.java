package com.example.pruebamenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pruebamenu.AdapterImageSlider.AdapterImageSlider;
import com.example.pruebamenu.AdapterList.ListCustomAdapter;
import com.example.pruebamenu.Models.listModel;
import com.example.pruebamenu.Models.sliderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends AppCompatActivity {
    private CardView cardx1, cardx2, cardx3, cardx4, cardProf;
    private TextView txtTitle;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userID, userName;
    private CollectionReference collectionReference;
    private List<sliderModel> modelList = new ArrayList<>();
    private AdapterImageSlider adapter;
    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_option);
        //this.setTitle(R.string.dashboard);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        collectionReference = db.collection("slider");
        getSliderItems(collectionReference);
        validateUser();
        saveCompraEstatus();
        viewPager2 = findViewById(R.id.viewPagerSlide);
        txtTitle = findViewById(R.id.txtTitleDash);
        cardx1 = findViewById(R.id.cardV1);
        cardx2 = findViewById(R.id.cardV2);
        cardx3 = findViewById(R.id.cardV3);
        cardx4 = findViewById(R.id.cardV4);
        cardProf = findViewById(R.id.cardProfile);
        txtTitle.setText(R.string.dashboard);
        cardx1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });
        cardx2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MandadoActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });
        cardProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserProfile.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
        });
    }

    public void getSliderItems(CollectionReference ref){
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    sliderModel model = new sliderModel(
                            doc.getString("id"),
                            doc.getString("name"),
                            doc.getString("desc"),
                            doc.getString("img"),
                            doc.getBoolean("activo")
                    );
                    modelList.add(model);
                    adapter = new AdapterImageSlider(modelList, viewPager2);
                    viewPager2.setAdapter(adapter);
                    showAnuncios();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Dashboard.this, "Hubo un error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showAnuncios() {
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(5));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(slideRunnable);
                sliderHandler.postDelayed(slideRunnable, 3000); //duracion del slide 3 sec
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        //pd.dismiss();
    }
    private Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    public void validateUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = mAuth.getCurrentUser().getUid();
            getUserData(userID);
        }
    }
    public void saveCompraEstatus() {
        SharedPreferences settings = getSharedPreferences("compraPref", MODE_PRIVATE);
        settings.edit().clear().apply();
    }

    public void setActionBarTitle(String name) {
        int idx = name.lastIndexOf(' ');
        if (idx == -1) {
            txtTitle.setText("¡Hola " + name + "!");
            //this.setTitle("¡Hola " + name + "!");
        }
        else{
            String firstName = name.substring(0, idx);
            String lastName = name.substring(idx + 1);
            txtTitle.setText("¡Hola " + firstName + "!");
            //this.setTitle("¡Hola " + firstName + "!");
        }
    }

    public void getUserData(String id) {
        db.collection("users").document(id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    userName = documentSnapshot.getString("name");
                    setActionBarTitle(userName);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.user_account:
                //Toast.makeText(this, String.valueOf(x), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), UserProfile.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        validateUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}