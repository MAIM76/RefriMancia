package com.example.refrimancia.modelo.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RespuestaPaginada<T> {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<T> data;

    @SerializedName(value = "page", alternate = {"pagina_actual"})
    private Integer page;

    @SerializedName(value = "total_pages", alternate = {"paginas_totales"})
    private Integer totalPages;

    @SerializedName(value = "total_items", alternate = {"total"})
    private Integer totalItems;

    @SerializedName("paginacion")
    private Paginacion paginacion;

    public String getStatus() {
        return status;
    }

    public List<T> getData() {
        return data;
    }

    public Integer getPage() {
        if (page != null) {
            return page;
        }
        return paginacion != null ? paginacion.getPaginaActual() : null;
    }

    public Integer getTotalPages() {
        if (totalPages != null) {
            return totalPages;
        }
        return paginacion != null ? paginacion.getTotalPaginas() : null;
    }

    public Integer getTotalItems() {
        if (totalItems != null) {
            return totalItems;
        }
        return paginacion != null ? paginacion.getTotalItems() : null;
    }

    public Integer getPageSize() {
        return paginacion != null ? paginacion.getItemsPorPagina() : null;
    }

    public static class Paginacion {
        @SerializedName(value = "total_recetas", alternate = {"total_usuarios", "total_comentarios"})
        private Integer totalItems;

        @SerializedName("total_paginas")
        private Integer totalPaginas;

        @SerializedName("pagina_actual")
        private Integer paginaActual;

        @SerializedName(value = "recetas_por_pagina", alternate = {"usuarios_por_pagina", "comentarios_por_pagina"})
        private Integer itemsPorPagina;

        public Integer getTotalItems() {
            return totalItems;
        }

        public Integer getTotalPaginas() {
            return totalPaginas;
        }

        public Integer getPaginaActual() {
            return paginaActual;
        }

        public Integer getItemsPorPagina() {
            return itemsPorPagina;
        }
    }
}
