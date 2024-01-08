package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.users.UserRepository
import kotlinx.android.synthetic.main.fragment_menu_parent.addChild
import kotlinx.android.synthetic.main.fragment_menu_parent.findChild
import kotlinx.android.synthetic.main.fragment_menu_parent.idCaregiver
import kotlinx.android.synthetic.main.fragment_menu_parent.statusTasks
import java.util.Optional

class MenuParentFragment : Fragment() {

    private val userRepository = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findChild.setOnClickListener {
            findNavController().navigate(R.id.action_menuCaregiverFragment_to_locationChildrenFragment)
        }

        addChild.setOnClickListener {
            findNavController().navigate(R.id.action_menuCaregiverFragment_to_newUserFragment)
        }

        statusTasks.setOnClickListener {
            findNavController().navigate(R.id.action_menuCaregiverFragment_to_childrenTasksStatusFragment)
        }

        userRepository.getCurrentUserMustExist {
            Optional.ofNullable(idCaregiver).ifPresent(){caregiver ->
                caregiver.text = String.format("Witaj: %s %s", it.firstName, it.lastName)
            }
        }

    }
}