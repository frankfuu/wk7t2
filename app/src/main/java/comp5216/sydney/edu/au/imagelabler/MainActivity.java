package comp5216.sydney.edu.au.imagelabler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // variable to hold the image and the directory
    InputImage mImageToProcess;
    File mediaStorageDir;

    // variable to hold the image view and the high confidence label view
    ImageView ivPreview;
    TextView mHighConfidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivPreview = (ImageView) findViewById(R.id.photopreview);
        mHighConfidence = (TextView) findViewById(R.id.labelview_confidence_output);

        mImageToProcess = get_image();
        process_image( mImageToProcess);
    }

    public InputImage get_image(){

        String image_path = "/data/data/comp5216.sydney.edu.au.imagelabler/sample_image1.jpg";

        mediaStorageDir  = new File(image_path);
        Uri fileUri = Uri.fromFile(mediaStorageDir);
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(this.getApplicationContext(), fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public void process_image(InputImage image){
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        // Task completed successfully
                        // ...
                        for (ImageLabel label : labels) {
                            String text = label.getText();
                            Log.d("TAG",text);
                        }

                        String firstLabel = String.valueOf(labels.get(0).getText());
                        String firstConfidence = String.valueOf(labels.get(0).getConfidence());
                        String highConfidentLabel ="  Label: " + firstLabel + "  Conf: "+ firstConfidence;
                        mHighConfidence.setText(highConfidentLabel);

                        Bitmap takenImage = BitmapFactory.decodeFile(mediaStorageDir.getAbsolutePath());
                        // Load the taken image into a preview
                        ivPreview.setImageBitmap(takenImage);
                        ivPreview.setVisibility(View.VISIBLE);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });
    }


}