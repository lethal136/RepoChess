package com.example.chessclub.data

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.chessclub.models.PlayerModel
import com.example.chessclub.navigation.ROUTE_VIEW_PLAYERS
import com.example.chessclub.network.ImgurService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class PlayerViewModel:ViewModel(){
    private val database = FirebaseDatabase.getInstance().reference.child("Players")
    private var valueEventListener: ValueEventListener? = null // Add this declaration

    private fun getImgurService(): ImgurService{
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

    val retrofit = Retrofit.Builder().baseUrl("https://api.imgur.com/")
        .addConverterFactory(
        GsonConverterFactory.create()).client(client)
        .build()
    return retrofit.create(ImgurService::class.java)
    }
    private fun getFileFromUri(context: Context,Uri: Uri):
            File?{
        return try {
            val  inputStream = context.contentResolver
                .openInputStream(Uri)
            val file = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            file.outputStream().use { output ->
                inputStream?.copyTo(output)
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun uploadPlayerWithImage(
        uri: Uri,
        context: Context,
        name: String,
        gender: String,
        nationality:String,
        username: String,
        desc: String,
        navController: NavController // Add NavController parameter
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = getFileFromUri(context, uri)
                if (file == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, reqFile)

                val response = getImgurService().uploadImage(
                    body,
                    "Client-ID fdb975352b451d5"
                )

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.data?.link ?: ""

                    val playerId = database.push().key ?: ""
                    val player = PlayerModel(
                        name, gender, nationality,username,desc, imageUrl, playerId
                    )

                    database.child(playerId).setValue(player)
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Player saved successfully", Toast.LENGTH_SHORT).show()
                                    navController.navigate(ROUTE_VIEW_PLAYERS)

                                }
                            }
                        }.addOnFailureListener {
                            viewModelScope.launch {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Failed to save player", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Upload error", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Exception: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }

        }
    }




    fun viewPlayers(
        player: MutableState<PlayerModel>,
        players: SnapshotStateList<PlayerModel>,
        context: Context,
        onDataLoaded: () -> Unit // Added callback parameter
    ): SnapshotStateList<PlayerModel> {
        val ref = FirebaseDatabase.getInstance().getReference("Players")

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                players.clear()
                for (snap in snapshot.children) {
                    try {
                        val value = snap.getValue(PlayerModel::class.java)
                        value?.let { players.add(it) }
                    } catch (e: DatabaseException) {
                        Log.e("PlayerViewModel", "Failed to deserialize player at ${snap.key}: ${e.message}")
                    }
                }
                if (players.isNotEmpty()) {
                    player.value = players.first()
                }
                onDataLoaded() // Notify that data loading is complete
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to fetch players: ${error.message}", Toast.LENGTH_SHORT).show()
                onDataLoaded() // Notify even if there’s an error
            }
        }.also { ref.addValueEventListener(it) }

        return players
    }

    fun updatePlayer(
        context: Context, navController: NavController,


        name: String, gender: String,


        username: String, desc: String, playerId: String, nationality: String
    ){


        val databaseReference = FirebaseDatabase.getInstance()


            .getReference("Players/$playerId")


        val updatePlayer = PlayerModel(name, gender,nationality,


            username, desc,"",playerId)





        databaseReference.setValue(updatePlayer)


            .addOnCompleteListener { task ->


                if (task.isSuccessful){





                    Toast.makeText(context,"Player Updated Successfully", Toast.LENGTH_LONG).show()


                    navController.navigate(ROUTE_VIEW_PLAYERS)


                }else{





                    Toast.makeText(context,"Player update failed", Toast.LENGTH_LONG).show()


                }


            }


    }








    fun deletePlayer(context: Context,playerId: String,


                      navController: NavController){


        AlertDialog.Builder(context)


            .setTitle("Delete player")


            .setMessage("Are you sure you want to delete this player?")


            .setPositiveButton("Yes"){ _, _ ->


                val databaseReference = FirebaseDatabase.getInstance()


                    .getReference("Players/$playerId")


                databaseReference.removeValue().addOnCompleteListener {


                        task ->


                    if (task.isSuccessful){





                        Toast.makeText(context,"Player deleted Successfully",Toast.LENGTH_LONG).show()


                    }else{


                        Toast.makeText(context,"Player not deleted",Toast.LENGTH_LONG).show()


                    }


                }


            }


            .setNegativeButton("No"){ dialog, _ ->


                dialog.dismiss()


            }


            .show()


    }


}

