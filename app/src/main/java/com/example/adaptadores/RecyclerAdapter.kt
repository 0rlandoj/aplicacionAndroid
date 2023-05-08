package com.example.adaptadores

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.scale
import androidx.recyclerview.widget.RecyclerView
import com.example.clases.Anime
import com.example.metodos.getImage
import com.example.ragnarok.MainActivityInformacionDeLaSerie
import com.example.ragnarok.R
import com.example.ragnarok.databinding.AnimeBinding
import java.io.File

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var animes: MutableList<Anime>  = ArrayList()
    private lateinit var context : Context




    fun recyclerAdapter(Animes : MutableList<Anime>, context: Context){
        this.animes = Animes
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.anime,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = animes[position]
        holder.bind(item,context)
    }

    override fun getItemCount(): Int {
        return animes.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var binding: AnimeBinding = AnimeBinding.bind(view)

        fun bind(anime:Anime, context: Context){

            val localFile = File.createTempFile("images", "jpg")
            getImage(anime).getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)

                binding.AnimeNameTextView.text = anime.idAnime
                binding.imageAnimeButton.setImageBitmap(bitmap.scale(320,370,true))

                binding.imageAnimeButton.layoutParams.width = 320//ancho
                binding.imageAnimeButton.layoutParams.height = 370//alto

            }

            binding.imageAnimeButton.setOnClickListener {
                val intent = Intent(context, MainActivityInformacionDeLaSerie::class.java)
                    .putExtra("nombreSerie", anime.idAnime)
                startActivity(context,intent, Bundle())
            }

        }
    }

}

     /*fun getImage (anime : Anime) : StorageReference {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://ragnarok-1c4a2.appspot.com/").child(anime.idAnime + "_1920x1080.jpg")

        return storageRef
    }*/