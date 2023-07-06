package sg.edu.nus.iss.thememorygame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import sg.edu.nus.iss.thememorygame.R;

public class GameWonActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_won);

        Button btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(this);

        Button btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnPlayAgain.setOnClickListener(this);


        Intent intent = getIntent();
        String timeElapsed = intent.getStringExtra("time_elapsed");

        TextView txtTime = findViewById(R.id.txtTime);
        txtTime.setText(timeElapsed);

    }

    @Override
    public void onClick(View view){
        int id = view.getId();

        if(id == R.id.btnHistory){
            Intent intent = new Intent(GameWonActivity.this, HistoryActivity.class);
            startActivity(intent);
        }

        if(id == R.id.btnPlayAgain){

        }
    }
}