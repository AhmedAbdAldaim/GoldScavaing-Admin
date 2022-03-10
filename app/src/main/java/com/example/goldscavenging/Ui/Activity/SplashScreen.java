package com.example.goldscavenging.Ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goldscavenging.Local_DB.LocalSession;
import com.example.goldscavenging.R;
import com.example.goldscavenging.Ui.Activity.Login;
import com.example.goldscavenging.Ui.Activity.MainActivity;

import java.util.Locale;

public class SplashScreen extends AppCompatActivity {
    ImageView imagesplash;
    Runnable runnable = ()-> {
        LocalSession localSession = new LocalSession(getApplicationContext());
        Boolean IsSessionCreated = localSession.getIsSessionCreated();;


        /*
        * Svae Language Selected
        * */
        SharedPreferences sharedPreferences = getSharedPreferences("langdb", Context.MODE_PRIVATE);
        String lang = sharedPreferences.getString("lang","ar");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());



        if((IsSessionCreated ))
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        else {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imagesplash = findViewById(R.id.imagesplash);
        imagesplash.postDelayed(runnable,3000);

    }

}

