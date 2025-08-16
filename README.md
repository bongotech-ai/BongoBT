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
implementation "ai.bongotech:bongobt:1.0.3"

---

## 🔐 Permissions

Add these permissions in your **AndroidManifest.xml**:
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

