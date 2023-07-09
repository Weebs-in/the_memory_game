package sg.edu.nus.iss.thememorygame.activity;

//Re-select the first selected image from the disordered images

import static sg.edu.nus.iss.thememorygame.activity.FetchActivity.channel;
import static sg.edu.nus.iss.thememorygame.activity.FetchActivity.getImageCache;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sg.edu.nus.iss.thememorygame.MainActivity;
import sg.edu.nus.iss.thememorygame.R;

public class RememberActivity extends AppCompatActivity {
    private final String testTag = "GUESS_TEST";

    private boolean guessSuccessful = false;

    private int numOfGuessRight = 0;
    private int numOfGuess = 0;

    private TextView textViewTimer;
    private CountDownTimer countDownTimer;
    private Map<Integer, Boolean> imageSelected;
    private final int maxSelectionCount = 6;
    private MediaPlayer[] mediaPlayers;

    /**
     * Make toast easier
     *
     * @param msg Message in toast
     */
    public void makeToastWithMsg(String msg) {
        Toast.makeText(RememberActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);
        mediaPlayers = new MediaPlayer[1];
        mediaPlayers[0] = MediaPlayer.create(getApplicationContext(), R.raw.win_sound);
        List<Integer> receivedList = getIntent().getIntegerArrayListExtra("selectedIds");
        List<Integer> unreceivedList = getIntent().getIntegerArrayListExtra("unselectedIds");

       /* for(int i=0;i<receivedList.size();i++){
            Log.d("select", String.valueOf(receivedList.get(i)));
        }
        for(int i=0;i<unreceivedList.size();i++){
            Log.d("unselect", String.valueOf(unreceivedList.get(i)));
        }*/
        if (receivedList == null) {
            Log.e(testTag, "You need to access this page via selecting items in the 1st page");
            // makeToastWithMsg("You need to access this page via selecting items in the 1st page");
            Intent intent = new Intent(RememberActivity.this, FetchActivity.class);
            channel = 2;
            startActivity(intent);
        } else {
           /* for (Integer integer : receivedList) {
                Log.d(testTag, "Selected image id: " + integer);
            }*/

            TextView textView = findViewById(R.id.match_count);
            textView.setText(numOfGuessRight + " of 6 matches");
            showImage(receivedList, unreceivedList);


            startCountdownTimer();

        }
    }

    private void showImage(List<Integer> receivedList, List<Integer> unreceivedList) {
        imageSelected = new HashMap<>();


        //all image number 1-12
        List<Integer> allNumbers = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            allNumbers.add(i);
        }
        //Randomly pick 6 out of 12 numbers
        List<Integer> randomNumbers = generateRandomNumbers(1, 12, 6);
        //the remain 6 numbers
        List<Integer> remainingNumbers = new ArrayList<>(allNumbers);
        remainingNumbers.removeAll(randomNumbers);

        Map<Integer, Integer> newPlace_imageNum = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            int imageId = receivedList.get(i);

            String cacheKey = "image" + imageId;
            // Fetching images from the cache
            Bitmap bitmap = getImageCache().get(cacheKey);
            // Displayed in the ImageView
            int imageGuessId = getResources().getIdentifier("image_guess_" + randomNumbers.get(i), "id", getPackageName());
            newPlace_imageNum.put(randomNumbers.get(i), imageId);
            ImageView imageView = findViewById(imageGuessId);
            imageView.setImageBitmap(bitmap);

