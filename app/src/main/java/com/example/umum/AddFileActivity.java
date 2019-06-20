package com.example.umum;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class AddFileActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int PICK_PDF_CODE = 2342;

    private EditText mAutor;
    private EditText mTitulo;
    //the firebase objects for storage and database
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    //vars
    private String path;
    @Nullable
    private FirebaseUser user;
    private String curso;
    private String codigo;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cag);

        dialog = new ProgressDialog(this);

        curso = getIntent().getStringExtra("curso");
        codigo = getIntent().getStringExtra("codigo");
        path = getIntent().getStringExtra("path");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Adicionar Documento");
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(getBaseContext(), Home.class));
            finish();
        });
        mStorageReference = FirebaseStorage.getInstance().getReference();
        TextView tvUpload = findViewById(R.id.tvUpload);
        mAutor = findViewById(R.id.c_autor);
        mTitulo = findViewById(R.id.c_titulo);

        //attaching listeners to views
        findViewById(R.id.cag_add).setOnClickListener(this);
        //  findViewById(R.id.textViewUploads).setOnClickListener(this);
        if (curso != null) {
            switch (curso) {
                case "eit":
                    tvUpload.setText("Partilhar de livro de Engenharia Informática");
                    path = "Eit/";
                    break;
                case "cag":
                    path = "Cag/";
                    tvUpload.setText("Partilhar de livro de Ciências de Administração e Gestão");
                    break;
                case "ced":
                    path = "Ced/";
                    tvUpload.setText("Partilhar de livro de Ciências de Educação ");
                    break;
                case "teo":
                    path = "Teo/";
                    tvUpload.setText("Partilhar de livro de Teologia");
                    break;
            }
        } else {
            tvUpload.setText("Actualizar dados do Livro");
            Button button = findViewById(R.id.cag_add);
            button.setText("Atualizar livro");
            //path = spinner.getSelectedItem().toString()+"/";

            if (path != null) {
                mDatabaseReference = FirebaseDatabase.getInstance().getReference(path);
            }
            mDatabaseReference.child(codigo).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mTitulo.setText(dataSnapshot.child("titulo").getValue(String.class));
                            mAutor.setText(dataSnapshot.child("autor").getValue(String.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    }
            );
        }
    }

    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (curso != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                return;
            }
            //creating an intent for file chooser
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Seleccione o ficheiro"), PICK_PDF_CODE);
        } else {

            dialog = new ProgressDialog(this);
            dialog.setMessage("Aguarde um instante");
            dialog.show();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference(path);
            UploadPDF upload = new UploadPDF(mAutor.getText().toString(), mTitulo.getText().toString(), Objects.requireNonNull(user).getUid(), path);

            mDatabaseReference.child(codigo).updateChildren(upload.toMap())
                    .addOnSuccessListener(aVoid -> {

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        new AlertDialog.Builder(this)
                                .setMessage("Actualizou os dados do arquivo")
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    Intent intent = new Intent(AddFileActivity.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();

                    });

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                uploadFile(data.getData());
            } else {
                Toast.makeText(this, "Ficheiro não seleccionado", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //this method is uploading the file
    private void uploadFile(@NonNull Uri data) {
        if (curso != null) {
            final StorageReference sRef = mStorageReference.child(path + System.currentTimeMillis() + ".pdf");
            sRef.putFile(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        new AlertDialog.Builder(this)
                                .setMessage("Arquivo partilhado")
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    Intent intent = new Intent(AddFileActivity.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();

                        sRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            UploadPDF upload = new UploadPDF(mAutor.getText().toString(), mTitulo.getText().toString(), String.valueOf(uri), Objects.requireNonNull(user).getUid(), path);
                            mDatabaseReference = FirebaseDatabase.getInstance().getReference(path);
                            mDatabaseReference.child(Objects.requireNonNull(mDatabaseReference.push().getKey())).setValue(upload);
                        });
                    })
                    .addOnFailureListener(exception -> Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show())
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        dialog = new ProgressDialog(this);
                        dialog.setMessage((int) progress + "% A processar...");
                        dialog.setCancelable(false);
                        dialog.show();
                    });
        }

    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.cag_add) {
            getPDF();
        }
    }
}
