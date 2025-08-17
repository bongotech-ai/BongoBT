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
package ai.bongotech.sample;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import ai.bongotech.bt.BongoBT;

public class DeviceConnect extends AppCompatActivity {
    TextView tvDisplay;
    EditText edMessage;
    LinearLayout laySend;
    public static String DEVICE_MAC = "";
    BongoBT bongoBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_device_connect);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDisplay = findViewById(R.id.tvDisplay);
        edMessage = findViewById(R.id.edMessage);
        laySend = findViewById(R.id.laySend);

        bongoBT = new BongoBT(this);


        /*
        // Optional // Serial Port Profile (SPP / default) Auto assigned
        bongoBT.setUuid(java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
         */

        bongoBT.connectTo(DEVICE_MAC, new BongoBT.BtConnectListener() {

            @Override
            public void onConnected() {
                tvDisplay.setText("✅ Connected");
            }

            @Override
            public void onReceived(String message) {
                tvDisplay.append("\n✅ "+message);
            }

            @Override
            public void onError(String reason) {
                tvDisplay.setText("❌ BT Connection Failed:\n"+reason+"\n");
            }
        });



        laySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edMessage.getText().toString();
                hideKeyboard(DeviceConnect.this);
                tvDisplay.append("\n➡ "+message);

                bongoBT.sendCommand(message);

                edMessage.setText("");
            }
        });



    }


    //----------------------


    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }


}