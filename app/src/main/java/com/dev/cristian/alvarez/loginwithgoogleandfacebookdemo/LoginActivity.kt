package com.dev.cristian.alvarez.loginwithgoogleandfacebookdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private var btnLoginFacebook: LoginButton? = null

    private lateinit var authFire: FirebaseAuth

    private var loginWithFacebookManager: LoginWithFacebookManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        authFire = FirebaseAuth.getInstance()
        loginWithFacebookManager = LoginWithFacebookManager()

        btnLoginFacebook = findViewById(R.id.login_button_facebook)
        btnLoginFacebook?.setReadPermissions("email");

//        Code to lauch login with Facebook from custom button
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));

        loginWithFacebookManager?.registerCallback()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        loginWithFacebookManager?.callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private inner class LoginWithFacebookManager {

        var callbackManager: CallbackManager? = null

        init {
            callbackManager = CallbackManager.Factory.create()
        }

        fun registerCallback() {
            LoginManager.getInstance().registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
//                val isLoggedIn = result?.accessToken != null && !result.accessToken.isExpired()
                    handleFacebookAccessToken(result.accessToken)
                }

                override fun onCancel() {
                    Toast.makeText(this@LoginActivity, "Login cancelado", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException?) {
                    error?.printStackTrace()
                    Toast.makeText(this@LoginActivity, "Error en el login: ${ error?.message }", Toast.LENGTH_SHORT).show()
                }

            })
        }

        private fun handleFacebookAccessToken(token: AccessToken) {
            val credentials = FacebookAuthProvider.getCredential(token.token)


            authFire.signInWithCredential(credentials)
                .addOnCompleteListener(this@LoginActivity) { task ->
                    if (task.isSuccessful) {

                        val user: FirebaseUser? = authFire.currentUser

                        Log.v("login_activity", "User: name: ${user?.displayName}, email: ${user?.email}")
                    } else {
                        task.exception?.message
                        Log.e("login_activity", "Error en login Facebook ${ task.exception?.message }")
                        Toast.makeText(this@LoginActivity, "Algo falló en el login con Facebook: ${ task.exception?.message }", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
