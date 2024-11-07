package vn.edu.usth.dropboxui.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.dropboxui.MainActivity;
import vn.edu.usth.dropboxui.R;

public class SignInActivity extends AppCompatActivity {

    private LoggingProblem loggingProblem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        loggingProblem = new LoggingProblem(this);


        Button loginButton = findViewById(R.id.continue_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageButton imageButton = findViewById(R.id.header_image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        TextView LoggingInTrouble = findViewById(R.id.logging_in_trouble);
        LoggingInTrouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loggingProblem.showLoggingProblem(v);
            }
        });
    }
}