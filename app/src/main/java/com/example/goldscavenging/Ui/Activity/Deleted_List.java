package com.example.goldscavenging.Ui.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.goldscavenging.Local_DB.LocalSession;
import com.example.goldscavenging.Model.UsersShowResponse;
import com.example.goldscavenging.Network.ApiClient;
import com.example.goldscavenging.Network.RequestInterface;
import com.example.goldscavenging.R;
import com.example.goldscavenging.Ui.Adapter.DeletedListShowAdapter;
import com.example.goldscavenging.Utilty.Utility;

public class Deleted_List extends AppCompatActivity {
    EditText edsearh;
    TextView Tv_Users_total,tv_empty,tv_connect;
    Button button_connect;
    RecyclerView recyclerView;
    DeletedListShowAdapter deletedListShowAdapter ;

    private static final String TAG_server = "Server";
    private static final String Tag_failure = "failure";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_list);

        //Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_deleted_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_connect = findViewById(R.id.connection);
        button_connect = findViewById(R.id.btnconnection);
        tv_empty = findViewById(R.id.empty);
        edsearh = findViewById(R.id.search);
        Tv_Users_total = findViewById(R.id.total);
        recyclerView = findViewById(R.id.rectable);
        ConnectivityManager connectivityManager = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE));
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);


        //       <-- SEARCH -->
        edsearh.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    deletedListShowAdapter.getFilter().filter(charSequence);
                }catch (Exception e){ }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    deletedListShowAdapter.getFilter().filter(charSequence);
                }catch (Exception e){ }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        //      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        edsearh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()==edsearh.getId()){
                    edsearh.setCursorVisible(true);
                }
            }
        });
        edsearh.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                edsearh.setCursorVisible(false);
                if(event !=null &&(event.getKeyCode()==KeyEvent.KEYCODE_ENTER)){
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(edsearh.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });



        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected())
        {
            getAllDelted();
        }
        else
        {
            tv_connect.setText(R.string.connect_internet);
            tv_connect.setVisibility(View.VISIBLE);
            button_connect.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            tv_empty.setVisibility(View.INVISIBLE);
            button_connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv_connect.setVisibility(View.INVISIBLE);
                    button_connect.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    getAllDelted();
                }
            });
        }
    }


    //<--   Git All Useres -->
    public void getAllDelted(){
        ProgressDialog loading = ProgressDialog.show(this,null,getString(R.string.wait), false, false);
        loading.setContentView(R.layout.progressbar);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);

        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        final RequestInterface requestInterface = ApiClient.getClient(ApiClient.BASE_URL).create(RequestInterface.class);
        Call<UsersShowResponse> call= requestInterface.GetAllDelteted("Bearer "+ LocalSession.getToken());
        call.enqueue(new Callback<UsersShowResponse>() {
            @Override
            public void onResponse(Call<UsersShowResponse> call, Response<UsersShowResponse> response)
            {
                if(response.isSuccessful())
                {
                    deletedListShowAdapter = new DeletedListShowAdapter(response.body().getUsersShowModel(), Deleted_List.this);
                    if (deletedListShowAdapter.getItemCount() == 0 )
                    {
                        loading.dismiss();
                        tv_empty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        tv_empty.setText(R.string.empty);
                        Tv_Users_total.setText(0+"");
                        return;
                    } else if(deletedListShowAdapter.getItemCount()>0){
                        loading.dismiss();
                        deletedListShowAdapter.notifyDataSetChanged();
                        tv_empty.setVisibility(View.INVISIBLE);
                        Tv_Users_total.setText(deletedListShowAdapter.getItemCount()+"");
                        deletedListShowAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(deletedListShowAdapter);
                    }
                }
                else
                {
                    loading.dismiss();
                    Log.i(TAG_server, response.errorBody().toString());
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), Deleted_List.this);
                }
            }

            @Override
            public void onFailure(Call<UsersShowResponse> call, Throwable t) {
                loading.dismiss();
                tv_connect.setText(R.string.connect_internet_slow);
                tv_connect.setVisibility(View.VISIBLE);
                button_connect.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                tv_empty.setVisibility(View.INVISIBLE);
                button_connect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tv_connect.setVisibility(View.INVISIBLE);
                        button_connect.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        getAllDelted();
                    }
                });
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
