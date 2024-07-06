package com.example.rakshapath

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rakshapath.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setUpActivity()
    }

    private fun setUpActivity() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLoginInstead.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnRegisterUser.setOnClickListener {
            if (validateFields()) {
                val name = binding.etName.text.toString().trim()
                val email = binding.etRegisterEmail.text.toString().trim()
                val password = binding.etRegisterPassword.text.toString().trim()

                checkEmailExistence(name, email, password)
            }
        }
    }

    private fun validateFields(): Boolean {
        val name = binding.etName.text.toString().trim()
        val email = binding.etRegisterEmail.text.toString().trim()
        val password = binding.etRegisterPassword.text.toString().trim()

        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return false
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegisterEmail.error = "Valid email is required"
            return false
        }

        if (password.isEmpty() || password.length < 6 || !password.matches(".*[A-Z].*".toRegex()) || !password.matches(".*[a-z].*".toRegex()) || !password.matches(".*\\d.*".toRegex()) || !password.matches(".*[@#\$%^&+=].*".toRegex())) {
            binding.etRegisterPassword.error = "Password must be at least 6 characters, contain a digit, a lower case, an upper case and a special character"
            return false
        }

        return true
    }

    private fun checkEmailExistence(name: String, email: String, password: String) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList<String>()
                if (signInMethods.isEmpty()) {
                    registerUser(name, email, password)
                } else {
                    Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                }
            } else {

                Log.e("TAG", "checkEmailExistence:${task.exception!!.message} ")
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    sendEmailVerification(user)
                    val userData = User(user?.uid ?: "", name, email, password)
                    saveUserData(user?.uid, userData)
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendEmailVerification(user: FirebaseUser?) {
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent to ${user.email}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(userId: String?, userData: User) {
        userId?.let {
            executor.execute {
                val firebaseRestManager = FirebaseRestManager<User>()
                val firebaseDatabaseRef = Firebase.database.getReference("rakshapathdb/user")
                firebaseRestManager.addItemWithCustomId(userData, userId, firebaseDatabaseRef) { success, error ->
                    handler.post {
                        if (success) {
                            showSuccessDialog()
                        } else {
                            auth.currentUser?.delete()?.addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    showWarningDialog(error?.message.toString())
                                } else {
                                    showWarningDialog("Failed to remove user authentication data.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showSuccessDialog() {
        val successLoadingHelper = SuccessLoadingHelper()
        successLoadingHelper.showSuccessDialog(this)
        successLoadingHelper.hideButtons()
        successLoadingHelper.updateText("User registered")
        handler.postDelayed({
            successLoadingHelper.dismissSuccessDialog()
        }, 2000)

        val intent =Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }



    private fun showWarningDialog(message: String) {
        val warningLoadingHelper = WarningLoadingHelper()
        warningLoadingHelper.showWarningDialog(this)
        warningLoadingHelper.hideButtons()
        warningLoadingHelper.updateText(message)
        handler.postDelayed({
            warningLoadingHelper.dismissWarningDialog()
        }, 2000)
    }
}
