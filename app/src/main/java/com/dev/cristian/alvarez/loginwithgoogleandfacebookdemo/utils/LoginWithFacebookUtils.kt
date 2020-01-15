package com.dev.cristian.alvarez.loginwithgoogleandfacebookdemo.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import java.util.*

class LoginWithFacebookUtils(val context: Activity) {
    var callbackManager: CallbackManager? = null

    var callbackCredentials: ((AuthCredential) -> Unit)? = null

    init {
        callbackManager = CallbackManager.Factory.create()

        registerCallback()
    }

    private fun registerCallback() {
        LoginManager.getInstance().registerCallback(callbackManager, object:
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val credentials = FacebookAuthProvider.getCredential(result.accessToken.token)

                callbackCredentials?.invoke(credentials)
            }

            override fun onCancel() {
                Toast.makeText(context, "Login cancelado", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                error?.printStackTrace()
                Toast.makeText(context, "Error en el login: ${ error?.message }", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun startLoginFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("email", "public_profile"));
    }
}