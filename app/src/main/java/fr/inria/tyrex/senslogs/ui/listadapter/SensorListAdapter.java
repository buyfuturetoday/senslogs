package fr.inria.tyrex.senslogs.ui.listadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.inria.tyrex.senslogs.R;
import fr.inria.tyrex.senslogs.control.SensorsPreferences;
import fr.inria.tyrex.senslogs.model.Sensor;

/**
 * Adapter for Sensor List using categories
 */
public class SensorListAdapter extends BaseExpandableListAdapter {

    private List<Group> mGroups;
    private Context mContext;
    private SensorsPreferences mSensorsPreferences;
    private SettingsClickListener mSettingsClickListener;
    private CheckboxClickListener mCheckboxClickListener;

    public SensorListAdapter(Context context, List<Group> groups, SensorsPreferences sensorsPreferences) {
        mContext = context;
        mSensorsPreferences = sensorsPreferences;
        mGroups = groups;
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).sensors.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).sensors.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.sensor_list_category, null);
        }
        Group group = (Group) getGroup(groupPosition);
        ((TextView) convertView).setText(group.category.getName(mContext));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.sensor_list_item, null);
        }
        Group group = (Group) getGroup(groupPosition);
        final Sensor sensor = group.sensors.get(childPosition);

        final CheckBox checkBoxItem = (CheckBox) convertView.findViewById(R.id.checkbox_item);
        TextView sensorNameTextView = (TextView) convertView.findViewById(R.id.sensor_name);
        TextView sensorTypeTextView = (TextView) convertView.findViewById(R.id.sensor_type);
        ImageButton settingsImageView = (ImageButton) convertView.findViewById(R.id.settings);

        sensorNameTextView.setText(sensor.getName());
        sensorTypeTextView.setText(sensor.getStringType());
        settingsImageView.setVisibility(sensor.hasSettings() ?
                View.VISIBLE : View.GONE);

        if(sensor.getName().equals(sensor.getStringType()) ||
                sensor.getStringType() == null) {
            sensorTypeTextView.setVisibility(View.GONE);
        } else {
            sensorTypeTextView.setVisibility(View.VISIBLE);
        }

        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSettingsClickListener != null) {
                    mSettingsClickListener.onSettingsClick(sensor);
                }
            }
        });

        // Not use setOnCheckedChangeListener() because it's called too often
        checkBoxItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckboxClickListener != null) {
                    mCheckboxClickListener.onCheckboxClick(sensor, checkBoxItem.isChecked());
                }
            }
        });

        checkBoxItem.setChecked(mSensorsPreferences.isChecked(sensor));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public static class Group {

        public Sensor.Category category;
        public final List<Sensor> sensors = new ArrayList<>();

        public Group(Sensor.Category category) {
            this.category = category;
        }
    }


    public void setOnSettingsClickListener(SettingsClickListener listener) {
        mSettingsClickListener = listener;
    }

    public interface SettingsClickListener {
        void onSettingsClick(Sensor sensor);
    }


    public void setOnCheckboxClickListener(CheckboxClickListener listener) {
        mCheckboxClickListener = listener;
    }

    public interface CheckboxClickListener {
        void onCheckboxClick(Sensor sensor, boolean isChecked);
    }
}