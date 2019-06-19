package com.example.umum;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {

    private EditText nEmail, password;
    private Button btLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth nAuth;
    private Intent homeActivity;
    private FirebaseAuth auth;
    private TextView btregst;

    public LogInActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        btregst = findViewById(R.id.registe_se);

        btregst.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        auth = FirebaseAuth.getInstance();


        nEmail = findViewById(R.id.lemail);
        password = findViewById(R.id.nsenha);
        btLogin = findViewById(R.id.btnLogin);
        loginProgress = findViewById(R.id.loginprogressBar);
        nAuth = FirebaseAuth.getInstance();
        homeActivity = new Intent(this, Home.class);

        loginProgress.setVisibility(View.INVISIBLE);
        btLogin.setOnClickListener(v -> {
            loginProgress.setVisibility(View.VISIBLE);
            btLogin.setVisibility(View.INVISIBLE);
            final String mail = nEmail.getText().toString();
            final String npassword = password.getText().toString();

            if (mail.isEmpty() || npassword.isEmpty()) {

                loginProgress.setVisibility(View.INVISIBLE);
                btLogin.setVisibility(View.VISIBLE);

                showMessage("Preencha os campos");

            } else {
                signIn(mail, npassword);
            }
        });

    }

    private void signIn(@NonNull String mail, @NonNull String password) {

        nAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                loginProgress.setVisibility(View.INVISIBLE);
                btLogin.setVisibility(View.VISIBLE);

                updateUI();

            } else {
                loginProgress.setVisibility(View.INVISIBLE);
                btLogin.setVisibility(View.VISIBLE);
                showMessage(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private void updateUI() {
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, Home.class));
            finish();
        }
    }
}
