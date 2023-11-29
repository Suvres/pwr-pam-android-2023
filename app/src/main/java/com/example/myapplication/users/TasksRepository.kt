package com.example.myapplication.users

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class TasksRepository: Repository() {
    private final val COLLECTION = "tasks"
    private val userRepository = UserRepository()

    companion object FirebaseManagerAuth {
        val auth = FirebaseAuth.getInstance()
        fun getCurrentUserID(): String? = auth.currentUser?.uid
        private const val TASK_REPOSITORY = "TASK_REPOSTITORY_DEBBUG"
    }

    fun save(task: UserTask) {
        cloud.collection(COLLECTION)
            .document(task.uid)
            .set(task)

        Log.d(FIREBASE_DEBUG, task.uid)
    }

    fun getTask(uid: String): Task<DocumentSnapshot> {
        return cloud.collection(COLLECTION).document(uid).get()
    }

    fun getTasksByUser(user: User): Task<QuerySnapshot> {
        return cloud.collection(COLLECTION).whereEqualTo("user", user.uid)
            .whereNotEqualTo("status", TaskStatus.DONE.name).get()
    }

    fun getActiveTasksByUser(user: User, unit: (List<UserTask>) -> Unit) {
        cloud.collection(COLLECTION)
            .whereEqualTo("user", user.uid)
            .whereEqualTo("status", TaskStatus.TODO.name)
            .addSnapshotListener { snap, e ->
                Log.d("ZADANIA", "ASDASD")
                if(snap !== null && !snap.isEmpty) {
                    unit.invoke(snap.toObjects(UserTask::class.java))
                }
            }
    }



    suspend fun getCountOfDoneTasks(user:User): Int{
        val count = cloud.collection(COLLECTION).whereEqualTo("user", user.uid)
            .whereEqualTo("status", TaskStatus.DONE.name).get()
            .await()

        return count.count();
    }

    suspend fun getCountOfTODOTasks(user:User): Int {
        val result = cloud.collection(COLLECTION).whereEqualTo("user", user.uid)
            .whereEqualTo("status", TaskStatus.TODO.name).get().await()

        return result.count()
    }

    fun addSnapshotListenerForChildren(unit: (list: List<UserTask>) -> Unit) {

        cloud.collection(COLLECTION)
            .whereEqualTo("status",TaskStatus.TODO.name)
            .addSnapshotListener{snapshot, e ->
                if(snapshot !== null && !snapshot.isEmpty) {
                    userRepository.getCurrentUserChildren { users ->
                        val tasksList = snapshot!!.toObjects(UserTask::class.java)

                        val tt = tasksList.sortedByDescending { u -> u.startDate }.filter { fil ->
                            users.map { us -> us.uid }.contains(fil.user)
                        }

                        unit.invoke(tt)
                    }
                }

        }
    }

    fun addSnapshotListenerForChildrenAll(unit: (list: List<UserTask>) -> Unit) {
        cloud.collection(COLLECTION).orderBy("closeDate", Query.Direction.DESCENDING).addSnapshotListener{snapshot, e ->
            userRepository.getCurrentUserChildren { users ->
                val tasksList = snapshot!!.toObjects(UserTask::class.java)

                val tt = tasksList.filter { fil ->
                    users.map { us -> us.uid }.contains(fil.user)
                }

                unit.invoke(tt)
            }
        }
    }


    fun deleteTask(task: UserTask){
        db.collection(COLLECTION).document(task.uid)
            .delete()
            .addOnSuccessListener {
                Log.w(
                    TASK_REPOSITORY,
                    "Usunięto zadanie"
                )
            }
            .addOnFailureListener { e ->
                Log.w(
                    TASK_REPOSITORY,
                    "Błąd przy usuwaniu zadania",e
                )
            }
    }




}





