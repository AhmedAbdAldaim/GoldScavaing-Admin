package com.example.goldscavenging.Ui.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.goldscavenging.Local_DB.LocalSession;
import com.example.goldscavenging.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

CardView CardViewAdded,CardViewShow,CardSetting,CardViewReport;
TextView Tv_Name,Tv_loginlast,Tv_Login_last_title;
LinearLayout linearLayout;
LocalSession localSession;
ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.date);
        Tv_Name = findViewById(R.id.name);
        Tv_loginlast = findViewById(R.id.datelast);
        Tv_Login_last_title = findViewById(R.id.loginlast);
        CardViewAdded = findViewById(R.id.cardview_Added);
        CardViewShow = findViewById(R.id.cardview_show);
        CardViewReport = findViewById(R.id.cardview_report);
        CardSetting = findViewById(R.id.cardview_setting);
        imageView = findViewById(R.id.logout);
        localSession = new LocalSession(MainActivity.this);

        Tv_Name.setText(localSession.getName());
        Tv_loginlast.setText(localSession.getLastlogin());

        if(Tv_loginlast.getText().toString()!=""){
            linearLayout.setVisibility(View.VISIBLE);
        }

        // <-- Logout -->
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              logout();
            }
        });

        // <-- Add Users -->
        CardViewAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, New_Registeration.class));
            }
        });

        // <-- Show Users -->
        CardViewShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ListShow.class));
            }
        });

        // <-- Setting -->
        CardSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Settings.class));
            }
        });

    }


    // <-- Logout -->
    private void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.logout_massage, null);
        Button btn_yes = view1.findViewById(R.id.btn_yes);
        Button btn_no = view1.findViewById(R.id.btn_no);

        builder.setView(view1);
        final AlertDialog dialog = builder.create();
        btn_yes.setOnClickListener(v ->
        {
            LocalSession.clearSession();
            localSession.lastDtae(date());
            Intent intent1 = new Intent(MainActivity.this, Login.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            finish();

        });
        btn_no.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }



    // <-- GetDate Funcation -->
    private String date() {
        SharedPreferences sharedPreferences = getSharedPreferences("langdb", Context.MODE_PRIVATE);
        String lang = sharedPreferences.getString("lang", "ar");

        Calendar calendar = Calendar.getInstance();
        String date = null;

        if (lang.equals("ar")) {
            DateFormat motf_ar = new SimpleDateFormat("EE - dd MMM yyyy -  HH:mm a ", Locale.forLanguageTag("ar"));
             date = motf_ar.format(calendar.getTime());
        } else if (lang.equals("en")) {
            DateFormat motf_en = new SimpleDateFormat("EE - dd MMM yyyy - HH:mm a ", Locale.ENGLISH);
            date = motf_en.format(calendar.getTime());
        }
        return date;
    }


}