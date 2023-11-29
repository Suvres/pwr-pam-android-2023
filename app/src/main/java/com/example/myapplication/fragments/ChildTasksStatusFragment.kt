package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentChildrenTasksStatusBinding
import com.example.myapplication.users.TasksRepository
import com.example.myapplication.users.UserTask
import com.example.myapplication.utils.adapter.TaskStatusAdapter


class ChildTasksStatusFragment : Fragment(),
    TaskStatusAdapter.TaskStatusAdapterInterface {

    private val tasksRepository = TasksRepository()
    private lateinit var binding: FragmentChildrenTasksStatusBinding
    private lateinit var childrenTasksStatusAdapter: TaskStatusAdapter
    private lateinit var statusList: MutableList<UserTask>

    private val TAG = "ChildrenTasksStatusFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChildrenTasksStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        getStatusTaskFromFirebase()

    }

    private fun init() {
        statusList = mutableListOf()

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        childrenTasksStatusAdapter = TaskStatusAdapter(statusList)
        binding.mainRecyclerView.adapter = childrenTasksStatusAdapter
    }

    private fun getStatusTaskFromFirebase() {
        tasksRepository.addSnapshotListenerForChildrenAll {
            statusList.clear()
            statusList.addAll(it)

            Log.d(TAG, "onDataChange: " + statusList)
            childrenTasksStatusAdapter.notifyDataSetChanged()
        }
    }

    override fun onDeleteItemClicked(userTask: UserTask, position: Int) {
    }


}