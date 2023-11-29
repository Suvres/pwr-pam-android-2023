package com.example.myapplication.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentFingerTappingBinding
import com.example.myapplication.users.TaskStatus
import com.example.myapplication.users.TasksRepository
import com.example.myapplication.users.UserRepository
import com.example.myapplication.users.UserTask
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.fragment_finger_tapping.clickButton
import kotlinx.android.synthetic.main.fragment_finger_tapping.numberOfClick
import kotlinx.android.synthetic.main.fragment_finger_tapping.root
import kotlinx.android.synthetic.main.fragment_finger_tapping.timerCounter
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class FingerTapping : Fragment() {

    private val tasksRepository = TasksRepository()
    private var startCount = false

    private lateinit var task: UserTask
    var count = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        startCount = false
        task = arguments?.getParcelable<UserTask>("userTask")!!
        return inflater.inflate(R.layout.fragment_finger_tapping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showInformation()
    }

    private fun fingerTappingCounter() {
        // time count down for 15 seconds,
        // with 1 second as countDown interval
        object : CountDownTimer(15000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerCounter.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                clickButton.visibility = View.GONE
                saveScore()

                //val contextView = binding.root
                Snackbar.make(root, "Wyniki zapisane", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Kliknij aby powrócić!") {
                        findNavController().navigate(R.id.action_fingerTapping_to_menuchildFragment)
                    }
                    .setBackgroundTint(resources.getColor(R.color.md_deep_purple_700))
                    .setActionTextColor(resources.getColor(R.color.white))
                    .show()
            }
        }.start()
    }

    private fun showInformation() {
        AlertDialog.Builder(requireContext())
            .setIcon(R.drawable.ic_fingertaping)
            .setTitle("Test Pamięci")
            .setMessage(
                " Przez 15 s klikaj jak najszybciej potrafisz w fioletowy przycisk," +
                        " po zakończonym teście wyniki zostaną zapisane " +
                        " Kliknij WYKONAJ jeśli jesteś gotowy do testu." +
                        " Czas zacznie odliczać się po kliknięciu w przycisk"
            )
            .setCancelable(true) //dialog box in cancellable
            .setPositiveButton("WYKONAJ") { DialogInterface, it ->
                clickButton.setOnClickListener {
                    count++
                    numberOfClick.text = count.toString()
                    if (!startCount) {
                        fingerTappingCounter()
                        startCount = true
                    }
                }
            }
            .setNegativeButton("Nie") { dialogInterface, it ->
                dialogInterface.cancel()
                findNavController().navigate(R.id.action_fingerTapping_to_menuchildFragment)
            }
            .show()
    }

    private fun saveScore() {
        val v = numberOfClick.text.toString().toInt() / 15
        val clicks = v.toString()
        task.result = "Średnia prędkość klikania: $clicks"
        task.status = TaskStatus.DONE
        task.closeDate = Timestamp(
            Date.from(
                LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            )
        )
        tasksRepository.save(task)
    }

}