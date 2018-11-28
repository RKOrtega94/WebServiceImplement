package com.rkosistemas.appwebservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String fileName = "data";
    String name;
    private TextView txt;
    private TextView prueba;
    String url = "http://192.168.1.8:8080/WebServiceExample/webresources/empresaFacadeREST/list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = (TextView) findViewById(R.id.txt);
        prueba = (TextView) findViewById(R.id.txtPrueba);
        getRestfullXML();
//        parseXML();
    }

    private void getRestfullXML(){
        Context context = null;
        final Context finalContext = context;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                name = (String) response;
                parseXML();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                prueba.setText("fallo");
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }

    private void parseXML(){
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = new ByteArrayInputStream(name.getBytes());
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            processParsing(parser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException{
        ArrayList<Empresa> empresas = new ArrayList<>();
        int eventType = parser.getEventType();
        Empresa currentEmpresa = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String eltName;

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();
                    if("empresa".equals(eltName)){
                        currentEmpresa = new Empresa();
                        empresas.add(currentEmpresa);
                    } else if (currentEmpresa != null){
                        if ("empresaId".equals(eltName)){
                            currentEmpresa.empresaId = parser.nextText();
                        } else if ("empresaRazonSocial".equals(eltName)){
                            currentEmpresa.empresaRazonSocial = parser.nextText();
                        } else if ("empresaRuc".equals(eltName)){
                            currentEmpresa.empresaRuc = parser.nextText();
                        } else {
                            txt.setText("null");
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }
        printEmpresas(empresas);
    }

    private void printEmpresas(ArrayList<Empresa> empresas){
        StringBuilder builder = new StringBuilder();

        for(Empresa empresa : empresas){
            builder.append(empresa.empresaId).append("\n")
                    .append(empresa.empresaRazonSocial).append("\n")
                    .append(empresa.empresaRuc).append("\n\n");
        }
        txt.setText(builder.toString());
    }

    private void saveData(){

    }
}
