package com.dev.cristian.alvarez.loginwithgoogleandfacebookdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class UserDataActivity : AppCompatActivity() {

    private val imgUser: ImageView? = null
    private val txtNameUser: TextView? = null
    private val txtEmailUser: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        startActivity(
            Intent(this, LoginActivity::class.java)
        )

        finish()
    }
}
