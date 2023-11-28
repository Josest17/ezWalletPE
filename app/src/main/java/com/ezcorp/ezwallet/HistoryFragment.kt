package com.ezcorp.ezwallet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
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

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val transfersLayout = view.findViewById<LinearLayout>(R.id.lastMovements)
        val movementsBtn = view.findViewById<Button>(R.id.movementsButton)
        val transfersBtn = view.findViewById<Button>(R.id.transfersButton)
        val viewModel = ViewModelProvider(this)[MovementsViewModel::class.java]
        viewModel.setShowTransfers(true)

        movementsBtn.setOnClickListener {
            transfersLayout.removeAllViews()
            viewModel.setShowTransfers(true)
            movementsBtn.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.secondary
                )
            )
            movementsBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            transfersBtn.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.inactive
                )
            )
            transfersBtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.inactive_font
                )
            )
        }

        transfersBtn.setOnClickListener {
            transfersLayout.removeAllViews()
            viewModel.setShowTransfers(false)
            movementsBtn.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.inactive
                )
            )
            movementsBtn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.inactive_font
                )
            )
            transfersBtn.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.secondary
                )
            )
            transfersBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        viewModel.showTransfers.observe(viewLifecycleOwner) { showTransfers ->
            if (showTransfers) {
                val transfersDB = db.collection("movements")
                transfersDB.whereEqualTo("owner", firebaseAuth.currentUser!!.uid).get()
                    .addOnSuccessListener { documents ->
                        val sortedDocuments =
                            documents.sortedByDescending { it.data["date"] as Timestamp }
                        for (document in sortedDocuments) {
                            transfersLayout.addView(
                                LayoutInflater.from(context).inflate(
                                    R.layout.movementscard, transfersLayout, false
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

                            nameText.text = document.data["name"].toString()
                            descriptionText.text = document.data["description"].toString()
                            val formattedAmount = DecimalFormat("#,##0.00").format(
                                document.data["amount"].toString().toDouble()
                            )
                            amountText.text = "$ $formattedAmount"
                            val timestamp = document.data["date"] as Timestamp
                            val seconds = timestamp.seconds
                            val date = Date(seconds * 1000L)
                            val formattedDate = SimpleDateFormat("dd.MM.yyyy HH:mm").format(date)
                            dateText.text = formattedDate
                            avatarImg.setImageResource(R.drawable.apple)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "No existen compras", Toast.LENGTH_SHORT).show()
                        Log.d("HistoryFragment", "Error getting documents: ", it)
                    }
            } else {
                val transfersDB = db.collection("transfers")
                transfersDB.where(
                    Filter.or(
                        Filter.equalTo(
                            "sender",
                            firebaseAuth.currentUser!!.uid
                        ), Filter.equalTo("receiver", firebaseAuth.currentUser!!.uid)
                    )
                ).get()
                    .addOnSuccessListener { documents ->
                        val sortedDocuments =
                            documents.sortedByDescending { it.data["date"] as Timestamp }
                        for (document in sortedDocuments) {
                            transfersLayout.addView(
                                LayoutInflater.from(context).inflate(
                                    R.layout.movementscard, transfersLayout, false
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
                        Toast.makeText(context, "No existen transferencias", Toast.LENGTH_SHORT)
                            .show()
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
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

class MovementsViewModel : ViewModel() {
    private val _showTransfers = MutableLiveData<Boolean>()
    val showTransfers: LiveData<Boolean>
        get() = _showTransfers

    fun setShowTransfers(show: Boolean) {
        _showTransfers.value = show
    }
}