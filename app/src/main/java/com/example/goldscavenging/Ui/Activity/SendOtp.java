package com.example.goldscavenging.Ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goldscavenging.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SendOtp extends AppCompatActivity {
EditText editTextphone;
TextView textViewwatcher;
Button buttonSEND;
ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);
        editTextphone = findViewById(R.id.ed_enterphonenumber);
        buttonSEND= findViewById(R.id.btn_send);
        progressBar = findViewById(R.id.progressbar);

//      <---EditText Counter The Length of CharSequence-->
        textViewwatcher = findViewById(R.id.textwatchercounter);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                textViewwatcher.setText(String.valueOf(charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        editTextphone.addTextChangedListener(textWatcher);

//      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        editTextphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==editTextphone.getId()){
                    editTextphone.setCursorVisible(true);
                }
            }
        });
        editTextphone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                editTextphone.setCursorVisible(false);
                if(event !=null &&(event.getKeyCode()==KeyEvent.KEYCODE_ENTER)){
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(editTextphone.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });


       buttonSEND.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               if (editTextphone.getText().toString().isEmpty()) {
                   editTextphone.setError(getResources().getString(R.string.phone_empty));
                   editTextphone.requestFocus();
                   return;
               } else {
                   progressBar.setVisibility(View.VISIBLE);
                   buttonSEND.setVisibility(View.INVISIBLE);
               PhoneAuthProvider.getInstance().verifyPhoneNumber("+249"+editTextphone.getText().toString(),60, TimeUnit.SECONDS
                       ,SendOtp.this ,new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                           @Override
                           public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                               progressBar.setVisibility(View.GONE);
                               buttonSEND.setVisibility(View.VISIBLE);
                           }

                           @Override
                           public void onVerificationFailed(@NonNull FirebaseException e) {
                               progressBar.setVisibility(View.GONE);
                               buttonSEND.setVisibility(View.VISIBLE);
                               Toast.makeText(SendOtp.this,R.string.error+e.getMessage() , Toast.LENGTH_SHORT).show();
                           }

                           @Override
                           public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                               progressBar.setVisibility(View.GONE);
                               buttonSEND.setVisibility(View.VISIBLE);
                               Intent intent = new Intent(getApplicationContext(),Verification_OTP.class);
                               intent.putExtra("mobile",editTextphone.getText().toString());
                               intent.putExtra("verify",s);
                               startActivity(intent);
                               super.onCodeSent(s, forceResendingToken);
                           }
                       });
                  }
             }
         });
    }
}
