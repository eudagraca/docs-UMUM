package com.example.umum;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UploadPDF {

    private String autor;
    private String titulo;
    private String url;
    private String userID;
    private String UploadPDFKey;
    private String curso;
    private String path;

    public UploadPDF() {
    }

    UploadPDF(String autor, String titulo, String url, String userID, String curso) {
        this.autor = autor;
        this.titulo = titulo;
        this.url = url;
        this.userID = userID;
        this.curso = curso;
    }

    public UploadPDF(String autor, String titulo, String uid, String curso) {
        this.autor = autor;
        this.titulo = titulo;
        this.curso = curso;
        this.userID = uid;
    }

    @NonNull
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("autor", getAutor());
        result.put("curso", getCurso());
        result.put("titulo", getTitulo());
        result.put("userID", getUserID());

        return result;
    }


    private String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUploadPDFKey() {
        return UploadPDFKey;
    }

    public void setUploadPDFKey(String id) {
        this.UploadPDFKey = id;
    }

    private String getCurso() {
        return curso;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
