package com.alexzamurca.animetrackersprint2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText username;
    EditText email;
    EditText password;
    EditText password2;
    Button signUpButton;
    TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        password2 = (EditText) findViewById(R.id.password2);
        email = (EditText) findViewById(R.id.email);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        loginText = (TextView) findViewById(R.id.loginText);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkData();
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });
    }

    private void checkData() {
        if (checkIfEmpty(username)) {
            Toast t = Toast.makeText(this, "Please enter a username.", Toast.LENGTH_SHORT);
            t.show();
        }
        if (!checkIfValidEmail(email)) {
            email.setError("Please enter a valid email.");
        }
        if (checkIfEmpty(password)) {
            Toast t = Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT);
            t.show();
        }
        if (checkIfEmpty(password2)) {
            Toast t = Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT);
            t.show();
        }
        if(!checkIfMatch(password, password2)) {
            password.setError("Passwords do not match.");
        }
        if(getLength(username) > 16) {
            username.setError("Username too long. Maximum character limit of 16.");
        }
        if(!checkIfStrong(password)) {
            password.setError("Password not strong enough. Must contain a number, lowercase and capital letter.");
        }
    }

    boolean checkIfEmpty(EditText text) {
        CharSequence string = text.getText().toString();
        return TextUtils.isEmpty(string);
    }

    boolean checkIfValidEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean checkIfMatch(EditText pw, EditText pw2) {
        return pw.getText().toString().equals(pw2.getText().toString());
    }

    int getLength(EditText text) {
        return text.getText().length();
    }

    boolean checkIfStrong(EditText text) {
        String pw = text.getText().toString();
        boolean hasUpperCase = !pw.equals(pw.toLowerCase());
        boolean hasLowerCase = !pw.equals(pw.toUpperCase());
        boolean hasNumber = Pattern.compile("[0-9]").matcher(pw).find();
        if (!hasUpperCase) {
            return false;
        }
        if (!hasLowerCase) {
            return false;
        }
        if (!hasNumber) {
            return false;
        }
        return true;
    }
    public void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}