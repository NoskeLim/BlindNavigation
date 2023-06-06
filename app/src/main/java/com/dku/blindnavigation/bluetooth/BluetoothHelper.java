package com.dku.blindnavigation.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.dku.blindnavigation.navigation.direction.DirectionType;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BluetoothHelper {
    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter btAdapter;
    private BluetoothClientThread clientThread;

    public BluetoothHelper(BluetoothAdapter btAdapter) {
        this.btAdapter = btAdapter;
    }

    public void startDiscovery() {
        btAdapter.startDiscovery();
    }

    public Set<BluetoothDevice> getPairedDevices() {
        return btAdapter.getBondedDevices();
    }

    public boolean connectBluetoothDevice(String macAddress) {
        btAdapter.cancelDiscovery();
        BluetoothDevice remoteDevice = btAdapter.getRemoteDevice(macAddress);
        try {
            clientThread = new BluetoothClientThread(remoteDevice.createRfcommSocketToServiceRecord(uuid));
            clientThread.start();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean sendDirectionToDevice(DirectionType directionType) {
        if(clientThread == null || !clientThread.isAlive()) return false;
        try {
            clientThread.sendMessage(String.valueOf(directionType.ordinal()));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void disconnectDevice() {
        if(clientThread == null || !clientThread.isAlive()) return;
        try {
            clientThread.finish();
        } catch (IOException e) {

        }
    }
}
