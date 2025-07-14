import com.google.firebase.auth.FirebaseAuth
import android.util.Log

fun authenticateUser() {
    val auth = FirebaseAuth.getInstance()
    if (auth.currentUser == null) {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    Log.d("AUTH", "Signed in anonymously as: $uid")
                } else {
                    Log.e("AUTH", "Sign-in failed: ${task.exception}")
                }
            }
    } else {
        Log.d("AUTH", "Already signed in: ${auth.currentUser?.uid}")
    }
}
