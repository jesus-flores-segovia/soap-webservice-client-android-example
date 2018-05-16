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

public class update extends AppCompatActivity {

    public static final String URL = "http://rideapp.somee.com/WebService.asmx";
    public static final String METHOD_NAME = "modificarUsuario";
    public static final String SOAP_ACTION = "http://tempuri.org/modificarUsuario";
    public static final String NAMESPACE = "http://tempuri.org/";

    private EditText idUsuario, password, nombre, apellidos, avatar, descripcion, correo;

    private UsuarioDTO user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        idUsuario = findViewById(R.id.editText7);
        password = findViewById(R.id.editText8);
        nombre = findViewById(R.id.editText9);
        apellidos = findViewById(R.id.editText10);
        avatar = findViewById(R.id.editText11);
        descripcion = findViewById(R.id.editText12);
        correo = findViewById(R.id.editText14);
    }

    public void update(View view){
        new TareaWSConsulta2(getApplicationContext()).execute(idUsuario.getText().toString());
    }

    //Tarea Asíncrona para llamar al WS de consulta en segundo plano
    private class TareaWSConsulta extends AsyncTask<UsuarioDTO,Void,Integer> {

        private Context context;

        public TareaWSConsulta(Context context) {
            this.context = context;
        }

        protected Integer doInBackground(UsuarioDTO... params) {

            Integer result = 0;

            PropertyInfo pi = new PropertyInfo();
            pi.setName("usuario");
            pi.setValue(params[0]);
            pi.setType(params[0].getClass());

            SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME);
            request.addProperty(pi);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(NAMESPACE, "UsuarioDTO", params[0].getClass());

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
                Toast.makeText(this.context, "No se ha podido modificar el usuario", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this.context, "Se ha modificado correctamente", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Tarea Asíncrona para llamar al WS de consulta en segundo plano
    private class TareaWSConsulta2 extends AsyncTask<String,Void,Boolean> {

        private Context context;

        public TareaWSConsulta2(Context context) {
            this.context = context;
        }

        protected Boolean doInBackground(String... params) {

            Boolean result = true;

            SoapObject request = new SoapObject(NAMESPACE,select.METHOD_NAME);
            request.addProperty("idUsuario", params[0]);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try {
                transporte.call(select.SOAP_ACTION,envelope);
                SoapObject resSoap = (SoapObject)envelope.getResponse();
                user = new UsuarioDTO(Integer.valueOf(resSoap.getPropertyAsString(0)), resSoap.getPropertyAsString(1), resSoap.getPropertyAsString(2), resSoap.getPropertyAsString(3),
                        resSoap.getPropertyAsString(4), resSoap.getPropertyAsString(5), resSoap.getPropertyAsString(6), resSoap.getPropertyAsString(7));

            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }


            return result;
        }

        protected void onPostExecute(Boolean result) {
            if(result){
                user.setPassword(password.getText().toString());
                user.setNombre(nombre.getText().toString());
                user.setApellidos(apellidos.getText().toString());
                user.setAvatar(avatar.getText().toString());
                user.setDescripcion(descripcion.getText().toString());
                user.setCorreo(correo.getText().toString());
                new TareaWSConsulta(getApplicationContext()).execute(user);
            }else{
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
