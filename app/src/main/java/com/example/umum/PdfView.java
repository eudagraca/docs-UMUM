package com.example.umum;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfView extends AppCompatActivity {

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        pdfView = findViewById(R.id.pdfView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Detalhes do livro");
        String path = getIntent().getStringExtra("curso");
        String objectID = getIntent().getStringExtra("id");
        ///Init realtime
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);

        databaseReference.child(objectID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String uri = dataSnapshot.child("url").getValue(String.class);
                new ReadPDF().execute(uri);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class ReadPDF extends AsyncTask<String, Void, InputStream> implements OnPageChangeListener, OnLoadCompleteListener
            , OnPageErrorListener {

        private Integer pageNumber = 0;

        @Nullable
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);

            pdfView.fromStream(inputStream)
                    .defaultPage(pageNumber)
                    .onPageChange(this)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .onPageChange(this)
                    .scrollHandle(new DefaultScrollHandle(getBaseContext()))
                    .spacing(0)
                    .pageSnap(true) // snap pages to screen boundaries
                    .pageFling(false)
                    .onPageError(this)
                    .pageFitPolicy(FitPolicy.BOTH)
                    .load();

        }


        @Override
        public void loadComplete(int nbPages) {

        }

        @Override
        public void onPageChanged(int page, int pageCount) {
            pageNumber = page;
        }

        @Override
        public void onPageError(int page, Throwable t) {

        }
    }
}
