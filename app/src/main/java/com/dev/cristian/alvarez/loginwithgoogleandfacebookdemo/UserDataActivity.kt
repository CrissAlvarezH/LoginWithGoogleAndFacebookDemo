package com.dev.cristian.alvarez.loginwithgoogleandfacebookdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserDataActivity : AppCompatActivity() {

    private var imgUser: ImageView? = null
    private var txtNameUser: TextView? = null
    private var txtEmailUser: TextView? = null
    private var txtUidUser: TextView? = null

    private lateinit var authFire:  FirebaseAuth

    override fun onStart() {
        super.onStart()

        val user = authFire.currentUser

        if (user == null) toLogin()
        else setDataUI(user)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)
        authFire = FirebaseAuth.getInstance()

        imgUser = findViewById(R.id.img_user)
        txtUidUser = findViewById(R.id.txt_uid_user)
        txtEmailUser = findViewById(R.id.txt_user_email)
        txtNameUser = findViewById(R.id.txt_user_name)
    }

    fun setDataUI(user: FirebaseUser) {
        txtNameUser?.text = user.displayName
        txtEmailUser?.text = user.email
        txtUidUser?.text = user.uid
    }

    private fun toLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
        )

        finish()
    }

    fun onClickBtnLogout(btn: View) {
        FirebaseAuth.getInstance().signOut()

        toLogin()
    }
}
