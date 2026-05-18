package com.example.refrimancia.ui.pablo;

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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.refrimancia.R;
import com.example.refrimancia.adapter.RecetaAdapter;
import com.example.refrimancia.util.SessionManager;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.RecetaService;
import com.example.refrimancia.util.ComentariosPopupHelper;
import com.example.refrimancia.api.ValoracionService;
import com.example.refrimancia.model.entity.Receta;
import com.example.refrimancia.model.request.ValoracionRequest;
import com.example.refrimancia.model.response.ValoracionUsuario;
import com.example.refrimancia.model.response.RespuestaPaginada;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragmento principal de la pantalla de inicio.
 * Muestra un listado paginado de recetas con búsqueda por texto, ingredientes y tipo.
 * Soporta refresco por swipe, reintentos automáticos ante fallos de red y un panel de error
 * con botón de reintento manual. Permite ver el detalle de cada receta, sus comentarios
 * y publicar valoraciones desde un popup.
 */
public class InicioFragment extends Fragment {

    // ======================== CONSTANTES ========================

    private static final String TAG = "InicioFragment";
    /** Número máximo de reintentos automáticos ante fallo de red en la carga principal. */
    private static final int MAX_REINTENTOS = 3;

    // ======================== VISTAS ========================

    private RecetaAdapter adaptador;
    private RecyclerView rvRecetas;
    private SearchView barraBusqueda;
    private FrameLayout searchOverlay;
    private ArrayAdapter<String> suggestionsAdapter;
    private List<String> currentSuggestions;
    private ProgressBar loadingIndicator;
    private ProgressBar loadingInicial;
    private SwipeRefreshLayout swipeRecetas;

    /** Panel de error de conexión (contiene el mensaje y el botón de reintentar). */
    private View errorContainer;
    private TextView tvErrorMensaje;
    private Button btnReintentar;

    // ======================== ESTADO DE PAGINACIÓN ========================

    private int paginaActual = 1;
    private boolean cargando = false;
    private boolean esUltimaPagina = false;

    // ======================== ESTADO DE BÚSQUEDA ========================

    private boolean modoBusqueda = false;
    private String ultimaConsultaTexto = "";
    private String ultimaConsultaIngredientes = "";
    private int paginaBusquedaActual = 1;
    private boolean esUltimaPaginaBusqueda = false;
    private List<String> ultimaConsultaTipos = new ArrayList<>();
    /** Caché de todas las recetas para permitir búsqueda local sin nueva paginación. */
    private List<Receta> todasLasRecetas = new ArrayList<>();
    private boolean recetasPrecargadas = false;
    private boolean reintentandoBusqueda = false;

    // ======================== REINTENTOS ========================

    private final android.os.Handler handlerReintento = new android.os.Handler(android.os.Looper.getMainLooper());
    private int contadorReintentos = 0;

