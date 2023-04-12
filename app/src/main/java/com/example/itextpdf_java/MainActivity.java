package com.example.itextpdf_java;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.example.itextpdf_java.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    ArrayList<Usuario> listaUsuarios = new ArrayList();

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isAceptado -> {
                if (isAceptado) Toast.makeText(this, "PERMISOS CONCECIDOS", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "PERMISOS DENEGADOS", Toast.LENGTH_SHORT).show();
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listaUsuarios.add(new Usuario("xcheko51x", "Sergio Peralta", "sergiop@local.com"));
        listaUsuarios.add(new Usuario("laurap", "Laura Perez", "laurap@local.com"));
        listaUsuarios.add(new Usuario("juanm", "Juan Morales", "juanm@local.com"));

        binding.btnCrearPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarPermisos(view);
            }
        });
    }

    private void verificarPermisos(View view) {
        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show();
            crearPDF();
        } else if(ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )) {
            Snackbar.make(view, "ESTE PERMISO ES NECESARIO PARA CREAR EL ARCHIVO", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }).show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void crearPDF() {
        try {
            String carpeta = "/archivospdf";
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + carpeta;

            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
                Toast.makeText(this, "CARPETA CREADA", Toast.LENGTH_SHORT).show();
            }

            File archivo = new File(dir, "usuarios.pdf");
            FileOutputStream fos = new FileOutputStream(archivo);

            Document documento = new Document();
            PdfWriter.getInstance(documento, fos);

            documento.open();

            Paragraph titulo = new Paragraph(
                    "Lista de usuarios\n\n\n",
                    FontFactory.getFont("arial", 22, Font.BOLD, BaseColor.BLUE)
            );
            documento.add(titulo);

            PdfPTable tabla = new PdfPTable(3);
            tabla.addCell("USUARIO");
            tabla.addCell("NOMBRE");
            tabla.addCell("EMAIL");

            for (int i = 0 ; i < listaUsuarios.size() ; i++) {
                tabla.addCell(listaUsuarios.get(i).usuario);
                tabla.addCell(listaUsuarios.get(i).nombre);
                tabla.addCell(listaUsuarios.get(i).email);
            }

            documento.add(tabla);

            documento.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch ( DocumentException e) {
            e.printStackTrace();
        }
    }
}