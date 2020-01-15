package com.dev.cristian.alvarez.loginwithgoogleandfacebookdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.dev.cristian.alvarez.loginwithgoogleandfacebookdemo.utils.LoginWithFacebookUtils
import com.dev.cristian.alvarez.loginwithgoogleandfacebookdemo.utils.LoginWithGoogleUtils
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*

class LoginActivity : AppCompatActivity() {
    private var btnLoginFacebook: Button? = null
    private var btnLoginGoogle: Button? = null

    private lateinit var authFire: FirebaseAuth

    private var loginWithFacebookUtils: LoginWithFacebookUtils? = null
    private var loginWithGoogleUtils: LoginWithGoogleUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        authFire = FirebaseAuth.getInstance()
        loginWithFacebookUtils = LoginWithFacebookUtils(this)
        loginWithGoogleUtils = LoginWithGoogleUtils(this)

        btnLoginFacebook = findViewById(R.id.btn_login_facebook)
        btnLoginFacebook?.setOnClickListener {
            loginWithFacebookUtils?.startLoginFacebook()
        }

        btnLoginGoogle = findViewById(R.id.btn_login_google)
        btnLoginGoogle?.setOnClickListener {
            loginWithGoogleUtils?.startLoginGoogle()
        }

        loginWithGoogleUtils?.callbackCredentials   = ::handleCredentialsLogin
        loginWithFacebookUtils?.callbackCredentials = ::handleCredentialsLogin
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loginWithFacebookUtils?.callbackManager?.onActivityResult(requestCode, resultCode, data)
        loginWithGoogleUtils?.onActivityResult(requestCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleCredentialsLogin(credentials: AuthCredential) {
        authFire.signInWithCredential(credentials)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = authFire.currentUser

                    Log.v("login_activity", "User: Id: ${ user?.uid } name: ${user?.displayName}, email: ${user?.email}")

                    Toast.makeText(this, "Login completado", Toast.LENGTH_SHORT).show()
                } else {
                    task.exception?.printStackTrace()
                    Log.e("login_activity", "Error en login ${ task.exception?.message }")
                    Toast.makeText(this@LoginActivity, "Algo falló en el login: ${ task.exception?.message }", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
