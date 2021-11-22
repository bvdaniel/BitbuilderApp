package com.example.bitbuilderapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    //Popup transaccion
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    TextView  output_estado2, output_e, output_t_fin, output_t_ini2;
    Button button_ok_terminar;

    //Popup chequeo
    TextView titulo_popupck, text_t_inick, text_energia_check, output_t_ini2ck, pregunta_check, output_eck;
    Button button_nueva, button_anterior;

    //Popup notyet
    TextView titulo_notyet;
    Button button_notyet;

    //Página principal
    private static final int GATT = 1;
    Button button_inicio, button_fin, button_desconectar;
    TextView titulo, text_estado_b, output_bluetooth, text_id_miner, output_id, output_user,text_user, text_timestamp_i, output_t_ini,
    text_t_ini, text_t_final, text_energia, text_estado_t;


    //-------------------------------------------
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;
    //-------------------------------------------
    DateFormat df = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
    String id = "";
    String date_ini ="";
    String date_fin ="";
    String checkener ="";
    String t_anterior ="";
    String username ="";
    String[] block= new String[]{"","","",""}; // 0: device name /1 Timestamp inicial / 2: Timestamp final / 3: Energía
    int corrigiendo = 0;
    int terminado =0;
    int notyet = 0;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        bluetoothIn = new Handler() {

            @SuppressLint("SetTextI18n")
            public void handleMessage(android.os.Message msg) {
                while(true) {  if (msg.what == handlerState) {

                        byte[] readbuff = (byte[]) msg.obj;

                        String s = new String(readbuff, 0, msg.arg1);

                                String[] sep = s.split("#");


                                if (sep[0].substring(0,1).equals("!")){
                                    checkener = sep[0].substring(1);
                                    float fener = 3.003f;
                                    fener = Float.parseFloat(checkener) / 1000;
                                    String formate = String.format("%.03f", fener);
                                    output_bluetooth.setText("Registro iniciado");
                                    output_t_ini.setText(sep[1]);
                                    break;
                                }

                                if (sep[0].substring(0,1).equals("*") && !sep[1].equals("E")){
                                    checkener = sep[0].substring(1);
                                    float fener = 3.003f;
                                    fener = Float.parseFloat(checkener) / 1000;
                                    String formate = String.format("%.03f", fener);
                                    output_eck.setText(formate + " kWh");
                                    t_anterior = sep[1];
                                    output_t_ini2ck.setText(sep[1]);
                                    break;
                                }
                                if (s.equals("=")){
                                    createNotyetDialog();
                                    break;
                                }
                                else if (s.equals("%")){
                                    try {
                                        createTransactionDialog();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }

                                if(sep[1].equals("E")) {
                                     createCheckDialog();
                                     break;
                                 }
                                else if(sep[1]!="E"){
                                    float fener = 3.003f;
                                    fener = Float.parseFloat(sep[0]) / 1000;
                                    String formate = String.format("%.03f", fener);
                                    output_e.setText(formate + " kWh");
                                    block[3]=formate;
                                    output_t_ini2.setText(block[1]);
                                    output_t_fin.setText(block[2]);
                                    output_estado2.setText("Terminada");
                                    terminado =1 ;
                                    try {
                                        sendBlock();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                }}}
        };


        btAdapter = BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();

        button_inicio = findViewById(R.id.button_inicio);
        button_fin = findViewById(R.id.button_fin);
        button_desconectar = findViewById(R.id.button_desconectar);
        output_e = (TextView) findViewById(R.id.output_e);

        titulo = findViewById(R.id.titulo);
        text_estado_b = findViewById(R.id.text_estado_b);
        output_bluetooth = findViewById(R.id.output_bluetooth);
        text_id_miner = findViewById(R.id.text_id_miner);
        output_id = findViewById(R.id.output_id);
        text_timestamp_i = findViewById(R.id.text_timestamp_i);
        output_t_ini = findViewById(R.id.output_t_ini);

        output_bluetooth.setText("En espera");
        output_user = findViewById(R.id.output_user);
        username = BackgroundTask.getUser();

        output_user.setText(username);


        button_inicio.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                date_ini = df.format(Calendar.getInstance().getTime());
                MyConexionBT.write("#" + date_ini);

            }
        });
        button_fin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                MyConexionBT.write("$");


                    }
                });

        button_desconectar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if (btSocket!=null)
                {
                    try {btSocket.close();}
                    catch (IOException e)
                    { Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();;}
                }
                finish();
            }
        });

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {

        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra(DispositivosVinculados.EXTRA_DEVICE_ADDRESS);
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
       // devicename = device.getName();
        output_id.setText(device.getName());
        block[0] = device.getName();

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();


    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            btSocket.close();
        } catch (IOException e2) {}
    }

    //Comprueba que el dispositivo Bluetooth
    //está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //Crea la clase que permite crear el evento de conexión
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run()
        {
            byte[] buffer = new byte[2048];
            int bytes;
            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                 //   bluetoothIn.obtainMessage(handlerState, bytes).sendToTarget();
                    bluetoothIn.obtainMessage(handlerState,bytes,-1,buffer).sendToTarget();

                    //  hanlder.obtainMessage()
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }
    @SuppressLint("HandlerLeak")
    public void createTransactionDialog() throws InterruptedException {
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popup,null);
        text_t_ini = (TextView) contactPopupView.findViewById(R.id.text_t_ini);
        output_t_ini2 = (TextView) contactPopupView.findViewById(R.id.output_t_ini2);
        text_t_final = (TextView) contactPopupView.findViewById(R.id.text_t_ini);
        output_t_fin = (TextView) contactPopupView.findViewById(R.id.output_t_fin);
        text_energia =(TextView) contactPopupView. findViewById(R.id.text_energia);
        output_e = (TextView) contactPopupView.findViewById(R.id.output_e);
        text_estado_t = (TextView) contactPopupView.findViewById(R.id.text_estado_t);
        output_estado2 = (TextView) contactPopupView.findViewById(R.id.output_estado2);
        button_ok_terminar = (Button)contactPopupView.findViewById(R.id.button_nueva);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        date_fin = df.format(Calendar.getInstance().getTime());
        block[2]=date_fin;
        MyConexionBT.write("/"+date_fin);

        if (corrigiendo ==1){
            output_t_ini2.setText(t_anterior);
            block[1] = t_anterior;
        }
        else if (corrigiendo ==0){output_t_ini2.setText(date_ini);
            block[1] = date_ini;};

        button_ok_terminar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                output_t_ini.setText("");
                output_bluetooth.setText("Registro enviado");
                date_ini ="";
                date_fin ="";
                dialog.dismiss();
                notyet=0;
            }

        });
    }
    public void sendBlock() throws InterruptedException {

        String method = "send";
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method, username, block[0], block[1], block[2], block[3]);

    }
    @SuppressLint("HandlerLeak")
    public void createCheckDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popupcheck,null);

        titulo_popupck = (TextView) contactPopupView.findViewById(R.id.titulo_popupck);
        text_t_inick = (TextView) contactPopupView.findViewById(R.id.text_t_inick);
        output_t_ini2ck = (TextView) contactPopupView.findViewById(R.id.output_t_ini2ck);
        text_energia_check =(TextView) contactPopupView. findViewById(R.id.text_energia_ck);
        output_eck = (TextView) contactPopupView.findViewById(R.id.output_eck);
        pregunta_check = (TextView) contactPopupView.findViewById(R.id.pregunta_check);
        button_nueva = (Button) contactPopupView.findViewById(R.id.button_nueva);
        button_anterior = (Button)contactPopupView.findViewById(R.id.button_anterior);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        MyConexionBT.write("*");

        button_nueva.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                MyConexionBT.write("|");
                output_t_ini.setText("");
                output_bluetooth.setText("Comience nuevo registro: 'En espera'");


                dialog.dismiss();
            }

        });
        button_anterior.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                dialog.dismiss();
                corrigiendo = 1;
                try {
                    createTransactionDialog();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                corrigiendo = 0;
            }

        });
    }


    @SuppressLint("HandlerLeak")
    public void createNotyetDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.popupnotyet,null);
        titulo_notyet = (TextView) contactPopupView.findViewById(R.id.titulo_notyet);
        button_notyet = (Button) contactPopupView.findViewById(R.id.button_notyet);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        button_notyet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }

        });
    }

}