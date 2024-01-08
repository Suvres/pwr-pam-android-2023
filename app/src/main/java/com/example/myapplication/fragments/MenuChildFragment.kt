package com.example.myapplication.fragments

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Person
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.users.TasksRepository
import com.example.myapplication.users.UserRepository
import com.example.myapplication.users.UserTask
import com.example.myapplication.utils.adapter.MenuChildrenListAdapter
import kotlinx.android.synthetic.main.fragment_menu_child.call
import kotlinx.android.synthetic.main.fragment_menu_child.idChildren

class MenuChildFragment : Fragment() {
    private var taskRepository = TasksRepository()
    private var userRepository = UserRepository()
    private var NOTIFICATION_CHANNEL_ID = "CHANNEL_BUBBLE_ID"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_child, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRepository.getCurrentUserMustExist { us ->
            idChildren.text = String.format("Witaj: %s %s", us.firstName, us.lastName)

            userRepository.getUserLambda(us.caregiver) {
                val number = it.phone


                call.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)))
                    startActivity(intent)
                }
            }
        }

        userRepository.getCurrentUserMustExist { us ->
            taskRepository.getActiveTasksByUser(us) {
                createTaskList(view, it)
            }
        }
    }

    private fun createTaskList(view: View, tasks: List<UserTask>) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.mainRecyclerView)

        val adapter = MenuChildrenListAdapter(tasks)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter.onItemClick = {
            if (it.type.navigationId !== null) {
                findNavController().navigate(it.type.navigationId!!, bundleOf("userTask" to it))
            }
        }
        recyclerView.adapter = adapter
    }


}