package com.example.refrimancia;

public class CreateRecipeResponse {
    private String status;
    private int id_receta;
    private String foto;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getId_receta() { return id_receta; }
    public void setId_receta(int id_receta) { this.id_receta = id_receta; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
