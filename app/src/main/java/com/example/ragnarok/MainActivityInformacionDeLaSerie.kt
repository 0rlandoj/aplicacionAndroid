package com.example.ragnarok

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.metodos.*
import com.example.ragnarok.databinding.ActivityMainInformaciondelaserieBinding
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivityInformacionDeLaSerie : AppCompatActivity() {

    private lateinit var binding: ActivityMainInformaciondelaserieBinding

    private var score = ""
    private var contador = 1
    private val genero = "genero"
    private var listaGeneros = ""

    private val idiomaDelDispositivo = Locale.getDefault().language

    //private val intent = intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainInformaciondelaserieBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val intent = intent
        val idAnime = intent.getStringExtra("nombreSerie")
        //val idAnime2 = intent.getStringExtra(MainActivityEstadisticas.id)

        //var serie = ""

        /*if(idAnime != null){
            serie = idAnime
        }
        else{
            serie = idAnime2 as String
        }*/
        /*if (idAnime2 == null){
            serie = "Akagami no Shirayuki-hime"
        }else{
            serie = idAnime2
        }*/
        //Log.d("ppppppp",idAnime2 as String)

        val docRef = getDB().collection("Anime").document(idAnime as String)
        val user = FirebaseAuth.getInstance().currentUser
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {

                animeExist(user, document)
                binding.starButton.setOnClickListener {
                    binding.starButton.setCircleEndColorRes(R.color.colorAccent)
                    binding.starButton.setCircleEndColorRes(R.color.colorPrimary)
                    binding.starButton.setAnimationScaleFactor(50f)
                    binding.starButton.setExplodingDotColorsRes(R.color.colorPrimary,R.color.colorAccent)
                    agregarEliminarAnimeEnUsuario(user, document)
                }


                val documentUserSecuela = document["secuela"] as? DocumentReference


                if (documentUserSecuela != null) {
                    binding.textView8.text = documentUserSecuela.id
                    binding.textView8.setOnClickListener {
                        val intent = Intent(this, MainActivityInformacionDeLaSerie::class.java)
                            .putExtra("nombreSerie", documentUserSecuela.id)
                        ContextCompat.startActivity(this, intent, Bundle())
                    }
                }


                //Log.d("mimimama","$or")

                //Recuperamos el estado de la serie
                if (getDocumentFinalizado(document)) {
                    binding.textViewEstado.text = getStringBuilder(
                        getString(R.string.condition),
                        getString(R.string.finished)
                    ) //"Estado: Finalizado"
                } else {
                    binding.textViewEstado.text = getStringBuilder(
                        getString(R.string.condition),
                        getString(R.string.In_transmission)
                    ) //"Estado: En emision"
                }

                //titulo de la serie
                binding.titulo.text = document.id


                /////DESCARGAR IMAGEN CLOUD STORAGE/////////
                try {
                    val localFile = File.createTempFile("images", "jpg")
                    getImage(document).getFile(localFile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                        binding.imageView.setImageBitmap(bitmap)
                        BitmapDrawable(resources, bitmap)
                        //bitmap.scale(200,200,false)
                        //imageButton.background = d
                    }.addOnFailureListener { }
                } catch (e: IOException) {
                }
                /////DESCARGAR IMAGEN CLOUD STORAGE/////////


                //Recuperamos el score de la serie//
                getScore(document).forEach { s, fl ->
                    score += fl
                    if (user != null) {
                        if (s == user.uid) {
                            val l = fl.toString()

                            binding.ratingBar.rating = java.lang.Float.parseFloat(l)
                            binding.MyScoreText.text = l
                        }
                    }

                }
                //Recuperamos el score de la serie//

                //Asignamos score a la serie//
                val setScore = document["score"] as MutableMap<String, Float>
                binding.ratingBar.setOnRatingBarChangeListener { _, fl, _ ->
                    if (user != null) {
                        setScore[user.uid] = fl
                    }
                    binding.MyScoreText.text = fl.toString()

                    setScoreBD(document, setScore)
                }
                //Asignamos score a la serie//


                getTranslations(document).get().addOnSuccessListener { document ->
                    val elementosTraducidos = document.get("translated") as MutableMap<*, *>
                    val sinopsisTraducida = elementosTraducidos["sinopsis"] as HashMap<*, *>
                    val sinopsis = sinopsisTraducida[idiomaDelDispositivo] as String
                    val elementosOrdenados = TreeMap(elementosTraducidos)
                    when (idiomaDelDispositivo) {
                        "es" -> {
                            binding.Sinopsis.text = sinopsis
                            elementosOrdenados.forEach { (key, value) ->
                                if (key == genero + contador) {
                                    val generosTraduccion = value as HashMap<*, *>
                                    listaGeneros += generosTraduccion[idiomaDelDispositivo].toString() + " "
                                    contador++
                                }
                            }
                            binding.textViewGeneros.text =
                                getStringBuilder(getString(R.string.genders), listaGeneros)
                        }
                        "en" -> {
                            binding.Sinopsis.text = sinopsis
                            elementosOrdenados.forEach { (key, value) ->
                                if (key == genero + contador) {
                                    val generosTraduccion = value as HashMap<*, *>
                                    listaGeneros += generosTraduccion[idiomaDelDispositivo].toString() + " "
                                    contador++
                                }
                            }
                            binding.textViewGeneros.text =
                                getStringBuilder(getString(R.string.genders), listaGeneros)
                        }
                    }


                }


                binding.button6.setOnClickListener {
                    val intent = Intent(this, MainActivityCapitulos::class.java)
                        .putExtra(ide, document.id)
                    startActivity(intent)
                }
            } else {
                Log.d("pruebaFallida", "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("pruebafallida2", "get failed with ", exception)
            }
        //--cargar anuncio de prueba de la vista--//
        val adRequest = AdRequest.Builder().build()
        binding.adViewAnuncioPrueba.loadAd(adRequest)
        Log.d("DispositivoDePrueba", "${adRequest.isTestDevice(this)}")
        //--cargar anuncio de prueba de la vista--//


        //binding.buttonLikeButton.point
    }//fin oncreate

    private fun getListaAnimesUsuario(documentoUsuario : QueryDocumentSnapshot) :  ArrayList<HashMap<*, *>> {


        return documentoUsuario.get("misAnimes") as ArrayList<HashMap<*, *>>
    }

    private fun agregarEliminarAnimeEnUsuario(user: FirebaseUser?, document1: DocumentSnapshot) {
        getDB().collection("Usuario").get().addOnSuccessListener { result ->
            for (documentoUsuario in result) {
                val userData = documentoUsuario.get("datos") as HashMap<*, *>
                val emailExpected = userData["correo"] as String
                val emailUser = user?.email as String
                if (emailExpected == emailUser) {
                    var animeExiste = false
                    var posicionAnime = 0
                    var iterador = 0
                    for (anime in getListaAnimesUsuario(documentoUsuario)) {
                        if (anime["id"] == document1.id) {
                            animeExiste = true
                            posicionAnime = iterador
                        }
                        iterador++
                    }

                    val listaUsuarioMisAnimes =
                        documentoUsuario.get("misAnimes") as ArrayList<Map<String, *>>
                    if (animeExiste) {
                        val duracion = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(applicationContext, getStringBuilder(getString(R.string.deleted_series)), duracion)
                        toast.show()
                        //------remove anime-------//
                        listaUsuarioMisAnimes.removeAt(posicionAnime)
                        setListaUsuarioMisAnimes(documentoUsuario, listaUsuarioMisAnimes)
                        //------remove anime-------//
                        //binding.button4.text = getString(R.string.add_anime)
                        binding.starButton.isLiked = false
                        binding.starButton.setCircleEndColorRes(R.color.colorAccent)
                        binding.starButton.setCircleEndColorRes(R.color.colorPrimary)
                        binding.starButton.setAnimationScaleFactor(50f)
                        binding.starButton.setExplodingDotColorsRes(R.color.colorPrimary,R.color.colorAccent)
                    } else {
                        val duracion = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(applicationContext, getStringBuilder(getString(R.string.added_series)), duracion)
                        toast.show()
                        //------add anime-------//
                        val mapaAnime = mutableMapOf<String, Any>()
                        mapaAnime.put("id", document1.id)
                        val mapaData = mutableMapOf<String, ArrayList<Any>>()
                        mapaData.put("capitulos", document1.get("capitulos") as ArrayList<Any>)
                        mapaAnime.put("data", mapaData)
                        listaUsuarioMisAnimes.add(mapaAnime)
                        setListaUsuarioMisAnimes(documentoUsuario, listaUsuarioMisAnimes)
                        //------add anime-------//
                        //binding.button4.text = getString(R.string.remove_anime)
                        binding.starButton.isLiked = true
                        binding.starButton.setCircleEndColorRes(R.color.colorAccent)
                        binding.starButton.setCircleEndColorRes(R.color.colorPrimary)
                        binding.starButton.setAnimationScaleFactor(50f)
                        binding.starButton.setExplodingDotColorsRes(R.color.colorPrimary,R.color.colorAccent)
                    }
                }
            }
        }
    }

    private fun getScore(document: DocumentSnapshot): HashMap<*, *> {

        return document["score"] as HashMap<*, *>
    }

    private fun setScoreBD(document: DocumentSnapshot, score: MutableMap<String, Float>) {

        getDocumentAnime(document).update("score", score)
    }

    private fun setListaUsuarioMisAnimes(
        documentoUsuario: QueryDocumentSnapshot,
        listaUsuarioMisAnimes: ArrayList<Map<String, *>>
    ) {

        getUserDocument(documentoUsuario).update("misAnimes", listaUsuarioMisAnimes)
    }

    private fun getDocumentFinalizado(document: DocumentSnapshot): Boolean {

        return document["finalizado"] as Boolean
    }


    private fun animeExist(user: FirebaseUser?, document1: DocumentSnapshot) {
        getDB().collection("Usuario").get().addOnSuccessListener { result ->
            var animeExiste = false
            for (documentoUsuario in result) {
                val userData = documentoUsuario.get("datos") as HashMap<*, *>
                val emailExpected = userData["correo"] as String
                val emailUser = user?.email as String
                if (emailExpected == emailUser) {
                    for (anime in getListaAnimesUsuario(documentoUsuario)) {
                        if (anime["id"] == document1.id) {
                            animeExiste = true
                        }
                    }
                }
            }

            binding.starButton.isLiked = animeExiste
            binding.starButton.setCircleEndColorRes(R.color.colorAccent)
            binding.starButton.setCircleEndColorRes(R.color.colorPrimary)
            binding.starButton.setAnimationScaleFactor(50f)
            binding.starButton.setExplodingDotColorsRes(R.color.colorPrimary,R.color.colorAccent)
            /*if(animeExiste){
                //binding.button4.text = getString(R.string.remove_anime)
                binding.starButton.isLiked = true
            }else{
                //binding.button4.text = getString(R.string.add_anime)
                binding.starButton.isLiked = false
            }*/
        }
    }

    //constantes
    companion object {
        const val ide = "documento"
    }

}//fin clase
