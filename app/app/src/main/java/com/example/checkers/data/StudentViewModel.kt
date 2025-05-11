package com.example.checkers.data

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.checkers.models.StudentModel
import com.example.checkers.navigation.ROUTE_VIEW_STUDENTS
import com.example.checkers.network.ImgurService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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

class StudentViewModel:ViewModel(){
    private val database = FirebaseDatabase.getInstance().reference.child("Students")

    private fun getImgurService(): ImgurService{
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

    val retrofit = Retrofit.Builder().baseUrl("http://api.imgur.com/")
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
    fun uploadStudentWithImage(
        uri: Uri,
        context: Context,
        name: String,
        gender: String,
        nationality:String,
        course: String,
        desc: String
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
                    "Client-ID 16f488ac8b94fee"
                )

                if (response.isSuccessful) {
                    val imageUrl = response.body()?.data?.link ?: ""

                    val studentId = database.push().key ?: ""
                    val student = StudentModel(
                        name, gender, nationality,course,desc, imageUrl, studentId
                    )

                    database.child(studentId).setValue(student)
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Student saved successfully", Toast.LENGTH_SHORT).show()

                                }
                            }
                        }.addOnFailureListener {
                            viewModelScope.launch {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Failed to save student", Toast.LENGTH_SHORT).show()
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




    fun viewStudents(


        student: MutableState<StudentModel>,


        students: SnapshotStateList<StudentModel>,


        context: Context


    ): SnapshotStateList<StudentModel> {


        val ref = FirebaseDatabase.getInstance().getReference("Students")





        ref.addValueEventListener(object: ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {


                students.clear()


                for (snap in snapshot.children) {


                    val value = snap.getValue(StudentModel::class.java)


                    value?.let {


                        students.add(it)


                    }


                }


                if (students.isNotEmpty()) {


                    student.value = students.first()


                }


            }





            override fun onCancelled(error: DatabaseError) {


                Toast.makeText(context, "Failed to fetch students: ${error.message}", Toast.LENGTH_SHORT).show()





            }


        })





        return students


    }


    fun updateStudent(
        context: Context, navController: NavController,


        name: String, gender: String,


        course: String, desc: String, studentId: String, nationality: String
    ){


        val databaseReference = FirebaseDatabase.getInstance()


            .getReference("Students/$studentId")


        val updatedStudent = StudentModel(name, gender,nationality,


            course, desc,"",studentId)





        databaseReference.setValue(updatedStudent)


            .addOnCompleteListener { task ->


                if (task.isSuccessful){





                    Toast.makeText(context,"Student Updated Successfully", Toast.LENGTH_LONG).show()


                    navController.navigate(ROUTE_VIEW_STUDENTS)


                }else{





                    Toast.makeText(context,"Student update failed", Toast.LENGTH_LONG).show()


                }


            }


    }








    fun deleteStudent(context: Context,studentId: String,


                      navController: NavController){


        AlertDialog.Builder(context)


            .setTitle("Delete Student")


            .setMessage("Are you sure you want to delete this student?")


            .setPositiveButton("Yes"){ _, _ ->


                val databaseReference = FirebaseDatabase.getInstance()


                    .getReference("Students/$studentId")


                databaseReference.removeValue().addOnCompleteListener {


                        task ->


                    if (task.isSuccessful){





                        Toast.makeText(context,"Student deleted Successfully",Toast.LENGTH_LONG).show()


                    }else{


                        Toast.makeText(context,"Student not deleted",Toast.LENGTH_LONG).show()


                    }


                }


            }


            .setNegativeButton("No"){ dialog, _ ->


                dialog.dismiss()


            }


            .show()


    }


}

