package com.example.goldscavenging.Ui.Activity;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goldscavenging.Local_DB.LocalSession;
import com.example.goldscavenging.Model.LoginResponse;
import com.example.goldscavenging.Network.ApiClient;
import com.example.goldscavenging.Network.RequestInterface;
import com.example.goldscavenging.R;
import com.example.goldscavenging.Utilty.Utility;


public class Login extends AppCompatActivity {

    EditText ed_phone,ed_password;
    Button button_login;
    ImageView imageView_visibilty,imageView_invisibilty;
    LocalSession localSession;

    private static final String Tag_failure = "failure";
    private static final String TAG_server  = "TAG_server";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localSession = new LocalSession(Login.this);
        ed_phone = findViewById(R.id.phone);
        ed_password  = findViewById(R.id.password);
        imageView_visibilty = findViewById(R.id.visibiltyoff);
        imageView_invisibilty = findViewById(R.id.visibilty);
        button_login = findViewById(R.id.btn_login);
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

        //WHEN CHANG PASSWORD FROM RESETPASWORD ACTIVITY
        Intent intent = getIntent();
        String phone_num = intent.getStringExtra("phone_number");
        String new_password = intent.getStringExtra("newpass");
        ed_phone.setText(phone_num);
        ed_password.setText(new_password);

        //      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        ed_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(view.getId()==ed_password.getId())
                {
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
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = ed_phone.getText().toString().trim();
                String password = ed_password.getText().toString().trim();


                    if (Valided(phone,password))
                    {
                        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected())
                        {
                        //<--Hidden Keyboard
                        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(inputMethodManager.isAcceptingText())
                        {
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        Login(phone,password);
                    } else
                        {
                            Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet), Login.this);
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
            ed_password.setError(getString(R.string.password_new_registeration_empty));
            ed_password.requestFocus();
            return false;
        }
        else if(password.length() <8)
        {
            ed_password.setError(getString(R.string.password_new_registeration_valid));
            ed_password.requestFocus();
            return false;
        }

        return true;
    }


    // <-- Send Data TO request And Git Response Status
    private void Login(String phone,String password){
        ProgressDialog loading = ProgressDialog.show(this,null,getString(R.string.wait), false, false);
        loading.setContentView(R.layout.progressbar);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        final RequestInterface requestInterface = ApiClient.getClient(ApiClient.BASE_URL).create(RequestInterface.class);
        Call<LoginResponse> call= requestInterface.Login(phone,password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response)
            {
                if(response.isSuccessful()) {
                    if (!response.body().isError())
                    {
                        localSession.createSession(
                                response.body().getToken(),
                                response.body().getLoginModel().getId(),
                                response.body().getLoginModel().getName(),
                                response.body().getLoginModel().getPhone(),
                                response.body().getLoginModel().getShop(),
                                response.body().getLoginModel().getRole(),
                                ed_password.getText().toString());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        loading.dismiss();
                        if(ed_phone.getHint().toString().contains("رقم الهاتف")||ed_password.getHint().toString().contains("كلمة المرور")) {
                            Utility.showAlertDialog(getString(R.string.error), response.body().getMessage_ar(), Login.this);
                        }
                       else if(ed_phone.getHint().toString().contains("Phone")||ed_password.getHint().toString().contains("Password")){
                            Utility.showAlertDialog(getString(R.string.error), response.body().getMessage_en(), Login.this);
                        }

                    }
                }
                else {
                        loading.dismiss();
                        Log.i(TAG_server, response.errorBody().toString());
                        Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), Login.this);
                     }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loading.dismiss();
                Utility.showAlertDialog(getString(R.string.error),getString(R.string.connect_internet_slow),Login.this);
                Utility.printLog(Tag_failure, t.getMessage());
            }
        });

    }
    public void forgetpassword(View view) {
        startActivity(new Intent(Login.this, SendOtp.class));
    }

}
