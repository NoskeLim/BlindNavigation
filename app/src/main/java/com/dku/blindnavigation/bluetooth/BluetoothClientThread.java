package com.dku.blindnavigation.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;

import java.io.DataOutputStream;
import java.io.IOException;

public class BluetoothClientThread extends Thread {
    private final BluetoothSocket btSocket;
    private DataOutputStream dos;

    public BluetoothClientThread(BluetoothSocket btSocket) throws IOException {
        this.btSocket = btSocket;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        try {
            btSocket.connect();
            this.dos = new DataOutputStream(btSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String msg) throws IOException {
        if(btSocket == null || !btSocket.isConnected())
            throw new RuntimeException();
        dos.writeUTF(msg);
    }

    public void finish() throws IOException {
        if(dos != null) dos.close();
        if(btSocket != null) btSocket.close();
    }
}
