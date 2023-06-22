package com.yeinerdpajaro.correntometro

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.UUID

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
import android.widget.AdapterView
import androidx.core.view.get


class BluetoothActivity : AppCompatActivity() {

    private lateinit var deviceAddress: String
    var bluetoothAdapter: BluetoothAdapter ? = null
    lateinit var m_pairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 1

    private lateinit var listView: ListView
    private lateinit var deviceListAdapter: ArrayAdapter<String>
    private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    companion object {
        val EXTRA_ADDRESS: String = "Device_adress"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.d("Bluetooth", "Bluetooth is not supported")
            Toast.makeText(this, "Bluetooth no es soportado", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }else{
            pairedDeviceList()
        }


    }

    private fun pairedDeviceList() {
        val m_pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        val deviceList: ArrayList<String> = ArrayList()

        val deviceListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)
        listView = findViewById<ListView>(R.id.list_device_bluetooth)
        listView.adapter = deviceListAdapter

        if (m_pairedDevices?.isEmpty() == false) {
            m_pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                val deviceInfo: String = "$deviceName\n$deviceHardwareAddress"
                deviceList.add(deviceInfo)
            }
        } else {
            Toast.makeText(this, "No se encontraron dispositivos emparejados", Toast.LENGTH_SHORT).show()
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val deviceInfo: String = deviceListAdapter.getItem(position) as String
            val address = deviceInfo.substringAfterLast("\n")

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_ENABLE_BLUETOOTH){
            if(resultCode == RESULT_OK){
                if(bluetoothAdapter!!.isEnabled){
                    Toast.makeText(this, "Bluetooth ha sido conectado", Toast.LENGTH_SHORT).show()
                    pairedDeviceList()
                }else{
                    Toast.makeText(this , "Bluetooth ha sido desconectado", Toast.LENGTH_SHORT).show()
                }
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this , "Bluetooth ha sido cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
