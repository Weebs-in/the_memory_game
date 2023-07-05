package sg.edu.nus.iss.thememorygame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import sg.edu.nus.iss.thememorygame.activity.FetchActivity;
import sg.edu.nus.iss.thememorygame.activity.GuessActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button popupButton = findViewById(R.id.popup_button);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });

    }

    public void openFetchActivity(View view) {
        Intent intent = new Intent(this, FetchActivity.class);
        startActivity(intent);
    }

    public void openGuessActivity(View view) {
        Intent intent = new Intent(this, GuessActivity.class);
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

