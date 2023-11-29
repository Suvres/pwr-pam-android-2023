package com.example.myapplication.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.myapplication.databinding.ListTaskStatusBinding
import com.example.myapplication.users.TasksRepository
import com.example.myapplication.users.UserRepository
import com.example.myapplication.users.UserTask
import java.text.SimpleDateFormat
import java.util.*


class TaskStatusAdapter (private val list: MutableList<UserTask>)
    :RecyclerView.Adapter<TaskStatusAdapter.TaskStatusViewHolder>() {

    private val userRepository = UserRepository()
    private val TAG = "TaskStatusAdapter"
    private val formatted = SimpleDateFormat("HH:mm,  dd.MM.yyyy ", Locale.ROOT)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskStatusViewHolder {
        val binding =
            ListTaskStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskStatusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskStatusAdapter.TaskStatusViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {

                userRepository.getUserLambda(this.user) {
                    fullName.text = String.format("%s %s", it.firstName, it.lastName)
                }

                taskIcon.setImageResource(this.type.icon)
                startTask.text = formatted.format(this.startDate.toDate())
                if (this.closeDate != null) {
                    endTask.text = formatted.format(this.closeDate!!.toDate())
                }

                statusTask.text=this.status.toString()
                resultTask.text=this.result


                Log.d(TAG, "onBindViewHolder: "+this)

            }
        }
    }

    interface TaskStatusAdapterInterface{
        fun onDeleteItemClicked(userTask: UserTask, position : Int)
    }

    override fun getItemCount(): Int {  //liczba zadań w liście
        return list.size
    }


    class TaskStatusViewHolder(val binding: ListTaskStatusBinding)
        : RecyclerView.ViewHolder(binding.root){

        val fullName = binding.fullName
        val taskIcon = binding.taskStatusIcon
        val startTask = binding.startTask
        val endTask = binding.endTask
        val statusTask = binding.statusTask
        val resultTask = binding.resultTask
        }
}