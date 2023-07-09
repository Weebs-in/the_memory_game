package sg.edu.nus.iss.thememorygame;

import android.os.Bundle;

import android.content.Intent;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;


public class SplashScreenActivity extends AppCompatActivity {
    // 2 second delay
    private static final long SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize a Handler and post a delayed task to navigate to MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start MainActivity
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);

                // Close the SplashActivity
                finish();
            }
        }, SPLASH_DELAY);
    }

    @Override
    public void onBackPressed() {
        // Exit the app
        finishAffinity(); // Close all activities in the task
        System.exit(0); // Terminate the app process
    }
}