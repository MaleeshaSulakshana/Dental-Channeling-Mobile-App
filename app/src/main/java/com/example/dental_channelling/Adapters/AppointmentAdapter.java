package com.example.dental_channelling.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.dental_channelling.Constructors.Appointment;
import com.example.dental_channelling.R;

import java.util.ArrayList;

public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    private Context mContext;
    private int mResource;

    public AppointmentAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Appointment> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView appointmentPatientName = convertView.findViewById(R.id.appointmentPatientName);
        TextView appointmentId = convertView.findViewById(R.id.appointmentId);
        TextView appointmentDate = convertView.findViewById(R.id.appointmentDate);

        appointmentPatientName.setText(getItem(position).getAppointmentPatientName());
        appointmentId.setText(getItem(position).getAppointmentId());
        appointmentDate.setText(getItem(position).getAppointmentDate());

        return convertView;
    }

}