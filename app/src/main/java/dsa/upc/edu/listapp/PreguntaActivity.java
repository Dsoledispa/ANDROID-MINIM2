package dsa.upc.edu.listapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dsa.upc.edu.listapp.store.Pregunta;
import dsa.upc.edu.listapp.store.StoreAPI;
import dsa.upc.edu.listapp.auth.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreguntaActivity extends AppCompatActivity {

    private EditText etTitulo, etMensaje, etRemitente;
    private Button btnEnviarPregunta, btnCancelar;
    private StoreAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);

        // Obtener referencia a los elementos del layout
        etTitulo = findViewById(R.id.etTitulo);
        etMensaje = findViewById(R.id.etMensaje);
        etRemitente = findViewById(R.id.etRemitente);
        btnEnviarPregunta = findViewById(R.id.btnEnviarPregunta);
        btnCancelar = findViewById(R.id.btnCancelar);

        Button btnVolverMenu = findViewById(R.id.btnVolverMenu);

        btnVolverMenu.setOnClickListener(v -> {
            Intent intent = new Intent(PreguntaActivity.this, PartidasMenuActivity.class);
            startActivity(intent);
            finish(); // opcional, si no quieres que el usuario pueda volver con el botón "Atrás"
        });

        // Obtener nombre de usuario desde el Intent (opcional)
        String nombreUsuario = getIntent().getStringExtra("nombreUsu");
        if (nombreUsuario != null) {
            etRemitente.setText(nombreUsuario);
        }

        api = ApiClient.getClient(PreguntaActivity.this).create(StoreAPI.class);

        btnEnviarPregunta.setOnClickListener(v -> {
            String titulo = etTitulo.getText().toString().trim();
            String mensaje = etMensaje.getText().toString().trim();
            String remitente = etRemitente.getText().toString().trim();

            if (titulo.isEmpty() || mensaje.isEmpty() || remitente.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Pregunta pregunta = new Pregunta();
            pregunta.setTitulo(titulo);
            pregunta.setMensaje(mensaje);
            pregunta.setRemitente(remitente);
            pregunta.setFecha(getFechaActual());

            api.registrarPregunta(pregunta).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(PreguntaActivity.this, "Pregunta enviada correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(PreguntaActivity.this, "Error al enviar la pregunta", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(PreguntaActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnCancelar.setOnClickListener(v -> finish());
    }

    private String getFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
