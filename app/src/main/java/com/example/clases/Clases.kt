package com.example.clases

// [START city_class]
data class Usuario(
    val datos : Datos,
    val misAnimes: ArrayList<*>
)

data class Datos(
    val nombre: String? = null,
    val telefono: String? = null,
    val correo: String? = null,
    val correoVerificado: Boolean? = false
)

data class Anime(
    val idAnime: String
)
// [END city_class]