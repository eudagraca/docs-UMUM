package com.example.umum.fragments;


import android.content.Context;
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
import com.example.umum.MyAdapter;
import com.example.umum.PdfView;
import com.example.umum.R;
import com.example.umum.UploadPDF;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class CagFragment extends Fragment {

    private List<UploadPDF> files;
    private RecyclerView rv;
    @Nullable
    private MyAdapter adapter;


    public CagFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context mcontext = getContext();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        SearchView searchView = view.findViewById(R.id.sv_pdf);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

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
        FloatingActionButton cg = view.findViewById(R.id.addEit);
        cg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFileActivity.class);
            intent.putExtra("curso", "cag");
            startActivity(intent);
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("CAG");

        rv = view.findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        files = new ArrayList<>();


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Cag")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                                UploadPDF pdf = new UploadPDF();
                                pdf.setAutor(dataSnap.child("autor").getValue(String.class));
                                pdf.setTitulo(dataSnap.child("titulo").getValue(String.class));
                                pdf.setUrl(dataSnap.child("url").getValue(String.class));
                                pdf.setUserID(dataSnap.child("userID").getValue(String.class));
                                pdf.setUploadPDFKey(dataSnap.getKey());
                                pdf.setPath(dataSnap.child("curso").getValue(String.class));
                                files.add(pdf);
                            }

                            adapter = new MyAdapter(getContext(), files);
                            rv.setAdapter(adapter);

                            adapter.setOnItemClickListener(position -> {
                                Intent intent = new Intent(getContext(), PdfView.class);
                                intent.putExtra("curso", files.get(position).getPath());
                                intent.putExtra("id", files.get(position).getUploadPDFKey());
                                startActivity(intent);
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return view;

    }

}