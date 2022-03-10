package com.example.goldscavenging.Ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.goldscavenging.Local_DB.LocalSession;
import com.example.goldscavenging.Model.ListviewSettingModel;
import com.example.goldscavenging.R;
import com.example.goldscavenging.Ui.Adapter.ListviewSettingAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Settings extends AppCompatActivity {

ListView listView;
LocalSession localSession;
RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.listsetting);
        localSession = new LocalSession(this);
        SharedPreferences sharedPreferences = getSharedPreferences("langdb", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        ArrayList<ListviewSettingModel> arrayList = new ArrayList<ListviewSettingModel>();
        arrayList.add(new ListviewSettingModel(getResources().getString(R.string.deltedlist),R.drawable.ic_deleted_recovery_blue));
        arrayList.add(new ListviewSettingModel(getResources().getString(R.string.edit_profile),R.drawable.ic_person));
        arrayList.add(new ListviewSettingModel(getResources().getString(R.string.language),R.drawable.ic_language));
        arrayList.add(new ListviewSettingModel(getResources().getString(R.string.logout),R.drawable.ic_logout));
        ListviewSettingAdapter arrayAdapter = new ListviewSettingAdapter(this,R.layout.item_row_listview_setting,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){

                    case 0:
                        startActivity(new Intent(Settings.this, Deleted_List.class));
                        break;

                    case 1:
                    startActivity(new Intent(Settings.this, Edit_Profile.class));
                    break;

                    case 2:
                        AlertDialog.Builder builderlan = new AlertDialog.Builder(Settings.this);
                        View viewlan = getLayoutInflater().inflate(R.layout.language_select, null);
                        RadioGroup radioGroup = viewlan.findViewById(R.id.radiogroup);

                        builderlan.setView(viewlan);
                        final AlertDialog dialoglan = builderlan.create();

                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                             int sel = radioGroup.getCheckedRadioButtonId();
                             radioButton = viewlan.findViewById(sel) ;

                                if(radioButton.getText().toString().equals("العربية")){
                                    dialoglan.dismiss();
                                    Locale locale = new Locale("ar");
                                    Locale.setDefault(locale);
                                    Configuration config = new Configuration();
                                    config.locale = locale;
                                    getResources().updateConfiguration(config,
                                            getResources().getDisplayMetrics());
                                    editor.putString("lang","ar");
                                    editor.commit();

                                    Intent intent = new Intent(Settings.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                }
                               else if(radioButton.getText().toString().equals("Engilsh")){
                                   dialoglan.dismiss();
                                   Locale locale = new Locale("en");
                                   Locale.setDefault(locale);
                                   Configuration config = new Configuration();
                                   config.locale = locale;
                                   getResources().updateConfiguration(config,
                                           getResources().getDisplayMetrics());
                                   editor.putString("lang","en");
                                   editor.commit();

                                    Intent intent = new Intent(Settings.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                        dialoglan.show();
                        break;

                    case 3:
                        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                        View view1 = getLayoutInflater().inflate(R.layout.logout_massage, null);
                        Button btn_yes = view1.findViewById(R.id.btn_yes);
                        Button btn_no = view1.findViewById(R.id.btn_no);

                        builder.setView(view1);
                        final AlertDialog dialog = builder.create();
                        btn_yes.setOnClickListener(v ->
                        {
                            LocalSession.clearSession();
                            localSession.lastDtae(date());
                            Intent intent1 = new Intent(Settings.this, Login.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent1);
                            finish();

                        });
                        btn_no.setOnClickListener(v -> dialog.dismiss());
                        dialog.show();
                        break;
                }

            }
        });
    }

    // <-- GetDate Funcation -->
    private String date(){
        Calendar calendar= Calendar.getInstance();
        DateFormat motf = new SimpleDateFormat("dd-MM-yyyy - hh:mm ");
        String date = motf.format(calendar.getTime());
        return date;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

