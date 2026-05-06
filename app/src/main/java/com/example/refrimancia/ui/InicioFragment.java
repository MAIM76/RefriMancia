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
import com.example.refrimancia.modelo.request.ComentarioRequest;
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

    // Constantes para sistema de reintento
    private static final int MAX_REINTENTOS = 3;
    private static final int DELAY_BASE_REINTENTO_MS = 500; // 0.5 segundos base

    private AdaptadorReceta adaptador;
    private RecyclerView rvRecetas;
    private SearchView barraBusqueda;
    private FrameLayout searchOverlay;
    private ArrayAdapter<String> suggestionsAdapter;
    private List<String> currentSuggestions;
    private ProgressBar loadingIndicator;

    // Vistas para manejo de errores
    private View errorContainer;
    private TextView tvErrorMensaje;
    private Button btnReintentar;

    private int paginaActual = 1;
    private boolean cargando = false;
    private boolean esUltimaPagina = false;
    private boolean modoBusqueda = false;
    private String ultimaConsultaTexto = "";
    private String ultimaConsultaIngredientes = "";
    private int contadorReintentos = 0;
    private android.os.Handler handlerReintento = new android.os.Handler(android.os.Looper.getMainLooper());

    // Lista completa de recetas para búsqueda (todas las recetas, no solo paginadas)
    private List<Receta> todasLasRecetas = new ArrayList<>();
    private boolean recetasPrecargadas = false;
    private boolean reintentandoBusqueda = false;

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

        try {
            rvRecetas = vista.findViewById(R.id.recipes_recycler_view);
            barraBusqueda = vista.findViewById(R.id.search_view);
            ImageButton btnFiltros = vista.findViewById(R.id.btn_filtros);
            searchOverlay = vista.findViewById(R.id.search_overlay);
            ListView searchSuggestionsList = vista.findViewById(R.id.search_suggestions_list);
            loadingIndicator = vista.findViewById(R.id.loading_indicator);

            // Validar que todas las vistas se encontraron
            if (rvRecetas == null || barraBusqueda == null || searchOverlay == null
                    || searchSuggestionsList == null || loadingIndicator == null) {
                Log.e(TAG, getString(R.string.error_configure_recipe_list));
                return;
            }

            // Inicializar vistas de error de conexión
            errorContainer = vista.findViewById(R.id.error_conexion_container);
            if (errorContainer != null) {
                tvErrorMensaje = errorContainer.findViewById(R.id.tv_error_mensaje);
                btnReintentar = errorContainer.findViewById(R.id.btn_reintentar);
                
                if (btnReintentar != null) {
                    btnReintentar.setOnClickListener(v -> {
                        Log.d(TAG, getString(R.string.log_user_retry));
                        contadorReintentos = 0; // Resetear contador
                        ocultarError();
                        cargarRecetasDesdeAPI();
                    });
                }
            }

            currentSuggestions = new ArrayList<>();
            suggestionsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, 
                    currentSuggestions);
            searchSuggestionsList.setAdapter(suggestionsAdapter);

            searchOverlay.setOnClickListener(v -> {
                barraBusqueda.clearFocus();
                searchOverlay.setVisibility(View.GONE);
            });

            searchSuggestionsList.setOnItemClickListener((parent, view, position, id) -> {
                if (position >= 0 && position < currentSuggestions.size()) {
                    String seleccionado = currentSuggestions.get(position).trim();
                    barraBusqueda.setQuery(seleccionado, false);
                    barraBusqueda.clearFocus();
                    searchOverlay.setVisibility(View.GONE);
                    if (!seleccionado.isEmpty()) {
                        ultimaConsultaTexto = seleccionado;
                        aplicarFiltrosCombinados();
                    }
                }
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
                    ultimaConsultaTexto = consulta != null ? consulta.trim() : "";
                    aplicarFiltrosCombinados();
                    barraBusqueda.clearFocus();
                    searchOverlay.setVisibility(View.GONE);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String nuevoTexto) {
                    if (barraBusqueda.hasFocus()) {
                        if (nuevoTexto.isEmpty()) {
                            searchOverlay.setVisibility(View.GONE);
                            ultimaConsultaTexto = "";
                            aplicarFiltrosCombinados();
                        } else {
                            searchOverlay.setVisibility(View.VISIBLE);
                            actualizarSugerencias(nuevoTexto);
                        }
                    }
                    return true;
                }
            });

            // Configurar botón de filtros
            if (btnFiltros != null) {
                btnFiltros.setOnClickListener(v -> mostrarPopupFiltros());
            }

            SessionManager sessionManager = new SessionManager(requireContext());
            if (sessionManager.fetchAuthToken() == null) {
                Intent loginIntent = new Intent(requireContext(), com.example.refrimancia.LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                requireActivity().finish();
                return;
            }
            cargarRecetasDesdeAPI();
            cargarTodasLasRecetasParaBusqueda();
        } catch (Exception e) {
            Log.e(TAG, "Error crítico en onViewCreated", e);
            Toast.makeText(requireContext(), R.string.error_configure_recipe_list, Toast.LENGTH_LONG).show();
        }
    }

    private void cargarRecetasDesdeAPI() {
        if (cargando || esUltimaPagina || modoBusqueda) return;
        cargando = true;
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        RecetaService servicio = 
                ClienteRetrofit.obtenerInstancia(requireContext()).create(RecetaService.class);
        Call<RespuestaPaginada<Receta>> llamada = servicio.obtenerRecetas(paginaActual);

        llamada.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Receta>> call, 
                    @NonNull Response<RespuestaPaginada<Receta>> response) {
                cargando = false;
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    // Éxito: ocultar mensaje de error si estaba visible
                    ocultarError();
                    contadorReintentos = 0; // Resetear contador de reintentos
                    
                    RespuestaPaginada<Receta> cuerpo = response.body();
                    List<Receta> recetas = cuerpo.getData();
                    Log.d(TAG, getString(R.string.log_recipes_loaded, recetas != null ? recetas.size() : 0));
                    
                    // Logging de depuración para la primera receta
                    if (recetas != null && !recetas.isEmpty()) {
                        Receta primera = recetas.get(0);
                        Log.d(TAG, getString(R.string.log_first_recipe_title, primera.getTitulo()));
                        Log.d(TAG, getString(R.string.log_first_recipe_desc, primera.getDescripcion() != null ? primera.getDescripcion().substring(0, Math.min(primera.getDescripcion().length(), 50)) : "null"));
                        Log.d(TAG, getString(R.string.log_first_recipe_ingredients, primera.getIngredientes() != null ? primera.getIngredientes().substring(0, Math.min(primera.getIngredientes().length(), 50)) : "null"));
                    }
                    
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
                    Log.e(TAG, getString(R.string.log_error_response, response.code()));
                    mostrarMensajeError(getString(R.string.error_load_recipes));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Receta>> call, 
                    @NonNull Throwable error) {
                cargando = false;
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                Log.e(TAG, getString(R.string.log_error_api_call, error.getMessage()));
                
                // Implementar sistema de reintento automático
                if (contadorReintentos < MAX_REINTENTOS) {
                    contadorReintentos++;
                    int delayMs = DELAY_BASE_REINTENTO_MS * contadorReintentos; // Delay exponencial
                    Log.d(TAG, getString(R.string.log_retrying_connection, delayMs, contadorReintentos, MAX_REINTENTOS));
                    mostrarMensajeError(getString(R.string.error_connection_retrying, delayMs/1000));
                    
                    handlerReintento.postDelayed(() -> {
                        if (isAdded() && !isRemoving()) {
                            cargarRecetasDesdeAPI();
                        }
                    }, delayMs);
                } else {
                    // Se agotaron los reintentos: mostrar pantalla de error
                    Log.e(TAG, getString(R.string.log_connection_attempts_exhausted));
                    mostrarError(getString(R.string.error_connection_attempts_exhausted, MAX_REINTENTOS));
                }
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

        // Usar la lista completa de recetas precargadas en lugar de solo las paginadas
        List<Receta> listaBusqueda = recetasPrecargadas ? todasLasRecetas : adaptador.getListaRecetasCompleta();

        for (Receta r : listaBusqueda) {
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

    /**
     * Carga TODAS las recetas recorriendo todas las páginas para permitir búsqueda completa.
     * La API siempre pagina, por lo que hay que acumular página a página.
     */
    private void cargarTodasLasRecetasParaBusqueda() {
        todasLasRecetas.clear();
        recetasPrecargadas = false;
        RecetaService servicio = ClienteRetrofit.obtenerInstancia(requireContext()).create(RecetaService.class);
        acumularPaginaParaBusqueda(servicio, 1);
    }

    private void acumularPaginaParaBusqueda(RecetaService servicio, int pagina) {
        servicio.obtenerRecetas(pagina).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Receta>> call,
                    @NonNull Response<RespuestaPaginada<Receta>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<Receta> recetas = response.body().getData();
                    if (recetas != null && !recetas.isEmpty()) {
                        todasLasRecetas.addAll(recetas);
                    }
                    Integer totalPaginas = response.body().getTotalPages();
                    boolean hayMas = recetas != null && !recetas.isEmpty()
                            && (totalPaginas == null || pagina < totalPaginas);
                    if (hayMas) {
                        acumularPaginaParaBusqueda(servicio, pagina + 1);
                    } else {
                        recetasPrecargadas = true;
                        Log.d(TAG, "Recetas precargadas para búsqueda: " + todasLasRecetas.size());
                    }
                } else {
                    recetasPrecargadas = !todasLasRecetas.isEmpty();
                    Log.e(TAG, "Error al precargar página " + pagina + " para búsqueda");
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Receta>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                recetasPrecargadas = !todasLasRecetas.isEmpty();
                Log.e(TAG, "Fallo al precargar página " + pagina + " para búsqueda: " + t.getMessage());
            }
        });
    }

    private void configurarRecyclerView() {
        try {
            List<Receta> listaRecetas = new ArrayList<>();
            adaptador = new AdaptadorReceta(listaRecetas, requireContext(),
                    receta -> startActivity(RecetaActivity.crearIntent(requireContext(), receta)));

            adaptador.setOnComentarioClickListener(this::mostrarPopupComentarios);
            adaptador.setOnValoracionClickListener(this::mostrarPopupValoracion);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvRecetas.setLayoutManager(layoutManager);
            rvRecetas.setAdapter(adaptador);

            rvRecetas.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (modoBusqueda || dy <= 0 || cargando || esUltimaPagina) return;
                    int visible = layoutManager.getChildCount();
                    int total = layoutManager.getItemCount();
                    int firstVisible = layoutManager.findFirstVisibleItemPosition();
                    if (visible + firstVisible >= total) {
                        cargarRecetasDesdeAPI();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error crítico en configurarRecyclerView", e);
            Toast.makeText(requireContext(), R.string.error_configure_recipe_list, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Aplica ambos filtros combinados: texto (título/descripción) e ingredientes.
     * - Sin filtros: vuelve a la lista paginada normal.
     * - Solo texto: filtra localmente sobre todasLasRecetas.
     * - Con ingredientes (con o sin texto): usa API /buscar/ingredientes y refina por texto localmente.
     */
    private void aplicarFiltrosCombinados() {
        boolean tieneFiltroTexto = !ultimaConsultaTexto.isEmpty();
        boolean tieneFiltroIngredientes = !ultimaConsultaIngredientes.isEmpty();

        // Si no hay filtros, volver a modo normal
        if (!tieneFiltroTexto && !tieneFiltroIngredientes) {
            modoBusqueda = false;
            refrescarListadoRecetas();
            return;
        }

        modoBusqueda = true;
        cargando = true;
        esUltimaPagina = true;
        paginaActual = 1;
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }

        // Si hay filtro de ingredientes, usar SIEMPRE la API (devuelve todas las coincidencias)
        if (tieneFiltroIngredientes) {
            buscarPorIngredientesAPI(ultimaConsultaIngredientes, ultimaConsultaTexto);
            return;
        }

        // Solo filtro de texto: filtrar localmente sobre las recetas precargadas
        if (recetasPrecargadas && !todasLasRecetas.isEmpty()) {
            filtrarRecetasLocalmenteCombinado(ultimaConsultaTexto, "");
        } else if (!reintentandoBusqueda) {
            // Aún cargando el caché: reintentar una vez en 1 segundo
            reintentandoBusqueda = true;
            cargando = false;
            handlerReintento.postDelayed(() -> {
                reintentandoBusqueda = false;
                if (isAdded()) {
                    aplicarFiltrosCombinados();
                }
            }, 1000);
        } else {
            cargando = false;
            if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
            mostrarMensajeError(getString(R.string.error_waiting_recipes));
        }
    }

    /**
     * Filtra recetas aplicando ambos criterios: texto e ingredientes.
     * El filtro de texto busca en título y descripción.
     * El filtro de ingredientes busca que la receta contenga CUALQUIERA de los ingredientes (OR).
     * Según la API de Postman: ingredientes=salmon,aguacate busca recetas con salmon O aguacate.
     */
    private void filtrarRecetasLocalmenteCombinado(String consultaTexto, String consultaIngredientes) {
        String textoLower = consultaTexto.toLowerCase().trim();
        String[] ingredientesArray = consultaIngredientes.toLowerCase().trim().split(",");
        // Limpiar espacios en cada ingrediente y filtrar vacíos
        List<String> ingredientesList = new ArrayList<>();
        for (String ingrediente : ingredientesArray) {
            String trimmed = ingrediente.trim();
            if (!trimmed.isEmpty()) {
                ingredientesList.add(trimmed);
            }
        }

        List<Receta> recetasFiltradas = new ArrayList<>();

        for (Receta receta : todasLasRecetas) {
            boolean coincideTexto = true;
            boolean coincideIngredientes = true;

            // Filtro por texto (título o descripción) - debe coincidir si hay texto
            if (!textoLower.isEmpty()) {
                coincideTexto = false;
                if (receta.getTitulo() != null && 
                    receta.getTitulo().toLowerCase().contains(textoLower)) {
                    coincideTexto = true;
                }
                if (receta.getDescripcion() != null && 
                    receta.getDescripcion().toLowerCase().contains(textoLower)) {
                    coincideTexto = true;
                }
            }

            // Filtro por ingredientes (CUALQUIERA de los ingredientes - OR lógico)
            if (!ingredientesList.isEmpty()) {
                String recetaIngredientesLower = receta.getIngredientes() != null ? 
                    receta.getIngredientes().toLowerCase() : "";
                
                coincideIngredientes = false; // Inicializar como false para OR
                for (String ingrediente : ingredientesList) {
                    if (recetaIngredientesLower.contains(ingrediente)) {
                        coincideIngredientes = true; // Coincide si tiene AL MENOS UN ingrediente
                        break;
                    }
                }
            }

            // La receta debe cumplir AMBOS filtros (AND entre texto e ingredientes)
            if (coincideTexto && coincideIngredientes) {
                recetasFiltradas.add(receta);
            }
        }

        cargando = false;
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.GONE);
        }

        adaptador.actualizarDatos(recetasFiltradas);

        if (recetasFiltradas.isEmpty()) {
            mostrarMensajeError(getString(R.string.error_no_recipes_found));
        }
    }

    /**
     * Busca recetas por ingredientes usando la API /api/recetas/buscar/ingredientes.
     * Si filtroTexto no está vacío, refina los resultados localmente por título/descripción.
     * Según Postman: el endpoint acepta ingredientes separados por coma y devuelve recetas
     * que contienen CUALQUIERA de los ingredientes (OR).
     */
    private void buscarPorIngredientesAPI(String ingredientes, String filtroTexto) {
        RecetaService servicio = 
                ClienteRetrofit.obtenerInstancia(requireContext()).create(RecetaService.class);
        Log.d(TAG, "Buscando por ingredientes: '" + ingredientes + "' con filtro texto: '" + filtroTexto + "'");
        servicio.buscarPorIngredientes(ingredientes).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Receta>> call, 
                    @NonNull Response<RespuestaPaginada<Receta>> response) {
                cargando = false;
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<Receta> recetas = response.body().getData();
                    if (recetas == null) {
                        recetas = new ArrayList<>();
                    }
                    Log.d(TAG, "API devolvió " + recetas.size() + " recetas con esos ingredientes");

                    // Si hay filtro de texto adicional, refinar localmente
                    if (filtroTexto != null && !filtroTexto.trim().isEmpty()) {
                        String textoLower = filtroTexto.toLowerCase().trim();
                        List<Receta> refinadas = new ArrayList<>();
                        for (Receta r : recetas) {
                            boolean enTitulo = r.getTitulo() != null && 
                                r.getTitulo().toLowerCase().contains(textoLower);
                            boolean enDesc = r.getDescripcion() != null && 
                                r.getDescripcion().toLowerCase().contains(textoLower);
                            if (enTitulo || enDesc) {
                                refinadas.add(r);
                            }
                        }
                        recetas = refinadas;
                        Log.d(TAG, "Tras refinar por texto: " + recetas.size() + " recetas");
                    }

                    adaptador.actualizarDatos(recetas);
                    if (recetas.isEmpty()) {
                        mostrarMensajeError(getString(R.string.error_no_recipes_found));
                    }
                } else {
                    Log.e(TAG, "Error en respuesta API ingredientes: " + response.code());
                    mostrarMensajeError(getString(R.string.error_no_recipes_found));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Receta>> call, 
                    @NonNull Throwable error) {
                cargando = false;
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
                Log.e(TAG, "Fallo conexión búsqueda ingredientes: " + error.getMessage());
                mostrarMensajeError(getString(R.string.error_search_recipes_connection));
            }
        });
    }

    private void mostrarPopupFiltros() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_filtros);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
        }

        dialog.setCanceledOnTouchOutside(true);

        SearchView searchViewPopup = dialog.findViewById(R.id.search_view_popup);
        EditText etIngredientes = dialog.findViewById(R.id.et_ingredientes_filtro);
        Button btnLimpiar = dialog.findViewById(R.id.btn_limpiar_filtros);
        Button btnAplicar = dialog.findViewById(R.id.btn_aplicar_filtros);

        // Pre-cargar valores actuales
        if (searchViewPopup != null && !ultimaConsultaTexto.isEmpty()) {
            searchViewPopup.setQuery(ultimaConsultaTexto, false);
        }
        if (etIngredientes != null && !ultimaConsultaIngredientes.isEmpty()) {
            etIngredientes.setText(ultimaConsultaIngredientes);
        }

        if (btnLimpiar != null) {
            btnLimpiar.setOnClickListener(v -> {
                if (searchViewPopup != null) {
                    searchViewPopup.setQuery("", false);
                }
                if (etIngredientes != null) {
                    etIngredientes.setText("");
                }
                ultimaConsultaTexto = "";
                ultimaConsultaIngredientes = "";
                aplicarFiltrosCombinados();
                barraBusqueda.setQuery("", false);
                dialog.dismiss();
            });
        }

        if (btnAplicar != null) {
            btnAplicar.setOnClickListener(v -> {
                String textoBusqueda = searchViewPopup != null ? 
                    searchViewPopup.getQuery().toString().trim() : "";
                String ingredientes = etIngredientes != null ? 
                    etIngredientes.getText().toString().trim() : "";
                
                ultimaConsultaTexto = textoBusqueda;
                ultimaConsultaIngredientes = ingredientes;
                
                // Sincronizar barra de búsqueda principal
                barraBusqueda.setQuery(textoBusqueda, false);
                
                aplicarFiltrosCombinados();
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void mostrarPopupComentarios(Receta receta) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_comentarios);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 
                    (int) (getResources().getDisplayMetrics().heightPixels * 0.8));
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

        ComentarioService comentarioService = 
                ClienteRetrofit.obtenerInstancia(requireContext()).create(ComentarioService.class);
        cargarComentariosReceta(comentarioService, receta.getIdReceta(), adaptadorComentario);

        if (btnEnviarComentario != null && etNuevoComentario != null) {
            btnEnviarComentario.setOnClickListener(v -> {
                String mensaje = etNuevoComentario.getText().toString().trim();
                if (mensaje.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.error_comment_empty, 
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                btnEnviarComentario.setEnabled(false);

                ComentarioRequest nuevoComentario = new ComentarioRequest(receta.getIdReceta(), mensaje);

                comentarioService.crearComentario(nuevoComentario).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, 
                            @NonNull Response<ResponseBody> response) {
                        btnEnviarComentario.setEnabled(true);
                        if (response.isSuccessful()) {
                            etNuevoComentario.setText("");
                            cargarComentariosReceta(comentarioService, receta.getIdReceta(), 
                                    adaptadorComentario);
                            rvComentarios.scrollToPosition(Math.max(adaptadorComentario.getItemCount() - 
                                    1, 0));
                            Toast.makeText(requireContext(), R.string.comment_published, 
                                    Toast.LENGTH_SHORT).show();
                            refrescarListadoRecetas();
                        } else {
                            Toast.makeText(requireContext(), R.string.error_publish_comment, 
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        btnEnviarComentario.setEnabled(true);
                        Toast.makeText(requireContext(), R.string.error_publish_comment_network, 
                                Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        dialog.show();
    }

    private void cargarComentariosReceta(ComentarioService comentarioService, int idReceta,
            AdaptadorComentario adaptadorComentario) {
        Call<RespuestaPaginada<Comentario>> call = comentarioService.obtenerComentariosPorReceta(idReceta);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Comentario>> call,
                    @NonNull Response<RespuestaPaginada<Comentario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Comentario> comentariosInfo = response.body().getData();
                    if (comentariosInfo != null) {
                        adaptadorComentario.setComentarios(comentariosInfo);
                    }
                } else {
                    Toast.makeText(requireContext(), R.string.error_load_comments,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Comentario>> call,
                    @NonNull Throwable t) {
                Toast.makeText(requireContext(), R.string.error_load_comments_network,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarPopupValoracion(Receta receta) {
        SessionManager sessionManager = new SessionManager(requireContext());
        if (sessionManager.fetchUserId() == -1) {
            Toast.makeText(requireContext(), R.string.error_login_required_rating,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_valoracion);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) (getResources().getDisplayMetrics().heightPixels * 0.5));
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
                    ? getString(R.string.rating_title_format, receta.getTitulo())
                    : getString(R.string.rating_title_default));
        }

        if (ratingBar != null && tvSeleccionada != null) {
            ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
                int seleccion = Math.round(rating);
                tvSeleccionada.setText(seleccion > 0
                        ? getString(R.string.rating_score_format, seleccion)
                        : getString(R.string.rating_score_empty));
            });
        }

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> dialog.dismiss());
        }

        if (btnEnviar != null && ratingBar != null) {
            btnEnviar.setOnClickListener(v -> {
                int puntuacion = Math.round(ratingBar.getRating());
                if (puntuacion < 1) {
                    Toast.makeText(requireContext(), R.string.error_select_rating,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                btnEnviar.setEnabled(false);

                ValoracionService valoracionService =
                        ClienteRetrofit.obtenerInstancia(requireContext()).create(ValoracionService.class);
                ValoracionRequest request = new ValoracionRequest(receta.getIdReceta(), puntuacion);
                valoracionService.crearValoracion(request).enqueue(new Callback<Valoracion>() {
                    @Override
                    public void onResponse(@NonNull Call<Valoracion> call,
                            @NonNull Response<Valoracion> response) {
                        btnEnviar.setEnabled(true);
                        if (response.isSuccessful()) {
                            adaptador.refrescarValoracionReceta(receta.getIdReceta());
                            dialog.dismiss();
                            Toast.makeText(requireContext(), R.string.review_saved, Toast.LENGTH_SHORT).show();
                            refrescarListadoRecetas();
                        } else {
                            Toast.makeText(requireContext(), R.string.error_save_review,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Valoracion> call, @NonNull Throwable t) {
                        btnEnviar.setEnabled(true);
                        Toast.makeText(requireContext(), R.string.error_save_review_network,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        dialog.show();
    }

    private void refrescarListadoRecetas() {
        paginaActual = 1;
        esUltimaPagina = false;
        modoBusqueda = false;
        ultimaConsultaTexto = "";
        ultimaConsultaIngredientes = "";
        recetasPrecargadas = false;
        if (adaptador != null) {
            adaptador.actualizarDatos(new ArrayList<>());
        }
        cargarRecetasDesdeAPI();
        cargarTodasLasRecetasParaBusqueda();
    }

    /**
     * Muestra el layout de error de conexión.
     * @param mensaje Mensaje personalizado de error (opcional)
     */
    private void mostrarError(String mensaje) {
        if (errorContainer != null && rvRecetas != null) {
            rvRecetas.setVisibility(View.GONE);
            errorContainer.setVisibility(View.VISIBLE);
            if (tvErrorMensaje != null && mensaje != null) {
                tvErrorMensaje.setText(mensaje);
            }
        }
    }

    /**
     * Oculta el layout de error de conexión y muestra el RecyclerView.
     */
    private void ocultarError() {
        if (errorContainer != null && rvRecetas != null) {
            errorContainer.setVisibility(View.GONE);
            rvRecetas.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancelar cualquier reintento pendiente
        handlerReintento.removeCallbacksAndMessages(null);
    }
}
