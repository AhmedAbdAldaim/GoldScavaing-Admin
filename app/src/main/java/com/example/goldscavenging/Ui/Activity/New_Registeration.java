package com.example.goldscavenging.Ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
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
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.goldscavenging.Local_DB.LocalSession;
import com.example.goldscavenging.Model.UsersAddedResponse;
import com.example.goldscavenging.Network.ApiClient;
import com.example.goldscavenging.Network.RequestInterface;
import com.example.goldscavenging.R;
import com.example.goldscavenging.Utilty.Utility;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

public class New_Registeration extends AppCompatActivity {

    EditText ed_name,ed_phone,ed_shop,ed_password,ed_password_confirm;
    ImageView imageView_visibilty,imageView_invisibilty;
    Button button_reg;

    private static final String Tag_failure = "failure";
    private static final String TAG_server  = "TAG_server";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registeration);

        //Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_new_registeration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ed_name = findViewById(R.id.name);
        ed_phone = findViewById(R.id.phone);
        ed_shop = findViewById(R.id.shop);
        ed_password = findViewById(R.id.password);
        ed_password_confirm = findViewById(R.id.passwordconfirm);
        imageView_visibilty = findViewById(R.id.visibiltyoff);
        imageView_invisibilty = findViewById(R.id.visibilty);
        button_reg =(Button)findViewById(R.id.btn_add);
        ConnectivityManager connectivityManager = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));


        imageView_visibilty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView_visibilty.setVisibility(View.GONE);
                imageView_invisibilty.setVisibility(View.VISIBLE);
                ed_password_confirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        });

        imageView_invisibilty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView_invisibilty.setVisibility(View.GONE);
                imageView_visibilty.setVisibility(View.VISIBLE);
                ed_password_confirm.setTransformationMethod(PasswordTransformationMethod.getInstance());


            }
        });

        //      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        ed_password_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(view.getId()==ed_password_confirm.getId())
                {
                    ed_password_confirm.setCursorVisible(true);
                }
            }
        });
        ed_password_confirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                ed_password_confirm.setCursorVisible(false);
                if(event !=null &&(event.getKeyCode()==KeyEvent.KEYCODE_ENTER)){
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(ed_password_confirm.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });


      // <-- Onclick Register Button -->
        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ed_name.getText().toString().trim();
                String phone = ed_phone.getText().toString().trim();
                String shop = ed_shop.getText().toString().trim();
                String password = ed_password.getText().toString().trim();
                String password_confrim = ed_password_confirm.getText().toString().trim();


                    if (Valided(name,phone,shop,password,password_confrim))
                    {
                        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected())
                        {
                            //<--Hidden Keyboard
                            InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            if(inputMethodManager.isAcceptingText())
                            {
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                            NewRegisteration(name,phone,shop,password);
                        }
                        else
                        {
                            Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet), New_Registeration.this);
                        }
                    }

                }
           });
       }



    //<--   Check Fields Function -->
    public Boolean Valided(String name,String phone,String shop,String password,String password_confrim){
        if(name.isEmpty()){
            ed_name.setError(getResources().getString(R.string.fullname_new_registeration_empty));
            ed_name.requestFocus();
            return false;
        }else if(!name.matches("[A-Z, ,a-z, ,ا-ي, ,ء-ي, ,أ-ي, ,آ-ئ]*")){
            ed_name.setError(getString(R.string.fullname_new_registeration_valid));
            ed_name.requestFocus();
            return false;
        }
        else if(name.length() <4){
            ed_name.setError(getString(R.string.fullname_new_registeration_empty));
            ed_name.requestFocus();
            return false;
        }
        else if(name.length()>40){
            ed_name.setError(getResources().getString(R.string.check_fullname_length_new_registeration));
            ed_name.requestFocus();
            return false;
        }

        if(phone.isEmpty()){
            ed_phone.setError(getResources().getString(R.string.phone_new_registeration_empty));
            ed_phone.requestFocus();
            return false;
        }else if(!phone.matches("[0-9]{10}"))
        {
            ed_phone.setError(getString(R.string.phone_new_registeration_valid));
            ed_phone.requestFocus();
            return false;
        }

        if(shop.isEmpty()){
            ed_shop.setError(getResources().getString(R.string.shop_new_registeration_empty));
            ed_shop.requestFocus();
            return false;
        }else if(!shop.matches("[A-Z, ,a-z, ,ا-ي, ,ء-ي, ,أ-ي, ,آ-ئ]*")){
            ed_shop.setError(getString(R.string.shop_new_registeration_valid));
            ed_shop.requestFocus();
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

        if (password_confrim.isEmpty())
        {
            ed_password_confirm.setError(getString(R.string.password_confirm_new_registeration_empty));
            ed_password_confirm.requestFocus();
            return false;
        }
        else if (!password_confrim.equals(password))
        {
            ed_password_confirm.setError(getString(R.string.password_similarity));
            ed_password_confirm.requestFocus();
            return false;
        }
        return true;
    }


    // <-- Send Data TO request And Git Response Status
    private void NewRegisteration(String name,String phone,String shop,String password){
        ProgressDialog loading = ProgressDialog.show(this,null,getString(R.string.wait), false, false);
        loading.setContentView(R.layout.progressbar);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        final RequestInterface requestInterface = ApiClient.getClient(ApiClient.BASE_URL).create(RequestInterface.class);
        Call<UsersAddedResponse> call= requestInterface.NewRegisteration(name,phone,shop,password, "Bearer "+LocalSession.getToken());
        call.enqueue(new Callback<UsersAddedResponse>() {
            @Override
            public void onResponse(Call<UsersAddedResponse> call, Response<UsersAddedResponse> response)
            {
                if(response.isSuccessful())
                {

                        ed_name.setText("");
                        ed_phone.setText("");
                        ed_shop.setText("");
                        ed_password.setText("");
                        ed_password_confirm.setText("");
                        loading.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(New_Registeration.this);
                        LayoutInflater layoutInflater = LayoutInflater.from(New_Registeration.this);
                        View view = layoutInflater.inflate(R.layout.successfuladd,null);
                        final TextView ok = view.findViewById(R.id.ok);
                        final Button button = view.findViewById(R.id.btn_ok);
                         builder.setView(view);
                         AlertDialog alertDialog = builder.create();
                         alertDialog.show();
                        button.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                alertDialog.dismiss();
                            }
                        });

                    }
                    else
                    {
                        loading.dismiss();
                        Log.i(TAG_server, response.errorBody().toString());
                        Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), New_Registeration.this);
                    }

            }

            @Override
            public void onFailure(Call<UsersAddedResponse> call, Throwable t) {
                loading.dismiss();
                Utility.showAlertDialog(getString(R.string.error),getString(R.string.connect_internet_slow),New_Registeration.this);
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
