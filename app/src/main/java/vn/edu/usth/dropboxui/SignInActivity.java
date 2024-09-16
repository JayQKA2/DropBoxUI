package vn.edu.usth.dropboxui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.dropboxui.ui.home.HomeFragment;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { // corrected parameter name
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ImageButton imageButton = (ImageButton) findViewById(R.id.header_image_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInActivity.this, "Back", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        Button button = (Button) findViewById(R.id.continue_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInActivity.this, "Log in", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignInActivity.this, HomeFragment.class);
                startActivity(intent);
            }
        });
    }

}
