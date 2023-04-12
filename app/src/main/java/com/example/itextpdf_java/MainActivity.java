package com.example.itextpdf_java;

import androidx.activity.result.ActivityResultLauncher;
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
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.interfaces.PdfDocumentActions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        final List<Map<String, String>>[] dataCargaCerrarCobros =  new List[]{new ArrayList<Map<String, String>>()};
        List<Map<String,String>> dataCerrarCobros = new ArrayList<Map<String, String>>();

        Map<String,String> tab = new HashMap<String,String>();

        tab.put("FormaPagoCC",  "1");
        tab.put("ClienteCC",    "2");
        tab.put("GuiaCC",       "3");
        tab.put("TotalCC",      "4");
        tab.put("TipoCC",       "5");
        tab.put("ValorCC",      "6");
        tab.put("ObservacionCC","7");
        tab.put("BancoCC",      "8");
        tab.put("ChequeCC",     "9");
        tab.put("IdCC",         "10");
        dataCerrarCobros.add(tab);
        dataCargaCerrarCobros[0] = dataCerrarCobros;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listaUsuarios.add(new Usuario("xcheko51x", "Sergio Peralta", "sergiop@local.com"));

        binding.btnCrearPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarPermisos(view, dataCargaCerrarCobros);
            }
        });
    }

    private void verificarPermisos(View view, List<Map<String, String>>[] dataCargaCerrarCobros) {
        if (
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show();
            crearPDF(dataCargaCerrarCobros);
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

    private void crearPDF(List<Map<String, String>>[] dataCargaCerrarCobros) {
        try {
            String carpeta = "/cerrar_cobros";
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + carpeta;

            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
                Toast.makeText(this, "CARPETA CREADA", Toast.LENGTH_SHORT).show();
            }

            File archivo = new File(dir, "COBRO.pdf");

            FileOutputStream fos = new FileOutputStream(archivo);

            Document documento = new Document();
            PdfWriter.getInstance(documento, fos);

            documento.open();

            Paragraph titulo = new Paragraph(
                    "COBRO PDF\n\n\n",
                    FontFactory.getFont("arial", 22, Font.BOLD, BaseColor.BLACK)
            );
            documento.add(titulo);

            int contador = 0;


            for (Map<String, String> variable:dataCargaCerrarCobros[0]
            ) {
                contador = variable.keySet().size();
            }


            System.out.println("columnas: " + contador);

            PdfPTable tabla = new PdfPTable(contador);
            tabla.setWidthPercentage(100);
            for (Map<String, String> variable:dataCargaCerrarCobros[0]
            ) {
                for(int i = 0; i < contador; i++){
                    Paragraph cabecera = new Paragraph(
                            variable.keySet().toArray()[i].toString().toUpperCase(),
                            FontFactory.getFont("arial", 10, Font.BOLD, BaseColor.BLACK)
                    );
                    cabecera.setAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(cabecera);
                }
            }

            for (Map<String, String> variable:dataCargaCerrarCobros[0]
            ) {
                for(int i = 0; i < contador; i++){
                    Paragraph dato = new Paragraph(
                            variable.values().toArray()[i].toString(),
                            FontFactory.getFont("arial", 10, BaseColor.BLACK)
                    );
                    dato.setAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(dato);
                }
            }

            documento.add(tabla);

            documento.close();
            System.out.println("PDF GENERADO");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch ( DocumentException e) {
            e.printStackTrace();
        }
    }
}
