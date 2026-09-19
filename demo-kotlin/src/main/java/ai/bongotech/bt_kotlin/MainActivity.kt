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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ai.bongotech.bt.BongoBT
import java.util.ArrayList
import java.util.HashMap

class MainActivity : AppCompatActivity() {

    private lateinit var bongoBT: BongoBT

    private lateinit var listView: ListView
    private lateinit var btSearch: Button
    private lateinit var progressBar: ProgressBar

    private var deviceList: ArrayList<HashMap<String, String>> = ArrayList()
    private var myAdapter: MyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.listView)
        btSearch = findViewById(R.id.btSearch)
        progressBar = findViewById(R.id.progressBar)

        bongoBT = BongoBT(this)
        bongoBT.enableLog(true)

        btSearch.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            deviceList = ArrayList()

            bongoBT.searchDevices(object : BongoBT.BtDiscoveryListener {
                override fun onStarted() {
                    myAdapter = MyAdapter()
                    listView.adapter = myAdapter
                }

                override fun onDeviceAdded(name: String, mac: String) {
                    val hashMap = HashMap<String, String>()
                    hashMap["name"] = name ?: "Unknown Device"
                    hashMap["mac"] = mac ?: ""
                    deviceList.add(hashMap)
                    myAdapter?.notifyDataSetChanged()
                }

                override fun onFinished(arrayList: ArrayList<HashMap<String, String>>?) {
                    if (arrayList != null) {
                        deviceList = arrayList
                    }
                    progressBar.visibility = View.GONE
                    myAdapter?.notifyDataSetChanged()
                }

                override fun onError(errorReason: String) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, errorReason, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private inner class MyAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return deviceList.size
        }

        override fun getItem(position: Int): Any {
            return deviceList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val myView = convertView ?: layoutInflater.inflate(R.layout.item, parent, false)

            val tvDevice = myView.findViewById<TextView>(R.id.tvDevice)
            val tvMac = myView.findViewById<TextView>(R.id.tvMac)
            val itemLayout = myView.findViewById<LinearLayout>(R.id.itemLayout)

            val hashMap = deviceList[position]
            val deviceName = hashMap["name"] ?: "Unknown Device"
            val deviceMac = hashMap["mac"] ?: ""

            tvDevice.text = deviceName
            tvMac.text = deviceMac

            itemLayout.setOnClickListener {
                DeviceConnect.DEVICE_MAC = deviceMac
                startActivity(Intent(this@MainActivity, DeviceConnect::class.java))
            }

            return myView
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bongoBT.release()
    }
}