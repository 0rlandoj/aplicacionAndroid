package com.example.ragnarok

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.example.metodos.getDB
import com.example.metodos.getUserDocument
import com.example.ragnarok.databinding.ActivityMainCapitulosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QueryDocumentSnapshot


class MainActivityCapitulos : AppCompatActivity() {

    private lateinit var binding: ActivityMainCapitulosBinding

    private var episodeNumberSwitchTag = 0
    private val user = FirebaseAuth.getInstance().currentUser

    private var episodeNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainCapitulosBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val intent = intent
        val s =  intent.getStringExtra(MainActivityInformacionDeLaSerie.ide)




        //-----------------------//
        getDB().collection("Usuario").get().addOnSuccessListener { result ->

            for (document in result){

                val userData = document.get("datos") as HashMap<*, *>
                val emailExpected = userData["correo"] as String
                val emailUser = user?.email as String

                if(emailExpected == emailUser){
                    val userListAnimes = document.get("misAnimes") as ArrayList<HashMap<*, *>>
                    for(anime in userListAnimes){
                        if(anime["id"].toString() == s.toString()){
                            val dataAnime = anime["data"] as HashMap<*, *>
                            val episodes = dataAnime["capitulos"] as ArrayList<Boolean>
                            for(episode in episodes){
                                val textViewEpisode = TextView(this)
                                textViewEpisode.text = episodeNumber.toString()
                                textViewEpisode.setPadding(320,50,100,15)
                                episodeNumber++

                                val switch = SwitchCompat(this)
                                switch.tag = episodeNumberSwitchTag
                                episodeNumberSwitchTag++

                                //----Logica switch------------------------------------//
                                episodioVistoNoVisto(episode, switch)
                                //----Logica switch------------------------------------//

                                ///////Actualizamos el estado del switch en base de datos////////
                                switch.setOnCheckedChangeListener { switchEpisode, isChecked ->
                                    if (isChecked) {
                                        // The toggle is enabled
                                        switchEpisode.text = getString(R.string.viewed)
                                        actualizarArrayCapitulos(document, episodes, switchEpisode,userListAnimes)
                                    } else {
                                        // The toggle is disabled
                                        switchEpisode.text = getString(R.string.not_viewed)
                                        actualizarArrayCapitulos(document, episodes, switchEpisode,userListAnimes)
                                    }
                                }
                                ///////Actualizamos el estado del switch en base de datos////////

                                binding.tableLayout.addView(createTableRow(textViewEpisode,switch))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createTableRow(textViewEpisode : TextView,  switch : SwitchCompat) : TableRow {

        val tableRow = TableRow(this)
        tableRow.addView(textViewEpisode)
        tableRow.addView(switch)

        return tableRow
    }

    private fun actualizarArrayCapitulos
    (userDocument : QueryDocumentSnapshot, episodes : ArrayList<Boolean>, switchEpisode : CompoundButton, userListAnimes : ArrayList<HashMap<*, *>>){

                        if(episodes[switchEpisode.tag.toString().toInt()]){
                            episodes.set(switchEpisode.tag.toString().toInt(),false)
                        }else{
                            episodes[switchEpisode.tag.toString().toInt()] = true
                        }
                        getDB().runTransaction { transaction ->
                            transaction.get(getUserDocument(userDocument))
                            transaction.update(getUserDocument(userDocument),"misAnimes",userListAnimes)
                        }
    }

    //Logica switch
    private fun episodioVistoNoVisto(episode : Boolean, switch : SwitchCompat){

        if(episode){
            switch.text = getString(R.string.viewed)
            switch.isChecked = true
        }else{
            switch.text = getString(R.string.not_viewed)
            switch.isChecked = false
        }

    }

    /*private fun actualizarSwitchBD(isChecked: Boolean, switchEpisode: CompoundButton){

        if (isChecked) {
            // The toggle is enabled
            switchEpisode.text = getString(R.string.viewed)
            actualizarArrayCapitulos(document, episodes, switchEpisode,userListAnimes)
        } else {
            // The toggle is disabled
            switchEpisode.text = getString(R.string.not_viewed)
            actualizarArrayCapitulos(document, episodes, switchEpisode,userListAnimes)
        }

    }*/

}