package com.ezcorp.ezwallet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val firebaseAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val avatar = view.findViewById<CircleImageView>(R.id.avatar)
        val name = view.findViewById<TextView>(R.id.name)
        val nameText = view.findViewById<EditText>(R.id.nameText)
        val phone = view.findViewById<EditText>(R.id.phoneText)
        val email = view.findViewById<EditText>(R.id.emailText)
        val editBtn = view.findViewById<Button>(R.id.editBtn)

        Picasso.get().load("https://api.dicebear.com/7.x/bottts-neutral/png?seed=${firebaseAuth.currentUser!!.uid}").into(avatar)

        db.collection("users").document(firebaseAuth.currentUser!!.uid).get().addOnSuccessListener {
            name.text = it.data?.get("name").toString()
            nameText.setText(it.data?.get("name").toString())
            phone.setText(it.data?.get("phone").toString())
            email.hint = it.data?.get("email").toString()
        }

        var isEditing = false

        editBtn.setOnClickListener {
            if (isEditing) {
                db.collection("users").document(firebaseAuth.currentUser!!.uid).update(
                    "name", nameText.text.toString(),
                    "phone", phone.text.toString(),
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this.context, "Cambios guardados", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this.context,
                            "Error al guardar los cambios",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("TAG", it.exception.toString())
                    }
                }
                name.text = nameText.text.toString()
                nameText.isEnabled = false
                phone.isEnabled = false
                editBtn.setBackgroundColor(resources.getColor(R.color.primary))
                editBtn.text = "Modificar"

            } else {
                nameText.isEnabled = true
                phone.isEnabled = true
                editBtn.setBackgroundColor(resources.getColor(R.color.secondary))
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
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}