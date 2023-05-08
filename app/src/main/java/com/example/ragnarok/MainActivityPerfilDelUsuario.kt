package com.example.ragnarok

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.metodos.getStringBuilder
import com.example.ragnarok.databinding.ActivityMainPerfilDelUsuarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivityPerfilDelUsuario : AppCompatActivity() {

    private lateinit var binding: ActivityMainPerfilDelUsuarioBinding

    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainPerfilDelUsuarioBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (user != null) {

            binding.correo.text =
                user.email?.let { getStringBuilder(binding.correo.text.toString(), it) }

            binding.nombre.text =
                user.displayName?.let { getStringBuilder(binding.nombre.text.toString(), it) }

            binding.telefono.text =
                user.phoneNumber?.let { getStringBuilder(binding.telefono.text.toString(), it) }

            if(user.isEmailVerified){
                binding.constraintLayout.removeView(binding.botonVerificar)
                binding.correoVerificado.text = getStringBuilder(getString(R.string.The_email_is_verified))
            }
            else{
                binding.correoVerificado.text = getStringBuilder(getString(R.string.The_email_is_not_verified))
            }
        }

        binding.botonVerificar.setOnClickListener {

            verificarEmail(user)
        }


        //--------------------------------bottomNavigationView-------------------------------------//
        /*binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mainItem -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.stadisticsItem -> {
                    val intent = Intent(this, MainActivityEstadisticas::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }*/
        //--------------------------------bottomNavigationView-------------------------------------//
    }//fin on run

    private fun verificarEmail(user : FirebaseUser?) {

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val duracion = Toast.LENGTH_SHORT

                    val toast = Toast.makeText(applicationContext, getStringBuilder("Mensaje de verificacion enviado"),duracion)
                    toast.show()
                    Log.d("pruebaVerificarEmail", "Email sent.")
                }
            }

    }
}//fin activity