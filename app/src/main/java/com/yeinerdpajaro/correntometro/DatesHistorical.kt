package com.yeinerdpajaro.correntometro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class DatesHistorical : AppCompatActivity() {

    private val elements: MutableList<Historial.ListElement> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_historial)
        init()
    }

    private fun init() {
        val dataList = readDataCsv()

        for ((index, data) in dataList.withIndex()) {
            if (index > 0) {
                val pulso = data[0].trim().toInt() // Obtener el primer dato (Pulsos) y convertirlo a Int
                val velocidad = data[1].trim().toFloat() // Obtener el segundo dato (Velocidad) y convertirlo a Float
                val caudal = data[2].trim().toFloat() // Obtener el tercer dato (Caudal) y convertirlo a Float

                // Agregar un nuevo elemento a la lista
                elements.add(Historial.ListElement(pulsos = pulso, velocidad = velocidad, caudal = caudal))

            }// Omitir la primera línea del archivo (nombres de las columnas)


        }

        val listAdapter = Historial(elements, this)
        val recyclerView: RecyclerView = findViewById(R.id.listRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listAdapter
    }

     fun readDataCsv(): List<List<String>> {
        val datosGuardados = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "datos_guardados.csv"
        val file = File(datosGuardados)

        if (!file.exists()) {
            // El archivo no existe, puedes manejar este caso según tus necesidades
            Toast.makeText(this, "No hay archivo para leer", Toast.LENGTH_SHORT).show()
            return emptyList()
        }

        val fileReader = FileReader(file)
        val bufferedReader = BufferedReader(fileReader)

        val dataList: MutableList<List<String>> = mutableListOf()

        var line: String? = bufferedReader.readLine()
        while (line != null) {
            val data = line.split(",") // Separar los datos por coma
            dataList.add(data)
            line = bufferedReader.readLine()
        }

        bufferedReader.close()
        return dataList
    }
}
