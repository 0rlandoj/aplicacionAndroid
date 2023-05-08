package com.example.ragnarok

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.metodos.getDB
import com.example.ragnarok.databinding.ActivityMainCurrentSeasonBinding
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*
import kotlin.collections.ArrayList


class MainActivityCurrentSeason : AppCompatActivity() {


    private lateinit var binding: ActivityMainCurrentSeasonBinding

    //private var rt : ArrayList<QueryDocumentSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainCurrentSeasonBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        //

        getDB().collection("translations")
            .get()
            .addOnSuccessListener { result ->

                val d : ArrayList<QueryDocumentSnapshot> = ArrayList()

                for (document in result) {

                    val t = document["input"] as MutableMap<*, *>

                    val r = t["temporada"] as? String

                    if(r != null){

                        Log.d("sisisi","si")
                        d.add(document)
                        //creadd(document)

                        //val y = Button(this)
                        //y.text = document.id
                        //binding.linealLayout.addView(y)

                    }
                }

                //d.sorted
                Log.d("eerrtt",d.size.toString())
            }
            .addOnFailureListener { exception ->
                Log.d("pruebaFallida", "Error getting documents: ", exception)
            }




        /*d.forEach { e ->

            val r = Button(this)
            r.text = e.id
            binding.linealLayout.addView(r)

        }*/

    }//fin on create

    /*fun creadd(tr : QueryDocumentSnapshot) {

        rt.add(tr)

    }*/

}