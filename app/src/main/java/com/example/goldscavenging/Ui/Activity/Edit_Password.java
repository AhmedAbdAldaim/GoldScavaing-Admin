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
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goldscavenging.Local_DB.LocalSession;
import com.example.goldscavenging.Model.EditProfileResponse;
import com.example.goldscavenging.Network.ApiClient;
import com.example.goldscavenging.Network.RequestInterface;
import com.example.goldscavenging.R;
import com.example.goldscavenging.Utilty.Utility;

public class Edit_Password extends AppCompatActivity {

    EditText ed_name,ed_phone,ed_newpassword,ed_passwordconfirm;
    Button button_upd;
    ImageView imageView_visibilty,imageView_invisibilty;
    LocalSession localSession;

    private static final String TAG_server = "Server";
    private static final String Tag_failure = "failure";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__password);

        //Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_check_edit_profile_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ed_name = findViewById(R.id.name);
        ed_phone = findViewById(R.id.phone);
        ed_newpassword = findViewById(R.id.newpassword);
        ed_passwordconfirm = findViewById(R.id.newpasswordconfirm);
        imageView_visibilty = findViewById(R.id.visibiltyoff);
        imageView_invisibilty = findViewById(R.id.visibilty);
        button_upd = findViewById(R.id.btn_upd);

        localSession = new LocalSession(this);
        ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));


        imageView_visibilty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView_visibilty.setVisibility(View.GONE);
                imageView_invisibilty.setVisibility(View.VISIBLE);
                ed_passwordconfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        });

        imageView_invisibilty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView_invisibilty.setVisibility(View.GONE);
                imageView_visibilty.setVisibility(View.VISIBLE);
                ed_passwordconfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());


            }
        });

        ed_name.setText(localSession.getName());
        ed_phone.setText(localSession.getPhone());


        //      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        ed_passwordconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == ed_passwordconfirm.getId()) {
                    ed_passwordconfirm.setCursorVisible(true);
                }
            }
        });
        ed_passwordconfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                ed_passwordconfirm.setCursorVisible(false);
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(ed_passwordconfirm.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
                String newpassword = ed_newpassword.getText().toString().trim();
                String passwordconfrim = ed_passwordconfirm.getText().toString().trim();

                if (Valided(newpassword, passwordconfrim))
                {
                    if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
                        //<--Hidden Keyboard
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputMethodManager.isAcceptingText()) {
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }

                        EditProfile_Password(name, phone, ed_newpassword.getText().toString());
                    }
                    else
                    {
                        Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet), Edit_Password.this);
                    }
                }
            }
        });
    }


    //<--   Check Fields Function -->
    public Boolean Valided(String newPassword,String passwordconfirm){

        if (newPassword.isEmpty())
        {
            ed_newpassword.setError(getString(R.string.newpassword_edit_profile_empty));
            ed_newpassword.requestFocus();
            return false;
        }else if(newPassword.length() <8)
        {
            ed_newpassword.setError(getString(R.string.password_check));
            ed_newpassword.requestFocus();
            return false;
        }
        if(passwordconfirm.isEmpty()){
            ed_passwordconfirm.setError(getString(R.string.password_confirm_edit_profile_empty));
            ed_passwordconfirm.requestFocus();
            return false;
        }
        else if (!newPassword.isEmpty()&&!passwordconfirm.equals(newPassword))
        {
            ed_passwordconfirm.setError(getString(R.string.password_similarity_edit_profile));
            ed_passwordconfirm.requestFocus();
            return false;
        }

        return true;

    }



    // <--  Send Data TO request And Git Response Status -->
    private void EditProfile_Password(String name,String phone,String password){
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
                            ed_newpassword.getText().toString());
                    Toast.makeText(Edit_Password.this, R.string.updatesuccssfl, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Edit_Password.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    loading.dismiss();
                    Log.i(TAG_server, response.errorBody().toString());
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), Edit_Password.this);
                }
            }

            @Override
            public void onFailure(Call<EditProfileResponse> call, Throwable t) {
                loading.dismiss();
                Utility.showAlertDialog(getString(R.string.error),getString(R.string.connect_internet_slow),Edit_Password.this);
                Utility.printLog(Tag_failure, t.getMessage());
            }
        });
    }


        @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            startActivity(new Intent(Edit_Password.this,MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Edit_Password.this,MainActivity.class));
        finish();
        super.onBackPressed();
    }
}
