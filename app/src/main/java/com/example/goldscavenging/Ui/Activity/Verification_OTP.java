package com.example.goldscavenging.Ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.goldscavenging.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Verification_OTP extends AppCompatActivity {
    EditText num1,num2,num3,num4,num5,num6;
    TextView textView;
    ProgressBar progressBar;
    TextView textViewresend,second;
    Button button;
    String phone,verify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification__o_t_p);
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        num4 = findViewById(R.id.num4);
        num5 = findViewById(R.id.num5);
        num6 = findViewById(R.id.num6);
        textView = findViewById(R.id.phone);
        Intent intent = getIntent();
        phone = intent.getStringExtra("mobile");
        verify = intent.getStringExtra("verify");
        textView.setText("+249"+phone);

        button= findViewById(R.id.button);
        progressBar = findViewById(R.id.progressbar);
        textViewresend = findViewById(R.id.resend);
        second = findViewById(R.id.second);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(num1.getText().toString().trim().isEmpty()||num2.getText().toString().trim().isEmpty()||
                        num3.getText().toString().trim().isEmpty()||num4.getText().toString().trim().isEmpty()||
                        num5.getText().toString().trim().isEmpty()||num6.getText().toString().trim().isEmpty()){
                    Toast.makeText(Verification_OTP.this, "Enter Code", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = num1.getText().toString()+num2.getText().toString()+num3.getText().toString()+num4.getText().toString()+num5.getText().toString()+num6.getText().toString();
                if(verify !=null){
                    progressBar.setVisibility(View.VISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthProvider = PhoneAuthProvider.getCredential(
                            verify,code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthProvider).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            button.setVisibility(View.VISIBLE);
                            if(task.isSuccessful()){
                                Toast.makeText(Verification_OTP.this, R.string.verificationsuccessfully, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Verification_OTP.this, Reset_Password.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(Verification_OTP.this, R.string.verificationerror, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

        textViewresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewresend.setVisibility(View.GONE);
                second.setVisibility(View.VISIBLE);

                new CountDownTimer(60000,1000){
                    @Override
                    public void onTick(long l) {
                        second.setVisibility(View.VISIBLE);
                        second.setText("Second Remaimber: " + l / 1000);
                    }

                    @Override
                    public void onFinish() {
                        textViewresend.setVisibility(View.VISIBLE);
                        second.setVisibility(View.GONE);
                        PhoneAuthProvider.getInstance().verifyPhoneNumber("+249" + phone, 60, TimeUnit.SECONDS
                                , Verification_OTP.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {

                                        Toast.makeText(Verification_OTP.this, R.string.error, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                        verify = s;
                                        Toast.makeText(Verification_OTP.this, R.string.sentotp, Toast.LENGTH_SHORT).show();
                                        super.onCodeSent(s, forceResendingToken);
                                    }
                                });
                            }
                      }.start();
                   }
               });

        setupOTPinput();
    }

    public void setupOTPinput(){

        num1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    num2.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        num2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    num3.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        num3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    num4.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        num4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    num5.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

         num5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    num6.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
         });
    }
}
