package com.shaihi.loadimage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        ImageView iv = findViewById(R.id.imageView);
                        iv.setImageURI(uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        Button loadBtn = findViewById(R.id.loadButton);
        loadBtn.setOnClickListener(v -> {
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        Button captureBtn = findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the user granted the app Camera permission.
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    // Request the Camera permission if it has not been granted. This generates a popup
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 0);
                } else {
                    Toast.makeText(MainActivity.this, "Seems we have access to the camera", Toast.LENGTH_SHORT).show();
                    //startCamera();
                }
            }
        });
    }

    private void startCamera() {
        // Registers a camera activity launcher.
        ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(), result -> {
                    if (result != null) {
                        Log.d("Camera", "Captured image: " + result);
                        ImageView iv = findViewById(R.id.imageView);
                        iv.setImageBitmap(result);
                    } else {
                        Log.d("Camera", "No image captured");
                    }
                });

        // Launch the camera to capture an image.
        cameraLauncher.launch(null);
    }

}