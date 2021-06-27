package com.example.rpi_controller;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.content.Intent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Design attributes
    Button btnOn;
    Button btnOff;
    Button btnPlot;
    EditText ipAddr;

    // Networking attributes
    Socket appSocket = null;
    public static String wifiIP = "";
    public static int wifiPort = 0;
    public static String CMD = "0";

    // Create a Dictionary to store time & CPU temperature
    Map<Long, Float> TimeTempMap = new HashMap<Long, Float>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOn = (Button) findViewById(R.id.btnOn);
        btnOff = (Button) findViewById(R.id.btnOff);
        ipAddr = (EditText) findViewById(R.id.ipAddr);
        btnPlot = (Button) findViewById(R.id.btnPlot);

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIpAndPort();
                CMD = "On";
                SocketAsyncTask cmd_to_send = new SocketAsyncTask();
                cmd_to_send.execute();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIpAndPort();;
                CMD = "Off";
                SocketAsyncTask cmd_to_send = new SocketAsyncTask();
                cmd_to_send.execute();
            }
        });

        btnPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Arjun Plotter Test", "Hello Plotter");
                Intent intent = new Intent(MainActivity.this, Plotter.class);
                intent.putExtra("HashMap", (Serializable) TimeTempMap);
                startActivity(intent);
            }
        });
    }

    public void getIpAndPort()
    {
        String ipAndPort = ipAddr.getText().toString();
        String temp[] = ipAndPort.split(":");
        wifiIP = temp[0];
        wifiPort = Integer.valueOf(temp[1]);
        Log.d("Arjun IP Test", "IP: " + wifiIP + " Port: " + wifiPort);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertKeyValue(float fTemp){
        Instant instant = Instant.now();
        TimeTempMap.put(instant.toEpochMilli(), fTemp);
    }

    public class SocketAsyncTask extends AsyncTask<Void,Void,Void>
    {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... voids) {
            Socket socket;
            byte[] temp = new byte[4];
            try{
                InetAddress address = InetAddress.getByName(MainActivity.wifiIP);
                socket = new java.net.Socket(address, MainActivity.wifiPort);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                dataOutputStream.writeBytes(CMD);
                dataInputStream.read(temp);

                String sTemp = new String(temp, StandardCharsets.UTF_8);
                float fTemp = Float.parseFloat(sTemp);
                Log.d("Arjun float test", " " + fTemp);
                insertKeyValue(fTemp);

                dataOutputStream.close();
                dataInputStream.close();
                socket.close();
            }catch(UnknownHostException err){err.printStackTrace();}catch (IOException err){err.printStackTrace();}
            return null;
        }
    }
}