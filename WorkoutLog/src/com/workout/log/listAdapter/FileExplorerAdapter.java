package com.workout.log.listAdapter;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.remic.workoutlog.R;

public class FileExplorerAdapter extends ArrayAdapter<File>{
	
	private ArrayList<File> items;
	private LayoutInflater layoutInflater;
	
	public FileExplorerAdapter(Context context, ArrayList<File> items){
		super(context,0,items);
		this.items = items;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if (v == null) v = layoutInflater.inflate(R.layout.listview_file_explorer, null);
		
		TextView fileName = (TextView) v.findViewById(R.id.fileName);
		TextView fileSize = (TextView) v.findViewById(R.id.fileSize);
		ImageView fileIcon = (ImageView) v.findViewById(R.id.icon);
		
		
		final File item = items.get(position);
		if (item != null){
			if(item.getName().toString() == ".."){
				fileName.setText(item.getName().toString());
				fileSize.setText("");
				fileIcon.setBackgroundResource(0);
			}else if (item.isDirectory()){
				fileName.setText(item.getName().toString());
				fileSize.setText("");
				fileIcon.setBackgroundResource(R.drawable.folder);
			}else{
				fileName.setText(item.getName().toString());
				fileSize.setText(String.valueOf((item.length() / 1024) + " kb"));
				fileIcon.setBackgroundResource(R.drawable.file);
			}
		}
		return v;
	}
}
