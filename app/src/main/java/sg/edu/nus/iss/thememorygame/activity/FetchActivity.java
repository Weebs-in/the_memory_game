package sg.edu.nus.iss.thememorygame.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.edu.nus.iss.thememorygame.MainActivity;
import sg.edu.nus.iss.thememorygame.R;

public class FetchActivity extends AppCompatActivity implements View.OnClickListener {
    private final String testTag = "IMG_TEST";
    public Button buttonSubmit;
    public ProgressBar progressBar;
    public TextView progressText;
    private static LruCache<String, Bitmap> imageCache;
    private static final int imageMaxCount = 20;
    private FetchImagesTask fetchImagesTask;
    private Map<Integer, Boolean> imageSelected;
    private final int maxSelectionCount = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);
        // set imageview listeners, and set selected = false
        imageSelected = new HashMap<>();
        for (int i = 1; i <= imageMaxCount; ++i) {
            int imageId = getResources().getIdentifier("image" + i, "id", getPackageName());
            imageSelected.put(i, false);

            // set listener
            ImageView imageView = findViewById(imageId);
            int finalI = i;
            imageView.setOnClickListener(view -> {
                // if images not all loaded, refuse
                if (getImageCacheCount() != imageMaxCount) {
                    makeToastWithMsg("Images not ready yet");
                }
                // if an image is clicked...
                else {
                    boolean isSelected = Boolean.TRUE.equals(imageSelected.get(finalI));
                    // if was selected, then reverse to the image
                    if (isSelected) {
                        imageView.setImageBitmap(imageCache.get("image" + finalI));
                        Log.d(testTag, "unselecting image" + finalI);
                    } else {
                        imageView.setImageResource(R.drawable.image_check);
                        Log.d(testTag, "selecting image" + finalI);
                    }
                    // reverse the selection status
                    imageSelected.put(finalI, !isSelected);
                    int selectedCount = getSelectedImageIds().size();
                    // if already 6 images selected, get a new intent to guess activity
                    if (selectedCount > maxSelectionCount) {
                        makeToastWithMsg("ERROR: Max number of item exceeded");
                    } else if (selectedCount == maxSelectionCount) {
                        Intent intent = new Intent(FetchActivity.this, GuessActivity.class);
                        intent.putIntegerArrayListExtra("selectedIds", (ArrayList<Integer>) getSelectedImageIds());
                        startActivity(intent);
                    }
                }
            });
        }
        // set selected images
        // set buttons and listeners
        buttonSubmit = findViewById(R.id.url_submit);
        buttonSubmit.setOnClickListener(FetchActivity.this);
        // set progress bar and text
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progress_text);
        // set lru cache
        int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
        int cacheSize = maxMemory / 8;
        imageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    /**
     * For another activity to retrieve from lru cache
     *
     * @return LruCache<String, Bitmap>
     */
    public static LruCache<String, Bitmap> getImageCache() {
        return imageCache;
    }

    /**
     * Get how many images has been cached
     * @return number of images cached
     */
    public static int getImageCacheCount() {
        int res = 0;
        for (int i = 1; i <= imageMaxCount; ++i) {
            if (imageCache.get("image" + i) != null) {
                res += 1;
            }
        }
        return res;
    }

    /**
     * Get selected id from map
     *
     * @return List of selected ids
     */
    public List<Integer> getSelectedImageIds() {
        List<Integer> res = new ArrayList<>();
        for (int k : imageSelected.keySet()) {
            if (Boolean.TRUE.equals(imageSelected.get(k))) {
                res.add(k);
            }
        }
        return res;
    }

    /**
     * Make toast easier
     *
     * @param msg Message in toast
     */
    public void makeToastWithMsg(String msg) {
        Toast.makeText(FetchActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Validate user's url
     * @param urlString url
     * @return valid or not
     */
    public boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clear all images when user wants to abort
     */
    public void clearAllImage() {
        for (int i = 1; i <= imageMaxCount; i++) {
            // clear selected
            imageSelected.put(i, false);
            // remove from cache
            imageCache.remove("image" + i);
            // clear imageview
            int imageId = getResources().getIdentifier("image" + i, "id", getPackageName());
            Log.i(testTag, "Removing image view id: " + imageId);
            ImageView imageView = findViewById(imageId);
            imageView.setImageResource(R.drawable.img_clear);
            // clear progress bar
            progressBar.setProgress(0);
            Drawable progressDrawable = progressBar.getProgressDrawable();
            progressDrawable.setColorFilter(null);
            // update text progress
            String textProgress = "Downloading ? of 20 images";
            progressText.setText(textProgress);
        }
    }

    /**
     * Another way to add click listener
     *
     * @param view View
     */
    @Override
    public void onClick(View view) {
        clearAllImage();
        EditText url_input = (EditText) findViewById(R.id.url_input);
        String target_url = String.valueOf(url_input.getText());

        // validate url
        if (!isValidUrl(target_url)) {
            makeToastWithMsg("ERROR: you need to enter a valid url");
        }
        // entering submit phase
        else if (view.getId() == R.id.url_submit) {
            Log.d(testTag, "Sending fetch request to: " + target_url);
            // make download progress bar visible
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            // check and abort the previous task
            if (fetchImagesTask != null && fetchImagesTask.getStatus() == AsyncTask.Status.RUNNING) {
                fetchImagesTask.cancel(true);
            }
            // fetch images
            fetchImagesTask = new FetchImagesTask();
            fetchImagesTask.execute(target_url);
        }
    }

    /**
     * Task to fetch images
     */
    private class FetchImagesTask extends AsyncTask<String, Integer, Void> {

        /**
         * Main task to fetch image
         *
         * @param strings params
         * @return void
         */
        @Override
        protected Void doInBackground(String... strings) {
            String targetUrl = strings[0];
            targetUrl = "https://stocksnap.io/";  // TODO: remove this line after testing
            Log.d(testTag, "Received image url: " + targetUrl);
            Log.i(testTag, "Ready to fetch");
            try {
                Document doc = Jsoup.connect(targetUrl).get();
                Elements imgElements = doc.select("img[src]");
                int count = 0;
                for (Element imgElement : imgElements) {
                    if (isCancelled()) {
                        break;
                    }
                    String imageUrl = imgElement.absUrl("src");
                    // filter svg images
                    if (!imageUrl.endsWith("svg")) {
                        Log.d(testTag, "Intercepting image" + (count + 1) + " url: " + imageUrl);
                        // get image and save
                        Connection.Response response = Jsoup.connect(imageUrl).ignoreContentType(true).execute();
                        byte[] imageData = response.bodyAsBytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                        // confirm process of image, set into image view
                        if (bitmap != null) {
                            imageCache.put("image" + (count + 1), bitmap);
                            // update progress bar while put image into imageview
                            publishProgress(count + 1);
                            count++;
                        }
                        if (count == imageMaxCount) {
                            break;
                        }
                    }
                }
                Log.i(testTag, "Fetch completed");
            } catch (Exception e) {
                Log.d(testTag, "Error stack: " + Arrays.toString(e.getStackTrace()));
                Log.d(testTag, "Error msg: " + e);
            }
            return null;
        }

        /**
         * After image is fetched from server, load the image into the view
         * and save it to the lru cache
         *
         * @param values params
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            int currentProgress = values[0];
            // update image view
            int imageId = getResources().getIdentifier("image" + currentProgress, "id", getPackageName());
            Log.i(testTag, "Setting image view id: " + imageId);
            ImageView imageView = findViewById(imageId);
            // set image view
            imageView.setImageBitmap(imageCache.get("image" + currentProgress));
            // update progress bar
            int progressPercentage = (int) ((currentProgress / (float) imageMaxCount) * 100);
            progressBar.setProgress(progressPercentage);
            if (currentProgress == imageMaxCount) {
                Drawable progressDrawable = progressBar.getProgressDrawable();
                int color = ContextCompat.getColor(FetchActivity.this, R.color.completeGreen);
                progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
            // update text progress
            String textProgress = currentProgress != imageMaxCount ? ("Downloading " + currentProgress + " of " + imageMaxCount + " images") : "Download completed";
            progressText.setText(textProgress);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FetchActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
