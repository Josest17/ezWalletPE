package com.ezcorp.ezwallet

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewCard : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_card)

        val addCardBtn = findViewById<Button>(R.id.addCardButton)
        val returnBtn = findViewById<ImageView>(R.id.returnBtn)
        val cardNumberText = findViewById<TextView>(R.id.cardNumberText)
        val cvvText = findViewById<TextView>(R.id.cvvText)
        val nameText = findViewById<TextView>(R.id.nameText)
        val expDateText = findViewById<TextView>(R.id.expDateText)
        val brandLogo = findViewById<ImageView>(R.id.brandLogo)

        val cardNumberEdit = findViewById<EditText>(R.id.cardNumberEdit)
        val cvvEdit = findViewById<EditText>(R.id.cvvEdit)
        val nameEdit = findViewById<EditText>(R.id.nameEdit)
        val expDateEdit = findViewById<EditText>(R.id.expDateEdit)

        cardNumberEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString()
                val formattedText = formatCardNumber(inputText)
                cardNumberText.text = formattedText

                if (inputText.isNotEmpty()) {
                    when (inputText[0].toString().toInt()) {
                        4 -> brandLogo.setImageResource(R.drawable.visa_logo)
                        5 -> brandLogo.setImageResource(R.drawable.mastercard_logo)
                        3 -> brandLogo.setImageResource(R.drawable.amex_logo)
                        else -> brandLogo.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                    }
                } else {
                    brandLogo.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                }

                if (s.isNullOrBlank()) {
                    cardNumberText.text = "XXXX XXXX XXXX XXXX"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        cvvEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cvvText.text = s.toString()
                if (s.isNullOrBlank()) {
                    cvvText.text = "XXX"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        nameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                nameText.text = s.toString()
                if (s.isNullOrBlank()) {
                    nameText.text = "Nombre y Apellidos"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        expDateEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                expDateText.text = s.toString()
                if (s.isNullOrBlank()) {
                    expDateText.text = "MM/YY"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        returnBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        addCardBtn.setOnClickListener {
            val cardNumber = cardNumberEdit.text.toString()
            val cvv = cvvEdit.text.toString()
            val name = nameEdit.text.toString()
            val expDate = expDateEdit.text.toString()

            if (cardNumber.isNotEmpty() && cvv.isNotEmpty() && name.isNotEmpty() && expDate.isNotEmpty()) {
                db.collection("cards").document().set(
                    hashMapOf(
                        "cardNumber" to cardNumber.takeLast(4),
                        "cvv" to cvv,
                        "expDate" to expDate,
                        "ownerName" to name,
                        "ownerUserId" to auth.currentUser!!.uid,
                        when (cardNumber[0].toString().toInt()) {
                            4 -> "brand" to "visa"
                            5 -> "brand" to "mastercard"
                            3 -> "brand" to "amex"
                            else -> "brand" to "other"
                        }
                    )
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Tarjeta agregada", Toast.LENGTH_SHORT).show()
                        val returnIntent = Intent(this, MainActivity::class.java)
                        startActivity(returnIntent)
                    } else {
                        Toast.makeText(this, "Error al agregar la tarjeta", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("TAG", it.exception.toString())
                    }
                }
            } else {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatCardNumber(inputText: String): String {
        val formattedText = StringBuilder()
        for (i in inputText.indices) {
            if (i % 4 == 0 && i > 0) {
                formattedText.append(" ")
            }
            formattedText.append(inputText[i])
        }
        return formattedText.toString()
    }
}