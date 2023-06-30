package sg.edu.nus.iss.thememorygame.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;

import sg.edu.nus.iss.thememorygame.R;

public class FetchActivity extends AppCompatActivity implements View.OnClickListener {
    public Button buttonSubmit;
    public ProgressBar progressBar;
    public TextView progressText;
    private static LruCache<String, Bitmap> imageCache;
    private static final int imageMaxCount = 20;
    private FetchImagesTask fetchImagesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);
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

    public static LruCache<String, Bitmap> getImageCache() {
        return imageCache;
    }

    public void makeToastWithMsg(String msg) {
        Toast.makeText(FetchActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void clearAllImage() {
        for (int i = 1; i <= imageMaxCount; i++) {
            // remove from cache
            imageCache.remove("image" + i);
            // clear imageview
            int imageId = getResources().getIdentifier("image" + i, "id", getPackageName());
            Log.i("IMG_TEST", "Removing image view id: " + imageId);
            ImageView imageView = findViewById(imageId);
            imageView.setImageResource(R.drawable.img_clear);
            // clear progress bar
            progressBar.setProgress(0);
            // update text progress
            String textProgress = "Downloading ? of 20 images";
            progressText.setText(textProgress);
        }
    }

    @Override
    public void onClick(View view) {
        clearAllImage();
        EditText url_input = (EditText) findViewById(R.id.url_input);
        String target_url = String.valueOf(url_input.getText());

        // entering submit phase
        if (view.getId() == R.id.url_submit) {
            Log.d("IMG_TEST", "Sending fetch request to: " + target_url);
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
            Log.d("IMG_TEST", "Received image url: " + targetUrl);
            Log.i("IMG_TEST", "Ready to fetch");
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
                        Log.d("IMG_TEST", "Intercepting image" + (count + 1) + " url: " + imageUrl);
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
                Log.i("IMG_TEST", "Fetch completed");
            } catch (Exception e) {
                Log.d("IMG_TEST", "Error stack: " + Arrays.toString(e.getStackTrace()));
                Log.d("IMG_TEST", "Error msg: " + e);
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
            Log.i("IMG_TEST", "Setting image view id: " + imageId);
            ImageView imageView = findViewById(imageId);
            // set image view
            imageView.setImageBitmap(imageCache.get("image" + currentProgress));
            // update progress bar
            int progressPercentage = (int) ((currentProgress / (float) imageMaxCount) * 100);
            progressBar.setProgress(progressPercentage);
            // update text progress
            String textProgress = currentProgress != imageMaxCount ? ("Downloading " + currentProgress + " of " + imageMaxCount + " images") : "Download completed";
            progressText.setText(textProgress);
        }
    }
}
