package vn.edu.usth.dropboxui;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.zip.Inflater;

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
                onBackPressed();
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