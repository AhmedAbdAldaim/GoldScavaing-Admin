package com.example.goldscavenging.Ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goldscavenging.Local_DB.LocalSession;
import com.example.goldscavenging.Model.EditProfileResponse;
import com.example.goldscavenging.Network.ApiClient;
import com.example.goldscavenging.Network.RequestInterface;
import com.example.goldscavenging.R;
import com.example.goldscavenging.Utilty.Utility;

public class Edit_Profile extends AppCompatActivity  {
    EditText ed_name,ed_phone,ed_oldpassword;
    Button button_upd;
    TextView Tv_editpassword;
    LocalSession localSession;
    private static final String TAG_server = "Server";
    private static final String Tag_failure = "failure";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        localSession = new LocalSession(this);
        ed_name = findViewById(R.id.name);
        ed_phone = findViewById(R.id.phone);
        ed_oldpassword = findViewById(R.id.oldpassword);
        Tv_editpassword = findViewById(R.id.editpassword);

//        ed_newpassword = findViewById(R.id.newpassword);
//        ed_passwordconfirm = findViewById(R.id.newpasswordconfirm);
        button_upd = findViewById(R.id.btn_upd);
        ConnectivityManager connectivityManager = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));

        ed_name.setText(localSession.getName());
        ed_phone.setText(localSession.getPhone());
        ed_oldpassword.setText(localSession.getPassword());


        // <-- Edit Password Button -->
         Tv_editpassword.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 startActivity(new Intent(Edit_Profile.this, Check_EditProfile_Password.class));
             }
         });

        //      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        ed_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(view.getId()==ed_phone.getId())
                {
                    ed_phone.setCursorVisible(true);
                }
            }
        });
        ed_phone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                ed_phone.setCursorVisible(false);
                if(event !=null &&(event.getKeyCode()==KeyEvent.KEYCODE_ENTER)){
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(ed_phone.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });


         // <-- Onclick Update Button -->
        button_upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ed_name.getText().toString().trim();
                String phone = ed_phone.getText().toString().trim();
                //String oldpassword = ed_oldpassword.getText().toString().trim();
               // String newpassword = ed_newpassword.getText().toString().trim();
                //String passwordconfrim = ed_passwordconfirm.getText().toString().trim();

                if (Valided(name, phone))
                    {
                        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected())
                        {
                            //<--Hidden Keyboard
                            InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            if(inputMethodManager.isAcceptingText())
                            {
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            }
//                            if (oldpassword.isEmpty())
//                            {
                                EditProfile(name, phone,ed_oldpassword.getText().toString());
//                            }
//                            else if (!newpassword.isEmpty() && !passwordconfrim.isEmpty())
//                            {

//                                if(!oldpassword.equals(localSession.getPassword()))
//                                {
//                                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.oldpassword_edit_profile_wrong), Edit_Profile.this);
//                                }
//                                else
//                                {
//                                    EditProfile(name, phone,newpassword);
//                                }
                            //}
                        }
                        else
                        {
                            Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet), Edit_Profile.this);
                        }
                    }
                }
           });
        }


    //<--   Check Fields Function -->
    public Boolean Valided(String name,String phone){
        if(name.isEmpty()){
            ed_name.setError(getResources().getString(R.string.fullname_edit_profile_empty));
            ed_name.requestFocus();
            return false;
        } else if(name.length() <4){
            ed_name.setError(getString(R.string.fullname_edit_profile_empty));
            ed_name.requestFocus();
            return false;
        }
        else if(name.length()>40){
            ed_name.setError(getResources().getString(R.string.check_fullname_length_edit_profile));
            ed_name.requestFocus();
            return false;
        }

        if(phone.isEmpty()){
            ed_phone.setError(getResources().getString(R.string.phone_edit_profile_empty));
            ed_phone.requestFocus();
            return false;
        }else if(!phone.matches("[0-9]{10}"))
        {
            ed_phone.setError(getString(R.string.phone_edit_profile_valid));
            ed_phone.requestFocus();
            return false;
        }

        return true;
    }


    // <--  Send Data TO request And Git Response Status -->
    private void EditProfile(String name,String phone,String password){
        ProgressDialog loading = ProgressDialog.show(this,null,getString(R.string.wait), false, false);
        loading.setContentView(R.layout.progressbar);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        final RequestInterface requestInterface = ApiClient.getClient(ApiClient.BASE_URL).create(RequestInterface.class);
        Call<EditProfileResponse> call= requestInterface.EditProfile(name,phone,password,"Bearer "+LocalSession.getToken());
        call.enqueue(new Callback<EditProfileResponse>() {
            @Override
            public void onResponse(Call<EditProfileResponse> call, Response<EditProfileResponse> response){
                 if(response.isSuccessful())
                 {
                     localSession.createSession(
                             LocalSession.getToken(),
                             response.body().getEditProfileModel().getId(),
                             response.body().getEditProfileModel().getName(),
                             response.body().getEditProfileModel().getPhone(),
                             response.body().getEditProfileModel().getShop(),
                             response.body().getEditProfileModel().getRole(),
                             ed_oldpassword.getText().toString());

                     Toast.makeText(Edit_Profile.this, R.string.updatesuccssfl, Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(Edit_Profile.this, MainActivity.class);
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                     startActivity(intent);
                     finish();
                 }
                 else
                  {
                     loading.dismiss();
                     Log.i(TAG_server, response.errorBody().toString());
                     Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), Edit_Profile.this);
                  }
                }

            @Override
            public void onFailure(Call<EditProfileResponse> call, Throwable t) {
                loading.dismiss();
                Utility.showAlertDialog(getString(R.string.error),getString(R.string.connect_internet_slow),Edit_Profile.this);
                Utility.printLog(Tag_failure, t.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
