package com.example.refrimancia;

public class LoginResponse {
    private String status;
    private String message;
    private String token;
    private UserData data;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserData getData() { return data; }
    public void setData(UserData data) { this.data = data; }

    public static class UserData {
        private int id_usuario;
        private String nombre_usuario;
        private String imagen_perfil;
        private String nombre_completo;
        private String fecha_nac;
        private String correo_electronico;

        public int getId_usuario() { return id_usuario; }
        public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

        public String getNombre_usuario() { return nombre_usuario; }
        public void setNombre_usuario(String nombre_usuario) { this.nombre_usuario = nombre_usuario; }

        public String getImagen_perfil() { return imagen_perfil; }
        public void setImagen_perfil(String imagen_perfil) { this.imagen_perfil = imagen_perfil; }

        public String getNombre_completo() { return nombre_completo; }
        public void setNombre_completo(String nombre_completo) { this.nombre_completo = nombre_completo; }

        public String getFecha_nac() { return fecha_nac; }
        public void setFecha_nac(String fecha_nac) { this.fecha_nac = fecha_nac; }

        public String getCorreo_electronico() { return correo_electronico; }
        public void setCorreo_electronico(String correo_electronico) { this.correo_electronico = correo_electronico; }
    }
}
