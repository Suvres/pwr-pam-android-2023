package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentChildrenBinding
import com.example.myapplication.users.User
import com.example.myapplication.users.UserRepository
import com.example.myapplication.utils.adapter.ChildrenAdapter
import kotlinx.android.synthetic.main.fragment_children.countChildren

class ChildrenFragment : Fragment() {

    private val userRepository = UserRepository()
    private lateinit var binding: FragmentChildrenBinding
    private lateinit var childrenAdapter: ChildrenAdapter  //zadania
    private lateinit var childrenList: MutableList<User>  //lista zadań

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChildrenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        getDataFromFirebase()

        childrenAdapter.onItemClick = {
            deleteChildren(it)
        }
    }

    private fun getDataFromFirebase() {
        userRepository.addSnapshotEventForChildren {
            childrenList.clear()
            childrenList.addAll(it)

            childrenAdapter.notifyDataSetChanged()
            countChildren.text =
                String.format("Ilość Twoich Dzieci: %s ", childrenAdapter.itemCount.toString())
        }
    }

    private fun init() {
        childrenList = mutableListOf()
        childrenList.clear()

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)
        childrenAdapter = ChildrenAdapter(childrenList)
        binding.mainRecyclerView.adapter = childrenAdapter
    }

    private fun deleteChildren(user: User) {
        AlertDialog.Builder(requireContext())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Usuwanie")
            .setMessage("Czy chcesz usunąć dziecko z listy użytkowników?")
            .setCancelable(true) //dialog box in cancellable
            .setPositiveButton("Tak") { DialogInterface, it ->
                userRepository.deleteChild(user);
                Toast.makeText(
                    requireContext(),
                    "Usunięto użytkownika-dziecko",
                    Toast.LENGTH_SHORT
                ).show()

            }
            .setNegativeButton("Nie") { dialogInterface, it ->
                dialogInterface.cancel()
                Toast.makeText(
                    requireContext(),
                    "Anulowano usuwanie użytkownika-dziecko",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .show()
    }
}

