package com.example.jesus.soapws;

import android.content.Context;
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

public class delete extends AppCompatActivity {

    public static final String URL = "http://rideapp.somee.com/WebService.asmx";
    public static final String METHOD_NAME = "borrarUsuario";
    public static final String SOAP_ACTION = "http://tempuri.org/borrarUsuario";
    public static final String NAMESPACE = "http://tempuri.org/";

    private EditText idUsuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        idUsuario = findViewById(R.id.editText6);
    }

    public void delete(View view){
        String usuario = idUsuario.getText().toString();
        new TareaWSConsulta(getApplicationContext()).execute(usuario);
    }

    //Tarea Asíncrona para llamar al WS de consulta en segundo plano
    private class TareaWSConsulta extends AsyncTask<String,Void,Boolean> {

        private Context context;

        public TareaWSConsulta(Context context) {
            this.context = context;
        }

        protected Boolean doInBackground(String... params) {

            Boolean result = true;

            SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
            request.addProperty("usuario", params[0]);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try {
                transporte.call(SOAP_ACTION,envelope);
                SoapPrimitive resSoap = (SoapPrimitive) envelope.getResponse();
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }


            return result;
        }

        protected void onPostExecute(Boolean result) {
            if(result){
                Toast.makeText(getApplicationContext(), "El usuario ha sido borrado", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "No ha podido borrarse el usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
