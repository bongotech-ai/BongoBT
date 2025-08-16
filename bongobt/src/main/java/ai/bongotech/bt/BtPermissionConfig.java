/*
 * Copyright 2025 Bongo iOTech Ltd. & Bongo Tech (www.bongotech.ai)
 * ©Jubayer Hossain, Founder of Bongo Tech
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
