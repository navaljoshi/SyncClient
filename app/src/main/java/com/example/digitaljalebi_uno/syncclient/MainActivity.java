package com.example.digitaljalebi_uno.syncclient;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/*
 * Permission needed:
 * <uses-permission android:name="android.permission.INTERNET"/>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 */

public class MainActivity extends ActionBarActivity {

    EditText editTextAddress;
    Button buttonConnect;
    TextView textPort;

    static final int SocketServerPORT = 8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText) findViewById(R.id.address);
        textPort = (TextView) findViewById(R.id.port);
        textPort.setText("port: " + SocketServerPORT);
        buttonConnect = (Button) findViewById(R.id.connect);

        buttonConnect.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                ClientRxThread clientRxThread =
                        new ClientRxThread(
                                editTextAddress.getText().toString(),
                                SocketServerPORT);
                Log.d("naval- IP given", editTextAddress.getText().toString());

                clientRxThread.start();
            }});
    }

    private class ClientRxThread extends Thread {
        String dstAddress;
        int dstPort;

        ClientRxThread(String address, int port) {
            dstAddress = address;
            dstPort = port;
        }

        @Override
        public void run() {
            Socket socket = null;
            Log.d("naval- ","Inside run");

            try {
                socket = new Socket(dstAddress, dstPort);
                Log.d("naval- ","Socket created");

                File file = new File(
                        Environment.getExternalStorageDirectory(),
                        "test.png");
                Log.d("naval- ","Create File png");
                try {
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());


                    Log.d("naval- ", "Create input stream");
                    byte[] bytes;
                    Log.d("naval- ", "Creating File stream");
                    FileOutputStream fos = null;
                    try {
                        bytes = (byte[]) ois.readObject();
                        fos = new FileOutputStream(file);
                        fos.write(bytes);
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        if (fos != null) {
                            fos.close();
                        }

                    }
                }
                catch (EOFException e)
                {
                    e.printStackTrace();
                }

                socket.close();
                Log.d("naval- ", "socket closed");

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "Finished",
                                Toast.LENGTH_LONG).show();
                    }});

            } catch (IOException e) {

                e.printStackTrace();

                final String eMsg = "Something wrong: " + e.getMessage();
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                eMsg,
                                Toast.LENGTH_LONG).show();
                    }});

            } finally {
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}