package com.yeinerdpajaro.correntometro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Historial(private val itemList: List<ListElement>, private val context: Context) : RecyclerView.Adapter<Historial.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.activity_historial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pulsos: TextView = itemView.findViewById(R.id.text_pulso_show)
        private val velocidad: TextView = itemView.findViewById(R.id.text_velocidad_show)
        private val caudal: TextView = itemView.findViewById(R.id.text_caudal_show)

        fun bindData(item: ListElement) {
            pulsos.text = item.pulsos.toString()
            velocidad.text = item.velocidad.toString()
            caudal.text = item.caudal.toString()

        }
    }

    data class ListElement(
        var pulsos: Int = 0,
        var velocidad: Float = 0.0F,
        var caudal: Float = 0.0F,
    )

}



