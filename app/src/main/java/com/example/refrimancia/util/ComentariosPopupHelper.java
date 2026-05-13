package com.example.refrimancia.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import com.example.refrimancia.R;
import com.example.refrimancia.adapter.ComentarioAdapter;
import com.example.refrimancia.api.ClienteRetrofit;
import com.example.refrimancia.api.ComentarioService;
import com.example.refrimancia.model.entity.Comentario;
import com.example.refrimancia.model.entity.Receta;
import com.example.refrimancia.model.request.ComentarioRequest;
import com.example.refrimancia.model.response.RespuestaPaginada;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComentariosPopupHelper {

    public static void mostrar(Context context, Receta receta) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_comentarios);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) (context.getResources().getDisplayMetrics().heightPixels * 0.8));
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.5f);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }

        dialog.setCanceledOnTouchOutside(true);

        RecyclerView rvComentarios = dialog.findViewById(R.id.rv_comentarios);
        rvComentarios.setLayoutManager(new LinearLayoutManager(context));
        ComentarioAdapter adaptadorComentario = new ComentarioAdapter(new ArrayList<>());
        adaptadorComentario.setIdUsuarioLogueado(new SessionManager(context).fetchUserId());
        rvComentarios.setAdapter(adaptadorComentario);

        EditText etNuevoComentario = dialog.findViewById(R.id.et_nuevo_comentario);
        ImageButton btnEnviarComentario = dialog.findViewById(R.id.btn_enviar_comentario);
        View layoutModoEdicion = dialog.findViewById(R.id.layout_modo_edicion);
        TextView btnCancelarEdicion = dialog.findViewById(R.id.btn_cancelar_edicion);

        ComentarioService comentarioService =
                ClienteRetrofit.obtenerInstancia(context).create(ComentarioService.class);
        cargarComentarios(context, comentarioService, receta.getIdReceta(), adaptadorComentario);

        final int[] comentarioEditandoId = {-1};

        Runnable cancelarEdicion = () -> {
            comentarioEditandoId[0] = -1;
            etNuevoComentario.setText("");
            etNuevoComentario.setHint(R.string.popup_comments_hint);
            layoutModoEdicion.setVisibility(View.GONE);
        };

        btnCancelarEdicion.setOnClickListener(v -> cancelarEdicion.run());

        adaptadorComentario.setOnEditarListener(comentario -> {
            comentarioEditandoId[0] = comentario.getIdComentario();
            etNuevoComentario.setText(comentario.getTexto());
            etNuevoComentario.setSelection(etNuevoComentario.getText().length());
            layoutModoEdicion.setVisibility(View.VISIBLE);
            etNuevoComentario.requestFocus();
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(etNuevoComentario, InputMethodManager.SHOW_IMPLICIT);
        });

        adaptadorComentario.setOnEliminarListener(comentario ->
                new AlertDialog.Builder(context)
                        .setTitle(R.string.comment_delete_title)
                        .setMessage(R.string.comment_delete_message)
                        .setPositiveButton(R.string.comment_delete_confirm, (d, w) ->
                                comentarioService.eliminarComentario(comentario.getIdComentario())
                                        .enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(@NonNull Call<ResponseBody> call,
                                                    @NonNull Response<ResponseBody> response) {
                                                if (response.isSuccessful()) {
                                                    cargarComentarios(context, comentarioService,
                                                            receta.getIdReceta(), adaptadorComentario);
                                                    Toast.makeText(context,
                                                            R.string.comment_deleted, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context,
                                                            R.string.error_delete_comment, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            @Override
                                            public void onFailure(@NonNull Call<ResponseBody> call,
                                                    @NonNull Throwable t) {
                                                Toast.makeText(context,
                                                        R.string.error_delete_comment, Toast.LENGTH_SHORT).show();
                                            }
                                        }))
                        .setNegativeButton(R.string.comment_delete_cancel, null)
                        .show());

        if (btnEnviarComentario != null && etNuevoComentario != null) {
            btnEnviarComentario.setOnClickListener(v -> {
                String mensaje = etNuevoComentario.getText().toString().trim();
                if (mensaje.isEmpty()) {
                    Toast.makeText(context, R.string.error_comment_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                btnEnviarComentario.setEnabled(false);

                if (comentarioEditandoId[0] != -1) {
                    final int idEditar = comentarioEditandoId[0];
                    comentarioService.modificarComentario(idEditar, new ComentarioRequest(0, mensaje)).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call,
                                @NonNull Response<ResponseBody> response) {
                            btnEnviarComentario.setEnabled(true);
                            if (response.isSuccessful()) {
                                cancelarEdicion.run();
                                cargarComentarios(context, comentarioService,
                                        receta.getIdReceta(), adaptadorComentario);
                                Toast.makeText(context,
                                        R.string.comment_updated, Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    String errorBody = response.errorBody() != null
                                            ? response.errorBody().string() : "null";
                                    Log.e("ComentariosPopup", "modificar error " + response.code() + ": " + errorBody);
                                } catch (Exception ignored) {}
                                Toast.makeText(context,
                                        R.string.error_update_comment, Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            btnEnviarComentario.setEnabled(true);
                            Log.e("ComentariosPopup", "modificar onFailure: " + t.getMessage());
                            Toast.makeText(context,
                                    R.string.error_update_comment, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    ComentarioRequest comentarioRequest = new ComentarioRequest(receta.getIdReceta(), mensaje);
                    comentarioService.crearComentario(comentarioRequest).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call,
                                @NonNull Response<ResponseBody> response) {
                            btnEnviarComentario.setEnabled(true);
                            if (response.isSuccessful()) {
                                etNuevoComentario.setText("");
                                cargarComentarios(context, comentarioService, receta.getIdReceta(),
                                        adaptadorComentario);
                                Toast.makeText(context, R.string.comment_published,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, R.string.error_publish_comment,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            btnEnviarComentario.setEnabled(true);
                            Toast.makeText(context, R.string.error_publish_comment_network,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        dialog.show();
    }

    public static void cargarComentarios(Context context, ComentarioService comentarioService,
            int recetaId, ComentarioAdapter adaptadorComentario) {
        comentarioService.obtenerComentariosPorReceta(recetaId).enqueue(
                new Callback<RespuestaPaginada<Comentario>>() {
                    @Override
                    public void onResponse(@NonNull Call<RespuestaPaginada<Comentario>> call,
                            @NonNull Response<RespuestaPaginada<Comentario>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Comentario> comentarios = response.body().getData();
                            if (comentarios != null) {
                                adaptadorComentario.setComentarios(comentarios);
                            }
                        } else {
                            Toast.makeText(context, R.string.error_load_comments,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<RespuestaPaginada<Comentario>> call,
                            @NonNull Throwable t) {
                        Toast.makeText(context, R.string.error_load_comments_network,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
