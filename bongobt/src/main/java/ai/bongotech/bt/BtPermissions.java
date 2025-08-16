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

import androidx.fragment.app.FragmentActivity;

public final class BtPermissions {
    // Jubayer Hossain @www.bongotech.ai
    private BtPermissions() {}

    public interface Callback {
        void onGranted();
        void onDenied(boolean neverAskAgain);
    }

    public static void ensureScanAndConnect(FragmentActivity activity, Callback cb) {
        String[] perms = BtPermissionConfig.scanAndConnect();

        PermissionHostFragment.of(activity).request(
                activity,
                perms.length == 0 ? "No permission required" : "Allow Bluetooth Permission",
                perms.length == 0
                        ? "Your device does not require runtime permissions for this feature."
                        : "This app uses Bluetooth only to discover and connect nearby devices. No data is stored or shared.",
                perms,
                new PermissionHostFragment.Callback() {
                    @Override public void onGranted() { cb.onGranted(); }
                    @Override public void onDenied(boolean naa) { cb.onDenied(naa); }
                }
        );
    }
}
