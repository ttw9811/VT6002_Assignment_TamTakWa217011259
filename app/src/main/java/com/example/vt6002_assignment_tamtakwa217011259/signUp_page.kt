package com.example.vt6002_assignment_tamtakwa217011259

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

class signUp_page : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_page)

        auth= FirebaseAuth.getInstance()
    }

    fun register(view: View){
        val editTextEmailAddress:EditText = findViewById(R.id.loginInput)
        val email=editTextEmailAddress.text.toString()

        val editTextPassword:EditText = findViewById(R.id.pwdInput)
        val password=editTextPassword.text.toString()




    }
}