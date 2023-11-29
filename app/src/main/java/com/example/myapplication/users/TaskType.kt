package com.example.myapplication.users

import com.example.myapplication.R

enum class TaskType(val navigationId: Int?, val icon: Int, val title: String)  {
    FINGER_TAP(R.id.action_menuchildFragment_to_fingerTapping, R.drawable.ic_fingertaping, "Stukanie palcami"),
    MEMORY_QUIZ(R.id.action_menuchildFragment_to_memoryQuiz, R.drawable.ic_quiz, "Testy pamięci"),
    WELLBEING_STATUS(R.id.action_menuchildFragment_to_wellBeingFragment,R.drawable.ic_happy, "Samopoczucie"),
    MEDICINE(R.id.action_menuchildFragment_to_medicineFragment, R.drawable.ic_medication, "Przypomnienie o lekach"),
    ACTIVITY(R.id.action_menuchildFragment_to_medicineFragment, R.drawable.ic_activity_run, "Aktywność fizyczna"),
    CLICK(R.id.action_menuchildFragment_to_medicineFragment, R.drawable.ic_happy, "Kliknij");

    companion object {
        fun findByTitle(title: String): TaskType {
            return values().first { it.title === title }
        }
    }

}