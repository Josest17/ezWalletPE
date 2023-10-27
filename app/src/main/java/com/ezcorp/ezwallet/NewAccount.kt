package com.ezcorp.ezwallet

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewAccount : AppCompatActivity() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_account)

        val btnCreate = findViewById<Button>(R.id.createBtn)
        val emailText = findViewById<EditText>(R.id.emailText)
        val passwordText = findViewById<EditText>(R.id.passwordText)
        val nameText = findViewById<EditText>(R.id.nameText)
        val phoneText = findViewById<EditText>(R.id.phoneText)
        val birthDate = findViewById<EditText>(R.id.birthDate)

        val cal = Calendar.getInstance()
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val selectedDate = "${day}/${month + 1}/$year"
            birthDate.setText(selectedDate)
        }, cal[Calendar.YEAR], cal[Calendar.MONTH], cal[Calendar.DAY_OF_MONTH])

        birthDate.isFocusable = false
        birthDate.isClickable = true
        birthDate.setOnClickListener { dpd.show() }

        btnCreate.setOnClickListener {
            if (emailText.text.isNotEmpty() || passwordText.text.isNotEmpty() || phoneText.text.isNotEmpty() || birthDate.text.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(
                    emailText.text.toString(),
                    passwordText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        db.collection("users").document(firebaseAuth.currentUser!!.uid).set(
                            hashMapOf(
                                "email" to emailText.text.toString(),
                                "name" to nameText.text.toString(),
                                "phone" to phoneText.text.toString(),
                                "birthDate" to birthDate.text.toString(),
                                "balance" to 0
                            )
                        )
                        Toast.makeText(this, "Cuenta Creada", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Error al crear cuenta", Toast.LENGTH_SHORT).show()
                        Log.d("TAG", it.exception.toString())
                    }
                }
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }
}