package com.alexzamurca.animetrackersprint2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

        username = findViewById(R.id.username);
        password =  findViewById(R.id.password);
        password2 =  findViewById(R.id.password2);
        email = findViewById(R.id.email);
        signUpButton = findViewById(R.id.signUpButton);
        loginText = findViewById(R.id.loginText);

        signUpButton.setOnClickListener(view ->
            {
                hideKeyboard();
                if(checkData())
                {
                    // send request that goes to login activity on post execute

                }
            }
        );

        loginText.setOnClickListener(view -> openLoginActivity());
    }

    private void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean checkData() {
        boolean isDataFine = true;

        if (checkIfEmpty(username)) {
            Toast t = Toast.makeText(this, "Please enter a username.", Toast.LENGTH_SHORT);
            t.show();
            isDataFine = false;
        }
        if (!checkIfValidEmail(email)) {
            email.setError("Please enter a valid email.");
            isDataFine = false;
        }
        if (checkIfEmpty(password)) {
            Toast t = Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT);
            t.show();
            isDataFine = false;
        }
        if (checkIfEmpty(password2)) {
            Toast t = Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT);
            t.show();
            isDataFine = false;
        }
        if(!checkIfMatch(password, password2)) {
            password.setError("Passwords do not match.");
            isDataFine = false;
        }
        if(getLength(username) > 16) {
            username.setError("Username too long. Maximum character limit of 16.");
            isDataFine = false;
        }
        if(!checkIfStrong(password)) {
            password.setError("Password not strong enough. Must contain a number, lowercase and capital letter.");
            isDataFine = false;
        }
        return isDataFine;
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