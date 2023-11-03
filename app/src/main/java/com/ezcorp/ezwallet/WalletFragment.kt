package com.ezcorp.ezwallet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WalletFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WalletFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_wallet, container, false)
        val addBtn = view.findViewById<Button>(R.id.add_button)
        val editBtn = view.findViewById<Button>(R.id.editButton)
        val deleteBtn = view.findViewById<Button>(R.id.deleteButton)
        val layoutCards = view.findViewById<LinearLayout>(R.id.layoutCards)
        val horizontalSV = view.findViewById<HorizontalScrollView>(R.id.horizontalSV)
        val editName = view.findViewById<EditText>(R.id.editName)
        val editCard = view.findViewById<EditText>(R.id.cardNumberEdit)
        val editId = view.findViewById<EditText>(R.id.idEdit)
        val editLayout = view.findViewById<LinearLayout>(R.id.editLayout)
        val mainText = view.findViewById<TextView>(R.id.mainText)

        val cardsDB = db.collection("cards")
        cardsDB.whereEqualTo("ownerUserId", firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    layoutCards.addView(
                        LayoutInflater.from(context).inflate(
                            R.layout.creditcard, layoutCards, false
                        )
                    )
                    val cardNumberText = layoutCards.getChildAt(layoutCards.childCount - 1)
                        .findViewById<TextView>(R.id.cardNumberText)
                    val nameText = layoutCards.getChildAt(layoutCards.childCount - 1)
                        .findViewById<TextView>(R.id.nameText)
                    val expDateText = layoutCards.getChildAt(layoutCards.childCount - 1)
                        .findViewById<TextView>(R.id.expDateText)
                    val brandLogo = layoutCards.getChildAt(layoutCards.childCount - 1)
                        .findViewById<ImageView>(R.id.brandLogo)
                    val idCard = layoutCards.getChildAt(layoutCards.childCount - 1)
                        .findViewById<TextView>(R.id.idCard)


                    cardNumberText.text = "XXXX XXXX XXXX ${document.data["cardNumber"].toString()}"
                    nameText.text = document.data["ownerName"].toString()
                    expDateText.text = document.data["expDate"].toString()
                    idCard.text = document.id
                    val brand = document.data["brand"]
                    if (brand == "other") {
                        brandLogo.setImageResource(com.google.android.material.R.drawable.navigation_empty_icon)
                    } else {
                        val resourceId = resources.getIdentifier(
                            "${brand.toString()}_logo",
                            "drawable",
                            context?.packageName
                        )
                        brandLogo.setImageResource(resourceId)
                    }
                }
                if (layoutCards.childCount >= 1) {
                    val child = layoutCards.getChildAt(0)
                    val nameText = child.findViewById<TextView>(R.id.nameText)
                    val cardNumberText = child.findViewById<TextView>(R.id.cardNumberText)
                    editName.setText(nameText.text)
                    editCard.setText(cardNumberText.text)
                    editId.setText(child.findViewById<TextView>(R.id.idCard).text)
                } else {
                    mainText.setText("No hay tarjetas")
                    editLayout.visibility = View.GONE
                }
            }

        horizontalSV.setOnScrollChangeListener() { v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            for (i in 0 until layoutCards.childCount) {
                val child = layoutCards.getChildAt(i)
                if (isViewVisible(child, horizontalSV)) {
                    val nameText = child.findViewById<TextView>(R.id.nameText)
                    val cardNumberText = child.findViewById<TextView>(R.id.cardNumberText)
                    editName.setText(nameText.text)
                    editCard.setText(cardNumberText.text)
                    editId.setText(child.findViewById<TextView>(R.id.idCard).text)
                    break
                }
            }
        }

        addBtn.setOnClickListener {
            val intent = Intent(requireActivity(), NewCard::class.java)
            startActivity(intent)
        }

        deleteBtn.setOnClickListener {
            cardsDB.document(editId.text.toString()).delete().addOnSuccessListener {
                Toast.makeText(context, "Tarjeta eliminada", Toast.LENGTH_SHORT).show()
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.fragment_container, WalletFragment())
                transaction?.commit()
            }
        }


        var isEditing = false
        editBtn.setOnClickListener {
            if (isEditing) {
                cardsDB.document(editId.text.toString()).update(
                    "ownerName", editName.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Cambios guardados", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al escribir los cambios", Toast.LENGTH_SHORT).show()
                    }
                }
                editName.isEnabled = false
                editBtn.setBackgroundColor(resources.getColor(R.color.primary))
                editBtn.text = "Modificar"
            } else {
                editName.isEnabled = true
                editBtn.setBackgroundColor(resources.getColor(R.color.black))
                editBtn.text = "Guardar"
            }
            isEditing = !isEditing
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
         * @return A new instance of fragment WalletFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WalletFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

private fun isViewVisible(view: View, scrollView: HorizontalScrollView): Boolean {
    val scrollViewWidth = scrollView.width
    val scrollViewStart = scrollView.scrollX
    val scrollViewEnd = scrollViewStart + scrollViewWidth

    val viewStart = view.left
    val viewEnd = view.right

    return viewStart >= scrollViewStart && viewEnd <= scrollViewEnd
}