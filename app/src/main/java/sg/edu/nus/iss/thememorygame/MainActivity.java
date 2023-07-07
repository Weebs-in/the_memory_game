package sg.edu.nus.iss.thememorygame;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import sg.edu.nus.iss.thememorygame.activity.GuessActivity;
import sg.edu.nus.iss.thememorygame.activity.HistoryActivity;
import sg.edu.nus.iss.thememorygame.activity.RememberActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.imgBrain);
        if(imageView!=null){
            imageView.setImageResource(R.drawable.brain);
        }

        Button popupButton = findViewById(R.id.popup_button);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

    }

    public void openGuessActivity(View view) {
        Intent intent = new Intent(this, GuessActivity.class);
        startActivity(intent);
    }

    public void openRememberActivity(View view) {
        Intent intent = new Intent(this, RememberActivity.class);
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

    public void showPopup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.popup_title));

        ScrollView scrollView = new ScrollView(MainActivity.this);
        TextView textView = new TextView(MainActivity.this);
        textView.setText(getResources().getString(R.string.long_text));
        textView.setLineSpacing(0, 2f);
        scrollView.addView(textView);

        builder.setView(scrollView);

        builder.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
