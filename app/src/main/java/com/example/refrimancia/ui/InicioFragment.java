package com.example.refrimancia.ui;

import android.app.Dialog;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.refrimancia.R;
import com.example.refrimancia.adaptador.AdaptadorReceta;
import com.example.refrimancia.adaptador.AdaptadorComentario;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.ComentarioService;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.modelo.Comentario;
import com.example.refrimancia.modelo.Receta;
import com.example.refrimancia.modelo.RespuestaPaginada;
import com.example.refrimancia.utils.DatosEjemplo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioFragment extends Fragment {

    private static final String TAG = "InicioFragment";
    private AdaptadorReceta adaptador;
    private RecyclerView rvRecetas;
    
    private int paginaActual = 1;
    private boolean cargando = false;
    private boolean esUltimaPagina = false;

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

        configurarRecyclerView();

        // Filtrado con la barra de búsqueda - busca en tiempo real mientras el usuario escribe
        barraBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String consulta) {
                adaptador.filtrar(consulta);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String nuevoTexto) {
                adaptador.filtrar(nuevoTexto);
                return true;
            }
        });

        // Cargar recetas desde la API
        DatosEjemplo.loginAutomaticoTemporal(exito -> {
            if (exito) {
                cargarRecetasDesdeAPI();
            } else {
                mostrarMensajeError("Error de autenticación inicial");
            }
        });
    }

    private void cargarRecetasDesdeAPI() {
        if (cargando || esUltimaPagina) return;
        cargando = true;
        
        RecetaService servicio = ClienteRetrofit.obtenerInstancia().create(RecetaService.class);

        Call<RespuestaPaginada<Receta>> llamada = servicio.obtenerRecetas(paginaActual);

        llamada.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Receta>> call, @NonNull Response<RespuestaPaginada<Receta>> response) {
                cargando = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<Receta> recetas = response.body().getData();
                    Log.d(TAG, "Recetas cargadas: " + (recetas != null ? recetas.size() : 0));
                    if (recetas != null && !recetas.isEmpty()) {
                        if (paginaActual == 1) {
                            adaptador.actualizarDatos(recetas);
                        } else {
                            adaptador.agregarDatos(recetas);
                        }
                        paginaActual++;
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
                Log.e(TAG, "Error en la llamada API: " + error.getMessage());
                mostrarMensajeError("Error de conexión");
            }
        });
    }

    private void mostrarMensajeError(String mensaje) {
        if (getContext() != null) {
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
        }
    }

    private void configurarRecyclerView() {
        List<Receta> listaRecetas = new ArrayList<>();
        adaptador = new AdaptadorReceta(listaRecetas, requireContext(), receta -> {
            if (getActivity() != null) {
                RecetaFragment fragment = RecetaFragment.newInstance(receta);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Configurar listener para el botón de comentarios
        adaptador.setOnComentarioClickListener(this::mostrarPopupComentarios);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvRecetas.setLayoutManager(layoutManager);
        rvRecetas.setAdapter(adaptador);
        
        rvRecetas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Scrolling down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (!cargando && !esUltimaPagina) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount - 2) {
                            cargarRecetasDesdeAPI();
                        }
                    }
                }
            }
        });
    }

    private void mostrarPopupComentarios(Receta receta) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_comentarios);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int)(getResources().getDisplayMetrics().heightPixels * 0.8));
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

        ComentarioService comentarioService = ClienteRetrofit.obtenerInstancia().create(ComentarioService.class);
        Call<RespuestaPaginada<Comentario>> call = comentarioService.obtenerComentariosPorReceta(receta.getIdReceta());

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

        dialog.show();
    }
}
