package sg.edu.nus.iss.thememorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import sg.edu.nus.iss.thememorygame.activity.FetchActivity;
import sg.edu.nus.iss.thememorygame.activity.GuessActivity;
import sg.edu.nus.iss.thememorygame.activity.HistoryActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.imgBrain);
        if(imageView!=null){
            imageView.setImageResource(R.drawable.brain);
        }
    }

    public void openFetchActivity(View view) {
        Intent intent = new Intent(this, FetchActivity.class);
        startActivity(intent);
    }

    public void openGuessActivity(View view) {
        Intent intent = new Intent(this, GuessActivity.class);
        startActivity(intent);
    }

    public void openHistoryActivity(View view){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
