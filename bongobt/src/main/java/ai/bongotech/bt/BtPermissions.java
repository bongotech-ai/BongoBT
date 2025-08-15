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
