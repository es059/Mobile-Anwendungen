package com.workout.log.listAdapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.workoutlog.R;
import com.nhaarman.listviewanimations.itemmanipulation.ExpandableListItemAdapter;

public class DailyStatisticListAdapter extends ExpandableListItemAdapter<String> {

    private Context mContext;
    private LayoutInflater layoutInflater;

    /*
     * This will create a new ExpandableListItemAdapter, providing a custom layout resource, 
     * and the two child ViewGroups' id's. If you don't want this, just pass either just the
     * Context, or the Context and the List<T> up to super.
     */
    public DailyStatisticListAdapter(Context context, List<String> items) {
        super(context, R.layout.activity_expandablelistitem_card, R.id.activity_expandablelistitem_card_title, R.id.activity_expandablelistitem_card_content, items);
        mContext = context;     
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getTitleView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.statistic_header, null);
        }
        TextView set = (TextView) v.findViewById(R.id.listview_exericse_header_text);
        set.setText("Headline 1");
        
        return v;
    }

    @Override
    public View getContentView(int position, View convertView, ViewGroup parent) {
		  View v = convertView;
		  if (v == null) {
	          v = layoutInflater.inflate(R.layout.statistic_item, null);
	      }


	    TextView set = (TextView) v.findViewById(R.id.specific_set);
        set.setText("Content 1");
        
        return v;
    } 
}
