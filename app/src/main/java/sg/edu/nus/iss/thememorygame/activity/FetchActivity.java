package sg.edu.nus.iss.thememorygame.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
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
    private static final int delay = 200;
    private final Handler setImageHandler = new Handler();
    final Runnable executeSetImagesFromCache = this::setImagesFromCache;

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

    public void fetchImages(String targetUrl) {
        // TODO: remove this later
        targetUrl = "https://stocksnap.io/";
        Log.d("IMG_TEST", "Received image url: " + targetUrl);
        Log.i("IMG_TEST", "Ready to fetch");

        String finalTargetUrl = targetUrl;
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(finalTargetUrl).get();
                Elements imgElements = doc.select("img[src]");
                int count = 0;
                for (Element imgElement : imgElements) {
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
                            count++;
                        }
                        if (count == imageMaxCount) {
                            break;
                        }
                    }
                }
                Log.i("IMG_TEST", "Fetch completed");
                setImageHandler.post(executeSetImagesFromCache);
            } catch (Exception e) {
                Log.d("IMG_TEST", "Error stack: " + Arrays.toString(e.getStackTrace()));
                Log.d("IMG_TEST", "Error msg: " + e);
            }
        }).start();
    }

    public void setImagesFromCache() {
        Log.i("IMG_TEST", "Start setting images");
        int currentProgress = 0;
        for (int i = 1; i <= imageMaxCount; i++) {
            // get image reference
            int imageId = getResources().getIdentifier("image" + i, "id", getPackageName());
            Log.i("IMG_TEST", "Setting image view id: " + imageId);
            ImageView imageView = findViewById(imageId);
            // set image view
            imageView.setImageBitmap(imageCache.get("image" + i));
            // update progress bar
            currentProgress++;
            int progress = (int) ((currentProgress / (float) imageMaxCount) * 100);
            progressBar.setProgress(progress);
            // update text progress
            String textProgress = "Downloading " + i + " of " + imageMaxCount + " images";
            progressText.setText(textProgress);
        }
        Log.i("IMG_TEST", "Image set completed");
    }

    @Override
    public void onClick(View view) {
        EditText url_input = (EditText) findViewById(R.id.url_input);
        String target_url = String.valueOf(url_input.getText());

        // entering submit phase
        if (view.getId() == R.id.url_submit) {
            Log.d("IMG_TEST", "Sending fetch request to: " + target_url);
            // Make download progress bar visible
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            // fetch images
            fetchImages(target_url);
        }
    }
}
