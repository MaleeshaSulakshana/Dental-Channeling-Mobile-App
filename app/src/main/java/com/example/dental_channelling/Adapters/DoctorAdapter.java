package com.example.dental_channelling.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dental_channelling.Constructors.Doctors;
import com.example.dental_channelling.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DoctorAdapter extends ArrayAdapter<Doctors> {

    private Context mContext;
    private int mResource;

    public DoctorAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Doctors> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        ImageView profileIcon = convertView.findViewById(R.id.profileIcon);
        TextView doctorName = convertView.findViewById(R.id.doctorName);
        TextView doctorId = convertView.findViewById(R.id.doctorId);
        TextView doctorPosition = convertView.findViewById(R.id.doctorPosition);

        if (!getItem(position).getImageUrl().equals("")){
            Picasso.get().load(getItem(position).getImageUrl()).into(profileIcon);
        }
        doctorName.setText(getItem(position).getDoctorName());
        doctorId.setText(getItem(position).getDoctorId());
        doctorPosition.setText(getItem(position).getDoctorPosition());

        return convertView;
    }

}