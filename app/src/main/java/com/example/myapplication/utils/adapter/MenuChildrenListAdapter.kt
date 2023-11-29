package com.example.myapplication.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ListCardViewBinding
import com.example.myapplication.users.UserRepository
import com.example.myapplication.users.UserTask
import java.text.SimpleDateFormat
import java.util.Locale

class MenuChildrenListAdapter(private val list: List<UserTask>):
    RecyclerView.Adapter<MenuChildrenListAdapter.MenuChildrenListHolder>() {

    private val userRepository = UserRepository()
    private val userTask = UserTask()
    private  val TAG = "MenuChildrenListAdapter"
    private val formatted = SimpleDateFormat("HH:mm,  dd.MM.yyyy ", Locale.ROOT)

    var onItemClick: ((UserTask) -> Unit)? = null


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuChildrenListHolder {
         val binding =
             ListCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MenuChildrenListHolder(binding = binding)
        }

        // binds the list items to a view
        override fun onBindViewHolder(holder: MenuChildrenListHolder, position: Int) {

            with(holder){
                with(list[position]){
                    cardView.setOnClickListener{
                        onItemClick?.invoke(this)
                    }

                    todoTitle.text =this.type.title
                    taskIcon.setImageResource(this.type.icon)
                    todoDate.text = formatted.format(this.startDate.seconds*1000)
                    todoChildren.text =this.description

                    userRepository.getUserLambda(this.uid) {
                        todoChildren.text = String.format("%s %s", it.firstName, it.lastName)
                    }

                    Log.d(TAG, "onBindViewHolder: "+this)

                }
            }
        }


        override fun getItemCount(): Int {
            return list.size
        }


         class MenuChildrenListHolder(binding: ListCardViewBinding) :
            RecyclerView.ViewHolder(binding.root) {

            val cardView = binding.cardView
            val todoTitle = binding.todoTitle
            val todoDate = binding.todoDate
            val taskIcon = binding.taskIcon
            val todoChildren = binding.todoChild
        }
    }