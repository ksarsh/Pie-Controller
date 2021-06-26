package com.example.rpi_controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    //Design attributes
    Button btnOn;
    Button btnOff;
    EditText ipAddr;

    // Networking attributes
    Socket appSocket = null;
    public static String wifiIP = "";
    public static int wifiPort = 0;
    public static String CMD = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOn = (Button) findViewById(R.id.btnOn);
        btnOff = (Button) findViewById(R.id.btnOff);
        ipAddr = (EditText) findViewById(R.id.ipAddr);

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
    }

    public void getIpAndPort()
    {
        String ipAndPort = ipAddr.getText().toString();
        Log.d("Arjun Test", "IP: " + ipAndPort);
        String temp[] = ipAndPort.split(":");
        wifiIP = temp[0];
        wifiPort = Integer.valueOf(temp[1]);
        Log.d("Arjun Test", "IP: " + wifiIP);
        Log.d("Arjun Test", "Port: " + wifiPort);
    }


    public class SocketAsyncTask extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            Socket socket;
            try{
                InetAddress address = InetAddress.getByName(MainActivity.wifiIP);
                socket = new java.net.Socket(address, MainActivity.wifiPort);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeBytes(CMD);
                dataOutputStream.close();
                socket.close();
            }catch(UnknownHostException err){err.printStackTrace();}catch (IOException err){err.printStackTrace();}
            return null;
        }
    }
}