package com.example.umum;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {

    private CircularImageView userImg;
    private static final int PReqCode = 1;
    private static final int REQUESCODE = 1;
    @Nullable
    private
    Uri pickedUri;

    private EditText userName, userEmail, userPassword, confPassword;
    private Button btCadastrar;
    private ProgressBar loading;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        userName = findViewById(R.id.edtNome);
        userEmail = findViewById(R.id.edtEmail);
        userPassword = findViewById(R.id.edtSenha);
        confPassword = findViewById(R.id.edtConfSenha);

        btCadastrar = findViewById(R.id.btnRegist);
        loading = findViewById(R.id.progressBar);
        loading.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance(Objects.requireNonNull(FirebaseApp.initializeApp(this)));

        btCadastrar.setOnClickListener(v -> {

            btCadastrar.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);
            final String email = userEmail.getText().toString();
            final String password = userPassword.getText().toString();
            final String password2 = confPassword.getText().toString();
            final String nome = userName.getText().toString();

            if (email.isEmpty() || nome.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                userPassword.setError(getString(R.string.prenche_campos));
                userName.setError(getString(R.string.prenche_campos));
                userEmail.setError(getString(R.string.prenche_campos));
                btCadastrar.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
            }

            if (!password.equals(password2)) {
                userPassword.setError(getString(R.string.conf_password));
                btCadastrar.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);
            }

            if (password.length() < 6) {
                // showMessage("senha minimo 6 digitos");
                userPassword.setError(getString(R.string.minimum_password));
                btCadastrar.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);

            }

            if (password2.length() < 6) {
                // showMessage("senha minimo 6 digitos");
                userPassword.setError(getString(R.string.minimum_password));
                btCadastrar.setVisibility(View.VISIBLE);
                loading.setVisibility(View.INVISIBLE);

            } else {
                CriarConta(nome, email, password2);
            }


        });


        userImg = findViewById(R.id.imageUser);
        userImg.addShadow();


        userImg.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 28) {
                verPedidoPermicao();
            } else {
                openGallery();
            }
        });
    }

    private void CriarConta(final String nome, @NonNull String email, @NonNull final String password2) {

        mAuth.createUserWithEmailAndPassword(email, password2)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showMessage("Conta criada");
                        updateUserInfo(nome, pickedUri, mAuth.getCurrentUser());

                    } else {
                        showMessage("Falha na criaçao da conta" + task.getException().getMessage());
                        btCadastrar.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void updateUserInfo(final String nome, Uri pickedUri, @NonNull final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_photo");
        final StorageReference imageFilePath = mStorage.child(Objects.requireNonNull(pickedUri.getLastPathSegment()));
        imageFilePath.putFile(pickedUri).addOnSuccessListener(taskSnapshot -> imageFilePath.getDownloadUrl().addOnSuccessListener(uri -> {

            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .setPhotoUri(uri).build();

            currentUser.updateProfile(profileUpdate)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            showMessage("Cadastro completo");
                            updateUI();
                        }
                    });
        }));

    }

    private void updateUI() {

        Intent homeActivity = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(homeActivity);
        finish();

    }

    private void showMessage(String messagem) {

        Toast.makeText(getApplicationContext(), messagem, Toast.LENGTH_LONG).show();

    }

    private void openGallery() {

        Intent gallerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        gallerIntent.setType("image/*");
        startActivityForResult(gallerIntent, REQUESCODE);
    }

    private void verPedidoPermicao() {

        if (ContextCompat.checkSelfPermission(CadastroActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CadastroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(CadastroActivity.this, "Permitir acesso", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(CadastroActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else openGallery();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            pickedUri = data.getData();
            userImg.setImageURI(pickedUri);

        }
    }
}

