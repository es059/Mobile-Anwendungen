package com.workout.log.dialog;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import com.example.workoutlog.R;
import com.workout.log.dialog.ListenerList.FireHandler;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FileDialog {
    private static final String PARENT_DIR = "..";
    private String[] fileList;
    private File currentPath;
    private static File rootPath;
    private Dialog dialog = null;
    
    public interface FileSelectedListener {
        void fileSelected(File file);
    }
    public interface DirectorySelectedListener {
        void directorySelected(File directory);
    }
    private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<FileDialog.FileSelectedListener>();
    private ListenerList<DirectorySelectedListener> dirListenerList = new ListenerList<FileDialog.DirectorySelectedListener>();
    private final FragmentActivity activity;
    private boolean selectDirectoryOption;
    private String fileEndsWith;    

    /**
     * @param activity 
     * @param initialPath
     */
    public FileDialog(FragmentActivity activity) {
        this.activity = activity;
        rootPath = Environment.getExternalStorageDirectory();
        loadFileList(rootPath);
    }

    /**
     * @return file dialog
     */
    public Dialog createFileDialog() {

        
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(currentPath.getPath());
        
        
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = (LinearLayout) inflater.inflate(R.layout.file_dialog, null);
        Switch toggleButton = (Switch) view.findViewById(R.id.switch_import_export);
        TextView textView = (TextView) view.findViewById(R.id.switch_header);
        
        if (!currentPath.toString().equals(rootPath.toString())){
        	toggleButton.setVisibility(View.GONE);
        }else{
        	toggleButton.setVisibility(View.VISIBLE);
        }
        
        textView.setText(currentPath.getPath());
        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1 == true) {
					/**
					 * Import
					 */
					setSelectDirectoryOption(false);
					setPositiveButtonEnable(false);
				}else{
					/**
					 * Export
					 */
					setSelectDirectoryOption(true);
					setPositiveButtonEnable (true);
				}
				
			}
        	
        });
        
        /**
         * Handle Export
         */
        builder.setCustomTitle(view);
        builder.setPositiveButton("Bestätigen", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                fireDirectorySelectedEvent(currentPath);
            }
        });
        builder.setNegativeButton("Abbrechen", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
   
        /**
         * Handle Import
         */
        builder.setItems(fileList, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String fileChosen = fileList[which];
                File chosenFile = getChosenFile(fileChosen);
                if (chosenFile.isDirectory() && chosenFile.canRead()) {
                    loadFileList(chosenFile);
                    dialog.cancel();
                    dialog.dismiss();
                    showDialog();
                }else if(!selectDirectoryOption){
                	fireFileSelectedEvent(chosenFile);
                }else{
                	showDialog();
                }
            }
        });

        dialog = builder.show();
        return dialog;
    }

    public void setPositiveButtonEnable(boolean value){
    	((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(value);
    }
    
    public void addFileListener(FileSelectedListener listener) {
        fileListenerList.add(listener);
    }

    public void removeFileListener(FileSelectedListener listener) {
        fileListenerList.remove(listener);
    }

    public void setSelectDirectoryOption(boolean selectDirectoryOption) {
        this.selectDirectoryOption = selectDirectoryOption;
    }

    public void addDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.add(listener);
    }

    public void removeDirectoryListener(DirectorySelectedListener listener) {
        dirListenerList.remove(listener);
    }

    /**
     * Show file dialog
     */
    public void showDialog() {
        createFileDialog().show();
    }

    private void fireFileSelectedEvent(final File file) {
        fileListenerList.fireEvent(new FireHandler<FileDialog.FileSelectedListener>() {
            public void fireEvent(FileSelectedListener listener) {
                listener.fileSelected(file);
            }
        });
    }

    private void fireDirectorySelectedEvent(final File directory) {
        dirListenerList.fireEvent(new FireHandler<FileDialog.DirectorySelectedListener>() {
            public void fireEvent(DirectorySelectedListener listener) {
                listener.directorySelected(directory);
            }
        });
    }

    private void loadFileList(File path) {
        this.currentPath = path;
        List<String> r = new ArrayList<String>();
        if (path.exists()) {
            if (path.getParentFile() != null) r.add(PARENT_DIR);
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
	                r.add(file);
	            }
            }
        }
        fileList = (String[]) r.toArray(new String[]{});
    }

    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) return currentPath.getParentFile();
        else return new File(currentPath, fileChosen);
    }

    public void setFileEndsWith(String fileEndsWith) {
        this.fileEndsWith = fileEndsWith != null ? fileEndsWith.toLowerCase() : fileEndsWith;
    }
 }

class ListenerList<L> {
private List<L> listenerList = new ArrayList<L>();

public interface FireHandler<L> {
    void fireEvent(L listener);
}

public void add(L listener) {
    listenerList.add(listener);
}

public void fireEvent(FireHandler<L> fireHandler) {
    List<L> copy = new ArrayList<L>(listenerList);
    for (L l : copy) {
        fireHandler.fireEvent(l);
    }
}

public void remove(L listener) {
    listenerList.remove(listener);
}

public List<L> getListenerList() {
    return listenerList;
}
}