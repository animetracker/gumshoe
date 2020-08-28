package com.alexzamurca.animetrackersprint2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alexzamurca.animetrackersprint2.series.Database.Login;
import com.alexzamurca.animetrackersprint2.series.Database.UpdateNotificationChange;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private NavController navController;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        sharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
        String session = sharedPreferences.getString("session", "");
        if(session.equals(""))
        {
            new LoginAsync().execute();
        }
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    private class LoginAsync extends AsyncTask<Void, Void, Void>
    {
        private boolean isSuccessful;
        private JSONObject response = null;

        @Override
        protected Void doInBackground(Void... voids)
        {
            Login login = new Login();
            String responseString = login.login("haha@yahoo.co.uk", "12345678");
            Log.d(TAG, "doInBackground: reponse from login:"+ responseString);
            try
            {
                response = new JSONObject(responseString);
                isSuccessful = !response.getBoolean("error");
                Log.d(TAG, "doInBackground: successful?:" + isSuccessful);
            }
            catch (JSONException e)
            {
                Log.d(TAG, "login: error trying to get response");
                isSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(isSuccessful)
            {
                Log.d(TAG, "onPostExecute: isSuccessful");
                if(response!=null)
                {
                    Log.d(TAG, "onPostExecute: response isnt null");
                    try
                    {
                        String session = response.getString("session");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("session", session);
                        editor.apply();
                        Log.d(TAG, "onPostExecute: changed session to new one from login");
                    }
                    catch (JSONException e)
                    {
                        Log.d(TAG, "onPostExecute: JSONException when trying to get session from response");
                    }

                }
            }
            else
            {
                Toast.makeText(MainActivity.this, "Failed to login in.", Toast.LENGTH_LONG).show();
            }
            navController.navigateUp();
            super.onPostExecute(aVoid);
        }
    }
}