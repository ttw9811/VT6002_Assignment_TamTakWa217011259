package com.example.vt6002_assignment_tamtakwa217011259

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentProviderClient
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import android.content.Intent
import android.text.Layout
import android.util.Log
import android.view.View
import com.example.vt6002_assignment_tamtakwa217011259.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception



class LoginPage : AppCompatActivity() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }

            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        val biometricLoginButton =
            findViewById<Button>(R.id.finger)
        biometricLoginButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        firebaseAuth = FirebaseAuth.getInstance()

        var btSighIn: SignInButton = findViewById(R.id.googleSignInBtn)
        btSighIn.setOnClickListener{
            Log.d("GOOGLE_SIGN_IN_TAG","onCreate: begin Google SignIn")
            val intent = mGoogleSignInClient.signInIntent
            startActivityForResult(intent,100)
        }
    }

    override fun onResume() {
        super.onResume()
        val biometricStatusTextView =
            findViewById<TextView>(R.id.biometric_status)
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                biometricStatusTextView.text = "App can authenticate using biometrics."
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                biometricStatusTextView.text = "No biometric features available on this device."
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                biometricStatusTextView.text = "Biometric features are currently unavailable."
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                // Prompts the user to create credentials that your app accepts.
                biometricStatusTextView.text = "Biometric features are not enrolled."
        }
    }

    fun openSignUp(view: View){
        val intent = Intent(this, signUp_page::class.java )
        startActivity(intent)
    }

    fun goToHome(view: View?){
        val intent = Intent(this, MainActivity::class.java )
        startActivity(intent)
    }

    override fun onActivityResult(requestCode:Int, resultCode: Int, data:Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100){
            Log.d("GOOGLE_SIGN_IN_TAG","onActivityResult:Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)

            }catch (e:Exception){
                Log.d("GOOGLE_SIGN_IN_TAG","onActivityResult: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?){
        val credential = GoogleAuthProvider.getCredential(account!!.idToken,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult->
                val firebaseUser = firebaseAuth.currentUser
                val uid = firebaseUser!!.uid
                val email = firebaseUser.email

                if(authResult.additionalUserInfo!!.isNewUser){
                    Log.d("GOOGLE_SIGN_IN_TAG","onActivityResult: create new account")
                    Toast.makeText(applicationContext, "" +
                            "Login Success",
                        Toast.LENGTH_SHORT)
                        .show()
                }else{
                    Log.d("GOOGLE_SIGN_IN_TAG","onActivityResult: existing user")
                }
                val openDialog = Dialog(this)
                openDialog.setContentView(R.layout.activity_main)
                val btnYES = openDialog.findViewById<Button>(R.id.openLogin)
                    btnYES.text="Sign Out"
                finish()
            }
            .addOnFailureListener{ e ->
                Log.d("GOOGLE_SIGN_IN_TAG","Loggin Failed")
            }
    }

}