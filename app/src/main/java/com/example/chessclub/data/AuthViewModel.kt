package com.example.chessclub.data

import android.content.Context
import android.content.LocusId
import android.service.autofill.UserData
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.chessclub.models.UserModel
import com.example.chessclub.navigation.ROUTE_HOME
import com.example.chessclub.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel: ViewModel() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)
    fun signup(firstname:String,lastname:String,email:String,password: String,
               navController: NavController,
               context: Context
    ){
        if (firstname.isBlank() || lastname.isBlank() || email.isBlank() || password.isBlank()){
            Toast.makeText(context,"please fill all the fields",Toast.LENGTH_LONG).show()

            return
        }
        _isLoading.value = true

        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful){
                    val userId = mAuth.currentUser?.uid ?:""
                    val userData = UserModel(firstname = firstname,lastname = lastname,
                        email=email,password = password,userId = userId)
                    saveUserToDatabase(userId,userData,navController,context)

                }else{
                    _errorMessage.value = task.exception?.message

                    Toast.makeText(context,"Registration failed",Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_LOGIN)
                }

            }
    }
    fun saveUserToDatabase(userId:String,userData:UserModel,
                           navController: NavController,
                           context: Context
    ){
    val regRef= FirebaseDatabase.getInstance()
        .getReference("Users/$userId")
    regRef.setValue(userData).addOnCompleteListener { regRef ->
        if (regRef.isSuccessful){
            Toast.makeText(context,"User Successfully Registered",Toast.LENGTH_LONG).show()
           navController.navigate(ROUTE_LOGIN)
        }else {
         _errorMessage.value = regRef.exception?.message
         Toast.makeText(context,"Database error",Toast.LENGTH_LONG).show()

        }
        }
    }
    fun login(email: String,password: String,navController: NavController,
              context: Context){
        if (email.isBlank() || password.isBlank()){

            Toast.makeText(context,"Email and password required",Toast.LENGTH_LONG).show()
            return

        }
        _isLoading.value = true

        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                _isLoading.value = false
                if (task.isSuccessful){


                 Toast.makeText(context,"User successfully logged in",Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_HOME)
                }else{
                    _errorMessage.value = task.exception?.message

                    Toast.makeText(context, "log in failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun logout(navController: NavController,context: Context){
        mAuth.signOut()
        Toast.makeText(context,"User successfully logged out",Toast.LENGTH_LONG).show()
        navController.navigate(ROUTE_LOGIN)

    }

    }

