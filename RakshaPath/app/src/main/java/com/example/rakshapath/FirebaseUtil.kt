package com.example.rakshapath

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseUtil {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
}