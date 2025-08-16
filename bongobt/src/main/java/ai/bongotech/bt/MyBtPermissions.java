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

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentActivity;

public final class MyBtPermissions {
    // Jubayer Hossain @www.bongotech.ai
    public interface Callback { void onGranted(); void onDenied(boolean neverAskAgain); }
    private MyBtPermissions() {}

    public static void ensureScanAndConnect(Context ctx, Callback cb) {
        String[] perms = BtPermissionConfig.scanAndConnect();
        if (perms.length == 0) { cb.onGranted(); return; }

        if (ctx instanceof FragmentActivity) {
            FragmentActivity fa = (FragmentActivity) ctx;
            PermissionHostFragment.of(fa).request(
                    fa,
                    "Allow Bluetooth Permission",
                    "This feature discovers and connects to nearby Bluetooth devices. No data is stored or shared.",
                    perms,
                    new PermissionHostFragment.Callback() {
                        @Override public void onGranted() { cb.onGranted(); }
                        @Override public void onDenied(boolean naa) { cb.onDenied(naa); }
                    }
            );
        } else {
            // Fallback: launch bridge activity
            String id = CallbackRegistry.put(new CallbackRegistry.Callback() {
                @Override public void onGranted() { cb.onGranted(); }
                @Override public void onDenied(boolean naa) { cb.onDenied(naa); }
            });
            Intent i = new Intent(ctx, PermissionBridgeActivity.class);
            i.putExtra(PermissionBridgeActivity.EXTRA_ID, id);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
        }
    }
}
