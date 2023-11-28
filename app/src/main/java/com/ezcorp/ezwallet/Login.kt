package com.ezcorp.ezwallet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.login_button)
        val loginGoogle = findViewById<Button>(R.id.login_button2)
        val loginWhatsapp = findViewById<Button>(R.id.login_button3)
        val emailText = findViewById<EditText>(R.id.emailText)
        val passwordText = findViewById<EditText>(R.id.passwordText)
        val register = findViewById<Button>(R.id.register1)

        loginButton.setOnClickListener {
            if (emailText.text.isNotEmpty() || passwordText.text.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(
                    emailText.text.toString(),
                    passwordText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        db.collection("users").document(firebaseAuth.currentUser!!.uid).get()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Bienvenido ${it.data?.get("name")}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", it.exception.toString())
                    }
                }
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
        loginGoogle.setOnClickListener {
            Toast.makeText(this, "Inicio de Sesión aún no implementado", Toast.LENGTH_SHORT).show()
        }
        loginWhatsapp.setOnClickListener {
            Toast.makeText(this, "Inicio de Sesión WhatsApp no implementado", Toast.LENGTH_SHORT)
                .show()
        }

        register.setOnClickListener {
            val intent = Intent(this, NewAccount::class.java)
            startActivity(intent)
        }

    }

}