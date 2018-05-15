package com.example.jesus.soapws;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String URL = "http://rideapp.somee.com/WebService.asmx";
    public static final String METHOD_NAME = "nuevoUsuario";
    public static final String SOAP_ACTION = "http://tempuri.org/nuevoUsuario";
    public static final String NAMESPACE = "http://tempuri.org/";

    private EditText idUsuario, password, nombre, apellidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idUsuario = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        nombre = findViewById(R.id.editText3);
        apellidos = findViewById(R.id.editText4);

    }

    public void insertar(View view){
        String idUsuario2 = idUsuario.getText().toString();
        String password2 = password.getText().toString();
        String nombre2 = nombre.getText().toString();
        String apellidos2 = apellidos.getText().toString();

        new TareaWSConsulta(getApplicationContext()).execute(idUsuario2, password2, nombre2, apellidos2, "", "");
    }

    public void select(View view){
        Intent i = new Intent(this, select.class);
        startActivity(i);
    }

    public void selectAll(View view){
        Intent i = new Intent(this, selectAll.class);
        startActivity(i);
    }

    public void update(View view){
        Intent i = new Intent(this, update.class);
        startActivity(i);
    }

    public void delete(View view){
        Intent i = new Intent(this, delete.class);
        startActivity(i);
    }

    //Tarea Asíncrona para llamar al WS de consulta en segundo plano
    private class TareaWSConsulta extends AsyncTask<String,Void,Integer> {

        private Context context;

        public TareaWSConsulta(Context context) {
            this.context = context;
        }

        protected Integer doInBackground(String... params) {

            Integer result = 0;

            UsuarioDTO usuario = new UsuarioDTO(params[0], params[1], params[2], params[3], params[4], params[5]);

            PropertyInfo pi = new PropertyInfo();
            pi.setName("usuario");
            pi.setValue(usuario);
            pi.setType(usuario.getClass());

            SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
            request.addProperty(pi);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(NAMESPACE, "UsuarioDTO", usuario.getClass());

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try {
                transporte.call(SOAP_ACTION,envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                result = Integer.parseInt(response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }


            return result;
        }

        protected void onPostExecute(Integer result) {
            if(result.equals(new Integer(0))){
                Toast.makeText(this.context, "No se ha podido crear el usuario", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this.context, "Se ha creado correctamente", Toast.LENGTH_LONG).show();
            }
        }
    }
}
