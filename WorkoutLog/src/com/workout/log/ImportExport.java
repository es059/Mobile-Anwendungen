package com.workout.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workoutlog.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.workout.log.analytics.MyApplication;
import com.workout.log.analytics.MyApplication.TrackerName;
import com.workout.log.db.DataBaseHelper;
import com.workout.log.listAdapter.FileExplorerAdapter;
import com.workout.log.navigation.OnBackPressedListener;

public class ImportExport extends Fragment implements OnItemClickListener {
	
    private static final String PARENT_DIR = "..";
    private ArrayList<File> fileList;
    private File currentPath;
    private static File rootPath;
    
	private FileExplorerAdapter adapter;
    private TextView filePathView = null;
    private ListView fileListe = null;
    
    private Stack<File> savedPaths = null;
    private String fileEndsWith = ".db";  
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.import_export_fragment, container, false);
        
		/**
		 * Handles the behavior if the back button is pressed
		 */
		((HelperActivity) getActivity())
				.setOnBackPressedListener(new OnBackPressedListener() {
					@Override
					public void doBack() {
						/**
			        	 * Load the new File list
			        	 */
						if (savedPaths.size() != 0){
							refreshListView(savedPaths.pop());
						}else{
							((HelperActivity) getActivity()).setOnBackPressedListener(null);
							getActivity().onBackPressed();
						}
					}
				});
		
		setHasOptionsMenu(true);
        return view;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		savedPaths = new Stack<File>();
	    rootPath = Environment.getExternalStorageDirectory();
	    loadFileList(rootPath);
	    
	    filePathView = (TextView) getView().findViewById(R.id.filePath);
	    fileListe = (ListView) getView().findViewById(R.id.fileList);
	    
	    setCurrentPathToView();
	    
	    adapter = new FileExplorerAdapter(getActivity(), fileList);
	    
	    fileListe.setAdapter(adapter);
	    fileListe.setOnItemClickListener(this);  
	}
	
    @Override
	public void onPause() {
		super.onPause();
		
		((HelperActivity) getActivity()).setOnBackPressedListener(null);
	}


	
	/**
	 * Sets the current Path as text for the TextView 
	 */
	public void setCurrentPathToView(){
		filePathView.setText(currentPath.getPath());
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.import_export_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_export) {
			/**
			 * Tracker
			 */
			// Get tracker.
	        Tracker t = ((MyApplication) getActivity().getApplication()).getTracker(
	            TrackerName.APP_TRACKER);
	        // Build and send an Event.
	        t.send(new HitBuilders.EventBuilder()
	            .setCategory("ClickEvent")
	            .setAction("Export")
	            .setLabel("Export the Database")
	            .build());
			exportDatabase();
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Export the database in the current folder
	 * 
	 * @author Eric Schmidt
	 */
	public void exportDatabase(){ 
		SimpleDateFormat sD = new SimpleDateFormat("dd.MM.yyyy");
		
		DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getActivity());
		dataBaseHelper.close();
		FileOutputStream newDb = null;
		try {
			newDb = new FileOutputStream(currentPath.toString() + "/WorkoutLog_" + sD.format(new Date()) +".db");
			dataBaseHelper.copyDatabase(null, newDb);
			Toast.makeText(getActivity(), getResources().getString(R.string.ImportExport_ExportTo) + " " + currentPath.toString(), Toast.LENGTH_SHORT).show();
			/**
        	 * Load the new File list
        	 */
			refreshListView(currentPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Import the database in the current folder
	 * 
	 * @author Eric Schmidt
	 */
	public void importDatabase(File chosenFile){
		DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getActivity());
		dataBaseHelper.close();
		File dbPath = chosenFile;
		try {
			if (dbPath.getName().endsWith(".db")){
				dataBaseHelper.importDatabase(dbPath.getAbsolutePath());								
				Toast.makeText(getActivity(), getResources().getString(R.string.ImportExport_Finished), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getActivity(), getResources().getString(R.string.ImportExport_ChooseDb), Toast.LENGTH_SHORT).show();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the ListView with the fileList of a given File
	 * 
	 * @author Eric Schmidt
	 */
	public void refreshListView(File directory){
		if (directory.getName() == PARENT_DIR) {
			loadFileList(getChosenFile(directory.getName() ));
		}else{
			loadFileList(directory);
		}
		 
		setCurrentPathToView();
		 
		adapter.clear();
		adapter.addAll(fileList);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		File fileChosen = fileList.get(arg2);
        if (fileChosen.isDirectory() && fileChosen.canRead()) {
        	/**
        	 * Save the currentPath to the ArrayList
        	 */
        	savedPaths.push(currentPath);
        	
        	/**
        	 * Load the new File list
        	 */
        	refreshListView(fileChosen);
        }else if(!fileChosen.isDirectory()){
        	/**
			 * Tracker
			 */
			// Get tracker.
	        Tracker t = ((MyApplication) getActivity().getApplication()).getTracker(
	            TrackerName.APP_TRACKER);
	        // Build and send an Event.
	        t.send(new HitBuilders.EventBuilder()
	            .setCategory("ClickEvent")
	            .setAction("Import")
	            .setLabel("Import the Database")
	            .build());
        	/**
			 * Import
			 */
        	importDatabase(fileChosen);
        }		
	}
	
	
	/**
	 * Load the current FileList in the given directory
	 * 
	 * @param path the directory
	 * @author Eric Schmidt
	 */
	private void loadFileList(File path) {
	    this.currentPath = path;
	    ArrayList<File> r = new ArrayList<File>();
	    if (path.exists()) {
	        if (path.getParentFile() != null) r.add(new File(PARENT_DIR));
	        FilenameFilter filter = new FilenameFilter() {
	            public boolean accept(File dir, String filename) {
	                File sel = new File(dir, filename);
	                if (!sel.canRead()) return false;
	                else {
	                    boolean endsWith = fileEndsWith != null ? filename.toLowerCase().endsWith(fileEndsWith) : true;
	                    return endsWith || sel.isDirectory();
	                }
	            }
	        };
	        String[] fileList = path.list(filter);
	        if (fileList != null){
	            for (String file : fileList) {
	                r.add(getChosenFile(file));
	            }
	        }
	    }
	    fileList = (ArrayList<File>) r;
	}

	
	/**
	 * Returns the file of the file path
	 * 
	 * @param fileChosen
	 * @return the file
	 */
	private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) return currentPath.getParentFile();
        else return new File(currentPath, fileChosen);
    }
	
}
