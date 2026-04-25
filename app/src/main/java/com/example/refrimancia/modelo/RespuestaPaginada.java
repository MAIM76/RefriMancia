package com.example.refrimancia.modelo;

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

    public String getStatus() {
        return status;
    }

    public List<T> getData() {
        return data;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Integer getTotalItems() {
        return totalItems;
    }
}
