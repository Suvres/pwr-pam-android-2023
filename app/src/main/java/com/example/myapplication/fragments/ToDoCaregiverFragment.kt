package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentTodoCaregiverBinding
import com.example.myapplication.users.TasksRepository
import com.example.myapplication.users.UserTask
import com.example.myapplication.utils.adapter.TaskAdapter

class ToDoCaregiverFragment : Fragment(),
    TaskAdapter.TaskAdapterInterface {

    private val TAG = "ToDoCaregiverFragment"
    private lateinit var binding: FragmentTodoCaregiverBinding //tworzenie widoku
    private var frag: ToDoDialogFragment? = null
    private val tasksRepository = TasksRepository()


    private lateinit var taskAdapter: TaskAdapter  //zadania
    private lateinit var toDoItemList: MutableList<UserTask>  //lista zadań

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle? //metoda, która inicjuje i tworzy wszystkie obiekty
    ): View? {

        binding = FragmentTodoCaregiverBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {  //właściwe wyświetlanie
        super.onViewCreated(view, savedInstanceState)
        init()
        getTaskFromFirebase()
        binding.addTaskBtn.setOnClickListener {
            if (frag != null)
                childFragmentManager.beginTransaction().remove(frag!!)
                    .commit() //mamy wycinek fragmentu - stąd child
            frag = ToDoDialogFragment() //okienko zadania
            frag!!.show(childFragmentManager, ToDoDialogFragment.TAG) //wyświetlenie

        }

        taskAdapter.onItemClick = {
            deleteTask(it)
        }

    }


    private fun init() { //inicjowanie z bazy zadań
        toDoItemList = mutableListOf()

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        taskAdapter = TaskAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.mainRecyclerView.adapter = taskAdapter
    }

    private fun getTaskFromFirebase() {  //pobieranie zadań z Firebase
        tasksRepository.addSnapshotListenerForChildren {
            toDoItemList.clear()
            toDoItemList.addAll(it)

            Log.d(TAG, "onDataChange: " + toDoItemList)
            taskAdapter.notifyDataSetChanged()
        }
    }

    private fun deleteTask(task: UserTask) {
        AlertDialog.Builder(requireContext())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Usunięcie zadania")
            .setMessage("Czy chcesz usunąć zadanie z listy?")
            .setCancelable(true) //dialog box in cancellable
            .setPositiveButton("Tak") { DialogInterface, it ->
                tasksRepository.deleteTask(task)
                Toast.makeText(requireContext(), "Usunięto zadanie", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("Nie") { dialogInterface, it ->
                dialogInterface.cancel()
                Toast.makeText(requireContext(), "Anulowano usuwanie zadania", Toast.LENGTH_SHORT)
                    .show()
            }
            .show()
    }

    override fun onDeleteItemClicked(toDoData: UserTask, position: Int) { //usuwanie
    }
}