    private final ActivityResultLauncher<Intent> recetaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    recargarRecetas();
                    if (getActivity() instanceof OnRecetaCambiadaListener) {
                        ((OnRecetaCambiadaListener) getActivity()).onRecetaCambiada();
                    }
                }
            }
    );

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
            android.widget.EditText searchEditText = barraBusqueda.findViewById(
                    androidx.appcompat.R.id.search_src_text);
            if (searchEditText != null) {
                searchEditText.setTextColor(android.graphics.Color.BLACK);
            }
            ImageButton btnFiltros = vista.findViewById(R.id.btn_filtros);
            searchOverlay = vista.findViewById(R.id.search_overlay);
            ListView searchSuggestionsList = vista.findViewById(R.id.search_suggestions_list);
            loadingIndicator = vista.findViewById(R.id.loading_indicator);
            loadingInicial = vista.findViewById(R.id.loading_inicial);
            swipeRecetas = vista.findViewById(R.id.swipe_recetas);
            if (swipeRecetas != null) {
                swipeRecetas.setColorSchemeResources(R.color.marron_oscuro);
                swipeRecetas.setOnRefreshListener(() -> {
                    contadorReintentos = 0;
                    ocultarError();
                    refrescarListadoRecetas();
                });
            }

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
                        contadorReintentos = 0;
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
        if (paginaActual == 1) {
            if (loadingInicial != null) loadingInicial.setVisibility(View.VISIBLE);
            if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
        } else {
            if (loadingIndicator != null) loadingIndicator.setVisibility(View.VISIBLE);
        }

        RecetaService servicio = 
                ClienteRetrofit.obtenerInstancia(requireContext()).create(RecetaService.class);
        Call<RespuestaPaginada<Receta>> llamada = servicio.obtenerRecetas(paginaActual);

        llamada.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Receta>> call, 
                    @NonNull Response<RespuestaPaginada<Receta>> response) {
                cargando = false;
                if (loadingInicial != null) loadingInicial.setVisibility(View.GONE);
                if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
                if (swipeRecetas != null) swipeRecetas.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Éxito: ocultar mensaje de error si estaba visible
                    ocultarError();
                    RespuestaPaginada<Receta> cuerpo = response.body();
                    List<Receta> recetas = cuerpo.getData();
                    Log.d(TAG, getString(R.string.log_recipes_loaded, recetas != null ? recetas.size() : 0));
                    
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
                    mostrarError(getString(R.string.error_load_recipes), true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Receta>> call,
                    @NonNull Throwable error) {
                cargando = false;
                if (loadingInicial != null) loadingInicial.setVisibility(View.GONE);
                if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
                if (swipeRecetas != null) swipeRecetas.setRefreshing(false);
                Log.e(TAG, getString(R.string.log_error_api_call, error.getMessage()));

                if (contadorReintentos < MAX_REINTENTOS) {
                    contadorReintentos++;
                    int delaySeg = contadorReintentos;
                    mostrarError(getString(R.string.error_connection_retrying, delaySeg), false);
                    handlerReintento.postDelayed(() -> {
                        if (isAdded() && !isRemoving()) {
                            cargarRecetasDesdeAPI();
                        }
                    }, delaySeg * 1000L);
                } else {
                    contadorReintentos = 0;
                    mostrarError(getString(R.string.error_conexion_mensaje), true);
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
            adaptador = new RecetaAdapter(listaRecetas, requireContext(),
                    receta -> recetaLauncher.launch(RecetaActivity.crearIntent(requireContext(), receta)));

            adaptador.setOnComentarioClickListener(this::mostrarPopupComentarios);
            adaptador.setOnValoracionClickListener(this::mostrarPopupValoracion);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvRecetas.setLayoutManager(layoutManager);
            rvRecetas.setAdapter(adaptador);

            rvRecetas.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    int visible = layoutManager.getChildCount();
                    int total = layoutManager.getItemCount();
                    int firstVisible = layoutManager.findFirstVisibleItemPosition();
                    if (modoBusqueda) {
                        boolean necesitaAPI = !ultimaConsultaIngredientes.isEmpty() || !ultimaConsultaTipos.isEmpty();
                        if (necesitaAPI && !cargando && !esUltimaPaginaBusqueda
                                && total > 0 && visible + firstVisible >= total - 3) {
                            buscarPorIngredientesAPI(ultimaConsultaIngredientes,
                                    ultimaConsultaTipos, ultimaConsultaTexto, paginaBusquedaActual);
                        }
                        return;
                    }
                    if (cargando || esUltimaPagina) return;
                    if (total > 0 && visible + firstVisible >= total - 3) {
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
     * Aplica filtros combinados: texto (título/descripción), ingredientes y tipo de comida.
     * - Sin filtros: vuelve a la lista paginada normal.
     * - Solo texto/tipo: filtra localmente sobre todasLasRecetas.
     * - Con ingredientes: usa API /buscar/ingredientes y refina localmente.
     */
    private void aplicarFiltrosCombinados() {
        boolean tieneFiltroTexto = !ultimaConsultaTexto.isEmpty();
        boolean tieneFiltroIngredientes = !ultimaConsultaIngredientes.isEmpty();
        boolean tieneFiltroTipo = !ultimaConsultaTipos.isEmpty();

        // Si no hay filtros, volver a modo normal
        if (!tieneFiltroTexto && !tieneFiltroIngredientes && !tieneFiltroTipo) {
            modoBusqueda = false;
            refrescarListadoRecetas();
            return;
        }

        modoBusqueda = true;
        paginaBusquedaActual = 1;
        esUltimaPaginaBusqueda = false;
        cargando = true;
        if (loadingInicial != null) loadingInicial.setVisibility(View.VISIBLE);
        if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);

        // Si hay filtro de ingredientes o tipo, usar la API
        if (tieneFiltroIngredientes || tieneFiltroTipo) {
            buscarPorIngredientesAPI(ultimaConsultaIngredientes,
                    ultimaConsultaTipos, ultimaConsultaTexto, 1);
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
        List<String> tiposLower = new ArrayList<>();
        for (String t : ultimaConsultaTipos) tiposLower.add(t.toLowerCase().trim());
        String[] ingredientesArray = consultaIngredientes.toLowerCase().trim().split(",");
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
            boolean coincideTipo = true;

            // Filtro por texto (título o descripción)
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

            // Filtro por tipo de comida (OR lógico entre todos los tipos seleccionados)
            if (!tiposLower.isEmpty()) {
                String categoria = receta.getCategoria() != null ?
                    receta.getCategoria().toLowerCase().trim() : "";
                coincideTipo = false;
                for (String t : tiposLower) {
                    if (categoria.equals(t)) {
                        coincideTipo = true;
                        break;
                    }
                }
            }

            // Filtro por ingredientes (OR lógico)
            if (!ingredientesList.isEmpty()) {
                String recetaIngredientesLower = receta.getIngredientes() != null ?
                    receta.getIngredientes().toLowerCase() : "";
                coincideIngredientes = false;
                for (String ingrediente : ingredientesList) {
                    if (recetaIngredientesLower.contains(ingrediente)) {
                        coincideIngredientes = true;
                        break;
                    }
                }
            }

            if (coincideTexto && coincideIngredientes && coincideTipo) {
                recetasFiltradas.add(receta);
            }
        }

        cargando = false;
        if (loadingInicial != null) loadingInicial.setVisibility(View.GONE);
        if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
        if (swipeRecetas != null) swipeRecetas.setRefreshing(false);

        adaptador.actualizarDatos(recetasFiltradas);

        if (recetasFiltradas.isEmpty()) {
            mostrarMensajeError(getString(R.string.error_no_recipes_found));
        }
    }

    /**
     * Busca recetas usando la API /api/recetas/buscar/ingredientes con soporte de paginación.
     * La API filtra por ingredientes (OR) y/o tipo_receta. Si filtroTexto no está vacío,
     * refina los resultados localmente por título/descripción.
     */
    private void buscarPorIngredientesAPI(String ingredientes, List<String> tipos, String filtroTexto, int page) {
        cargando = true;
        if (page == 1) {
            if (loadingInicial != null) loadingInicial.setVisibility(View.VISIBLE);
            if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
        } else {
            if (loadingIndicator != null) loadingIndicator.setVisibility(View.VISIBLE);
        }
        RecetaService servicio =
                ClienteRetrofit.obtenerInstancia(requireContext()).create(RecetaService.class);
        String ingParam = (ingredientes != null && !ingredientes.isEmpty()) ? ingredientes : null;
        List<String> tiposParam = (tipos != null && !tipos.isEmpty()) ? tipos : null;
        Log.d(TAG, "Buscando: ingredientes='" + ingParam + "' tipos='" + tiposParam
                + "' filtroTexto='" + filtroTexto + "' page=" + page);
        servicio.buscarPorIngredientes(ingParam, tiposParam, page).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RespuestaPaginada<Receta>> call,
                    @NonNull Response<RespuestaPaginada<Receta>> response) {
                cargando = false;
                if (loadingInicial != null) loadingInicial.setVisibility(View.GONE);
                if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
                if (swipeRecetas != null) swipeRecetas.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    RespuestaPaginada<Receta> cuerpo = response.body();
                    List<Receta> recetasAPI = cuerpo.getData();
                    if (recetasAPI == null) recetasAPI = new ArrayList<>();
                    Log.d(TAG, "Búsqueda pág " + page + ": " + recetasAPI.size() + " recetas");

                    // Refinar localmente solo por texto (tipo ya lo filtra la API)
                    List<Receta> recetas = recetasAPI;
                    String textoLower = filtroTexto != null ? filtroTexto.toLowerCase().trim() : "";
                    if (!textoLower.isEmpty()) {
                        List<Receta> refinadas = new ArrayList<>();
                        for (Receta r : recetasAPI) {
                            boolean pasaTexto =
                                (r.getTitulo() != null && r.getTitulo().toLowerCase().contains(textoLower)) ||
                                (r.getDescripcion() != null && r.getDescripcion().toLowerCase().contains(textoLower));
                            if (pasaTexto) refinadas.add(r);
                        }
                        recetas = refinadas;
                        Log.d(TAG, "Tras refinar por texto: " + recetas.size() + " recetas");
                    }

                    if (page == 1) {
                        adaptador.actualizarDatos(recetas);
                    } else {
                        adaptador.agregarDatos(recetas);
                    }

                    // La paginación se basa en la respuesta de la API, no en el filtrado local
                    Integer totalPaginas = cuerpo.getTotalPages();
                    if ((totalPaginas != null && page >= totalPaginas) || recetasAPI.isEmpty()) {
                        esUltimaPaginaBusqueda = true;
                    } else {
                        paginaBusquedaActual = page + 1;
                    }

                    if (page == 1 && adaptador.getItemCount() == 0) {
                        mostrarMensajeError(getString(R.string.error_no_recipes_found));
                    }
                } else {
                    Log.e(TAG, "Error en respuesta API búsqueda: " + response.code());
                    mostrarMensajeError(getString(R.string.error_no_recipes_found));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RespuestaPaginada<Receta>> call,
                    @NonNull Throwable error) {
                cargando = false;
                if (loadingInicial != null) loadingInicial.setVisibility(View.GONE);
                if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
                if (swipeRecetas != null) swipeRecetas.setRefreshing(false);
                Log.e(TAG, "Fallo conexión búsqueda: " + error.getMessage());
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
            window.setGravity(Gravity.TOP);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
        }

        dialog.setCanceledOnTouchOutside(true);

        LinearLayout llTipos = dialog.findViewById(R.id.ll_tipos_comida);
        EditText etIngredientes = dialog.findViewById(R.id.et_ingredientes_filtro);
        Button btnLimpiar = dialog.findViewById(R.id.btn_limpiar_filtros);
        Button btnAplicar = dialog.findViewById(R.id.btn_aplicar_filtros);

        // Generar checkboxes dinámicamente para cada tipo (saltando "Todos")
        String[] tiposComida = getResources().getStringArray(R.array.tipos_comida);
        List<CheckBox> checkboxes = new ArrayList<>();
        if (llTipos != null) {
            for (int i = 1; i < tiposComida.length; i++) {
                CheckBox cb = new CheckBox(requireContext());
                cb.setText(tiposComida[i]);
                cb.setTextSize(14f);
                cb.setTextColor(requireContext().getColor(R.color.gris_marron));
                cb.setButtonTintList(android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.gris_marron)));
                cb.setPadding(8, 8, 8, 8);
                cb.setChecked(ultimaConsultaTipos.contains(tiposComida[i]));
                llTipos.addView(cb);
                checkboxes.add(cb);
            }
        }

        // Pre-cargar ingredientes actuales
        if (etIngredientes != null && !ultimaConsultaIngredientes.isEmpty()) {
            etIngredientes.setText(ultimaConsultaIngredientes);
        }

        if (btnLimpiar != null) {
            btnLimpiar.setOnClickListener(v -> {
                for (CheckBox cb : checkboxes) cb.setChecked(false);
                if (etIngredientes != null) etIngredientes.setText("");
                ultimaConsultaTipos = new ArrayList<>();
                ultimaConsultaIngredientes = "";
                aplicarFiltrosCombinados();
                dialog.dismiss();
            });
        }

        if (btnAplicar != null) {
            btnAplicar.setOnClickListener(v -> {
                List<String> tiposSeleccionados = new ArrayList<>();
                for (CheckBox cb : checkboxes) {
                    if (cb.isChecked()) {
                        tiposSeleccionados.add(cb.getText().toString());
                    }
                }
                ultimaConsultaTipos = tiposSeleccionados;
                ultimaConsultaIngredientes = etIngredientes != null ?
                        etIngredientes.getText().toString().trim() : "";
                aplicarFiltrosCombinados();
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void mostrarPopupComentarios(Receta receta) {
        ComentariosPopupHelper.mostrar(requireContext(), receta);
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

        ValoracionService valoracionService =
                ClienteRetrofit.obtenerInstancia(requireContext()).create(ValoracionService.class);

        valoracionService.obtenerValoracionUsuario(receta.getIdReceta()).enqueue(new Callback<ValoracionUsuario>() {
            @Override
            public void onResponse(@NonNull Call<ValoracionUsuario> call,
                    @NonNull Response<ValoracionUsuario> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null
                        && response.body().getData().isHaValorado()
                        && ratingBar != null && tvSeleccionada != null) {
                    int puntuacionPrevia = response.body().getData().getPuntuacion();
                    ratingBar.setRating(puntuacionPrevia);
                    tvSeleccionada.setText(getString(R.string.rating_score_format, puntuacionPrevia));
                }
            }
            @Override
            public void onFailure(@NonNull Call<ValoracionUsuario> call, @NonNull Throwable t) {}
        });

        if (btnEnviar != null && ratingBar != null) {
            btnEnviar.setOnClickListener(v -> {
                int puntuacion = Math.round(ratingBar.getRating());
                if (puntuacion < 1) {
                    Toast.makeText(requireContext(), R.string.error_select_rating,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                btnEnviar.setEnabled(false);

                ValoracionRequest request = new ValoracionRequest(receta.getIdReceta(), puntuacion);
                valoracionService.crearValoracion(request).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call,
                            @NonNull Response<ResponseBody> response) {
                        btnEnviar.setEnabled(true);
                        if (response.isSuccessful()) {
                            adaptador.refrescarValoracionReceta(receta.getIdReceta());
                            dialog.dismiss();
                            Toast.makeText(requireContext(), R.string.review_saved, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), R.string.error_save_review,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        btnEnviar.setEnabled(true);
                        Toast.makeText(requireContext(), R.string.error_save_review_network,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        dialog.show();
    }

    /**
     * Interfaz implementada por la actividad contenedora para propagar
     * cambios de receta al fragmento de usuario.
     */
    public interface OnRecetaCambiadaListener {
        void onRecetaCambiada();
    }

    public void recargarRecetas() {
        contadorReintentos = 0;
        ocultarError();
        refrescarListadoRecetas();
    }

    private void refrescarListadoRecetas() {
        boolean hayFiltrosActivos = !ultimaConsultaTexto.isEmpty()
                || !ultimaConsultaIngredientes.isEmpty()
                || !ultimaConsultaTipos.isEmpty();

        paginaActual = 1;
        esUltimaPagina = false;
        cargando = false;
        paginaBusquedaActual = 1;
        esUltimaPaginaBusqueda = false;
        recetasPrecargadas = false;
        if (adaptador != null) {
            adaptador.actualizarDatos(new ArrayList<>());
        }

        if (hayFiltrosActivos) {
            cargarTodasLasRecetasParaBusqueda();
            aplicarFiltrosCombinados();
        } else {
            modoBusqueda = false;
            cargarRecetasDesdeAPI();
            cargarTodasLasRecetasParaBusqueda();
        }
    }

    /**
     * Muestra el panel de error con el mensaje.
     * @param mensaje Texto a mostrar en el panel de error
     * @param conBoton Si es {@code true}, muestra el botón de reintentar; si no, lo oculta
     */
    private void mostrarError(String mensaje, boolean conBoton) {
        if (errorContainer != null && rvRecetas != null) {
            rvRecetas.setVisibility(View.GONE);
            errorContainer.setVisibility(View.VISIBLE);
            if (tvErrorMensaje != null && mensaje != null) {
                tvErrorMensaje.setText(mensaje);
            }
            if (btnReintentar != null) {
                btnReintentar.setVisibility(conBoton ? View.VISIBLE : View.GONE);
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
        handlerReintento.removeCallbacksAndMessages(null);
    }
}
