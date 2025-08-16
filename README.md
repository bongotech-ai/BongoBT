# BongoBT  
_A simple and reliable Android Bluetooth communication library_  

[![Maven Central](https://img.shields.io/maven-central/v/ai.bongotech/bongobt)](https://central.sonatype.com/artifact/ai.bongotech/bongobt)  
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)  

---

## ✨ Features
- 🔍 Auto-handles Bluetooth permissions & discovery  
- 📡 Scan nearby devices with callbacks for start, found, finish, and errors  
- 🔗 Connect to any Bluetooth device by MAC address  
- 💬 Send & receive messages easily  
- 🔑 UUID customization with safe default (SPP profile)  
- 📱 Minimal boilerplate — just plug and play  

---

## 📦 Installation  

Add the dependency in your **app-level** `build.gradle`:  

```gradle
implementation "ai.bongotech:bongobt:1.0.2"
```

---

## 🔐 Permissions  

Add these permissions in your **AndroidManifest.xml**:  

```xml
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission
    android:name="android.permission.BLUETOOTH_SCAN"
    android:usesPermissionFlags="neverForLocation" />
<uses-permission
    android:name="android.permission.BLUETOOTH"
    android:maxSdkVersion="30" />
<uses-permission
    android:name="android.permission.BLUETOOTH_ADMIN"
    android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## 🚀 Usage  

### 1. Initialize Library  

```java
public class MainActivity extends AppCompatActivity {
    BongoBT bongoBT = new BongoBT(this);
}
```

---

### 2. Search Nearby Devices  

```java
bongoBT.searchDevices(new BongoBT.BtDiscoveryListener() {
    @Override
    public void onStarted() {
        // Discovery started
    }

    @Override
    public void onDeviceAdded(String name, String mac) {
        // A new device discovered
    }

    @Override
    public void onFinished(ArrayList<HashMap<String, String>> arrayList) {
        // Discovery finished
        // Each device is stored as a HashMap inside the ArrayList
        // name → hashMap.get("name")
        // mac  → hashMap.get("mac")
    }

    @Override
    public void onError(String errorReason) {
        // Handle discovery error
    }
});
```

---

### 3. Connect to a Device  

```java
bongoBT.connectTo("your_device_mac", new BongoBT.BtConnectListener() {
    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        // Device is connected
    }

    @Override
    public void onReceived(String message) {
        // Message received from device
    }

    @Override
    public void onError(String reason) {
        // Handle connection error
    }
});
```

---

### 4. Send Commands  

```java
bongoBT.sendCommand("your_command");
```

---

### 5. Optional — Custom UUID  

(Default: Serial Port Profile **SPP**)  

```java
bongoBT.setUuid(java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
```

---

### 6. Get Connected Device  

```java
BluetoothDevice device = bongoBT.getConnectedDevice();
if (device != null) {
    String deviceName = device.getName();
    String deviceMac = device.getAddress();
}
```

---

## 📜 License  

```
Copyright 2025 Bongo iOTech Ltd. (www.bongotech.ai)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

See the full [LICENSE](LICENSE) file for details.  

---

## 👨‍💻 Author
© Jubayer Hossain
© Maintained by **Bongo iOTech Ltd.**
🌍 Building IoT & Robotics solutions from Dhaka, Bangladesh  

Website: [www.bongotech.ai](https://www.bongotech.ai)  
