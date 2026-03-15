# 📱 Integración de API REST - RefriMancia

## ✅ Implementación Completada

Se ha integrado exitosamente la API REST de MySQL en tu proyecto Android usando **Retrofit**. Aquí está el resumen de los cambios:

---

## 📦 Dependencias Agregadas

```gradle
// Retrofit - Cliente HTTP
implementation(libs.retrofit)
implementation(libs.retrofit.gson)

// OkHttp - Cliente HTTP subyacente
implementation(libs.okhttp)
implementation(libs.okhttp.logging)

// Gson - Serialización JSON
implementation(libs.gson)

// Coroutines - Para operaciones asincrónicas
implementation(libs.coroutines.core)
implementation(libs.coroutines.android)
```

---

## 🔧 Archivos Creados

### 1. **ClienteRetrofit.java**
- Singleton que configura Retrofit
- Base URL: `http://refrimacia-backend.onrender.com/`
- Incluye logging HTTP para debugging
- Convierte automáticamente JSON ↔ Objetos Java

### 2. **RecetaService.java**
- Interface que define los endpoints disponibles
- Método: `obtenerRecetas()` → GET `/api/recetas/listar`
- Devuelve: `List<Receta>`

---

## 📡 Cómo Funciona la Integración

### En `InicioFragment.java`:

```java
// 1. Obtener instancia del cliente
RecetaService servicio = ClienteRetrofit.obtenerInstancia()
    .create(RecetaService.class);

// 2. Hacer la llamada
Call<List<Receta>> llamada = servicio.obtenerRecetas();

// 3. Procesar respuesta de forma asincrónica
llamada.enqueue(new Callback<List<Receta>>() {
    @Override
    public void onResponse(...) {
        // ✅ Éxito: actualizar UI con las recetas
        adaptador.actualizarDatos(recetas);
    }

    @Override
    public void onFailure(...) {
        // ❌ Error: mostrar datos de ejemplo como fallback
        cargarRecetasEjemplo();
    }
});
```

---

## 🔄 Flujo de Carga de Datos

```
┌─────────────────┐
│   App Inicia    │
└────────┬────────┘
         │
         ▼
┌──────────────────────────┐
│  InicioFragment cargado  │
└────────┬─────────────────┘
         │
         ▼
┌──────────────────────────────────┐
│ cargarRecetasDesdeAPI() ejecutado │
└────────┬─────────────────────────┘
         │
         ▼
    ┌────────────────────────────┐
    │ Llamada HTTP a la API      │
    │ GET /api/recetas/listar    │
    └────────┬───────────────────┘
             │
        ┌────┴──────┐
        │            │
        ▼            ▼
   ✅ Éxito      ❌ Error
        │            │
        ▼            ▼
   Actualizar   Mostrar datos
   RecyclerView  de ejemplo
```

---

## 📝 Estructura Esperada de Respuesta JSON

Tu API debe devolver un JSON con esta estructura:

```json
[
  {
    "id": 1,
    "titulo": "Tortilla española",
    "descripcion": "Clásica tortilla de patata con huevo y cebolla",
    "imagen": "url_imagen.jpg"
  },
  {
    "id": 2,
    "titulo": "Gazpacho andaluz",
    "descripcion": "Sopa fría de tomate perfecta para el verano",
    "imagen": "url_imagen.jpg"
  }
]
```

---

## 🛠️ Agregar Nuevos Endpoints

Si necesitas más endpoints (crear, actualizar, eliminar recetas), agrégalos a `RecetaService.java`:

```java
public interface RecetaService {
    // Obtener todas las recetas
    @GET("api/recetas/listar")
    Call<List<Receta>> obtenerRecetas();

    // Obtener una receta por ID
    @GET("api/recetas/{id}")
    Call<Receta> obtenerReceta(@Path("id") int id);

    // Crear receta
    @POST("api/recetas/crear")
    Call<Receta> crearReceta(@Body Receta receta);

    // Actualizar receta
    @PUT("api/recetas/{id}")
    Call<Receta> actualizarReceta(@Path("id") int id, @Body Receta receta);

    // Eliminar receta
    @DELETE("api/recetas/{id}")
    Call<Void> eliminarReceta(@Path("id") int id);
}
```

---

## 🔐 Mejoras Futuras

1. **Agregar Autenticación (Token JWT)**
   ```java
   @Header("Authorization") String token
   ```

2. **Manejo de Errores más Robusto**
   - Crear clase ErrorHandler
   - Mensajes de error más específicos

3. **Usar ViewModel + LiveData**
   - Mejor gestión del ciclo de vida
   - Evitar memory leaks

4. **Implementar Caché Local**
   - Room Database para sincronización offline
   - Sincronizar cuando haya conexión

5. **Agregar Imágenes Reales**
   - Usar Glide o Picasso para cargar imágenes de URLs
   - `implementation("com.github.bumptech.glide:glide:4.15.1")`

---

## ✨ Características Actuales

✅ Carga automática de recetas desde la API  
✅ Manejo de errores con fallback a datos de ejemplo  
✅ Búsqueda filtrada en tiempo real  
✅ UI responsiva con RecyclerView  
✅ Logging HTTP para debugging  
✅ Código modular y reutilizable  

---

## 📲 Próximos Pasos

1. **Sincronizar el proyecto** en Android Studio (Build → Rebuild Project)
2. **Probar en un dispositivo** o emulador
3. **Validar que la API esté accesible** desde tu dispositivo
4. **Agregar más funcionalidades** (crear, editar, eliminar recetas)

---

**Nota:** Si la API no está disponible, la app mostrará automáticamente las recetas de ejemplo como fallback.

