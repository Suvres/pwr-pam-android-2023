package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentMemoryQuizBinding
import com.example.myapplication.users.TaskStatus
import com.example.myapplication.users.TasksRepository
import com.example.myapplication.users.UserRepository
import com.example.myapplication.users.UserTask
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.fragment_memory_quiz.eightN
import kotlinx.android.synthetic.main.fragment_memory_quiz.fiveN
import kotlinx.android.synthetic.main.fragment_memory_quiz.fourN
import kotlinx.android.synthetic.main.fragment_memory_quiz.nineN
import kotlinx.android.synthetic.main.fragment_memory_quiz.number
import kotlinx.android.synthetic.main.fragment_memory_quiz.oneN
import kotlinx.android.synthetic.main.fragment_memory_quiz.sevenN
import kotlinx.android.synthetic.main.fragment_memory_quiz.sixN
import kotlinx.android.synthetic.main.fragment_memory_quiz.threeN
import kotlinx.android.synthetic.main.fragment_memory_quiz.twoN
import kotlinx.android.synthetic.main.fragment_memory_quiz.zeroN
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MemoryQuizFragment : Fragment() {

    private val tasksRepository = TasksRepository()
    private val randomNumbers = MutableList(3) { Random.nextInt(0, 9) }
    private var ourList = mutableListOf<Int>()
    private lateinit var fadeOut: Animation
    private lateinit var fadeIn: Animation
    private lateinit var task: UserTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ourList = mutableListOf()
        fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        task = arguments?.getParcelable<UserTask>("userTask")!!
        return inflater.inflate(R.layout.fragment_memory_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonsList =  listOf<Button>(zeroN, oneN, twoN, threeN, fourN, fiveN, sixN, sevenN, eightN, nineN);
        buttonsList.forEach { it.isClickable = true }
        showInformation()

        buttonsList.forEach {
            it.setOnClickListener { view ->
                it.startAnimation(fadeOut)
                it.startAnimation(fadeIn)
                ourList.add(it.text.toString().toInt())

                if (ourList.size == randomNumbers.size) {
                    buttonsList.forEach { b -> b.isClickable = false }
                    var result = "Nieudało się"
                    if(ourList.equals(randomNumbers.reversed())) {
                        result = "Udało się"
                    }
                    task.result = result
                    task.status = TaskStatus.DONE
                    task.closeDate = Timestamp(
                        Date.from(
                            LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
                        )
                    )
                    tasksRepository.save(task)


                    Snackbar.make(requireView(), result, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Kliknij aby powrócić!"){
                            findNavController().navigate(R.id.action_memoryQuiz_to_menuchildFragment)
                        }
                        .setBackgroundTint(resources.getColor(R.color.md_deep_purple_700))
                        .setActionTextColor(resources.getColor(R.color.white))
                        .show()

                    return@setOnClickListener
                }
            }
        }
    }

    private fun showInformation() {
       AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.ic_quiz)
           .setTitle("Test Pamięci")
            .setMessage("Pojawią się 3 liczby. Zapamiętaj je i następnie zaznacz je na klawiaturze w odwrotnej kolejności." +
                    " Kliknij WYKONAJ jeśli jesteś gotowy do testu")
           .setCancelable(true) //dialog box in cancellable
           .setPositiveButton("WYKONAJ") {DialogInterface, it ->
                setNumber()
            }
            .setNegativeButton("Nie"){dialogInterface, it ->
                dialogInterface.cancel()
                findNavController().navigate(R.id.action_memoryQuiz_to_menuchildFragment)}
            .show()
    }

    fun setNumber() {
        val tmpList = randomNumbers.toMutableList()
        val executorService = Executors.newSingleThreadScheduledExecutor()
        executorService.scheduleAtFixedRate({
            val l = tmpList.removeFirstOrNull()
            if(l == null) {
                number.text = "Liczby"
                executorService.shutdownNow();
            } else {
                number.text = l.toString()
                number.startAnimation(fadeIn)
            }
        },1, 1, TimeUnit.SECONDS);
        Log.d("Liczby", "2222")
    }
}