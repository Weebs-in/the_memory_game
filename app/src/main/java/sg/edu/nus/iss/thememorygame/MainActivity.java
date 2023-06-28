package sg.edu.nus.iss.thememorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import sg.edu.nus.iss.thememorygame.activity.FetchActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openFetchActivity(View view) {
        Intent intent = new Intent(this, FetchActivity.class);
        startActivity(intent);
    }
}
