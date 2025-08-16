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

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public final class PermissionBridgeActivity extends Activity {
    // Jubayer Hossain @www.bongotech.ai

    static final String EXTRA_ID = "req_id";

    @Override protected void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        String id = getIntent().getStringExtra(EXTRA_ID);
        String[] perms = BtPermissionConfig.scanAndConnect();

        // Already granted?
        boolean all = true;
        for (String p : perms) all &= checkSelfPermission(p)==PackageManager.PERMISSION_GRANTED;
        if (all) {
            CallbackRegistry.consume(id, true, false);
            finish(); return;
        }
        ActivityCompat.requestPermissions(this, perms, 42);
    }

    @Override public void onRequestPermissionsResult(int code, String[] perms, int[] res) {
        super.onRequestPermissionsResult(code, perms, res);
        if (code != 42) { finish(); return; }
        boolean all = true, naa = false;
        for (int i = 0; i < perms.length; i++) {
            boolean g = (i < res.length) && (res[i] == PackageManager.PERMISSION_GRANTED);
            all &= g;
            if (!g && !ActivityCompat.shouldShowRequestPermissionRationale(this, perms[i])) naa = true;
        }
        String id = getIntent().getStringExtra(EXTRA_ID);
        CallbackRegistry.consume(id, all, naa);
        finish();
    }
}
