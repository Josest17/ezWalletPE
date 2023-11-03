package com.ezcorp.ezwallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DecimalFormat

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

        Picasso.get().load("https://api.dicebear.com/7.x/bottts-neutral/png?seed=${firebaseAuth.currentUser!!.uid}").into(avatar)

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
            transaction?.addToBackStack(null);
            val bottomNavigationView =
                activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView?.selectedItemId = R.id.credit_card
            transaction?.commit()
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