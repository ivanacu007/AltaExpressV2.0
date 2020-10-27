package com.example.pruebamenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class MandadoActivity extends AppCompatActivity {
    private TextView txaviso, txvcount, showHide;
    private CardView cardEx;
    private LinearLayout linearEx;
    private ImageView imgEx;
    private EditText edtmandado;
    private boolean isExpanded = false;
    private int option;
    private FloatingActionButton btnContinue;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandado);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.mandado);
        txaviso = findViewById(R.id.txaviso);
        cardEx = findViewById(R.id.cardExp);
        linearEx = findViewById(R.id.layotExpand);
        imgEx = findViewById(R.id.imgEx);
        showHide = findViewById(R.id.showhidetext);
        txvcount = findViewById(R.id.countlineMandado);
        edtmandado = findViewById(R.id.edtMandado);
        btnContinue = findViewById(R.id.floatingActionButtonM);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            txaviso.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
        cardEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardExpanded();
            }
        });
        edtmandado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = edtmandado.length();
                String convert = String.valueOf(length);
                txvcount.setText(convert + "/1200");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = edtmandado.getText().toString().trim();
                validateUser(v);
            }
        });
    }

    public void validateUser(View v) {
        if (edtmandado.length()<1){
            hideKeyboard(v);
            Toast.makeText(this, "No puedes enviar una lista vacía", Toast.LENGTH_SHORT).show();
            return;
        }
        saveMandadoContent();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(MandadoActivity.this, CompleteMandado.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
        } else {
            option = 2;
            Intent i = new Intent(MandadoActivity.this, DeliveryData.class);
            i.putExtra("OPTION", option);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        }
    }

    public void saveMandadoContent(){
        SharedPreferences sharedPreferences = getSharedPreferences("mandadocontent", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("content", content);
        editor.apply();
    }

    public void cardExpanded(){
        isExpanded = !isExpanded;
        if (isExpanded) {
            //Picasso.get().load(R.drawable.ic_up).into(imgEx);
            imgEx.setImageDrawable(null);
            imgEx.setBackgroundResource(R.drawable.ic_up);
            linearEx.setVisibility(View.VISIBLE);
            showHide.setText(getResources().getString(R.string.hidetext));
        }
        else {
            imgEx.setImageDrawable(null);
            imgEx.setBackgroundResource(R.drawable.ic_down);
            linearEx.setVisibility(View.GONE);
            showHide.setText(getResources().getString(R.string.showtext));
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