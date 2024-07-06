package com.example.rakshapath

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import com.example.rakshapath.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        checkIfUserIsAuthenticated()

        setUpActivity()
    }

    private fun setUpActivity() {
        // Apply insets listener to adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkIfUserIsAuthenticated() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in
            Toast.makeText(this, "User is already authenticated", Toast.LENGTH_SHORT).show()
            // You can redirect the user to another activity if needed
            // For example:
            // val intent = Intent(this, MainActivity::class.java)
            // startActivity(intent)
            // finish()
        } else {
            // No user is signed in
            Toast.makeText(this, "No user is authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
