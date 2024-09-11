package vn.edu.usth.dropbox;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.dropbox.R;

public class MainActivity extends AppCompatActivity {

    Button musicButton, moviesButton, picturesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicButton = findViewById(R.id.musicButton);
        moviesButton = findViewById(R.id.moviesButton);
        picturesButton = findViewById(R.id.picturesButton);

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Music button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        moviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Movies button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        picturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Pictures button clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
