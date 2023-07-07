package sg.edu.nus.iss.thememorygame.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import sg.edu.nus.iss.thememorygame.MainActivity;
import sg.edu.nus.iss.thememorygame.R;
import sg.edu.nus.iss.thememorygame.adapters.HistoryAdapter;

public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    List<String> timeStampsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        SharedPreferences pref = getSharedPreferences("time_stamps", MODE_PRIVATE);

        if(pref.contains("time")){

            String timeStamp = pref.getString("time","");

            // Deserializing, sorting and showing the best 10 time taken
            List<Integer> integerList;
            if (deserialize(timeStamp).size() > 10) {
                integerList = deserialize(timeStamp).subList(0, 10);
            }
            else {
                integerList = deserialize(timeStamp);
            }


            for (Integer integer : integerList){
                String formattedTime = formatTime(integer);
                timeStampsList.add(formattedTime);
            }

        }


        HistoryAdapter adapter = new HistoryAdapter(this, timeStampsList);

        ListView historyListView = findViewById(R.id.historyListView);
        if(historyListView != null){
            historyListView.setAdapter(adapter);
            historyListView.setOnItemClickListener(this);
        }

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id){

        TextView textView = v.findViewById(R.id.txtHistory);
        String str = textView.getText().toString();

        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public List<Integer> deserialize(String str){

        String[] savedArray = str.split(",");
        List<String> savedStringList = Arrays.asList(savedArray);

        List<Integer> savedIntegerList = new ArrayList<Integer>();

        for (String ele: savedStringList) {
            savedIntegerList.add(Integer.parseInt(ele));
        }

        Collections.sort(savedIntegerList);

        return savedIntegerList;
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}