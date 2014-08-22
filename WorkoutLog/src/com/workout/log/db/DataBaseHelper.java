package com.workout.log.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static DataBaseHelper mInstance = null;
	private static SQLiteDatabase myDataBase = null;
	
	private static String DB_PATH = "/data/data/com.remic.workoutlog/databases/";
	private static String DB_NAME = "WorkoutLog.db";

	private final Context myContext;
	
	/**
	 * Use Singleton Design-Pattern to improve performance of the application
	 * 
	 * @author Eric Schmidt
	 */
	public static DataBaseHelper getInstance(Context context){
		if (mInstance == null){
			mInstance = new DataBaseHelper(context.getApplicationContext());
		}
		/**
		 * Open the database only if the the connection is closed
		 */
		if (!myDataBase.isOpen()){
			openDataBase();
		}
		return mInstance;
	}
	
    /**
     * constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
	private DataBaseHelper(Context context) {
	    super(context, DB_NAME, null, 1);
	    this.myContext = context; 
	    try {
			this.createDataBase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    openDataBase();
    }
	 
	/**
	* Creates a empty database on the system and rewrites it with your own database.
	*/
	private void createDataBase() throws IOException{
	    boolean dbExist = checkDataBase();
	    if(dbExist){
	    	//do nothing - database already exist
	   	}else{
	   		//By calling this method and empty database will be created into the default system path
	   		//of your application so we are gonna be able to overwrite that database with our database.
	     	this.getReadableDatabase();
	       	try {
	   			copyDataBase();
	   		} catch (IOException e) {
	       		throw new Error("Error copying database");
	       	}
	   	}
	}
	 
	/**
	* Check if the database already exist to avoid re-copying the file each time you open the application.
	* @return true if it exists, false if it doesn't
	*/
	private boolean checkDataBase(){
	  	SQLiteDatabase checkDB = null;
	   	try{
	  		String myPath = DB_PATH + DB_NAME;
	   		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	   	}catch(SQLiteException e){
	  		//database does't exist yet.
	   	}
	   	if(checkDB != null){
	   		checkDB.close();
	   	}
	   	return checkDB != null ? true : false;
	}
	/**
	* Copies your database from your local assets-folder to the just created empty database in the
	* system folder, from where it can be accessed and handled.
	* This is done by transfering bytestream.
	* */
	private void copyDataBase() throws IOException{
		//Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		//Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
				myOutput.write(buffer, 0, length);
		}
		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}
	    
	private static void openDataBase() throws SQLException{ 
	    //Open the database
	    String myPath = DB_PATH + DB_NAME;
	    myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}
	    
	@Override
	public synchronized void close() {
	     if(myDataBase != null)myDataBase.close();
	     super.close();
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}
	
	public boolean importDatabase(String dbPath) throws IOException {
	    // Close the SQLiteOpenHelper so it will commit the created empty
	    // database to internal storage.
	    close();
	    File newDb = new File(dbPath);
	    File oldDb = new File(DB_PATH + DB_NAME);
	    if (newDb.exists()) {
	    	copyDatabase(new FileInputStream(newDb), new FileOutputStream(oldDb));
	        // Access the copied database so SQLiteHelper will cache it and mark
	        // it as created.
	        getWritableDatabase().close();
	        return true;
	    }
	    return false;
	}
	
	/**
     * Creates the specified <code>toFile</code> as a byte for byte copy of the
     * <code>fromFile</code>. If <code>toFile</code> already exists, then it
     * will be replaced with a copy of <code>fromFile</code>. The name and path
     * of <code>toFile</code> will be that of <code>toFile</code>.<br/>
     * <br/>
     * <i> Note: <code>fromFile</code> and <code>toFile</code> will be closed by
     * this function.</i>
     * 
     * @param fromFile
     *            - FileInputStream for the file to copy from.
     * @param toFile
     *            - FileInputStream for the file to copy to.
     */
    public void copyDatabase(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
    	if (fromFile == null) fromFile = new FileInputStream(new File(DB_PATH + DB_NAME));
    	
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }
}
