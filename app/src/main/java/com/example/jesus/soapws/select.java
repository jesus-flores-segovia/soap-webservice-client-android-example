package com.example.jesus.soapws;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.URLEncoder;

public class select extends AppCompatActivity {

    public static final String URL = "http://rideapp.somee.com/WebService.asmx";
    public static final String METHOD_NAME = "obtenerUsuario";
    public static final String SOAP_ACTION = "http://tempuri.org/obtenerUsuario";
    public static final String NAMESPACE = "http://tempuri.org/";

    private EditText idUsuario;
    private TextView usuario, password, nombre, apellidos, avatar, descripcion, correo;

    private UsuarioDTO user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        idUsuario = findViewById(R.id.editText5);
        usuario = findViewById(R.id.textView);
        password = findViewById(R.id.textView2);
        nombre = findViewById(R.id.textView3);
        apellidos = findViewById(R.id.textView4);
        avatar = findViewById(R.id.textView5);
        descripcion = findViewById(R.id.textView6);
        correo = findViewById(R.id.textView7);
    }

    public void select(View view){

        new TareaWSConsulta(getApplicationContext()).execute(idUsuario.getText().toString());
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
            request.addProperty("idUsuario", params[0]);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try {
                transporte.call(SOAP_ACTION,envelope);
                SoapObject resSoap = (SoapObject)envelope.getResponse();

                // Se corrige el password obtenido, eliminando caracteres de control '%00'. Acuerdate que tambien elimina espacios, VALIDAR PASS AL CREAR USUARIO
                String pass = resSoap.getPropertyAsString(2).replaceAll("\\W", "");
                user = new UsuarioDTO(Integer.valueOf(resSoap.getPropertyAsString(0)), resSoap.getPropertyAsString(1), pass, resSoap.getPropertyAsString(3),
                        resSoap.getPropertyAsString(4), resSoap.getPropertyAsString(5), resSoap.getPropertyAsString(6), resSoap.getPropertyAsString(7));

            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }


            return result;
        }

        protected void onPostExecute(Boolean result) {
            if(result){
                usuario.setText(user.getUsuario());
                password.setText(user.getPassword());
                nombre.setText(user.getNombre());
                apellidos.setText(user.getApellidos());
                avatar.setText(user.getAvatar());
                descripcion.setText(user.getDescripcion());
                correo.setText(user.getCorreo());
            }else{
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
