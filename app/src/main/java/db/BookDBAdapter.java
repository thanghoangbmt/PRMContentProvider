package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import utils.ToolUtilities;

public class BookDBAdapter {
    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ISBN = "isbn";

    private static final String DBName = "BookSQLite.db";
    private static final String TableName = "Books";
    private static final int DBVersion = 3;

    private static final String CREATE_SQL_STATEMENT = "CREATE TABLE " + TableName
            + "(" + KEY_ID + " integer primary key autoincrement, "
            + KEY_TITLE + " text not null, "
            + KEY_ISBN + " text not null);";

    private static final String DROP_TABLE_STATEMENT = "Drop Table If Exists " + TableName;

    private final Context context;
    private SQLiteDatabase db;

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, DBName, null, DBVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                Log.d("DBHelper", "onCreate");
                boolean dbExist = ToolUtilities.checkDB(context, DBName);
                if (dbExist) {
                    db.execSQL(CREATE_SQL_STATEMENT);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("DBHelper", "onUpgrade");
            db.execSQL(DROP_TABLE_STATEMENT);
            onCreate(db);
        }
    }

    private DBHelper dbHelper;

    public BookDBAdapter(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public BookDBAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public SQLiteDatabase openDB() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return db;
    }

    public void close() {
        db.close();
    }

    public boolean createBookInstance(String title, String isbn) {
        ContentValues content = new ContentValues();
        content.put(KEY_TITLE, title);
        content.put(KEY_ISBN, isbn);

        long result = db.insert(TableName, null, content);

        if (result > -1)
            return true;
        return false;
    }

    public boolean deleteBookInstance(int id) {
        int result = db.delete(TableName, KEY_ID + " = ?", new String[]{id + ""});
        if (result > 0)
            return true;
        return false;
    }

    public Cursor findAll() {
        Cursor result = db.query(TableName,
                new String[]{KEY_ID, KEY_TITLE, KEY_ISBN},
                null, null, null, null, null);
        return result;
    }

    public Cursor getBookInstance(int id) {
        Cursor result = db.query(TableName,
                new String[]{KEY_ID, KEY_TITLE, KEY_ISBN},
                KEY_ID + " = " + id + "", null, null, null, null);
        if (result != null)
            result.moveToFirst();
        return result;
    }

    public boolean updateTitleBookInstance(int id, String title) {
        ContentValues content = new ContentValues();
        content.put(KEY_TITLE, title);
        int result = db.update(TableName, content,KEY_ID + " = ?", new String[]{id + ""});
        if (result > 0)
            return true;
        return false;
    }

    public boolean updateIsbnBookInstance(int id, String isbn) {
        ContentValues content = new ContentValues();
        content.put(KEY_ISBN, isbn);
        int result = db.update(TableName, content,KEY_ID + " = ?", new String[]{id + ""});
        if (result > 0)
            return true;
        return false;
    }

    public void clearAllData() {
        db.execSQL("Delete from " + TableName);
    }
}
