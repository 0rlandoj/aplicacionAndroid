package com.example.ragnarok

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.adaptadores.RecyclerAdapter
import com.example.clases.Anime
import com.example.clases.Datos
import com.example.clases.Usuario
import com.example.metodos.getDB
import com.example.ragnarok.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QueryDocumentSnapshot


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mRecyclerView: RecyclerView
    private val mAdapter = RecyclerAdapter()
    private lateinit var listaDeanimes: MutableList<Anime>

    //private val user = FirebaseAuth.getInstance().currentUser

    // [START auth_fui_create_launcher]
    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }
    // [END auth_fui_create_launcher]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        createSignInIntent()
    }

    // [START auth_fui_result]
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
            //si inicia sesion
            // Successfully signed in

            //app check
            FirebaseApp.initializeApp(/*context=*/ this)
            val firebaseAppCheck = FirebaseAppCheck.getInstance()
            firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance())
            //app check

            anuncios()

            if (user != null) {
                Log.d("pruebaUsuario1", user.uid)
            }

            //Una vez inicia sesion el usuario comprobamos si existe
            //el correo electronico.
            //si no existe lo creamos en la base de datos de "usuario"
            if (user != null) {
                createUser(user)
            }

            //-------------------------------//
            listaDeanimes = ArrayList()

            getDB().collection("Anime")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {

                        listaDeanimes.add(Anime(document.id))
                    }
                    setUpRecycleView(listaDeanimes)
                }
                .addOnFailureListener { exception ->
                    Log.d("pruebaFallida", "Error getting documents: ", exception)
                }

            //--cargar anuncio de prueba de la vista--//
            val adRequest = AdRequest.Builder().build()
            binding.anuncioPrincipal.loadAd(adRequest)
            //--cargar anuncio de prueba de la vista--//


            binding.navigationView.setNavigationItemSelectedListener(this)
            //-------------------------------//
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            back()
        }
    }
    // [END auth_fui_result]


    //Se reemplaza por la funcion onSignInResult
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == RC_SIGN_IN) {
            IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                //si inicia sesion
                // Successfully signed in

                //app check
                FirebaseApp.initializeApp(/*context=*/ this)
                val firebaseAppCheck = FirebaseAppCheck.getInstance()
                firebaseAppCheck.installAppCheckProviderFactory(
                    SafetyNetAppCheckProviderFactory.getInstance())
                //app check


                if (user != null) {
                    Log.d("pruebaUsuario1", user.uid)
                }

                //Una vez inicia sesion el usuario comprobamos si existe
                //el correo electronico.
                //si no existe lo creamos en la base de datos de "usuario"
                if (user != null) {
                    createUser(user)
                }

                //-------------------------------//
                listaDeanimes = ArrayList()

                getDB().collection("Anime")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {

                            listaDeanimes.add(Anime(document.id))
                        }
                        setUpRecycleView(listaDeanimes)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("pruebaFallida", "Error getting documents: ", exception)
                    }

                //--cargar anuncio de prueba de la vista--//
                val adRequest = AdRequest.Builder().build()
                binding.anuncioPrincipal.loadAd(adRequest)
                //--cargar anuncio de prueba de la vista--//


                binding.navigationView.setNavigationItemSelectedListener(this)
                //-------------------------------//

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)


            } else {
                //si no inicia secion
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                back()
            }
        }
    }*/













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
            R.id.stadisticsItem -> {
                val intent = Intent(this, MainActivityEstadisticas::class.java)
                startActivity(intent)
            }
            R.id.currentSeason -> {
                val intent = Intent(this, MainActivityCurrentSeason::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    private fun setUpRecycleView(ListaDeanimes : MutableList<Anime>){
        mRecyclerView = findViewById(R.id.recycleView)
        mRecyclerView.setHasFixedSize(true)
        //mRecyclerView.layoutManager = GridLayoutManager(this,3)
        mRecyclerView.layoutManager = LinearLayoutManager (this)
        mAdapter.recyclerAdapter(ListaDeanimes, this)
        mRecyclerView.adapter = mAdapter
    }

    //Anuncios adMob//
    private fun anuncios(){
        //--firebase admob--//
        //creamos una lista con los dispositivos de pruebas para admob
        val listaDispositivosDePrueba = listOf("F02F673BD6645863AA575EB485091A19")
        val requestConfiguration =
            RequestConfiguration.Builder().setTestDeviceIds(listaDispositivosDePrueba).build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        MobileAds.initialize(this)
        //--firebase admob--//
    }
    //Anuncios adMob//

    //metodo para que se deslize y esconda el menu lateral
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    /*---------------------Autenticacion---------------------*/
    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(
            //AuthUI.IdpConfig.AnonymousBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Create and launch sign-in intent
        //antiguo
        /*startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )*/
        // [END auth_fui_create_intent]

        // Create and launch sign-in intent
        // Nuevo
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
        // [END auth_fui_create_intent]
    }
    /*---------------------Autenticacion---------------------*/

    //no funciona con sesion anonima
    //Comprobamos si el usuario existe en la base de datos de 'usuario'
    //
    private fun createUser(user: FirebaseUser)  {
        getDB().collection("Usuario").get().addOnSuccessListener { result ->

            var crearUsuario = 0

            for (document in result) {
                if(existUser(user, document) == 1){
                    crearUsuario = 1
                }
            }

            if(crearUsuario == 0){
                createDocument(user)
            }
        }
    }

    //recorre los documentos recuperados de la base de datos de "usuario"
    //Si el correo existe devuelve 1, si no devuelve 0
    private fun existUser(user: FirebaseUser, document: QueryDocumentSnapshot) : Int {
        var existe = 0
        val datosDelUsuario = document.get("datos") as HashMap<*, *>
        val correoDelUsuario = datosDelUsuario["correo"] as String
        val correoDeLaSesion = user.email as String
        if(correoDelUsuario == correoDeLaSesion){
            existe++
        }
        return existe
    }

    private fun createDocument(user: FirebaseUser?) {
        val datos = Datos(
            user?.displayName,
            user?.phoneNumber,
            user?.email,
            user?.isEmailVerified
        )
        val animes = ArrayList<Any?>()
        val usuario = Usuario(datos, animes)
        createUser(usuario)
    }

    private fun createUser(usuario : Usuario) {

        getDB().collection("Usuario").add(usuario)
    }

    private fun back() {
        onBackPressed()
    }


}
