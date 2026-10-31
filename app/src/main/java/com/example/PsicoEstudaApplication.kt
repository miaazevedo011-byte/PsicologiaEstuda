package com.example

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class PsicoEstudaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("PsicoEstudaApp", "FirebaseApp initialized in Application.onCreate")
            }
        } catch (e: Exception) {
            Log.e("PsicoEstudaApp", "Failed to initialize FirebaseApp in Application.onCreate", e)
        }
    }
}
