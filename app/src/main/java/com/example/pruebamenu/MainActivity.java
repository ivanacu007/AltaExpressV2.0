package com.example.pruebamenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pruebamenu.AdapterList.ListCustomAdapter;
import com.example.pruebamenu.Models.listModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<listModel> modelList = new ArrayList<>();
    private ListCustomAdapter adapter;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private DialogFragment dialogFragment, loadingSC;
    private int count = 0;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.estab_list);
        //Splash();
        mRecyclerView = findViewById(R.id.rcvList);
        progressBar = findViewById(R.id.progressList);
        mRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("dbfood");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getData(collectionReference);

    }

    private void getData(CollectionReference refColl) {
        mRecyclerView.removeAllViews();
        refColl.orderBy("name").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            int status = doc.getLong("status").intValue();
                            if (status > 0) {
                                listModel model = new listModel(
                                        doc.getString("id"),
                                        doc.getString("name"),
                                        doc.getString("img"),
                                        doc.getLong("status").intValue());
                                //txID.setText(id);
                                modelList.add(model);
                                adapter = new ListCustomAdapter(MainActivity.this, modelList);
                                adapter.notifyDataSetChanged();
                                mRecyclerView.setAdapter(adapter);
                                recyclerViewAnimation(mRecyclerView);
                            } /*else {
                                Toast.makeText(FondasRes.this, "No se encontraron datos disponibles", Toast.LENGTH_SHORT).show();
                            }*/
                        }
                        progressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void recyclerViewAnimation(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LayoutAnimationController layoutAnimationController = AnimationUtils
                .loadLayoutAnimation(context, R.anim.layout_slide_right);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public void LoadingScreen() {
        dialogFragment = LoadingScreen.getInstance();
        dialogFragment.show(getSupportFragmentManager(), "loading screen");
/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                     dialogFragment.dismiss();
            }
        }, 3000);*/
    }

    public void Splash() {
        dialogFragment = WelcomeDisplay.getInstance();
        dialogFragment.show(getSupportFragmentManager(), "welcome screen");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogFragment.dismiss();
                //count = 1;
            }
        }, 2000);
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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
