package com.example.metodos

import com.example.clases.Anime
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

    private const val url = "gs://ragnarok-1c4a2.appspot.com/"

    //RECUPERAMOS INSTANCIAS//
    fun getDB() : FirebaseFirestore {

        return FirebaseFirestore.getInstance()
    }

    fun getDBStorage() : FirebaseStorage {

        return FirebaseStorage.getInstance()
    }
    //RECUPERAMOS INSTANCIAS//

    //RECUPERAMOS IGAMENES DE FIREBASE STORAGE//
    fun getImage(anime: Anime): StorageReference {

        return getDBStorage().getReferenceFromUrl(url).child(anime.idAnime + "_1920x1080.jpg")
    }

    fun getImage(anime: DocumentSnapshot): StorageReference {

        return getDBStorage().getReferenceFromUrl(url).child(anime.id + "_1920x1080.jpg")
    }
    //RECUPERAMOS IGAMENES DE FIREBASE STORAGE//

    //BASE DE DATOS 'ANIME'//
    fun getDocumentAnime(documentoUsuario : DocumentSnapshot) : DocumentReference {

        return getDB().collection("Anime").document(documentoUsuario.id)
    }

    //BASE DE DATOS 'ANIME'//

    //BASE DE DATOS 'USUARIO'//
    fun getUserDocument(document : QueryDocumentSnapshot) : DocumentReference{

        return getDB().collection("Usuario").document(document.id)
    }
    //BASE DE DATOS 'USUARIO'//

    //STRING BUILDER//
    private fun stringBuilder() : StringBuilder {

        return StringBuilder()
    }

    fun getStringBuilder(text : String) : StringBuilder {

        return stringBuilder().append(text)
    }

    fun getStringBuilder(text : String, text2 : String) : StringBuilder {

        return stringBuilder().append(text).append(text2)
    }
    //STRING BUILDER//

    fun getTranslations(document : DocumentSnapshot) : DocumentReference {

        return getDB().collection("translations").document(document.id)
    }
















