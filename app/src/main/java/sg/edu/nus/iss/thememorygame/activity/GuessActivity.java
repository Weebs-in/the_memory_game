package sg.edu.nus.iss.thememorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sg.edu.nus.iss.thememorygame.MainActivity;
import sg.edu.nus.iss.thememorygame.R;

public class GuessActivity extends AppCompatActivity implements View.OnClickListener {
    private final String testTag = "GUESS_TEST";

    // For Sending elapsed time for each game won (Hardcoded)
    private int elapsedSecs = 0;
    // For storing the elapsed times for history (Hardcoded)
    List<Integer> historyList = new ArrayList<>(Arrays.asList(150, 20, 260, 10, 90, 50, 180, 40, 75));


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
//        if (receivedList == null) {
//            Log.e(testTag, "You need to access this page via selecting items in the 1st page");
//            makeToastWithMsg("You need to access this page via selecting items in the 1st page");
//            Intent intent = new Intent(GuessActivity.this, FetchActivity.class);
//            startActivity(intent);
//        } else {
//            for (Integer integer : receivedList) {
//                Log.d(testTag, "Selected image id: " + integer);
//            }

        Button btnSendTimeElapsed = findViewById(R.id.btnSendTimeElapsed);
        btnSendTimeElapsed.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GuessActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {

        elapsedSecs = 90;

        int id = view.getId();

        if (id == R.id.btnSendTimeElapsed) {
            Intent intent = new Intent(GuessActivity.this, GameWonActivity.class);
            intent.putExtra("time_elapsed", formatTime(elapsedSecs));
            startActivity(intent);

            String timeStamps = serialize(historyList);

            SharedPreferences pref = getSharedPreferences("time_stamps",MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("time",timeStamps);
            editor.commit();
        }
    }

    public String formatTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        String hoursFormat;
        String minutesFormat;
        String secondsFormat;

        if (hours < 10) hoursFormat = "0" + String.valueOf(hours);
        else hoursFormat = String.valueOf(hours);

        if (minutes < 10) minutesFormat = "0" + String.valueOf(minutes);
        else minutesFormat = String.valueOf(minutes);

        if (seconds < 10) secondsFormat = "0" + String.valueOf(seconds);
        else secondsFormat = String.valueOf(seconds);

        return String.format("%s:%s:%s", hoursFormat, minutesFormat, secondsFormat);
    }

    public String serialize(List<Integer> historyList) {

        String savedString = "";
        for (Integer history : historyList) {
            savedString += history.toString() + ',';
        }
        return savedString;
    }
}
