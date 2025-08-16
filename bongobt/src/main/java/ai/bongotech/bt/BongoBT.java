/*
 * Copyright 2025 Bongo iOTech Ltd. (www.bongotech.ai)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package ai.bongotech.bt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class BongoBT {
    // Jubayer Hossain @www.bongotech.ai
    public static final int REQ_ENABLE_BT = 9922;
    private static final String TAG = "BongoBT";

    private final Context appContext;
    private Context myContext;
    private ArrayList<HashMap<String, String>> searchArrayList = new ArrayList<>();
    BluetoothAdapter bluetoothAdapter;
    private BtDiscoveryListener discoveryListener;
    private final Handler main = new Handler(Looper.getMainLooper());

    public BongoBT(Context context) {
        this.appContext = context;
        this.myContext = context;
    }

    private BluetoothDevice connectedDevice;
    public BluetoothDevice getConnectedDevice() {
        return connectedDevice;
    }

    private void setConnectedDevice(BluetoothDevice connectedDevice) {
        this.connectedDevice = connectedDevice;
    }


    public interface BtDiscoveryListener {
        void onStarted();
        void onDeviceAdded(String name, String mac);
         void onFinished(ArrayList<HashMap<String, String>> arrayList);
        void onError(String errorReason);
    }







    @SuppressLint("MissingPermission")
    public void searchDevices(BtDiscoveryListener dl) {
        searchArrayList = new ArrayList<>();
        setDiscoveryListener(dl);
        setSearchArrayList(searchArrayList);
        stopDiscovery();


        MyBtPermissions.ensureScanAndConnect(appContext, new MyBtPermissions.Callback() {
            @Override public void onGranted() {

                BluetoothManager bm = (BluetoothManager) appContext.getSystemService(Context.BLUETOOTH_SERVICE);
                 bluetoothAdapter = (bm != null) ? bm.getAdapter() : BluetoothAdapter.getDefaultAdapter();

                if (bluetoothAdapter == null) {
                    logw("Bluetooth Not Supported");
                    dl.onError("Bluetooth Not Supported");
                    return;
                }

                if (!bluetoothAdapter.isEnabled()) {
                    //launchEnableDialogIfPossible();
                     dl.onError("Bluetooth is OFF. Please turn on BT.");
                    requestEnableAndWait(new BtEnableResult() {
                        @Override
                        public void onResult(boolean enabled) {
                            if (enabled) finalDiscovery(dl);
                        }
                    });


                    return;
                }

                // Finally Going to discover
                finalDiscovery(dl);


            }

            @Override public void onDenied(boolean neverAskAgain) {
                logw("Permissions denied by user");
                dl.onError("Permissions denied by you.");
            }
        });
    }



    public interface BtEnableResult {
        void onResult(boolean enabled);
    }

    @SuppressLint("MissingPermission")
    public void requestEnableAndWait(BtEnableResult cb) {
        // Start enable intent
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);

        // Register temp receiver to wait for state change
        BroadcastReceiver tempReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    if (state == BluetoothAdapter.STATE_ON || state == BluetoothAdapter.STATE_OFF) {
                        appContext.unregisterReceiver(this);
                        cb.onResult(state == BluetoothAdapter.STATE_ON);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        appContext.registerReceiver(tempReceiver, filter);
    }



    /** Try ACTION_REQUEST_ENABLE from Activity; otherwise NEW_TASK fallback (no result). */
    @SuppressLint("MissingPermission")
    private void launchEnableDialogIfPossible() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        Activity activity = findActivity(appContext);
        if (activity != null) {
            activity.startActivityForResult(intent, REQ_ENABLE_BT);
            logd("Requested BT enable via startActivityForResult()");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appContext.startActivity(intent);
            logw("Started enable intent without Activity; result won't be delivered.");
        }
    }


    @SuppressLint("MissingPermission")
    private void finalDiscovery(BtDiscoveryListener dl){
        logd("Bluetooth ready → onStarted()");
        dl.onStarted();


        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                if (device != null && deviceHardwareAddress!=null) {
                    if (!isDeviceListed(deviceHardwareAddress)) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("type", "Paired");
                        hashMap.put("name", deviceName);
                        hashMap.put("mac", "" + deviceHardwareAddress);
                        searchArrayList.add(hashMap);
                        logd("Paired Found: " + deviceName + " [" + deviceHardwareAddress + "]");
                        dl.onDeviceAdded(deviceName != null ? deviceName : "Unknown", deviceHardwareAddress);
                    }
                }

            } //End of For loop
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        appContext.registerReceiver(broadcastReceiver, filter);

        if (bluetoothAdapter!=null) {
            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.startDiscovery();
        }


    }




    /** Extract Activity if the provided Context ultimately wraps one ~ Jubayer */
    private static Activity findActivity(Context ctx) {
        while (ctx instanceof ContextWrapper) {
            if (ctx instanceof Activity) return (Activity) ctx;
            ctx = ((ContextWrapper) ctx).getBaseContext();
        }
        return null;
    }



    private boolean isDeviceListed(String macAddress){
        boolean isDeviceListed= false;

        if (searchArrayList!=null) {
            for (int i = 0; i < searchArrayList.size(); i++) {
                HashMap<String, String> myHashmap = searchArrayList.get(i);
                String deviceMac = ""+myHashmap.get("mac");
                if (deviceMac.contains(macAddress)) {
                    isDeviceListed = true;
                    break;
                }
            }
        }

        return isDeviceListed;
    }




    //Juba -- > Custom BroadCastReceiver
    private void setDiscoveryListener(BtDiscoveryListener dl) {
        this.discoveryListener = dl;
    }

    private void setSearchArrayList(ArrayList<HashMap<String, String>> arrayList) {
        this.searchArrayList = arrayList;
    }

    @SuppressLint("MissingPermission")
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress();

                    if (deviceHardwareAddress != null && !isDeviceListed(deviceHardwareAddress)) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("type", "Found");
                        hashMap.put("name", deviceName != null ? deviceName : "Unknown");
                        hashMap.put("mac", deviceHardwareAddress);
                        searchArrayList.add(hashMap);

                        if (discoveryListener != null) {
                            discoveryListener.onDeviceAdded(deviceName != null ? deviceName : "Unknown", deviceHardwareAddress);
                        } else  logd("discoveryListener is NULL");


                        logd("New Found: " + deviceName + " [" + deviceHardwareAddress + "]");
                    }
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                logd("Discovery finished");
                stopDiscovery();

                if (discoveryListener != null) {
                    discoveryListener.onFinished(searchArrayList);
                } else  logd("discoveryListener is NULL");
            }
        }
    };




    @SuppressLint("MissingPermission")
    private void stopDiscovery() {
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        try {
            appContext.unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            logw("Receiver already unregistered");
        }
    }



    public void release(){
        logw("BongoBT Library Released");
        try { appContext.unregisterReceiver(broadcastReceiver); } catch (Exception ignore) {}
        disconnect();
        cancelOngoingConnect();
    }







    //Juba -------------------------------------------------------------

    private android.bluetooth.BluetoothSocket currentSocket;
    private Thread connectThread;

    public interface BtConnectListener {
        void onConnected();
        void onReceived(String message);
        void onError(String reason);
    }





    // juba --> Connection --> BongoBT
    @SuppressLint("MissingPermission")
    public void connectTo(String mac, BtConnectListener conL) {

        MyBtPermissions.ensureScanAndConnect(appContext, new MyBtPermissions.Callback() {
            @Override public void onGranted() {

                BluetoothManager bm = (BluetoothManager) appContext.getSystemService(Context.BLUETOOTH_SERVICE);
                bluetoothAdapter = (bm != null) ? bm.getAdapter() : BluetoothAdapter.getDefaultAdapter();

                if (bluetoothAdapter == null) {
                    logw("Bluetooth Not Supported");
                    conL.onError("Bluetooth Not Supported");
                    return;
                }

                if (!bluetoothAdapter.isEnabled()) {
                    //launchEnableDialogIfPossible();
                    conL.onError("Bluetooth is OFF. Please turn on BT.");
                    requestEnableAndWait(new BtEnableResult() {
                        @Override
                        public void onResult(boolean enabled) {
                            if (enabled) finalBtConnection(mac, conL);
                        }
                    });


                    return;
                }

                // Finally Going to Connect
                finalBtConnection(mac, conL);


            }

            @Override public void onDenied(boolean neverAskAgain) {
                logw("Permissions denied");
                conL.onError("Permissions denied! Please allow permission and continue");
            }
        });





    }



    private static final UUID DEFAULT_SPP_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private UUID sppUuid;

    // Setter
    public void setUuid(UUID uuid) {
        this.sppUuid = (uuid != null) ? uuid : DEFAULT_SPP_UUID;
    }

    // Getter
    public UUID getUuid() {
        return (sppUuid != null) ? sppUuid : DEFAULT_SPP_UUID;
    }



    @SuppressLint("MissingPermission")
    private void finalBtConnection(String mac, BtConnectListener cb){


        if (mac == null || mac.length() < 11) {
            main.post(() -> {
                if (cb != null) cb.onError("Invalid MAC address");
            });

            return;
        }

        BluetoothManager bm = (BluetoothManager) appContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = (bm != null) ? bm.getAdapter() : BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            main.post(() -> {
                if (cb != null) cb.onError("Bluetooth not supported");
            });
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            main.post(() -> {
                if (cb != null) cb.onError("Bluetooth is disabled"); launchEnableDialogIfPossible();
            });
            return;
        }

        try { if (bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery(); } catch (Exception ignore) {}

        final BluetoothDevice device;
        try { device = bluetoothAdapter.getRemoteDevice(mac); }
        catch (IllegalArgumentException e) {
            main.post(() -> {
                if (cb != null) cb.onError("Invalid MAC format");
            });
            return;
        }

        // If LE-only, bail to GATT
        int t = device.getType();
        if (t == BluetoothDevice.DEVICE_TYPE_LE || t == BluetoothDevice.DEVICE_TYPE_UNKNOWN) {

            main.post(() -> {
                if (cb != null) cb.onError("Target seems BLE-only (use GATT)");
            });

            return;
        }

        //final java.util.UUID SPP_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        if (sppUuid==null) sppUuid = DEFAULT_SPP_UUID;


        cancelOngoingConnect();
        disconnect();                    // ensure previous socket is dead
        refreshUuids(device);            // refresh SDP a bit
        sleep(120);                   // tiny backoff

        connectThread = new Thread(() -> {
            BluetoothSocket socket = null;
            boolean bonded = (device.getBondState() == BluetoothDevice.BOND_BONDED);

            try {
                // PATH A: already bonded → prefer SECURE first ~ Juba
                if (bonded) {
                    try {
                        socket = device.createRfcommSocketToServiceRecord(sppUuid);
                        socket.connect();
                    } catch (Exception eSecureFirst) {
                        safeClose(socket); socket = null;
                        // Fallback insecure then secure again!
                        try {
                            socket = device.createInsecureRfcommSocketToServiceRecord(sppUuid);
                            socket.connect();
                        } catch (Exception eInsec) {
                            safeClose(socket); socket = null;
                            // Reflection channel- 001 --juba
                            try {
                                socket = (BluetoothSocket) device.getClass()
                                        .getMethod("createRfcommSocket", int.class)
                                        .invoke(device, 1);
                                socket.connect();
                            } catch (Exception eRef) {
                                safeClose(socket); socket = null;

                                main.post(() -> {
                                    if (cb != null) cb.onError("Connection failed (bonded path)");
                                });

                                loge("Connect fail(bonded) " + mac, eRef);
                                return;
                            }
                        }
                    }
                } else {
                    // PATH B: not bonded → try INSECURE to pop pairing ~ Juba
                    try {
                        socket = device.createInsecureRfcommSocketToServiceRecord(sppUuid);
                        socket.connect();
                    } catch (Exception eInsecFirst) {
                        safeClose(socket); socket = null;
                    }

                    // Ensure bonding complete (handles that “popup appears then vanishes” race)
                    if (!ensureBonded(device, 10000)) {
                        main.post(() -> {
                            if (cb != null) cb.onError("Pairing required but not completed!");
                        });

                        return;
                    }

                    // After bond, prefer SECURE
                    sleep(150);
                    try {
                        socket = device.createRfcommSocketToServiceRecord(sppUuid);
                        socket.connect();
                    } catch (Exception eSecure) {
                        safeClose(socket); socket = null;
                        // Final fallback: reflection //

                        try {
                            socket = (BluetoothSocket) device.getClass()
                                    .getMethod("createRfcommSocket", int.class)
                                    .invoke(device, 1);
                            socket.connect();
                        } catch (Exception eRef) {
                            safeClose(socket); socket = null;

                            main.post(() -> {
                                if (cb != null) cb.onError("Connection failed after pairing");
                            });
                            loge("Connect Failed (after pair) " + mac, eRef);
                            return;
                        }
                    }
                }

                synchronized (this) { currentSocket = socket; }
                BluetoothSocket finalSocket1 = socket;
                main.post(() -> {
                    if (cb != null) cb.onConnected();
                });

                logd("✅ Connected: " + device.getName() + " [" + device.getAddress() + "]");
                setConnectedDevice(device);

                // Receive Command
                BluetoothSocket finalSocket = socket;
                new Thread(() -> {
                    try {
                        InputStream is = finalSocket.getInputStream();
                        byte[] buf = new byte[1024];
                        int len;
                        while (!Thread.currentThread().isInterrupted() && finalSocket.isConnected() && (len = is.read(buf)) != -1) {
                            if (len > 0) {
                                String received = new String(buf, 0, len, StandardCharsets.UTF_8);
                                if (!received.isEmpty()) {
                                    main.post(() -> {
                                        if (cb != null) cb.onReceived(received);
                                    });

                                }
                            }
                        }

                    } catch (IOException e) {
                        Log.e(TAG, "Read loop ended", e);
                    }
                }).start();


            } finally {
                synchronized (this) { connectThread = null; }
            }
        });
        connectThread.start();


    }




    private static void safeClose(BluetoothSocket s) { if (s != null) try { s.close(); } catch (Exception ignore) {} }
    private static void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }

    @SuppressLint("MissingPermission")
    private boolean ensureBonded(BluetoothDevice device, long timeoutMs) {
        int st = device.getBondState();
        if (st == BluetoothDevice.BOND_BONDED) return true;

        // Trigger pairing if needed
        if (st == BluetoothDevice.BOND_NONE) {
            try { device.createBond(); } catch (Throwable ignored) {}
        }

        long deadline = System.currentTimeMillis() + timeoutMs;
        int last = -1;
        while (System.currentTimeMillis() < deadline) {
            int s = device.getBondState();
            if (s != last) { Log.d(TAG, "Bond state: " + s); last = s; }
            if (s == BluetoothDevice.BOND_BONDED) return true;
            if (s == BluetoothDevice.BOND_NONE) break; // user canceled / failed
            sleep(150);
        }
        return device.getBondState() == BluetoothDevice.BOND_BONDED;
    }

    /** Rare cases need a re-pair ~Juba */
    private boolean removeBond(BluetoothDevice device) {
        try {
            return (boolean) device.getClass().getMethod("removeBond").invoke(device);
        } catch (Exception e) { return false; }
    }

    /** Refresh UUIDs via SDP (async). We just give it a small wait window ~Jubayer */
    @SuppressLint("MissingPermission")
    private void refreshUuids(BluetoothDevice device) {
        try { device.fetchUuidsWithSdp(); } catch (Exception ignored) {}
        sleep(400);
    }






    private void cancelOngoingConnect() {
        Thread t;
        synchronized (this) { t = connectThread; }
        if (t != null && t.isAlive()) {
            try { t.interrupt(); } catch (Exception ignore) {}
        }
    }

    public void disconnect() {
        cancelOngoingConnect();
        android.bluetooth.BluetoothSocket s;
        synchronized (this) { s = currentSocket; currentSocket = null; }
        if (s != null) {
            try { s.close(); } catch (Exception ignore) {}
            logd("Bluetooth socket closed.");
        }
    }



    public boolean sendCommand(String cmd) {
        BluetoothSocket s;
        synchronized (this) { s = currentSocket; }
        if (s == null) {
            logw("No active connection");
            return false;
        }
        try {
            OutputStream os = s.getOutputStream();
            os.write(cmd.getBytes(StandardCharsets.UTF_8));
            os.flush();
            return true;
        } catch (IOException e) {
            loge("Message Sending Failed", e);
            return false;
        }
    }




    private boolean DEBUG = false;

    public void enableLog(boolean enabled) {
        DEBUG = enabled;
    }

    private void logd(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

    private void logw(String msg) {
        if (DEBUG) Log.w(TAG, msg);
    }

    private void loge(String msg, Throwable t) {
        if (DEBUG) {
            if (t != null) {
                Log.e(TAG, msg, t);
            } else {
                Log.e(TAG, msg);
            }
        }
    }





    //----------------------------







}
