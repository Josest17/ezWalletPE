package com.ezcorp.ezwallet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TransfersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransfersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transfers, container, false)
        val input = view.findViewById<AutoCompleteTextView>(R.id.input)
        val userID = view.findViewById<EditText>(R.id.userID)
        val amount = view.findViewById<TextView>(R.id.amount)
        val sendBtn = view.findViewById<Button>(R.id.buttonSend)
        val button1 = view.findViewById<Button>(R.id.button1)
        val button2 = view.findViewById<Button>(R.id.button2)
        val button3 = view.findViewById<Button>(R.id.button3)
        val button4 = view.findViewById<Button>(R.id.button4)
        val button5 = view.findViewById<Button>(R.id.button5)
        val button6 = view.findViewById<Button>(R.id.button6)
        val button7 = view.findViewById<Button>(R.id.button7)
        val button8 = view.findViewById<Button>(R.id.button8)
        val button9 = view.findViewById<Button>(R.id.button9)
        val button0 = view.findViewById<Button>(R.id.button0)
        val buttonDot = view.findViewById<Button>(R.id.buttonDot)
        val buttonBackspace = view.findViewById<Button>(R.id.buttonBackspace)
        val userList = ArrayList<String>()
        val usersMap = HashMap<String, String>()

        val usersDB = db.collection("users")
        usersDB.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val name = document.data["name"].toString()
                val id = document.id
                if (id != firebaseAuth.currentUser!!.uid) {
                    userList.add(name)
                    usersMap[name] = id
                }
            }
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            userList
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        input.threshold = 3
        input.setAdapter(adapter)

        input.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.adapter.getItem(position).toString()
            val selectedID = usersMap[selectedName]
            userID.setText(selectedID)
        }

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    userID.setText("")
                }
            }
        })

        button0.setOnClickListener {
            if (amount.text.toString() == "0.00") {
                amount.text = "0.00"
            } else {
                amount.append("0")
            }
        }

        button1.setOnClickListener {
            if (amount.text.toString() == "0.00") {
                amount.text = "1"
            } else {
                amount.append("1")
            }
        }

        button2.setOnClickListener {
            if (amount.text.toString() == "0.00") {
                amount.text = "2"
            } else {
                amount.append("2")
            }
        }

        button3.setOnClickListener {
            if (amount.text.toString() == "0.00") {
                amount.text = "3"
            } else {
                amount.append("3")
            }
        }

        button4.setOnClickListener {
            if (amount.text.toString() == "0.00") {
                amount.text = "4"
            } else {
                amount.append("4")
            }
        }

        button5.setOnClickListener {
            if (amount.text.toString() == "0.00") {
                amount.text = "5"
            } else {
                amount.append("5")
            }
        }

        button6.setOnClickListener {
            if (amount.text.toString() == "0.00") {
                amount.text = "6"
            } else {
                amount.append("6")
            }
        }

        button7.setOnClickListener {
            if (amount.text.toString() == "0.00") {
                amount.text = "7"
            } else {
                amount.append("7")
            }
        }

        button8.setOnClickListener {
            if (amount.text.toString() == "0.00") {
                amount.text = "8"
            } else {
                amount.append("8")
            }
        }

        button9.setOnClickListener {
            if (amount.text.toString() == "0.00") {
                amount.text = "9"
            } else {
                amount.append("9")
            }
        }

        buttonDot.setOnClickListener {
            if (amount.text.toString() == "0.00") {
                amount.text = "0."
            } else {
                amount.append(".")
            }
        }

        buttonBackspace.setOnClickListener {
            val text = amount.text.toString()
            if (text.isEmpty() || text == "0.00") {
                return@setOnClickListener
            }

            if (text.length == 1) {
                amount.text = "0.00"
            } else {
                val newText = text.substring(0, text.length - 1)
                amount.text = newText
            }
        }

        sendBtn.setOnClickListener {
            val amountValue = amount.text.toString().toDouble()
            val receiverAcc = userID.text.toString()
            var balance = 0.00
            var balanceReceiver = 0.00

            val usersDB = db.collection("users")
            val documentSender = usersDB.document(firebaseAuth.currentUser!!.uid)
            val documentReceiver = usersDB.document(receiverAcc)

            documentSender.get().addOnCompleteListener { firstIt ->
                if (firstIt.isSuccessful) {
                    balance = firstIt.result!!.data!!["balance"].toString().toDouble()
                    documentReceiver.get().addOnCompleteListener { secondIt ->
                        if (secondIt.isSuccessful) {
                            balanceReceiver =
                                secondIt.result!!.data!!["balance"].toString().toDouble()
                            if (receiverAcc.isEmpty() || amountValue == 0.00 || balance < amountValue) {
                                Toast.makeText(
                                    context,
                                    "No es posible realizar la transacción",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val transfersDB = db.collection("transfers")
                                transfersDB.add(
                                    hashMapOf(
                                        "sender" to firebaseAuth.currentUser!!.uid,
                                        "receiver" to receiverAcc,
                                        "amount" to amountValue,
                                        "date" to FieldValue.serverTimestamp()
                                    )
                                )
                                documentSender.update("balance", balance - amountValue)
                                documentReceiver.update("balance", balanceReceiver + amountValue)
                                Toast.makeText(
                                    context,
                                    "Transacción realizada",
                                    Toast.LENGTH_SHORT
                                ).show()
                                input.setText("")
                                val intent = activity?.intent
                                activity?.finish()
                                startActivity(intent)
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "No es posible realizar la transacción",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "No es posible realizar la transacción",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransfersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransfersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}