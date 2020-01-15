package com.dev.cristian.alvarez.loginwithgoogleandfacebookdemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import java.util.*

class LoginActivity : AppCompatActivity() {

    private val COD_LOGIN_GOOGLE = 4134

    private var btnLoginFacebook: LoginButton? = null
    private var btnLoginGoogle: Button? = null

    private lateinit var authFire: FirebaseAuth

    private var loginWithFacebookUtils: LoginWithFacebookUtils? = null
    private var loginWithGoogleUtils: LoginWithGoogleUtils? = null

    private lateinit var gso: GoogleSignInOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        authFire = FirebaseAuth.getInstance()
        loginWithFacebookUtils = LoginWithFacebookUtils(this)
        loginWithGoogleUtils = LoginWithGoogleUtils(this)

        btnLoginFacebook = findViewById(R.id.login_button_facebook)
        btnLoginFacebook?.setReadPermissions("email");

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


    private fun handleCredentialsLogin(credentials: AuthCredential): Unit {
        authFire.signInWithCredential(credentials)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = authFire.currentUser

                    Log.v("login_activity", "User: Id: ${ user?.uid } name: ${user?.displayName}, email: ${user?.email}")

                    Toast.makeText(this, "Login completado", Toast.LENGTH_SHORT).show()
                } else {
                    task.exception?.printStackTrace()
                    Log.e("login_activity", "Error en login ${ task.exception?.message }")
                    Toast.makeText(this@LoginActivity, "Algo falló en el login con Google: ${ task.exception?.message }", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private class LoginWithGoogleUtils(val context: Activity) {

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

    private class LoginWithFacebookUtils(val context: Activity) {
        var callbackManager: CallbackManager? = null

        var callbackCredentials: ((AuthCredential) -> Unit)? = null

        init {
            callbackManager = CallbackManager.Factory.create()

            registerCallback()
        }

        private fun registerCallback() {
            LoginManager.getInstance().registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
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
}
