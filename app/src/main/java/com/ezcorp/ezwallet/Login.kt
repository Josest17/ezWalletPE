package com.ezcorp.ezwallet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.login_button)
        val loginGoogle = findViewById<Button>(R.id.login_button2)
        val loginWhatsapp = findViewById<Button>(R.id.login_button3)
        val emailText = findViewById<EditText>(R.id.emailText)
        val register = findViewById<Button>(R.id.register1)

        loginButton.setOnClickListener {
            Toast.makeText(this, "Bienvenido ${emailText.text}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        loginGoogle.setOnClickListener {
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        loginWhatsapp.setOnClickListener {
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        register.setOnClickListener {
            val intent = Intent(this, NewAccount::class.java)
            startActivity(intent)
        }

    }

}