package com.dku.blindnavigation.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dku.blindnavigation.navigation.direction.DirectionType;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BluetoothHelper {
    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter btAdapter;
    private BluetoothClientThread clientThread;
    private final Context mContext;

    public BluetoothHelper(Context context) {
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
    }

    public void startDiscovery() {
        btAdapter.startDiscovery();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean connectBluetoothDevice() {
        btAdapter.cancelDiscovery();
        SharedPreferences setting = mContext.getSharedPreferences("setting", Context.MODE_PRIVATE);
        String macAddr = setting.getString("MAC_ADDR", null);
        if(macAddr == null || macAddr.isEmpty()) return false;

        BluetoothDevice remoteDevice = btAdapter.getRemoteDevice(macAddr);
        try {
            clientThread = new BluetoothClientThread(remoteDevice.createRfcommSocketToServiceRecord(uuid));
            clientThread.run();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public void sendDirectionToDevice(DirectionType directionType) {
        if(clientThread == null) return;
        try {
            clientThread.sendMessage(String.valueOf(directionType.ordinal()));
        } catch (IOException e) {
            Log.d("BluetoothHelper", String.valueOf(e));
        }
    }

    public void disconnectDevice() {
        if(clientThread == null) return;
        try {
            clientThread.finish();
        } catch (IOException e) {
            Log.d("BluetoothHelper", String.valueOf(e));
        }
    }
}
