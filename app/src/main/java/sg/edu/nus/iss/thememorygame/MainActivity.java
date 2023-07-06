package sg.edu.nus.iss.thememorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import sg.edu.nus.iss.thememorygame.activity.FetchActivity;
import sg.edu.nus.iss.thememorygame.activity.GuessActivity;
import sg.edu.nus.iss.thememorygame.activity.RememberActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void openGuessActivity(View view) {
        Intent intent = new Intent(this, GuessActivity.class);
        startActivity(intent);


    }

    public void openRememberActivity(View view) {
        Intent intent = new Intent(this, RememberActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
