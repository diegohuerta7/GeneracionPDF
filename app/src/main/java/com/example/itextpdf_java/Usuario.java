package com.example.itextpdf_java;

public class Usuario {
    String usuario;
    String nombre;
    String email;

    public Usuario(String usuario, String nombre, String email) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
