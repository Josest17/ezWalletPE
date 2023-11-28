package com.ezcorp.ezwallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainMenu.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMenu : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)
        val avatar = view.findViewById<CircleImageView>(R.id.avatar)
        val balance = view.findViewById<TextView>(R.id.balance)
        val btnWallet = view.findViewById<Button>(R.id.my_wallet_btn)
        val transfersLayout = view.findViewById<LinearLayout>(R.id.lastMovements)
        val noTransfersView = view.findViewById<TextView>(R.id.lastMovementsTitle)

        Picasso.get()
            .load("https://api.dicebear.com/7.x/bottts-neutral/png?seed=${firebaseAuth.currentUser!!.uid}")
            .into(avatar)

        db.collection("users").document(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
            val balanceValue = it.data?.get("balance")
            val formattedBalance = DecimalFormat("#,##0.00").format(balanceValue)
            balance.text = "$ $formattedBalance"
        }

        avatar?.setOnClickListener {
            val fragmentProfile = ProfileFragment()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            val bottomNavigationView =
                activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)

            bottomNavigationView?.selectedItemId = R.id.profile
            transaction?.replace(R.id.fragment_container, fragmentProfile)
            transaction?.commit()
        }

        btnWallet?.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragment_container, WalletFragment())
            transaction?.addToBackStack(null)
            val bottomNavigationView =
                activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView?.selectedItemId = R.id.credit_card
            transaction?.commit()
        }

        val transfersDB = db.collection("transfers")
        transfersDB.where(
            Filter.or(
                Filter.equalTo("sender", firebaseAuth.currentUser!!.uid),
                Filter.equalTo("receiver", firebaseAuth.currentUser!!.uid)
            )
        ).get().addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                noTransfersView.text = "No existen transferencias"
                return@addOnSuccessListener
            }
            val sortedDocuments = documents.sortedByDescending { it.data["date"] as Timestamp }
            val lastSeven = sortedDocuments.subList(0, 6)

            for (document in lastSeven) {
                transfersLayout.addView(
                    LayoutInflater.from(context).inflate(
                        R.layout.minemovements, transfersLayout, false
                    )
                )
                val nameText =
                    transfersLayout.getChildAt(transfersLayout.childCount - 1)
                        .findViewById<TextView>(R.id.nameText)
                val descriptionText =
                    transfersLayout.getChildAt(transfersLayout.childCount - 1)
                        .findViewById<TextView>(R.id.descriptionText)
                val amountText =
                    transfersLayout.getChildAt(transfersLayout.childCount - 1)
                        .findViewById<TextView>(R.id.amountText)
                val dateText =
                    transfersLayout.getChildAt(transfersLayout.childCount - 1)
                        .findViewById<TextView>(R.id.dateText)
                val avatarImg =
                    transfersLayout.getChildAt(transfersLayout.childCount - 1)
                        .findViewById<ImageView>(R.id.avatar)

                val receiverID = document.data["receiver"].toString()
                val senderID = document.data["sender"].toString()

                val receiverDB = db.collection("users").document(receiverID)
                val senderDB = db.collection("users").document(senderID)
                receiverDB.get().addOnSuccessListener { documents ->
                    val receiverName = documents.data!!["name"].toString()
                    nameText.text = "Transfer: $receiverName"
                }
                senderDB.get().addOnSuccessListener { documents ->
                    val senderName = documents.data!!["name"].toString()
                    descriptionText.text = "de: $senderName"
                }
                val formattedAmount = DecimalFormat("#,##0.00").format(
                    document.data["amount"].toString().toDouble()
                )
                amountText.text = "$ $formattedAmount"
                val timestamp = document.data["date"] as Timestamp
                val seconds = timestamp.seconds
                val date = Date(seconds * 1000L)
                val formattedDate = SimpleDateFormat("dd.MM.yyyy HH:mm").format(date)
                dateText.text = formattedDate
                if (senderID == firebaseAuth.currentUser!!.uid) {
                    Picasso.get()
                        .load("https://api.dicebear.com/7.x/bottts-neutral/png?seed=${receiverID}")
                        .into(avatarImg)
                } else {
                    Picasso.get()
                        .load("https://api.dicebear.com/7.x/bottts-neutral/png?seed=${senderID}")
                        .into(avatarImg)
                }

            }
        }.addOnFailureListener {
            noTransfersView.text = "No existen transferencias"
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
         * @return A new instance of fragment MainMenu.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainMenu().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}