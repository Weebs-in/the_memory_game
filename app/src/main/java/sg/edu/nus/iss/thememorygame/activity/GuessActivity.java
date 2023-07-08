package sg.edu.nus.iss.thememorygame.activity;

import static sg.edu.nus.iss.thememorygame.activity.FetchActivity.channel;
import static sg.edu.nus.iss.thememorygame.activity.FetchActivity.getImageCache;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import sg.edu.nus.iss.thememorygame.MainActivity;
import sg.edu.nus.iss.thememorygame.R;

public class GuessActivity extends AppCompatActivity {
    private final String testTag = "GUESS_TEST";


    private boolean guessSuccessful = false;
    private int numOfGuessRight = 0;

    private boolean FirstPictureTurnOn = false;
    private boolean SecondPictureTurnOn = false;
    private String firstImage = null;
    private String secondImage = null;
    private int firstImageGuessPlace = -1;

    private int mSeconds = 0;

    private static int testTime = 0;

    private List<Integer> historyList = new ArrayList<>();

    private MediaPlayer[] mediaPlayers;

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

        mediaPlayers = new MediaPlayer[3];
        mediaPlayers[0] = MediaPlayer.create(getApplicationContext(), R.raw.correct_sound);
        mediaPlayers[1] = MediaPlayer.create(getApplicationContext(), R.raw.wrong_sound);
        mediaPlayers[2] = MediaPlayer.create(getApplicationContext(), R.raw.win_sound);

        List<Integer> receivedList = getIntent().getIntegerArrayListExtra("selectedIds");

