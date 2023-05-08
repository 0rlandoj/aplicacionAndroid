package com.example.ragnarok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.GravityCompat
import com.example.metodos.getDB
import com.example.metodos.getStringBuilder
import com.example.ragnarok.databinding.ActivityMainEstadisticasBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivityEstadisticas : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainEstadisticasBinding

    private var listaAnimesIncompletos: ArrayList<String> = ArrayList()
    private var listaAnimesCompletos: ArrayList<String> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainEstadisticasBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.navigationView.setNavigationItemSelectedListener(this)

        //------------------------bottomNavigationView-------------------------------------//
        /*binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.accountItem -> {
                    val intent = Intent(this, MainActivityPerfilDelUsuario::class.java)
                    startActivity(intent)
                    true
                }
                R.id.mainItem -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }*/
        //--------------------------------bottomNavigationView-------------------------------------//

        recuperaAnimes()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                //TODO("Not yet implemented")
                if (tab != null) {
                    when(tab.position){

                        0 ->{

                            binding.lineasLayout.removeAllViews()
                            listaAnimesIncompletos.forEach { nombreSerie ->

                                val boton = buton()
                                boton.text = nombreSerie
                                binding.lineasLayout.addView(boton)
                                boton.setOnClickListener {

                                    val intent = orto(nombreSerie)
                                    startActivity(intent)

                                }
                            }

                        }


                        1 -> {

                            binding.lineasLayout.removeAllViews()
                            listaAnimesCompletos.forEach { nombreSerie ->

                                val boton = buton()
                                boton.text = nombreSerie
                                binding.lineasLayout.addView(boton)
                                boton.setOnClickListener {

                                    val intent = orto(nombreSerie)
                                    startActivity(intent)

                                }

                            }


                        }

                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //TODO("Not yet implemented")
            }

        } )


    }//finOncreate

 private fun orto(nombreSerie : String) : Intent {

     return Intent(this, MainActivityInformacionDeLaSerie::class.java)
         .putExtra("nombreSerie",nombreSerie)
 }


    //--------
    private fun recuperaAnimes (){

        val user = FirebaseAuth.getInstance().currentUser
        getDB().collection("Usuario").get().addOnSuccessListener {result ->
            for (document in result){
                val datosUsuario = document.get("datos") as HashMap<*, *>
                val correoUsuario = datosUsuario["correo"] as String
                val correoUsuarioIniciadoSesion = user?.email as String
                //Cuando encuentramos el documento perteneciente al usuario que inicio sesion, recupero los datos del documento
                if(correoUsuario == correoUsuarioIniciadoSesion){
                    val listaAnime = document.get("misAnimes") as ArrayList<*>
                    val iterador = (0 until listaAnime.size).iterator()
                    var True = true
                    var animeCompleto = 0
                    var animeIncompleto = 0
                    //recorremos los animes añadidos a misANimes del usuario en la sesion iniciada
                    while(True){
                        if(iterador.hasNext()){
                            val anime = listaAnime[iterador.next()] as HashMap<*, *>
                            val dataAnime = anime["data"] as HashMap<*, *>
                            val listaCapitulos = dataAnime["capitulos"] as ArrayList<Boolean>
                            var capitulosTotalesVistos = 0
                            val capitulosTotales = listaCapitulos.size
                            var animeCompletado = false
                            for(capitulo in listaCapitulos){
                                if(capitulo){
                                    capitulosTotalesVistos++
                                }
                            }
                            if(capitulosTotalesVistos == capitulosTotales){
                                animeCompletado = true
                            }

                            if(animeCompletado){
                                animeCompleto++
                                listaAnimesCompletos.add(anime["id"] as String)

                            }else{
                                animeIncompleto++
                                listaAnimesIncompletos.add(anime["id"] as String)
                            }







                        }else{
                            True = false
                        }
                    }

                    binding.animesAgregadosTextView.text = getStringBuilder(binding.animesAgregadosTextView.text.toString(),listaAnime.size.toString())

                    binding.animesVistosTextView.text = getStringBuilder(binding.animesVistosTextView.text.toString(),animeCompleto.toString())

                    binding.animesIncompletostextView.text = getStringBuilder(binding.animesIncompletostextView.text.toString(),animeIncompleto.toString())

                }

            }
        }
    }

    private fun buton(): AppCompatButton {
        return AppCompatButton(this)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.accountItem -> {
                val intent = Intent(this, MainActivityPerfilDelUsuario::class.java)
                startActivity(intent)
            }
            R.id.mainItem -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.currentSeason -> {
                val intent = Intent(this, MainActivityCurrentSeason::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    //metodo para que se deslize y esconda el menu lateral
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}
