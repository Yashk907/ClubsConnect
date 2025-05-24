import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

internal fun getUserInfoFromFireStore(
    onResult: (Triple<String?, String?, String?>,String?) -> Unit,
    onError: (Exception) -> Unit = {}
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid

    if (uid == null) {
        Log.e("getuserValues", "UID is null")
        onResult(Triple(null, null, null),null)
        return
    }

    FirebaseFirestore.getInstance()
        .collection("users")
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
            val type = document.getString("role") ?: "MISSING"
            val name = document.getString("username") ?: "MISSING"
            val email = document.getString("email")
            Log.d("getuserValues", "Fetched name: $name, type: $type")
            onResult(Triple(uid, name, type),email)
        }
        .addOnFailureListener { exception ->
            Log.e("getuserValues", "Error getting user type: ${exception.message}")
            onError(exception)
        }
}