        if (receivedList == null) {
            Log.e(testTag, "You need to access this page via selecting items in the 1st page");
            // makeToastWithMsg("You need to access this page via selecting items in the 1st page");
            channel = 1;
            Intent intent = new Intent(GuessActivity.this, FetchActivity.class);
            startActivity(intent);
        } else {
            TextView textView = findViewById(R.id.match_count);
            textView.setText(numOfGuessRight + " of 6 matches");
            showImage(receivedList);
            runTimer();
        }
    }

    private void showImage(List<Integer> receivedList) {

        Map<Integer, Integer> newPlace_imageNum = new HashMap<>();
        //all image number 1-12
        List<Integer> allNumbers = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            allNumbers.add(i);
        }
        //Randomly pick 6 out of 12 numbers,takes 6 random place
        List<Integer> randomNumbers = generateRandomNumbers(1, 12, 6);
        //the remain 6 place
        List<Integer> remainingNumbers = new ArrayList<>(allNumbers);
        remainingNumbers.removeAll(randomNumbers);


        for (int i = 0; i < 6; i++) {
            int imageId = receivedList.get(i);

            String cacheKey = "image" + imageId;
            // Fetching images from the cache
            // Displayed in the ImageView
            int imageGuessId = getResources().getIdentifier("image_guess_" + randomNumbers.get(i), "id", getPackageName());
            newPlace_imageNum.put(randomNumbers.get(i), imageId);
            ImageView imageView = findViewById(imageGuessId);
            imageView.setImageResource(R.drawable.img_clear);

            int finalI = randomNumbers.get(i);
            imageView.setOnClickListener(view -> {
                imageView.setImageBitmap(getImageCache().get("image" + newPlace_imageNum.get(finalI)));
                if (FirstPictureTurnOn == false) {
                    // if no picture has been clicked, set this one as the first
                    FirstPictureTurnOn = true;
                    firstImageGuessPlace = imageView.getId();
                    firstImage = "image" + newPlace_imageNum.get(finalI);
                    Log.d("first", firstImage);
                } else if (SecondPictureTurnOn == false) {
                    // If one picture has been clicked, but not a second, set the current picture as the second one，
                    SecondPictureTurnOn = true;
                    secondImage = "image" + newPlace_imageNum.get(finalI);
                    Log.d("second", secondImage);

                    // Check if both selected images are the same
                    if (firstImage.equals(secondImage)) {
                        mediaPlayers[0].start();
                        // if both photos are the same, they remain open
                        // make successfully matched photos unclickable
                        ImageView firstImageView = findViewById(firstImageGuessPlace);
                        imageView.setAlpha(0.2f);
                        firstImageView.setAlpha(0.2f);
                        imageView.setEnabled(false);
                        firstImageView.setEnabled(false);
                        numOfGuessRight++;
                        TextView textView = findViewById(R.id.match_count);
                        textView.setText(numOfGuessRight + " of 6 matches");
                        if (numOfGuessRight == 6) {
                            guessSuccessful = true;
                            mediaPlayers[2].start();
                            timeRecording();
                            AlertDialog.Builder dlg = new AlertDialog.Builder(GuessActivity.this)
                                    .setTitle("Congratulations")
                                    .setMessage("You are successful!")
                                    .setPositiveButton("Return",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dlg, int which) {
                                                    Intent intent = new Intent(GuessActivity.this, HistoryActivity.class);
                                                    startActivity(intent);
//                                                    finish();
                                                }
                                            });
                            dlg.show();
                        }
                        FirstPictureTurnOn = false;
                        SecondPictureTurnOn = false;
                    } else {
                        mediaPlayers[1].start();
                        // if the pictures clicked are different, hide them again
                        // first picture
                        ImageView firstImageView = findViewById(firstImageGuessPlace);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // set the second picture as R.drawable.img_clear
                                imageView.setImageResource(R.drawable.img_clear);
                                // set the second picture as R.drawable.img_clear
                                firstImageView.setImageResource(R.drawable.img_clear);
                            }
                        }, 300); // delay by 3s
                        // reset the values，in preparation for next match
                        FirstPictureTurnOn = false;
                        SecondPictureTurnOn = false;
                        firstImageGuessPlace = -1;
                    }
                }
            });
        }


        // another 6 duplicated picture
        for (int i = 0; i < 6; i++) {
            int imageId = receivedList.get(i);

            String cacheKey = "image" + imageId;
            int imageGuessId = getResources().getIdentifier("image_guess_" + remainingNumbers.get(i), "id", getPackageName());
            newPlace_imageNum.put(remainingNumbers.get(i), imageId);
            ImageView imageView = findViewById(imageGuessId);
            imageView.setImageResource(R.drawable.img_clear);


            int finalI = remainingNumbers.get(i);
            imageView.setOnClickListener(view -> {
                imageView.setImageBitmap(getImageCache().get("image" + newPlace_imageNum.get(finalI)));
                if (FirstPictureTurnOn == false) {
                    // if there was no image open, set current image as first one
                    FirstPictureTurnOn = true;
                    firstImageGuessPlace = imageView.getId();
                    firstImage = "image" + newPlace_imageNum.get(finalI);
                    Log.d("first", firstImage);
                } else if (SecondPictureTurnOn == false) {
                    // if there is one image open, set current one as second one
                    SecondPictureTurnOn = true;
                    secondImage = "image" + newPlace_imageNum.get(finalI);
                    Log.d("second", secondImage);

                    // check if both images are the same
                    if (firstImage.equals(secondImage)) {
                        mediaPlayers[0].start();
                        // if both images are the same, remain open
                        // ensure both photos cannot be clicked
                        ImageView firstImageView = findViewById(firstImageGuessPlace);
                        imageView.setAlpha(0.2f);
                        firstImageView.setAlpha(0.2f);
                        imageView.setEnabled(false);
                        firstImageView.setEnabled(false);
                        numOfGuessRight++;
                        TextView textView = findViewById(R.id.match_count);
                        textView.setText(numOfGuessRight + " of 6 matches");
                        if (numOfGuessRight == 6) {
                            guessSuccessful = true;
                            mediaPlayers[2].start();
                            timeRecording();
                            AlertDialog.Builder dlg = new AlertDialog.Builder(GuessActivity.this)
                                    .setTitle("Congratulations")
                                    .setMessage("You are successful!")
                                    .setPositiveButton("Return",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dlg, int which) {
                                                    Intent intent = new Intent(GuessActivity.this, HistoryActivity.class);
                                                    startActivity(intent);
//                                                    finish();
                                                }
                                            });
                            dlg.show();
                        }
                        FirstPictureTurnOn = false;
                        SecondPictureTurnOn = false;
                    } else {

                        // if both images are different, hide them
                        //第一张图
                        mediaPlayers[1].start();
                        ImageView firstImageView = findViewById(firstImageGuessPlace);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 设置第二张图片为 R.drawable.img_clear
                                imageView.setImageResource(R.drawable.img_clear);
                                // 设置第一张图片为 R.drawable.img_clear
                                firstImageView.setImageResource(R.drawable.img_clear);
                            }
                        }, 300); // 延迟0.3秒钟执行
                        // 重置已翻开的图片变量，为下一次翻转做准备
                        FirstPictureTurnOn = false;
                        SecondPictureTurnOn = false;
                        firstImageGuessPlace = -1;
                    }
                }

            });
        }

    }


    public void timeRecording() {
        SharedPreferences pref = getSharedPreferences("time_stamps",MODE_PRIVATE);

        if(pref.contains("time")){
            String timeStrings = pref.getString("time", "");
            if(timeStrings != null){
                List<Integer> integerList = deserialize(timeStrings);
                integerList.add(mSeconds);
                historyList.addAll(integerList);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("time",serialize(historyList));
                editor.commit();
            }
        }
        else {
            historyList.add(mSeconds);

            String timeStamps = serialize(historyList);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("time",timeStamps);
            editor.commit();
        }
    }

    public List<Integer> generateRandomNumbers(int min, int max, int count) {
        List<Integer> randomNumbers = new ArrayList<>();
        Random random = new Random();

        while (randomNumbers.size() < count) {
            int randomNumber = random.nextInt(max - min + 1) + min;
            if (!randomNumbers.contains(randomNumber)) {
                randomNumbers.add(randomNumber);
            }
        }

        return randomNumbers;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GuessActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public String formatSecondsToTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds);
    }

    public String serialize(List<Integer> historyList) {

        String savedString = "";
        for (Integer history : historyList) {
            savedString += history.toString() + ',';
        }
        return savedString;
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

    private void runTimer() {
        final TextView txtTime = findViewById(R.id.timer);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                txtTime.setText(formatSecondsToTime(mSeconds));
                mSeconds++;
                if (guessSuccessful) {
                    handler.removeCallbacks(this); // 猜对了，停止计时器的执行
                } else {
                    handler.postDelayed(this, 1000); // 继续每秒执行一次
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                }
            }
    }

}
