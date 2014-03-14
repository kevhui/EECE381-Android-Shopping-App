package com.juan.shopping.sqlitehelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExternalDbOpenHelper extends SQLiteOpenHelper {

	public static String DB_PATH = "/data/data/com.juan.shopping/databases/";
	public static String DB_NAME = "walmart.sqlite3";
	public SQLiteDatabase database;
	public final Context context;

	public SQLiteDatabase getDb() {
		return database;
	}

	public ExternalDbOpenHelper(Context context, String databaseName) {
		super(context, databaseName, null, 1);
		this.context = context;
		createDataBase();
	}

	public void createDataBase() {
		boolean dbExist = context.getDatabasePath(DB_NAME).exists();
		if (!dbExist) {
			this.getReadableDatabase();
			try {
				Log.e(this.getClass().toString(), "Going to copy");
				copyDataBase();
			} catch (IOException e) {
				Log.e(this.getClass().toString(), "Copying error");
				throw new Error("Error copying database!");
			}
		} else {
			Log.i(this.getClass().toString(), "Database already exists");
		}
	}

	private void copyDataBase() throws IOException {

        //Open your local db as the input stream
		Log.e(this.getClass().toString(), "Getting Assets");
        InputStream myInput = context.getAssets().open(DB_NAME);
		Log.e(this.getClass().toString(), "Getting outfile path");
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
		Log.e(this.getClass().toString(), "Open empty db");
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
		Log.e(this.getClass().toString(), "copy");
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[2048];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
		Log.e(this.getClass().toString(), "finish copy");
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
	}


	// query all categories
	public List<String> getAllCategories() {
		List<String> categoryList = new ArrayList<String>();

		String selectQuery = "SELECT DISTINCT category FROM items";
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		
		// loop through all rows and add to list
		if (c.moveToFirst()) {
			do {
				categoryList.add(c.getString(c.getColumnIndex("category")));
			} while (c.moveToNext());
		}

		return categoryList;
	}

	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
