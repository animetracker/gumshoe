package com.alexzamurca.animetrackersprint2.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alexzamurca.animetrackersprint2.MainActivity;
import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.algorithms.CheckConnection;
import com.alexzamurca.animetrackersprint2.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.notifications.UpdatingDBChannel;
import com.alexzamurca.animetrackersprint2.Database.Login;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = "LoginActivity";

    EditText enterEmail;
    EditText enterPassword;
    TextView createAccountText;
    Button signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enterEmail = findViewById(R.id.enterEmail);
        enterPassword = findViewById(R.id.enterPassword);
        createAccountText = findViewById(R.id.SignUpText);
        signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(view ->
        {
            hideKeyboard();
            if(checkData())
            {
                login();
            }
        });

        createAccountText.setOnClickListener(view -> openSignUpActivity());
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

    public void openSignUpActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public boolean checkData() {
        boolean isDataFine = true;

        if (!checkIfValidEmail(enterEmail)) {
            enterEmail.setError("Please enter a valid email.");
            isDataFine = false;
        }
        if (checkIfEmpty(enterPassword)) {
            Toast t = Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT);
            t.show();
            isDataFine = false;
        }
        return isDataFine;
    }

    boolean checkIfValidEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean checkIfEmpty(EditText text) {
        CharSequence string = text.getText().toString();
        return TextUtils.isEmpty(string);
    }

    private void openMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("coming_from_login", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private void setUpDatabaseCheckAlarm()
    {
        Log.d(TAG, "setUpDatabaseCheckAlarm: setting up");
        UpdatingDBChannel updatingDBChannel = new UpdatingDBChannel(this);
        updatingDBChannel.startAlarmAt4();
    }

    private void login()
    {
        CheckConnection checkConnection = new CheckConnection(this);
        if(checkConnection.isConnected())
        {
            LoginAsync loginAsync = new LoginAsync();
            loginAsync.execute();
        }
        else
        {
            Log.d(TAG, "login: no internet connection");

            NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
            noConnectionDialog.show(getSupportFragmentManager(), "NoConnectionDialog");
        }
    }

    private class LoginAsync extends AsyncTask<Void, Void, Void>
    {
        private boolean isSuccessful;
        private boolean incorrectLogin;
        private JSONObject response = null;

        @Override
        protected Void doInBackground(Void... voids)
        {
            Login login = new Login(LoginActivity.this);
            String responseString = login.login(enterEmail.getText().toString(), enterPassword.getText().toString());
            Log.d(TAG, "doInBackground: reponse from login:"+ responseString);
            try
            {
                response = new JSONObject(responseString);
                incorrectLogin = response.getBoolean("error");
                isSuccessful = true;
            }
            catch (JSONException e)
            {
                Log.d(TAG, "login: error trying to get response");
                isSuccessful = false;
                incorrectLogin = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(isSuccessful && !incorrectLogin)
            {
                Log.d(TAG, "onPostExecute: isSuccessful");
                if(response!=null)
                {
                    Log.d(TAG, "onPostExecute: response isn't null");
                    try
                    {
                        String session = response.getString("session");
                        int session_duration = response.getInt("validFor");

                        SharedPreferences sharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("session", session);
                        Log.d(TAG, "onPostExecute: session set to: " + session);

                        Log.d(TAG, "onPostExecute: changed session to new one from login");

                        editor.putBoolean("logged_in", true);
                        Log.d(TAG, "onPostExecute: logged_in set to true");
                        editor.apply();

                        setUpDatabaseCheckAlarm();

                        Toast.makeText(LoginActivity.this, "Welcome " + enterEmail.getText().toString() +"!", Toast.LENGTH_LONG).show();

                        openMainActivity();
                    }
                    catch (JSONException e)
                    {
                        Log.d(TAG, "onPostExecute: JSONException when trying to get session from response");
                    }
                }
            }
            else if(isSuccessful && incorrectLogin)
            {
                Toast.makeText(LoginActivity.this, "Incorrect login! Check your password and email are correct.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(LoginActivity.this, "Failed to login in (Database Issue)!", Toast.LENGTH_LONG).show();
            }

            super.onPostExecute(aVoid);
        }
    }
}