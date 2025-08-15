package ai.bongotech.sample;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;

import ai.bongotech.bt.BongoBT;
import ai.bongotech.bt.BtPermissions;

public class MainActivity extends AppCompatActivity {
    // Jubayer Hossain @www.bongotech.ai
    BongoBT bongoBT;

    ListView listView;
    Button btSearch;
    ProgressBar progressBar;
    ArrayList<HashMap<String, String>> deviceList = new ArrayList<>();
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listView);
        btSearch = findViewById(R.id.btSearch);
        progressBar = findViewById(R.id.progressBar);


        bongoBT = new BongoBT(this);
        bongoBT.enableLog(true);

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                deviceList = new ArrayList<>();

                bongoBT.searchDevices(new BongoBT.BtDiscoveryListener() {
                    @Override
                    public void onStarted() {
                        myAdapter = new MyAdapter();
                        listView.setAdapter(myAdapter);

                    }

                    @Override
                    public void onDeviceAdded(String name, String mac) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("name", name);
                        hashMap.put("mac", mac);
                        deviceList.add(hashMap);
                        myAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFinished(ArrayList<HashMap<String, String>> arrayList) {
                        deviceList = arrayList;
                        progressBar.setVisibility(View.GONE);
                        if (deviceList!=null) myAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String errorReason) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), errorReason, Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });




        // end of oncreate()
    }



    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View myView = inflater.inflate(R.layout.item, parent, false);

            TextView tvDevice = myView.findViewById(R.id.tvDevice);
            TextView tvMac = myView.findViewById(R.id.tvMac);
            LinearLayout itemLayout = myView.findViewById(R.id.itemLayout);

            HashMap<String, String> hashMap = deviceList.get(position);
            String deviceName = hashMap.get("name");
            String deviceMac = hashMap.get("mac");

            tvDevice.setText(deviceName);
            tvMac.setText(deviceMac);

            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Connect with MAC

                    DeviceConnect.DEVICE_MAC = deviceMac; // Passing Mac to Another Class
                    startActivity(new Intent(MainActivity.this, DeviceConnect.class));

                }
            });


            return myView;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bongoBT.release();
    }
}