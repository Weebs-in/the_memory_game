package sg.edu.nus.iss.thememorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import sg.edu.nus.iss.thememorygame.activity.FetchActivity;
import sg.edu.nus.iss.thememorygame.activity.GuessActivity;

public class MainActivity extends AppCompatActivity {

    MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openFetchActivity(View view) {
        if(player == null){
            player = MediaPlayer.create(this, R.raw.fetch_epic_dramatic_action_trailer);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                    player=null;
                }
            });
        }
        player.start();
        Intent intent = new Intent(this, FetchActivity.class);
        startActivity(intent);
    }

    public void openGuessActivity(View view) {
        if(player == null){
            player = MediaPlayer.create(this, R.raw.guess_stranger_things);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                    player=null;
                }
            });
        }
        player.start();
        Intent intent = new Intent(this, GuessActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
