package com.example.refrimancia.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refrimancia.R;
import com.example.refrimancia.SessionManager;
import com.example.refrimancia.adaptador.AdaptadorComentario;
import com.example.refrimancia.adaptador.AdaptadorReceta;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.ComentarioService;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.api.ValoracionService;
import com.example.refrimancia.modelo.entidad.Comentario;
import com.example.refrimancia.modelo.entidad.Receta;
import com.example.refrimancia.modelo.entidad.Valoracion;
import com.example.refrimancia.modelo.request.ValoracionRequest;
import com.example.refrimancia.modelo.response.RespuestaPaginada;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioFragment extends Fragment {

    private static final String TAG = "InicioFragment";

    private AdaptadorReceta adaptador;
    private RecyclerView rvRecetas;
    private FrameLayout searchOverlay;
    private ArrayAdapter<String> suggestionsAdapter;
    private List<String> currentSuggestions;
    private ProgressBar loadingIndicator;

    private int paginaActual = 1;
    private boolean cargando = false;
    private boolean esUltimaPagina = false;
    private boolean modoBusquedaIngredientes = false;
    private String ultimaConsultaIngredientes = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle estadoGuardado) {
        return inflater.inflate(R.layout.inicio_fragment, contenedor, false);
    }

    @Override
    public void onViewCreated(@NonNull View vista, @Nullable Bundle estadoGuardado) {
        super.onViewCreated(vista, estadoGuardado);

        rvRecetas = vista.findViewById(R.id.recipes_recycler_view);
        SearchView barraBusqueda = vista.findViewById(R.id.search_view);
        searchOverlay = vista.findViewById(R.id.search_overlay);
        ListView searchSuggestionsList = vista.findViewById(R.id.search_suggestions_list);
        loadingIndicator = vista.findViewById(R.id.loading_indicator);

        currentSuggestions = new ArrayList<>();
        suggestionsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, currentSuggestions);
        searchSuggestionsList.setAdapter(suggestionsAdapter);

        searchOverlay.setOnClickListener(v -> {
            barraBusqueda.clearFocus();
            searchOverlay.setVisibility(View.GONE);
        });

        searchSuggestionsList.setOnItemClickListener((parent, view, position, id) -> {
            String nombreSeleccionado = currentSuggestions.get(position);
            barraBusqueda.setQuery(nombreSeleccionado, false);
            barraBusqueda.clearFocus();
            searchOverlay.setVisibility(View.GONE);
            manejarSeleccionSugerencia(nombreSeleccionado);
        });

        configurarRecyclerView();

        barraBusqueda.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && barraBusqueda.getQuery().length() > 0) {
                searchOverlay.setVisibility(View.VISIBLE);
                actualizarSugerencias(barraBusqueda.getQuery().toString());
            } else if (!hasFocus) {
                searchOverlay.setVisibility(View.GONE);
            }
        });

        barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String consulta) {
                String consultaNormalizada = consulta != null ? consulta.trim() : "";
                if (consultaNormalizada.isEmpty()) {
                    salirBusquedaIngredientes();
                } else {
                    buscarRecetasPorIngredientes(consultaNormalizada);
                }
                barraBusqueda.clearFocus();
                searchOverlay.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String nuevoTexto) {
                if (barraBusqueda.hasFocus()) {
                    if (nuevoTexto.isEmpty()) {
                        searchOverlay.setVisibility(View.GONE);
                        salirBusquedaIngredientes();
                    } else {
                        searchOverlay.setVisibility(View.VISIBLE);
                        actualizarSugerencias(nuevoTexto);
                    }
                }
                return true;
            }
        });

        barraBusqueda.setSuggestionsAdapter(null);

        SessionManager sessionManager = new SessionManager(requireContext());
        if (sessionManager.fetchAuthToken() == null) {
            Intent loginIntent = new Intent(requireContext(), com.example.refrimancia.LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            requireActivity().finish();
            return;
        }
        cargarRecetasDesdeAPI();
    }

    private void cargarRecetasDesdeAPI() {
        if (cargando || esUltimaPagina || modoBusquedaIngredientes) return;
        cargando = true;
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        RecetaService servicio = ClienteRetrofit.obtenerInstancia(requireContext()).create(RecetaService.class);
        Call<RespuestaPaginada<Receta>> llamada = servicio.obtenerRecetas(paginaActual);

        llamada.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Receta>> call, @NonNull Response<RespuestaPaginada<Receta>> response) {
                cargando = false;
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    RespuestaPaginada<Receta> cuerpo = response.body();
                    List<Receta> recetas = cuerpo.getData();
                    Log.d(TAG, "Recetas cargadas: " + (recetas != null ? recetas.size() : 0));
                    if (recetas != null && !recetas.isEmpty()) {
                        if (paginaActual == 1) {
                            adaptador.actualizarDatos(recetas);
                        } else {
                            adaptador.agregarDatos(recetas);
                        }

                        Integer totalPaginas = cuerpo.getTotalPages();
                        if (totalPaginas != null && paginaActual >= totalPaginas) {
                            esUltimaPagina = true;
                        } else {
                            paginaActual++;
                        }
                    } else {
                        esUltimaPagina = true;
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta: " + response.code());
                    mostrarMensajeError("Error al cargar recetas");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Receta>> call, @NonNull Throwable error) {
                cargando = false;
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                Log.e(TAG, "Error en la llamada API: " + error.getMessage());
                mostrarMensajeError("Error de conexion");
            }
        });
    }

    private void mostrarMensajeError(String mensaje) {
        if (getContext() != null) {
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarSugerencias(String texto) {
        currentSuggestions.clear();
        if (texto.isEmpty()) {
            suggestionsAdapter.notifyDataSetChanged();
            return;
        }

        String textoBusqueda = texto.toLowerCase();
        List<String> sugerenciasLower = new ArrayList<>();

        for (Receta r : adaptador.getListaRecetasCompleta()) {
            if (r.getTitulo() != null) {
                String tituloLower = r.getTitulo().toLowerCase();
                if (tituloLower.contains(textoBusqueda) && !sugerenciasLower.contains(tituloLower)) {
                    currentSuggestions.add(r.getTitulo());
                    sugerenciasLower.add(tituloLower);
                }
            }
        }

        suggestionsAdapter.notifyDataSetChanged();
    }

    private void manejarSeleccionSugerencia(String nombreReceta) {
        if (nombreReceta == null || nombreReceta.trim().isEmpty()) {
            return;
        }
        buscarRecetasPorIngredientes(nombreReceta.trim());
    }

    private void configurarRecyclerView() {
        List<Receta> listaRecetas = new ArrayList<>();
        adaptador = new AdaptadorReceta(listaRecetas, requireContext(), receta -> {
            startActivity(RecetaActivity.crearIntent(requireContext(), receta));
        });

        adaptador.setOnComentarioClickListener(this::mostrarPopupComentarios);
        adaptador.setOnValoracionClickListener(this::mostrarPopupValoracion);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvRecetas.setLayoutManager(layoutManager);
        rvRecetas.setAdapter(adaptador);

        rvRecetas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (modoBusquedaIngredientes) {
                    return;
                }
                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (!cargando && !esUltimaPagina && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        cargarRecetasDesdeAPI();
                    }
                }
            }
        });
    }

    private void buscarRecetasPorIngredientes(String consulta) {
        modoBusquedaIngredientes = true;
        ultimaConsultaIngredientes = consulta;
        cargando = true;
        esUltimaPagina = true;
        paginaActual = 1;
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        RecetaService servicio = ClienteRetrofit.obtenerInstancia(requireContext()).create(RecetaService.class);
        servicio.buscarPorIngredientes(consulta).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Receta>> call, @NonNull Response<RespuestaPaginada<Receta>> response) {
                cargando = false;
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<Receta> recetas = response.body().getData();
                    adaptador.actualizarDatos(recetas != null ? recetas : new ArrayList<>());
                } else {
                    mostrarMensajeError("No se encontraron recetas con esos ingredientes");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Receta>> call, @NonNull Throwable error) {
                cargando = false;
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                mostrarMensajeError("Error de conexion al buscar recetas");
            }
        });
    }

    private void salirBusquedaIngredientes() {
        if (!modoBusquedaIngredientes) {
            return;
        }
        modoBusquedaIngredientes = false;
        ultimaConsultaIngredientes = "";
        refrescarListadoRecetas();
    }

    private void mostrarPopupComentarios(Receta receta) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_comentarios);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().heightPixels * 0.8));
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
        }

        dialog.setCanceledOnTouchOutside(true);

        RecyclerView rvComentarios = dialog.findViewById(R.id.rv_comentarios);
        rvComentarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        AdaptadorComentario adaptadorComentario = new AdaptadorComentario(new ArrayList<>());
        rvComentarios.setAdapter(adaptadorComentario);

        EditText etNuevoComentario = dialog.findViewById(R.id.et_nuevo_comentario);
        ImageButton btnEnviarComentario = dialog.findViewById(R.id.btn_enviar_comentario);

        ComentarioService comentarioService = ClienteRetrofit.obtenerInstancia(requireContext()).create(ComentarioService.class);
        cargarComentariosReceta(comentarioService, receta.getIdReceta(), adaptadorComentario);

        if (btnEnviarComentario != null && etNuevoComentario != null) {
            btnEnviarComentario.setOnClickListener(v -> {
                String mensaje = etNuevoComentario.getText().toString().trim();
                if (mensaje.isEmpty()) {
                    Toast.makeText(requireContext(), "Escribe un comentario antes de enviar", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnEnviarComentario.setEnabled(false);

                Comentario nuevoComentario = new Comentario();
                nuevoComentario.setIdReceta(receta.getIdReceta());
                nuevoComentario.setMensaje(mensaje);

                comentarioService.crearComentario(nuevoComentario).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        btnEnviarComentario.setEnabled(true);
                        if (response.isSuccessful()) {
                            etNuevoComentario.setText("");
                            cargarComentariosReceta(comentarioService, receta.getIdReceta(), adaptadorComentario);
                            rvComentarios.scrollToPosition(Math.max(adaptadorComentario.getItemCount() - 1, 0));
                            Toast.makeText(requireContext(), "Comentario publicado", Toast.LENGTH_SHORT).show();
                            refrescarListadoRecetas();
                        } else {
                            Toast.makeText(requireContext(), "No se pudo publicar el comentario", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        btnEnviarComentario.setEnabled(true);
                        Toast.makeText(requireContext(), "Error de red al publicar comentario", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        dialog.show();
    }

    private void cargarComentariosReceta(ComentarioService comentarioService, int idReceta, AdaptadorComentario adaptadorComentario) {
        Call<RespuestaPaginada<Comentario>> call = comentarioService.obtenerComentariosPorReceta(idReceta);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Comentario>> call, @NonNull Response<RespuestaPaginada<Comentario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comentario> comentariosInfo = response.body().getData();
                    if (comentariosInfo != null) {
                        adaptadorComentario.setComentarios(comentariosInfo);
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al cargar comentarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Comentario>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error de red al cargar comentarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarPopupValoracion(Receta receta) {
        SessionManager sessionManager = new SessionManager(requireContext());
        if (sessionManager.fetchUserId() == -1) {
            Toast.makeText(requireContext(), "Debes iniciar sesión para valorar", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_valoracion);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().heightPixels * 0.5));
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
        }

        dialog.setCanceledOnTouchOutside(true);

        TextView tvReceta = dialog.findViewById(R.id.tv_valoracion_receta);
        RatingBar ratingBar = dialog.findViewById(R.id.rb_valoracion);
        TextView tvSeleccionada = dialog.findViewById(R.id.tv_valoracion_seleccionada);
        Button btnCancelar = dialog.findViewById(R.id.btn_cancelar_valoracion);
        Button btnEnviar = dialog.findViewById(R.id.btn_enviar_valoracion);

        if (tvReceta != null) {
            tvReceta.setText(receta.getTitulo() != null
                    ? "Valorar: " + receta.getTitulo()
                    : "Valorar receta");
        }

        if (ratingBar != null && tvSeleccionada != null) {
            ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
                int seleccion = Math.round(rating);
                tvSeleccionada.setText(seleccion > 0
                        ? "Puntuación: " + seleccion + "/5"
                        : "Puntuación: sin elegir");
            });
        }

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnEnviar != null && ratingBar != null) {
            btnEnviar.setOnClickListener(v -> {
                int puntuacion = Math.round(ratingBar.getRating());
                if (puntuacion < 1) {
                    Toast.makeText(requireContext(), "Selecciona una puntuación entre 1 y 5", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnEnviar.setEnabled(false);

                ValoracionService valoracionService = ClienteRetrofit.obtenerInstancia(requireContext()).create(ValoracionService.class);
                ValoracionRequest request = new ValoracionRequest(receta.getIdReceta(), puntuacion);
                valoracionService.crearValoracion(request).enqueue(new Callback<Valoracion>() {
                    @Override
                    public void onResponse(@NonNull Call<Valoracion> call, @NonNull Response<Valoracion> response) {
                        btnEnviar.setEnabled(true);
                        if (response.isSuccessful()) {
                            adaptador.refrescarValoracionReceta(receta.getIdReceta());
                            dialog.dismiss();
                            Toast.makeText(requireContext(), "Reseña guardada", Toast.LENGTH_SHORT).show();
                            refrescarListadoRecetas();
                        } else {
                            Toast.makeText(requireContext(), "No se pudo guardar la reseña", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Valoracion> call, @NonNull Throwable t) {
                        btnEnviar.setEnabled(true);
                        Toast.makeText(requireContext(), "Error de red al guardar la reseña", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        dialog.show();
    }

    private void refrescarListadoRecetas() {
        paginaActual = 1;
        esUltimaPagina = false;
        modoBusquedaIngredientes = false;
        ultimaConsultaIngredientes = "";
        if (adaptador != null) {
            adaptador.actualizarDatos(new ArrayList<>());
        }
        cargarRecetasDesdeAPI();
    }
}
