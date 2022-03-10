package com.example.goldscavenging.Ui.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.goldscavenging.Local_DB.LocalSession;
import com.example.goldscavenging.Model.RestpasswordResponse;
import com.example.goldscavenging.Network.ApiClient;
import com.example.goldscavenging.Network.RequestInterface;
import com.example.goldscavenging.R;
import com.example.goldscavenging.Utilty.Utility;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reset_Password extends AppCompatActivity {

    public EditText ed_phone,ed_password;
    Button button_Resetpassword;
    ImageView imageView_visibilty,imageView_invisibilty;
    public String phone,password;
    LocalSession localSession;

    private static final String TAG_server = "Server";
    private static final String Tag_failure = "failure";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset__password);

        localSession = new LocalSession(Reset_Password.this);
        ed_phone = findViewById(R.id.phone);
        ed_password = findViewById(R.id.password);
        imageView_visibilty = findViewById(R.id.visibiltyoff);
        imageView_invisibilty = findViewById(R.id.visibilty);
        button_Resetpassword =(Button)findViewById(R.id.btn_login);

        ConnectivityManager connectivityManager = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));

        imageView_visibilty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView_visibilty.setVisibility(View.GONE);
                imageView_invisibilty.setVisibility(View.VISIBLE);
                ed_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        });

        imageView_invisibilty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView_invisibilty.setVisibility(View.GONE);
                imageView_visibilty.setVisibility(View.VISIBLE);
                ed_password.setTransformationMethod(PasswordTransformationMethod.getInstance());


            }
        });

        //<---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        ed_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==ed_password.getId()){
                    ed_password.setCursorVisible(true);
                }
            }
        });
        ed_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                ed_password.setCursorVisible(false);
                if(event !=null &&(event.getKeyCode()==KeyEvent.KEYCODE_ENTER)){
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(ed_password.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });


        //  <-- Onclick Register Button-->
        button_Resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                phone = ed_phone.getText().toString().trim();
                password = ed_password.getText().toString().trim();

                    if(Valided(phone,password))
                    {
                        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected())
                        {

                        //<--Hidden Keyboard
                        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                        RestPassword(phone, password);
                        }
                        else
                        {
                            Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet), Reset_Password.this);
                        }
                    }
                }
            });
        }


    //<--   Check Fields Function -->
    public Boolean Valided(String phone,String password){
        if(phone.isEmpty()){
            ed_phone.setError(getResources().getString(R.string.phone_empty));
            ed_phone.requestFocus();

            return false;
        }else if(!phone.matches("[0-9]{10}"))
        {
            ed_phone.setError(getString(R.string.phone_valid));
            ed_phone.requestFocus();
            return false;
        }

        if (password.isEmpty())
        {
            ed_password.setError(getString(R.string.password_empty));
            ed_password.requestFocus();
            recreate();
            return false;
        } else if(password.length() <8)
        {
            ed_password.setError(getString(R.string.password_check));
            ed_password.requestFocus();
            recreate();
            return false;
        }

        return true;
    }



    // <-- Send Data TO request And Git Response Status
    public void RestPassword(String phone,String password){
        ProgressDialog loading = ProgressDialog.show(this,null,getString(R.string.wait), false, false);
        loading.setContentView(R.layout.progressbar);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);


        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        final RequestInterface requestInterface = ApiClient.getClient(ApiClient.BASE_URL).create(RequestInterface.class);
        Call<RestpasswordResponse> call= requestInterface.RestPassword(phone,password);
        call.enqueue(new Callback<RestpasswordResponse>() {
            @Override
            public void onResponse(Call<RestpasswordResponse> call, Response<RestpasswordResponse> response) {
                if(response.isSuccessful())
                {
                    if(!response.body().isError())
                    {
                        loading.dismiss();
                        Toast.makeText(Reset_Password.this, R.string.recovercount, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Reset_Password.this, Login.class);
                        intent.putExtra("phone_number",phone);
                        intent.putExtra("newpass",password);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        loading.dismiss();
                        SharedPreferences sharedPreferences = getSharedPreferences("langdb", Context.MODE_PRIVATE);
                        String lang = sharedPreferences.getString("lang", "ar");

                        if(lang.equals("ar")) {
                            Utility.showAlertDialog(getString(R.string.error), response.body().getMessage_ar(), Reset_Password.this);
                        }else if(lang.equals("en")){
                            Utility.showAlertDialog(getString(R.string.error),  response.body().getMessage_en(), Reset_Password.this);
                        }
                    }
                }
                else
                {
                    loading.dismiss();
                    Log.i(TAG_server, response.errorBody().toString());
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), Reset_Password.this);

                }
            }

            @Override
            public void onFailure(Call<RestpasswordResponse> call, Throwable t) {
                loading.dismiss();
                Utility.showAlertDialog(getString(R.string.error),getString(R.string.connect_internet_slow),Reset_Password.this);
                Utility.printLog(Tag_failure, t.getMessage());
            }
        });
    }
}
