package sg.edu.nus.iss.thememorygame.activity;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import sg.edu.nus.iss.thememorygame.R;

public class MyBackgroundMusicService extends Service {
    private MediaPlayer mediaPlayer;

        public MyBackgroundMusicService() {
        }

        @Override
        public void onCreate() {
        super.onCreate();
        // Initialize and configure your MediaPlayer here
        mediaPlayer = MediaPlayer.create(this, R.raw.fetch_epic_dramatic_action_trailer);
        mediaPlayer.setLooping(true);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            String action = intent.getAction();
            if(action != null){
                if(action.equals("PLAY_MUSIC")){
                    startMusic();
                }else if (action.equals("PAUSE_MUSIC")){
                    pauseMusic();
                }
            }
//        // Start or pause the music based on the intent received
//        if (intent != null && intent.getAction() != null) {
//            if (intent.getAction().equals("PLAY_MUSIC")) {
//                startMusic();
//                } else if (intent.getAction().equals("PAUSE_MUSIC")) {
//                    pauseMusic();
//                }
//            }
        return START_STICKY;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        // Release the MediaPlayer resources when the service is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            }
        }

        // Helper methods to control the music playback
        private void startMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            }
        }

        private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            }
        }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}