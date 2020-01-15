package com.dev.cristian.alvarez.loginwithgoogleandfacebookdemo.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.dev.cristian.alvarez.loginwithgoogleandfacebookdemo.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class LoginWithGoogleUtils(val context: Activity) {
    private val COD_LOGIN_GOOGLE = 2365

    private var googleSignInClient: GoogleSignInClient? = null
    var callbackCredentials: ((AuthCredential) -> Unit)? = null

    init {
        googleSignInClient = getGoogleSignInOptions()
    }

    private fun getGoogleSignInOptions(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.request_id_token_signin_google))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun startLoginGoogle() {
        context.startActivityForResult(
            googleSignInClient?.signInIntent, COD_LOGIN_GOOGLE
        )
    }

    fun onActivityResult(requestCode: Int, data: Intent?) {
        if (requestCode == COD_LOGIN_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            handleResultGoogleSignIn(task)
        }
    }

    private fun handleResultGoogleSignIn(googleTask: Task<GoogleSignInAccount>) {
        try {
            val googleAccount = googleTask.getResult(ApiException::class.java)
            Log.d("login_activity", "firebaseAuthWithGoogle: " + googleAccount?.id)

            val credentials = GoogleAuthProvider.getCredential(googleAccount?.idToken, null)

            callbackCredentials?.invoke(credentials)
        } catch (e: ApiException) {
            e.printStackTrace()
            Toast.makeText(context, "Ocurrió un error al iniciar sesión: ${ e.message }", Toast.LENGTH_SHORT).show()
        }
    }
}