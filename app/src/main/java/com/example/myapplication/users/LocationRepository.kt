package com.example.myapplication.users

class LocationRepository : Repository() {

    private val COLLECTION = "localization"

    fun save(location: LocationUser) {
        cloud.collection(COLLECTION)
            .document(location.user).set(location)
    }

    fun getLocation(user: User, unit: (LocationUser) -> Unit, failed: ( (String?) -> Unit)) {
        cloud.collection(COLLECTION).document(user.uid).get()
            .addOnSuccessListener {
                if(it.exists()) {
                    unit.invoke(it.toObject(LocationUser::class.java)!!)
                } else {
                    failed.invoke("Nie posiada aktualnej lokalizacji")
                }
            }.addOnFailureListener {
                failed.invoke(it.message)
            }
    }
}