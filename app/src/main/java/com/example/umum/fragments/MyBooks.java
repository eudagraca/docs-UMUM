package com.example.umum.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.umum.AddFileActivity;
import com.example.umum.Home;
import com.example.umum.MyAdapter;
import com.example.umum.PdfView;
import com.example.umum.R;
import com.example.umum.UploadPDF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyBooks extends Fragment {

    @Nullable
    private MyAdapter adapter;
    private RecyclerView rv;
    private ArrayList<UploadPDF> files;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;

    public MyBooks() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        SearchView searchView = view.findViewById(R.id.sv_pdf);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        FloatingActionButton fb = view.findViewById(R.id.addEit);
        fb.setVisibility(View.GONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                Objects.requireNonNull(adapter).getFilter().filter(s);
                return false;
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Meus Livros");

        rv = view.findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        files = new ArrayList<>();

        List<String> itens = new ArrayList<>();

        itens.clear();
        itens.add("Cag");
        itens.add("Ced");
        itens.add("Eit");
        itens.add("Teo");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        for (String item : itens) {
            mDatabase.child(item).orderByChild("userID").equalTo(Objects.requireNonNull(user).getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                                    UploadPDF pdf = new UploadPDF();
                                    pdf.setAutor(npsnapshot.child("autor").getValue(String.class));
                                    pdf.setTitulo(npsnapshot.child("titulo").getValue(String.class));
                                    pdf.setUrl(npsnapshot.child("url").getValue(String.class));
                                    pdf.setUserID(npsnapshot.child("userID").getValue(String.class));
                                    pdf.setUploadPDFKey(npsnapshot.getKey());
                                    pdf.setPath(npsnapshot.child("curso").getValue(String.class));
                                    files.add(pdf);
                                }
                                adapter = new MyAdapter(getContext(), files);
                                rv.setAdapter(adapter);

                                adapter.setOnItemClickListener(position -> {
                                    Intent intent = new Intent(getContext(), PdfView.class);
                                    intent.putExtra("id", files.get(position).getUploadPDFKey());
                                    intent.putExtra("curso", files.get(position).getPath());
                                    startActivity(intent);

                                });

                                adapter.setOnItemLongClickListener(position -> alertDialog(files.get(position).getUploadPDFKey()
                                        , files.get(position).getPath()));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        return view;
    }


    private void alertDialog(String codigo, String path) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Oque deseja fazer?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Apagar",
                (dialog, id) -> {

                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Aguarde um instante");
                    progressDialog.show();

                    mDatabase.child(path).child(codigo).removeValue((databaseError, databaseReference) -> {

                        progressDialog.dismiss();
                        new AlertDialog.Builder(getContext())
                                .setMessage("Apagou o livro")
                                .setPositiveButton(android.R.string.yes, (dialog1, which) -> {
                                    Intent intent = new Intent(getContext(), Home.class);
                                    startActivity(intent);
                                })
                                .setIcon(android.R.drawable.alert_dark_frame)
                                .show();
                    });

                });

        builder1.setNegativeButton(
                "Actualizar",
                (dialog, id) -> {

                    Intent intent = new Intent(getContext(), AddFileActivity.class);
                    intent.putExtra("codigo", codigo);
                    intent.putExtra("path", path);
                    startActivity(intent);

                });


        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
