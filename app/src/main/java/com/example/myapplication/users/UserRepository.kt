package com.example.myapplication.users

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot

class UserRepository: Repository() {

    private final val COLLECTION = "users"


    companion object FirebaseManagerAuth {
        val auth = FirebaseAuth.getInstance()
        fun getCurrentUserID(): String? = auth.currentUser?.uid
        private const val USER_REPOSITORY = "USER_REPOSTITORY_DEBBUG"
    }


    fun save(user: User, unit: () -> Unit) {
        cloud.collection(COLLECTION)
            .document(user.uid)
            .set(user).addOnSuccessListener {
                Log.d(FIREBASE_DEBUG, user.uid)
                unit.invoke()
            };
    }

    fun getChildren(uid: String, unit: (List<User>) -> Unit) {
        cloud.collection(COLLECTION).whereEqualTo("caregiver", uid)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    unit.invoke(it.toObjects(User::class.java))
                }
            }
    }

    fun getCurrentUserChildren(unit: (List<User>) -> Unit) {
        getCurrentUserMustExist { user ->
            getChildren(user.uid, unit)
        }
    }

    fun addSnapshotEventForChildren(unit: (List<User>) -> Unit) {
        cloud.collection(COLLECTION).addSnapshotListener{_, _ ->
            getCurrentUserChildren {
                unit.invoke(it)
            }
        }
    }

    fun getUser(uid: String): Task<DocumentSnapshot> {

        return cloud.collection(COLLECTION)
            .document(uid).get()
    }

    fun getUserLambda(uid: String, unit: ((User) -> Unit)) {
        getUser(uid).addOnSuccessListener {
            if (it.exists()) {
                unit.invoke(it.toObject(User::class.java)!!)
            }
        }
    }

    fun getCurrentUser(): Task<DocumentSnapshot>? {
        if (getCurrentUserID().isNullOrBlank()) {
            return null;
        }

        return cloud.collection(COLLECTION).document(getCurrentUserID()!!).get()
    }

    fun getCurrentUserMustExist(unit: ((User) -> Unit)) {
        getCurrentUser()?.addOnSuccessListener {
            if (it.exists()) {
                unit.invoke(it.toObject(User::class.java)!!)
            }
        }
    }

    fun createFromFirebaseUser(firebaseUser: FirebaseUser, password: String, unit: () -> Unit) {
        val user = User(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            firstName = firebaseUser.displayName ?: "",
            phone = firebaseUser.phoneNumber,
            password = password
        )
        this.save(user, unit)
    }

    fun deleteChild(user: User) {
        db.collection(COLLECTION).document(user.uid)
            .delete()
            .addOnSuccessListener {
                Log.w(
                    USER_REPOSITORY,
                    "Usunięto użytkownika"
                )
            }
            .addOnFailureListener { e ->
                Log.w(
                    USER_REPOSITORY,
                    "Błąd przy usuwaniu użytkownika", e
                )
            }
    }
}