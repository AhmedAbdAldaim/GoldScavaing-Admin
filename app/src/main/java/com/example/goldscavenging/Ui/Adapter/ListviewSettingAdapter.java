package com.example.goldscavenging.Ui.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.goldscavenging.Model.ListviewSettingModel;
import com.example.goldscavenging.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListviewSettingAdapter extends ArrayAdapter<ListviewSettingModel> {
    private Context context;
    private int res;
    public ListviewSettingAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ListviewSettingModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.res = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(res,parent,false);
        TextView textView = view.findViewById(R.id.tilte);
        ImageView imageView = view.findViewById(R.id.image);

        textView.setText(getItem(position).getTitle());
        imageView.setImageResource(getItem(position).getImage());

        return view;
    }
}
