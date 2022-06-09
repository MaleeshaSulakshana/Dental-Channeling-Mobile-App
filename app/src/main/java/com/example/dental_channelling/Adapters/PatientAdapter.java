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

import com.example.dental_channelling.Constructors.Patients;
import com.example.dental_channelling.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PatientAdapter extends ArrayAdapter<Patients> {

    private Context mContext;
    private int mResource;

    public PatientAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Patients> objects) {
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
        TextView patientId = convertView.findViewById(R.id.patientId);
        TextView patientName = convertView.findViewById(R.id.patientName);

        if (!getItem(position).getPatientImage().equals("")){
            Picasso.get().load(getItem(position).getPatientImage()).into(profileIcon);
        }
        patientId.setText(getItem(position).getPatientId());
        patientName.setText(getItem(position).getPatientName());

        return convertView;
    }
}
