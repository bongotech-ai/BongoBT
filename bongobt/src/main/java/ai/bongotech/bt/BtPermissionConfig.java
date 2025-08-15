package ai.bongotech.bt;

import android.Manifest;
import android.os.Build;

final class BtPermissionConfig {
    // Jubayer Hossain @www.bongotech.ai
    private BtPermissionConfig() {}
    static String[] scanAndConnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new String[]{ Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION };
        } else {
            return new String[0];
        }
    }
}
