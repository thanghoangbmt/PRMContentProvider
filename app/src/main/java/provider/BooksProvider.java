package provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import db.BookDBAdapter;

public class BooksProvider extends ContentProvider {

    private static final String PROVIDER_NAME = "none";
    public static final Uri CONTENT_URI = Uri.parse("content://"
            + PROVIDER_NAME + "/books");
    private static final int BOOKS = 1;
    private static final int BOOK_ID = 2;
    private static final UriMatcher uriMathcer;

    static {
        uriMathcer = new UriMatcher(UriMatcher.NO_MATCH);
        uriMathcer.addURI(PROVIDER_NAME, "books", BOOKS);
        uriMathcer.addURI(PROVIDER_NAME, "books/#", BOOKS);
    }

    private SQLiteDatabase bookDB;
    private static final String DBName = "BookSQLite.db";
    private static final String TableName = "Books";

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ISBN = "isbn";

    public BooksProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMathcer.match(uri)) {
            case BOOKS:
                count:
                bookDB.delete(TableName, selection, selectionArgs);
                break;
            case BOOK_ID:
                String id = uri.getPathSegments().get(1);
                count = bookDB.delete(TableName,
                        KEY_ID + "=" + id
                                + (!TextUtils.isEmpty(selection) ? " AND ("
                                + selection + ")" : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknow URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMathcer.match(uri)) {
            case BOOKS:
                return "vnd.android.cursor.dir/vnd.contentproviders.books";
            case BOOK_ID:
                return "vnd.android.cursor.item/vnd.contentproviders.books";
            default:
                throw new IllegalArgumentException("Unknow URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = bookDB.insert(TableName, null, values);
        if (rowID > 0) {
            Uri thisUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(thisUri, null);
            return thisUri;
        }
        throw new SQLException("Fail to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        BookDBAdapter adapter = new BookDBAdapter(context);
        adapter.open();
        bookDB = adapter.openDB();
        if (bookDB == null)
            return false;
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(TableName);
        if (uriMathcer.match(uri) == BOOK_ID) {
            sqlBuilder.appendWhere(KEY_ID + " = " + uri.getPathSegments().get(1));
        }

        if (sortOrder == null || sortOrder == "")
            sortOrder = KEY_TITLE;

        Cursor c = sqlBuilder.query(bookDB, projection, selection, selectionArgs,
                null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        switch (uriMathcer.match(uri)) {
            case BOOKS:
                count = bookDB.update(TableName, values, selection, selectionArgs);
                break;
            case BOOK_ID:
                count = bookDB.update(TableName, values, KEY_ID + " = "
                        + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection)? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