            imageSelected.put(randomNumbers.get(i), false);
            int finalI = randomNumbers.get(i);
            imageView.setOnClickListener(view -> {

                boolean isSelected = Boolean.TRUE.equals(imageSelected.get(finalI));
                // if was selected, then reverse to the image
                if (isSelected) {
                    imageView.setImageBitmap(getImageCache().get("image" + newPlace_imageNum.get(finalI)));
                    imageSelected.put(finalI, !isSelected);
                    numOfGuessRight--;
                    numOfGuess--;
                    TextView textView = findViewById(R.id.match_count);
                    textView.setText(numOfGuessRight + " of 6 matches");
                    Log.d(testTag, "unselecting image" + newPlace_imageNum.get(finalI));
                } else {
                    if (numOfGuess >= maxSelectionCount) {
                        makeToastWithMsg("ERROR: Max number of item exceeded");
                    } else {
                        imageView.setImageResource(R.drawable.image_check);
                        imageSelected.put(finalI, !isSelected);
                        numOfGuessRight++;
                        numOfGuess++;
                        TextView textView = findViewById(R.id.match_count);
                        textView.setText(numOfGuessRight + " of 6 matches");

                        Log.d(testTag, "selecting image" + newPlace_imageNum.get(finalI));
                        if (numOfGuessRight == 6) {
                            guessSuccessful = true;
                            mediaPlayers[0].start();
                            String title = getString(R.string.alert_right);
                            AlertDialog.Builder dlg = new AlertDialog.Builder(RememberActivity.this)
                                    .setTitle(title)
                                    .setPositiveButton("Return",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dlg, int which) {
                                                    Intent intent = new Intent(RememberActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                            dlg.show();
                        }
                    }
                }


            });
        }

        //用于混淆的图片
        for (int i = 0; i < 6; i++) {
            int imageId = unreceivedList.get(i);

            String cacheKey = "image" + imageId;
            Bitmap bitmap = getImageCache().get(cacheKey);
            int imageGuessId = getResources().getIdentifier("image_guess_" + remainingNumbers.get(i), "id", getPackageName());
            newPlace_imageNum.put(remainingNumbers.get(i), imageId);
            ImageView imageView = findViewById(imageGuessId);
            imageView.setImageBitmap(bitmap);

            imageSelected.put(remainingNumbers.get(i), false);
            int finalI = remainingNumbers.get(i);
            imageView.setOnClickListener(view -> {
                boolean isSelected = Boolean.TRUE.equals(imageSelected.get(finalI));
                // if was selected, then reverse to the image
                if (isSelected) {

                    numOfGuess--;
                    imageView.setImageBitmap(getImageCache().get("image" + newPlace_imageNum.get(finalI)));
                    imageSelected.put(finalI, !isSelected);
                    Log.d(testTag, "unselecting image" + newPlace_imageNum.get(finalI));
                } else {
                    if (numOfGuess >= maxSelectionCount) {
                        makeToastWithMsg("ERROR: Max number of item exceeded");
                    } else {
                        numOfGuess++;
                        imageView.setImageResource(R.drawable.image_check);
                        imageSelected.put(finalI, !isSelected);
                        Log.d(testTag, "selecting image" + newPlace_imageNum.get(finalI));
                    }
                }


            });
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
        Intent intent = new Intent(RememberActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void startCountdownTimer() {

        textViewTimer = findViewById(R.id.timer);

        // Create a countdown timer with the total duration and interval
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the UI on each interval of the countdown
                long seconds = millisUntilFinished / 1000;
                textViewTimer.setText("Time remaining: " + seconds);
            }

            @Override
            public void onFinish() {
                // Perform actions when the countdown finishes
                textViewTimer.setText("Time's up!");
                if (guessSuccessful) {

                    String title = getString(R.string.alert_time_is_up);
                    String msg = getString(R.string.alert_right);
                    AlertDialog.Builder dlg = new AlertDialog.Builder(RememberActivity.this)
                            .setTitle(title)
                            .setMessage(msg)
                            .setPositiveButton("Return",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dlg, int which) {
                                            Intent intent = new Intent(RememberActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                    dlg.show();
                } else {
                    String title = getString(R.string.alert_time_is_up);
                    String msg = getString(R.string.alert_wrong);
                    AlertDialog.Builder dlg = new AlertDialog.Builder(RememberActivity.this)
                            .setTitle(title)
                            .setMessage(msg)
                            .setPositiveButton("Return",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dlg, int which) {
                                            Intent intent = new Intent(RememberActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                            .setNegativeButton("Try Again",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = getIntent();
                                            finish();
                                            startActivity(intent);
                                        }
                                    });
                    dlg.show();
                }
            }
        };

        // Start the countdown timer
        countDownTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the countdown timer when the activity is destroyed to prevent memory leaks
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

}
