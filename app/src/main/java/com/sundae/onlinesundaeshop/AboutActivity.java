package com.sundae.onlinesundaeshop;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity {
    TextView aboutText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.flavor), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Set page title
        setTitle(R.string.about_text_title);

        // Get the About Us text from the intent and set it to the TextView
        aboutText = findViewById(R.id.about_text);
        Intent intent = getIntent();
        String body = intent.getStringExtra(getString(R.string.about_text_title));
        aboutText.setText(body);

        // Make text scrollable
        aboutText.setMovementMethod(new ScrollingMovementMethod());
    }
}