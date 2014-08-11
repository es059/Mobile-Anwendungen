package com.workout.log.listAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.workoutlog.R;
import com.nhaarman.listviewanimations.itemmanipulation.ExpandableListItemAdapter;
import com.workout.log.data.StatisticListElement;

public class DailyStatisticListAdapter extends ExpandableListItemAdapter<StatisticListElement> {

    private LayoutInflater layoutInflater;
    private List<StatisticListElement> items;
    private Context context = null;
    
    /**
     * This will create a new ExpandableListItemAdapter, providing a custom layout resource, 
     * and the two child ViewGroups' id's. If you don't want this, just pass either just the
     * Context, or the Context and the List<T> up to super.
     */
    public DailyStatisticListAdapter(Context context, List<StatisticListElement> items) {
        super(context, R.layout.activity_expandablelistitem_card, R.id.activity_expandablelistitem_card_title, R.id.activity_expandablelistitem_card_content, items);   
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.context = context;
        
    }

    @SuppressLint("SimpleDateFormat") 
    @Override
    public View getTitleView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = layoutInflater.inflate(R.layout.statistic_header, null);
        }
        v.setBackgroundColor(Color.parseColor("#FF9900"));
        TextView date = (TextView) v.findViewById(R.id.listview_exericse_header_text);
        TextView set = (TextView) v.findViewById(R.id.header_Set);
        
        SimpleDateFormat sp = new SimpleDateFormat("dd.MM.yyyy");
        Date tempDate = null;
        try {
			tempDate = sp.parse(items.get(position).getTimestamp());
		} catch (ParseException e) {
			e.printStackTrace();
		}
        sp = new SimpleDateFormat ("EEE, MMM d, yyyy");
        
        date.setText(sp.format(tempDate));
        set.setText(items.get(position).getPerformanceActualList().size() + " " + context.getResources().getString((R.string.Set)));
        date.setTextColor(Color.WHITE);
        set.setTextColor(Color.WHITE);
        return v;
    }

    @Override
    public View getContentView(int position, View convertView, ViewGroup parent) {	
    	View v = convertView;
    	if (v == null) {
	      	v = layoutInflater.inflate(R.layout.statistic_item_listview, null);
	      
	      	LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.statistic_layout);
			
			View headerInformation = layoutInflater.inflate(R.layout.daily_statistic_item_header, null);
			linearLayout.addView(headerInformation);
			
			for(int i = 0; i <  items.get(position).getPerformanceActualList().size() ; i++) {
				View tempView = layoutInflater.inflate(R.layout.statistic_item, null);
				
				final TextView set = (TextView) tempView.findViewById(R.id.specific_set);
				final TextView weight = (TextView) tempView.findViewById(R.id.specific_weight);
				final TextView rep = (TextView) tempView.findViewById(R.id.specific_rep);
				
				set.setText(String.valueOf(items.get(position).getPerformanceActualList().get(i).getSet()));
				if (items.get(position).getPerformanceActualList().get(i).getWeight() != -1){
					weight.setText(String.valueOf(items.get(position).getPerformanceActualList().get(i).getWeight()));
				}else{
					weight.setText("-");
				}
				
				if (items.get(position).getPerformanceActualList().get(i).getRepetition() != -1){
					rep.setText(String.valueOf(items.get(position).getPerformanceActualList().get(i).getRepetition()));
				}else{
					rep.setText("-");
				}
				linearLayout.addView(tempView);
			}
    	}
    	return v;
    } 
}
