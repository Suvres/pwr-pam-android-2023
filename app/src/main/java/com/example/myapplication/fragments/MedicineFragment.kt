package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.users.TaskStatus
import com.example.myapplication.users.TasksRepository
import com.example.myapplication.users.UserTask
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date


class MedicineFragment : Fragment() {
    private val tasksRepository = TasksRepository()
    private lateinit var task: UserTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        task = arguments?.getParcelable<UserTask>("userTask")!!
        return inflater.inflate(R.layout.fragment_well_being, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        task.status = TaskStatus.DONE
        task.closeDate = Timestamp(
            Date.from(
                LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            )
        )
        tasksRepository.save(task)

        findNavController().navigate(R.id.action_medicineFragment_to_menuChildrenFragment)

    }
}