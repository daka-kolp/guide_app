package com.example.guideapp.presentation.fragments.content.sights


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.guideapp.R
import com.example.guideapp.core.domain.entities.Sight
import com.squareup.picasso.Picasso

class ResultsRecycleViewAdapter(private var items: MutableList<Sight> = mutableListOf()) : RecyclerView.Adapter<TaskViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val listItemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.result_item, parent, false)
        return TaskViewHolder(listItemView)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.location.text = item.geolocation.formatted()
        val image = item.photos?.first()
        if(image != null) Picasso.get().load(image).into(holder.image)

    }
}

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.name)
    val location: TextView = itemView.findViewById(R.id.location)
    val image: ImageView = itemView.findViewById(R.id.sight_image)
}
