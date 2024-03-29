package com.cfd.map.mohit.locationalarm.locationalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cfd.map.mohit.locationalarm.locationalarm.R;

import java.util.ArrayList;

/**
 * Created by Mohit on 2/2/2017.
 */

public class GeoAlarmAdapter extends RecyclerView.Adapter<GeoAlarmAdapter.AlarmHolder> {
    private Context ctx;
    private ArrayList<GeoAlarm> mAlarms;
    private static MyClickListener myClickListener;

    public GeoAlarmAdapter(ArrayList<GeoAlarm> myAlarms) {
        mAlarms = myAlarms;
    }

    public static class AlarmHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //declare view items for each view in alarm_item.xml
        TextView alarmName, alarmLocation, alarmRingtone, alarmRange;
        CheckBox alarmVibration;
        Button alarmDelete;
        ToggleButton alarmSwitch;


        public AlarmHolder(View v) {
            super(v);

            //find each view in the alarm_item custom row
            alarmName = (TextView) v.findViewById(R.id.alarm_name);
            alarmLocation = (TextView) v.findViewById(R.id.alarm_location);
            alarmRingtone = (TextView) v.findViewById(R.id.alarm_ringtone);
            alarmVibration = (CheckBox) v.findViewById(R.id.alarm_vibration);
            alarmRange = (TextView) v.findViewById(R.id.show_range);
            alarmDelete = (Button) v.findViewById(R.id.alarm_delete);
            alarmSwitch = (ToggleButton) v.findViewById(R.id.on_off);


            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myClickListener.onItemClick(getLayoutPosition(), view);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }


    @Override
    public GeoAlarmAdapter.AlarmHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_item, parent, false);
        this.ctx = parent.getContext();
        AlarmHolder movieHolder = new AlarmHolder(view);
        return movieHolder;
    }

    @Override
    public void onBindViewHolder(GeoAlarmAdapter.AlarmHolder holder, final int position) {

        //Set values to the alarm_item layout (custom row)
        holder.alarmName.setText(mAlarms.get(position).getName());
        holder.alarmLocation.setText(mAlarms.get(position).getLocationCoordinate());
        holder.alarmRingtone.setText(mAlarms.get(position).getRingtoneName());
        holder.alarmVibration.setChecked(mAlarms.get(position).getVibration());
        holder.alarmRange.setText("" + mAlarms.get(position).getRadius());
        holder.alarmSwitch.setChecked(mAlarms.get(position).getStatus());
        holder.alarmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.alarmDatabase.delete(mAlarms.get(position).getmId());
                deleteItem(position);
                ctx.stopService(new Intent(ctx,GeoService.class));
                ctx.startService(new Intent(ctx,GeoService.class));


            }
        });
        holder.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAlarms.get(position).setStatus(isChecked);

                } else {

                    mAlarms.get(position).setStatus(isChecked);

                }
                MainActivity.alarmDatabase.updateData(mAlarms.get(position));
                ctx.stopService(new Intent(ctx,GeoService.class));
                ctx.startService(new Intent(ctx,GeoService.class));
            }
        });
        holder.alarmVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    mAlarms.get(position).setVibration(isChecked);

                } else {

                    mAlarms.get(position).setVibration(isChecked);

                }
                MainActivity.alarmDatabase.updateData(mAlarms.get(position));

            }
        });

    }

    @Override
    public int getItemCount() {

        if (mAlarms == null) {
            return 0;
        } else {
            return mAlarms.size();
        }
    }

    public interface MyClickListener {
         void onItemClick(int position, View v);

    }

    void addItem(int index) {
        notifyItemInserted(index);
    }

    void deleteItem(int index) {
        mAlarms.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, mAlarms.size());
    }

    void refreshItem(int index) {
        notifyItemChanged(index);
        MainActivity.alarmDatabase.updateData(mAlarms.get(index));
    }
}
