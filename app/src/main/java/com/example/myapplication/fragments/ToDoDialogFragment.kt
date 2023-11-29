package com.example.myapplication.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentToDoDialogBinding
import com.example.myapplication.users.TaskType
import com.example.myapplication.users.TasksRepository
import com.example.myapplication.users.User
import com.example.myapplication.users.UserRepository
import com.example.myapplication.users.UserTask
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.fragment_to_do_dialog.todoStart
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ToDoDialogFragment() : DialogFragment(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
    private lateinit var binding: FragmentToDoDialogBinding
    private val tasksRepository = TasksRepository()
    private val userRepository = UserRepository()

    private val calendar = Calendar.getInstance()
    private val formatted = SimpleDateFormat("HH:mm,  dd.MM.yyyy ", Locale.ROOT)

    companion object { //tworzenie nowego obiektu
        const val TAG = "DialogFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String) =
            ToDoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)
                    putString("task", task)
                }
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? { // Inflate the layout for this fragment

        binding = FragmentToDoDialogBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initValues()

        binding.todoClose.setOnClickListener { //zamykanie
            dismiss()
        }

        binding.todoNextBtn.setOnClickListener { //następne zadanie


            val task = UserTask(
                type = TaskType.findByTitle(binding.typeSpinner.selectedItem.toString()),
                user = (binding.userSpinner.selectedItem as User).uid,
                startDate = Timestamp(calendar.timeInMillis / 1000, 0),
                description = binding.todoDescritption.text.toString()
            )
            tasksRepository.save(task)

            dismiss()
        }
    }

    private fun initValues() {


        todoStart.text = formatted.format(Timestamp.now().seconds * 1000)

        todoStart.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }



        userRepository.getCurrentUserMustExist {
            view?.findViewById<Spinner>(R.id.type_spinner)?.adapter = ArrayAdapter(requireContext(),
                android.R.layout.simple_spinner_item, TaskType.values().map { i -> i.title })

            userRepository.getChildren(it.uid) { users ->
                view?.findViewById<Spinner>(R.id.user_spinner)
                    ?.adapter =
                    ArrayAdapter<User>(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item, users
                    )
            }
        }

    }

    override fun onResume() {
        val dialogWindow: Window? = dialog!!.window
        val lp: WindowManager.LayoutParams = dialogWindow!!.attributes
        lp.x = 50 // set your X position here
        lp.y = 500 // set your Y position here

        //dialogWindow.attributes = lp
        val params = dialogWindow.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialogWindow.setGravity(Gravity.TOP)

        super.onResume()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        displayFormattedDate(calendar.timeInMillis)
        TimePickerDialog(
            requireContext(),
            this,
            calendar.get(Calendar.HOUR),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun displayFormattedDate(timestamp: Long) {
        todoStart.text = formatted.format(timestamp)
        Log.i("Formatting", timestamp.toString())
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        displayFormattedDate(calendar.timeInMillis)
    }

}
