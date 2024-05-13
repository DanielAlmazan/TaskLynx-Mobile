package edu.tasklynx.tasklynxmobile.ui;

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import edu.tasklynx.tasklynxmobile.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
	private lateinit var binding: ActivitySplashScreenBinding

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         binding = ActivitySplashScreenBinding.inflate(layoutInflater)
         setContentView(binding.root)

         val intent = Intent(this, MainActivity::class.java)
         Handler(Looper.getMainLooper()).postDelayed({
             startActivity(intent)
             overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            this.finish()
         }, 1500)
     }

}
