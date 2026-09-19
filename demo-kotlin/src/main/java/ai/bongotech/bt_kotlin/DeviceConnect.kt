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
package ai.bongotech.bt_kotlin

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ai.bongotech.bt.BongoBT

class DeviceConnect : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private lateinit var edMessage: EditText
    private lateinit var laySend: LinearLayout
    private lateinit var bongoBT: BongoBT

    companion object {
        var DEVICE_MAC: String = ""

        fun hideKeyboard(activity: Activity) {
            val view: View? = activity.currentFocus
            if (view != null) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_device_connect)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvDisplay = findViewById(R.id.tvDisplay)
        edMessage = findViewById(R.id.edMessage)
        laySend = findViewById(R.id.laySend)

        bongoBT = BongoBT(this)

        /*
        // Optional // Serial Port Profile (SPP / default) Auto assigned
        bongoBT.setUuid(java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
        */

        bongoBT.connectTo(DEVICE_MAC, object : BongoBT.BtConnectListener {
            override fun onConnected() {
                tvDisplay.text = "✅ Connected"
            }

            override fun onReceived(message: String) {
                tvDisplay.append("\n✅ $message")
            }

            override fun onError(reason: String) {
                tvDisplay.text = "❌ BT Connection Failed:\n$reason\n"
            }
        })

        laySend.setOnClickListener {
            val message = edMessage.text.toString()
            hideKeyboard(this@DeviceConnect)
            tvDisplay.append("\n➡ $message")

            bongoBT.sendCommand(message)

            edMessage.setText("")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bongoBT.release()
    }
}