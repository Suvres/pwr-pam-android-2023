package com.example.myapplication.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.EachTodoItemBinding
import com.example.myapplication.users.TasksRepository
import com.example.myapplication.users.User
import com.example.myapplication.users.UserRepository
import com.example.myapplication.users.UserTask
import java.text.SimpleDateFormat
import java.util.*


class TaskAdapter(private val list: MutableList<UserTask>)
    : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val userRepository = UserRepository()
    private  val TAG = "TaskAdapter"
    private var listener:TaskAdapterInterface? = null
    private val formatted = SimpleDateFormat("HH:mm,  dd.MM.yyyy ", Locale.ROOT)


    fun setListener(listener:TaskAdapterInterface){
        this.listener = listener
    }

    var onItemClick : ((UserTask) -> Unit)? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                cardView.setOnClickListener{
                    onItemClick?.invoke(this)
                }
                todoTitle.text = this.type.title
                taskIcon.setImageResource(this.type.icon)
                todoDate.text = formatted.format(this.startDate.seconds*1000)
                todoDescription.text =this.description
                userRepository.getUserLambda(this.user) {
                        todoChild.text = String.format("%s %s", it.firstName, it.lastName)
                }
                Log.d(TAG, "onBindViewHolder: "+this)


            }
        }
    }


    interface TaskAdapterInterface{
        fun onDeleteItemClicked(userTask: UserTask , position : Int)
    }

    override fun getItemCount(): Int {  //liczba zadań w liście
        return list.size
    }


    class TaskViewHolder(val binding: EachTodoItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        val cardView = binding.cardTask
        val todoTitle = binding.todoTitle
        val taskIcon = binding.taskIcon
        val todoDate = binding.todoDate
        val todoDescription = binding.todoDescription
        val todoChild = binding.todoChild
    }
}