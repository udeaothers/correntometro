package com.yeinerdpajaro.correntometro

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import java.util.*
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.yeinerdpajaro.correntometro.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException



//val bufferedWriter = null
private var shouldSaveData = true // Bandera para controlar si se deben guardar los datos o no

class MainActivity : AppCompatActivity() {
    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        val fileWriter  = null
    }


    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        val BtnDesconectar = findViewById<Button>(R.id.btnDesconectar)
        val BtnHistorial = findViewById<Button>(R.id.BtnHistorial)
        val BtnGuardarDatos = findViewById<Button>(R.id.BtnGuardarDatos)


        m_address = intent.getStringExtra(BluetoothActivity.EXTRA_ADDRESS).toString()
        ConnectToDevice(this).execute()


        BtnDesconectar.setOnClickListener{
            disconnect()
        }

        BtnGuardarDatos.setOnClickListener{
            receiveDataAsync()
        }
        BtnHistorial.setOnClickListener{
            val intent = Intent(this, DatesHistorical::class.java)
            startActivity(intent)
        }

        // Inicializar el spinner del menú
        var menuSpinner = findViewById(R.id.menu_spinner) as Spinner
        val adapter = ArrayAdapter.createFromResource(this, R.array.menu_items, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menuSpinner.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about_item -> {
                // Realizar las acciones cuando se seleccione "Acerca de"
                // Por ejemplo, mostrar un diálogo o abrir una nueva actividad
                val intent = Intent(this, Acerca_de::class.java)
                startActivity(intent)

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    /*Permite la recepción de datos de forma sincronica*/
    private fun receiveDataAsync() {
        val textVelocidad = findViewById<TextView>(R.id.Textvelocidad)


        if (m_bluetoothSocket != null) {
            val inputStream = m_bluetoothSocket!!.inputStream
            val textCaudal = findViewById<TextView>(R.id.Textcaudal)
            val textPulsos = findViewById<TextView>(R.id.textPulsos)

            // Utilizar una corutina para la lectura asíncrona
            CoroutineScope(Dispatchers.IO).launch {
                val buffer = ByteArray(256)
                while (isActive) {
                    try {
                        val bytesRead = inputStream.read(buffer)
                        val receivedData = buffer.copyOfRange(0, bytesRead)

                        // Actualizar la interfaz de usuario en el hilo principal
                        withContext(Dispatchers.Main) {
                            val data = String(receivedData)
                            val dataArray = data.split(",")
                            save_data_csv(data)

                            // Verificar que hay al menos tres datos separados por comas
                            if (dataArray.size >= 3) {
                                val dato1 = dataArray[0]    //Pulso
                                val dato2 = dataArray[1]    //velocidad
                                val dato3 = dataArray[2]    //Caudal

                                // Actualizar la interfaz de usuario en el hilo principal
                                withContext(Dispatchers.Main) {
                                    // Utilizar los datos extraídos como desees

                                    textPulsos.text = dato1
                                    textVelocidad.text = dato2
                                    textCaudal.text = dato3
                                }
                            }

                            //textVelocidad.text = data
                            //Toast.makeText("Dato recibido", Toast.LENGTH_SHORT).show()
                            //Toast.makeText(this, "Dato recibido: $data", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        break
                    }
                }
            }
        }
    }


    private fun save_data_csv(buffer: String) {
        if (!shouldSaveData) return // Verificar si se deben guardar los datos
        val DatosGuardados = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "datos_guardados.csv"
        val file = File(DatosGuardados)
        val isNewFile = !file.exists()

        val fileWriter = FileWriter(file, true)
        val bufferedWriter = BufferedWriter(fileWriter)

        if (isNewFile) {
            // Agregar nombres de columnas solo si el archivo es nuevo
            val columnNames = "Pulsos (p/seg) , Velocidad (m/seg) , Caudal (m³/seg)" // Reemplaza con los nombres de tus columnas
            bufferedWriter.write(columnNames)
            bufferedWriter.newLine()
        }

        bufferedWriter.write(buffer) // Agregar contenido de los datos
        bufferedWriter.close()
    }

    /*Permite realizar el cambio del estado a conectado para mostrar al usuario*/
    private fun conected_state(){
        val color = ContextCompat.getColor(this, R.color.green_up)
        val text = findViewById<TextView>(R.id.texEstado)

        text.text = "Conectado"
        text.setTextColor(color)

        Toast.makeText(this, "Texto Conectado", Toast.LENGTH_SHORT).show()

    }


    /*Funcion que me permite desconectar del bluetooth*/
     fun disconnect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
                shouldSaveData = false // Establecer la bandera en falso para dejar de guardar los datos
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    /*Permite la conexion de dispositivo por bluetooth */
    public class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }

        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting...", "please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()

                }
            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            } else {
                m_isConnected = true
                (context as MainActivity).conected_state() // Llamar a la función conected_state() en la instancia de MainActivity

            }
            m_progress.dismiss()
        }
    }

}