package com.example.myapplication.utils.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ListChildrenBinding
import com.example.myapplication.databinding.ListChildrenBinding.*
import com.example.myapplication.users.TasksRepository
import com.example.myapplication.users.User
import com.example.myapplication.users.UserRepository
import kotlinx.coroutines.runBlocking

class ChildrenAdapter(private val list: MutableList<User>):
    RecyclerView.Adapter<ChildrenAdapter.ChildrenViewHolder>() {

    private val taskRepository = TasksRepository()
    private val userRepository = UserRepository()
    private val TAG = "ChildrenAdapter"

    var onItemClick : ((User) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildrenViewHolder {
        val binding =
            inflate(LayoutInflater.from(parent.context), parent, false)
        return ChildrenViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ChildrenViewHolder, position: Int) {
          with(holder) {
            with(list[position]) {
                //przycisk
                deleteBtn.setOnClickListener {
                    onItemClick?.invoke(this)
                }

                val user = this
                runBlocking {
                    val doneCount = taskRepository.getCountOfDoneTasks(user)
                    val todoCount = taskRepository.getCountOfTODOTasks(user)

                    //ilośc zadań
                    status.text = (todoCount + doneCount).toString()
                    //wykonanych
                    statusDone.text = doneCount.toString()
                    //niewykonanych
                    statusNoDone.text = todoCount.toString()
                    //nazwa dziecka

                }


                userRepository.getUserLambda(this.uid) {
                   childrenTitle.text = String.format("%s %s", it.firstName, it.lastName)
                }
                Log.d(TAG, "onBindViewHolder: "+this)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


class ChildrenViewHolder(val binding: ListChildrenBinding):
        RecyclerView.ViewHolder(binding.root){

        val childrenTitle = binding.childrenTitle
        val status = binding.status
        val statusDone = binding.statusDone
        val statusNoDone = binding.statusNoDone
        val deleteBtn = binding.deleteUser
        }
    }



