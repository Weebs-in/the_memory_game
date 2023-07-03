package sg.edu.nus.iss.thememorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import sg.edu.nus.iss.thememorygame.MainActivity;
import sg.edu.nus.iss.thememorygame.R;

public class GuessActivity extends AppCompatActivity {
    private final String testTag = "GUESS_TEST";

    /**
     * Make toast easier
     *
     * @param msg Message in toast
     */
    public void makeToastWithMsg(String msg) {
        Toast.makeText(GuessActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);
        List<Integer> receivedList = getIntent().getIntegerArrayListExtra("selectedIds");
        if (receivedList == null) {
            Log.e(testTag, "You need to access this page via selecting items in the 1st page");
            makeToastWithMsg("You need to access this page via selecting items in the 1st page");
            Intent intent = new Intent(GuessActivity.this, FetchActivity.class);
            startActivity(intent);
        } else {
            for (Integer integer : receivedList) {
                Log.d(testTag, "Selected image id: " + integer);
            }
        }
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GuessActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
