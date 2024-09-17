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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


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
                showLoggingProblem(v);
            }
        });
    }

    private void showLoggingProblem(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.logging_problem, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.showAtLocation(anchorView, Gravity.TOP, 0, 0);
        dimBehind(popupWindow);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                clearDim();
            }
        });

        TextView forgotPassword = popupView.findViewById(R.id.forgot_password);
        TextView singleSignOn = popupView.findViewById(R.id.single_sign_on);
        TextView sendEmail = popupView.findViewById(R.id.send_email);
        TextView signInApple = popupView.findViewById(R.id.sign_in_apple);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle forgot password click
                Toast.makeText(SignInActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
            }
        });

        singleSignOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle single sign-on click
                Toast.makeText(SignInActivity.this, "Single Sign-On clicked", Toast.LENGTH_SHORT).show();
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle send email click
                Toast.makeText(SignInActivity.this, "Send Email clicked", Toast.LENGTH_SHORT).show();
            }
        });

        signInApple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign in with Apple click
                Toast.makeText(SignInActivity.this, "Sign in with Apple clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void dimBehind(PopupWindow popupWindow) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.3f;
        getWindow().setAttributes(layoutParams);
    }

    private void clearDim() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 1f;
        getWindow().setAttributes(layoutParams);
    }

}