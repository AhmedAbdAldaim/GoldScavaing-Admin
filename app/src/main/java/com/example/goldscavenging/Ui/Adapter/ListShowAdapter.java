package com.example.goldscavenging.Ui.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goldscavenging.Local_DB.LocalSession;
import com.example.goldscavenging.Model.DeleteResponse;
import com.example.goldscavenging.Model.UsersSatausResponse;
import com.example.goldscavenging.Model.UsersShowModel;
import com.example.goldscavenging.Network.ApiClient;
import com.example.goldscavenging.Network.RequestInterface;
import com.example.goldscavenging.R;
import com.example.goldscavenging.Ui.Activity.Edit_Profile;
import com.example.goldscavenging.Ui.Activity.ListShow;
import com.example.goldscavenging.Ui.Activity.New_Registeration;
import com.example.goldscavenging.Utilty.Utility;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("unchecked")
public class ListShowAdapter extends RecyclerView.Adapter<ListShowAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<UsersShowModel> list;
    private List<UsersShowModel> filter;

    public ListShowAdapter(List<UsersShowModel> usersShowModels, Context context)
    {
        this.context = context;
        this.list = usersShowModels;
        filter = new ArrayList<>(usersShowModels);
    }

    @NonNull
    @Override
    public ListShowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.custom_list_show, parent, false);
        return new ListShowAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListShowAdapter.ViewHolder holder, int position) {
        holder.Tv_name.setText(list.get(position).getName());
        holder.Tv_phone.setText(list.get(position).getPhone());
        holder.Tv_shop.setText(list.get(position).getShop());
        holder.Tv_date.setText(list.get(position).getAdded_at());


        // <-- Check Active Or NonActive To Users -->
        holder.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.connectivityManager.getActiveNetworkInfo() != null && holder.connectivityManager.getActiveNetworkInfo().isConnected())
                {
                    if (holder.aSwitch.isChecked()) {
                        holder.Tv_status.setText("active");
                    } else if (!holder.aSwitch.isChecked()) {
                        holder.Tv_status.setText("nonactive");
                    }
                    ProgressDialog loading = ProgressDialog.show(context, null, context.getString(R.string.wait), false, false);
                    loading.setContentView(R.layout.progressbar);
                    loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    loading.setCancelable(false);
                    loading.setCanceledOnTouchOutside(false);

                    String status = holder.Tv_status.getText().toString();
                    final RequestInterface requestInterface = ApiClient.getClient(ApiClient.BASE_URL).create(RequestInterface.class);
                    Call<UsersSatausResponse> call = requestInterface.status(list.get(position).getId(), status, "Bearer " + LocalSession.getToken());
                    call.enqueue(new Callback<UsersSatausResponse>() {
                        @Override
                        public void onResponse(Call<UsersSatausResponse> call, Response<UsersSatausResponse> response)
                        {
                            if (response.isSuccessful())
                            {
                                if (holder.aSwitch.isChecked())
                                {
                                    loading.dismiss();
                                    holder.Tv_status.setBackground(context.getDrawable(R.color.greann));
                                    Snackbar.make(view, context.getResources().getString(R.string.active_status) + " " + list.get(position).getName(), Snackbar.LENGTH_SHORT).show();
                                }
                                else if (!holder.aSwitch.isChecked())
                                {
                                    loading.dismiss();
                                    holder.Tv_status.setBackground(context.getDrawable(R.color.red));
                                    Snackbar.make(view, context.getResources().getString(R.string.nonactive_status) + " " + list.get(position).getName(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                loading.dismiss();
                                Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.servererror), context);
                            }
                        }

                        @Override
                        public void onFailure(Call<UsersSatausResponse> call, Throwable t) {
                            loading.dismiss();
                            Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet_slow), context);
                        }

                    });

                }
                else
                {
                    Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet), context);
                    if (holder.aSwitch.isChecked())
                    {
                        holder.aSwitch.setChecked(false);
                    }
                    else if(!holder.aSwitch.isChecked())
                    {
                        holder.aSwitch.setChecked(true);
                    }
                 }
            }
        });



        // <-- Delete To Users -->
        holder.Tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.connectivityManager.getActiveNetworkInfo() != null && holder.connectivityManager.getActiveNetworkInfo().isConnected())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view1 = ((Activity) context).getLayoutInflater().inflate(R.layout.delete_massage, null);
                    TextView hidder = view1.findViewById(R.id.tv_hidder);
                    Button btn_yes = view1.findViewById(R.id.btn_yes);
                    Button btn_no = view1.findViewById(R.id.btn_no);
                    builder.setView(view1);
                    hidder.setText(context.getResources().getString(R.string.user_remove_massage) + " " + list.get(position).getName());
                    final AlertDialog dialog = builder.create();
                    btn_yes.setOnClickListener(v ->
                    {
                        dialog.dismiss();

                        ProgressDialog loading = ProgressDialog.show(context, null, context.getString(R.string.wait), false, false);
                        loading.setContentView(R.layout.progressbar);
                        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        loading.setCancelable(false);
                        loading.setCanceledOnTouchOutside(false);

                        final RequestInterface requestInterface = ApiClient.getClient(ApiClient.BASE_URL).create(RequestInterface.class);
                        Call<DeleteResponse> call = requestInterface.DeleteUser(list.get(position).getId(), "yes","Bearer " + LocalSession.getToken());
                        call.enqueue(new Callback<DeleteResponse>() {
                            @Override
                            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response)
                            {
                                if (response.isSuccessful())
                                {
                                        loading.dismiss();
                                        Toast.makeText(context, context.getResources().getString(R.string.Done_remove_massage) + " " + list.get(position).getName(), Toast.LENGTH_SHORT).show();
                                        list.remove(position);
                                        notifyDataSetChanged();
                                        notifyItemRemoved(position);
                                        ((ListShow) context).getUserShow();

                                }
                                else
                                {
                                    loading.dismiss();
                                    Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.servererror), context);
                                }
                            }

                            @Override
                            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                                loading.dismiss();
                                Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet_slow), context);
                            }

                        });

                    });

                    btn_no.setOnClickListener(v -> dialog.dismiss());
                    dialog.show();
                }
                else
                {
                    Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet), context);
                }
            }
        });



        // <-- Delete To Users -->
            holder.Tv_delete_mac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.connectivityManager.getActiveNetworkInfo() != null && holder.connectivityManager.getActiveNetworkInfo().isConnected())
                {
                        ProgressDialog loading = ProgressDialog.show(context, null, context.getString(R.string.wait), false, false);
                        loading.setContentView(R.layout.progressbar);
                        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        loading.setCancelable(false);
                        loading.setCanceledOnTouchOutside(false);

                        final RequestInterface requestInterface = ApiClient.getClient(ApiClient.BASE_URL).create(RequestInterface.class);
                        Call<DeleteResponse> call = requestInterface.DeleteMAC(list.get(position).getId(), "Bearer " + LocalSession.getToken());
                        call.enqueue(new Callback<DeleteResponse>() {
                            @Override
                            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response)
                            {
                                if (response.isSuccessful())
                                {
                                    loading.dismiss();
                                    Toast.makeText(context, "Successfully MAC Address Deleted" + " " + list.get(position).getName(), Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    loading.dismiss();
                                    Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.servererror), context);
                                }
                            }

                            @Override
                            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                                loading.dismiss();
                                Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet_slow), context);
                            }

                        });

                }
                else
                {
                    Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet), context);
                }
            }
        });





         // <-- Save Switch State -->
        if (list.get(position).getStatus().equals("active"))
        {
            holder.aSwitch.setChecked(true);
            holder.Tv_status.setBackground(context.getDrawable(R.color.greann));
        }
        else
        {
            holder.aSwitch.setChecked(false);
            holder.Tv_status.setBackground(context.getDrawable(R.color.red));
        }
    }



    @Override
    public int getItemCount() {
        return list.size();
    }



    // <-- Search -->
    @Override
    public Filter getFilter() {
        return filterr;
    }

    public Filter filterr = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String key = charSequence.toString();
            List<UsersShowModel> usersShowModels = new ArrayList<>();
            if (key.isEmpty() || key.length() == 0)
            {
                usersShowModels.addAll(filter);
            }
            else
            {
                for (UsersShowModel item : list)
                {
                    if (item.getName().toLowerCase().contains(key)||item.getName().toUpperCase().contains(key)|| item.getPhone().toLowerCase().contains(key))
                    {
                        usersShowModels.add(item);
                    }
                }

            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = usersShowModels;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((Collection<? extends UsersShowModel>) filterResults.values);
            notifyDataSetChanged();
        }
    };



    class ViewHolder extends RecyclerView.ViewHolder {
        TextView Tv_name,Tv_phone,Tv_shop, Tv_date, Tv_status, Tv_delete,Tv_delete_mac;
        Switch aSwitch;
        CardView cardView;
        ConnectivityManager connectivityManager;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Tv_name = itemView.findViewById(R.id.name);
            Tv_phone = itemView.findViewById(R.id.phone);
            Tv_shop = itemView.findViewById(R.id.shop);
            Tv_date = itemView.findViewById(R.id.addeddate);
            aSwitch = itemView.findViewById(R.id.swithc);
            Tv_status = itemView.findViewById(R.id.status);
            Tv_delete = itemView.findViewById(R.id.delete);
            Tv_delete_mac = itemView.findViewById(R.id.delete_mac);
            cardView = itemView.findViewById(R.id.card);
            connectivityManager = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));

        }
    }
}




