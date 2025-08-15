package ai.bongotech.bt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.*;

import java.util.Map;

public final class PermissionHostFragment extends Fragment {
    // Jubayer Hossain @www.bongotech.ai
    interface Callback { void onGranted(); void onDenied(boolean neverAskAgain); }

    private static final String TAG = "BtPermHost";
    private ActivityResultLauncher<String[]> launcher;
    private Callback pending;

    public static PermissionHostFragment of(FragmentActivity act) {
        FragmentManager fm = act.getSupportFragmentManager();
        Fragment f = fm.findFragmentByTag(TAG);
        if (f instanceof PermissionHostFragment) return (PermissionHostFragment) f;
        PermissionHostFragment h = new PermissionHostFragment();
        fm.beginTransaction().add(h, TAG).commitNow();
        return h;
    }

    @Override public void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        setRetainInstance(true);
        launcher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                (Map<String, Boolean> res) -> {
                    if (pending == null) return;
                    boolean all = !res.containsValue(Boolean.FALSE);
                    boolean naa = false;
                    Activity a = getActivity();
                    if (!all && a != null) {
                        for (String p : res.keySet()) {
                            if (Boolean.FALSE.equals(res.get(p)) && !shouldShowRequestPermissionRationale(p)) {
                                naa = true; break;
                            }
                        }
                    }
                    Callback cb = pending; pending = null;
                    if (all) cb.onGranted(); else cb.onDenied(naa);
                });
    }

    void request(FragmentActivity act, String title, String msg, String[] perms, Callback cb) {
        // already granted?
        boolean granted = true;
        for (String p : perms) granted &= act.checkSelfPermission(p)==PackageManager.PERMISSION_GRANTED;
        if (granted) { cb.onGranted(); return; }

        boolean rationale = false;
        for (String p : perms) if (shouldShowRequestPermissionRationale(p)) { rationale = true; break; }

        pending = cb;
        if (rationale) {
            new AlertDialog.Builder(act).setTitle(title).setMessage(msg)
                    .setPositiveButton("Allow", (d,w) -> launcher.launch(perms))
                    .setNegativeButton("Cancel", (d,w) -> { Callback c=pending; pending=null; if(c!=null)c.onDenied(false); })
                    .show();
        } else {
            launcher.launch(perms);
        }
    }
}
