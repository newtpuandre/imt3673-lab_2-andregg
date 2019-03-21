package local.andregg.lab_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import java.util.ArrayList;

public class NewsStorage extends SQLiteOpenHelper {

    //Variables
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "andregg_lab_2.db";

    public static long lastAddedID = 0;

    public NewsStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FeedReaderContract.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(FeedReaderContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    //Returns all items in the SQLite db in descending order
    public ArrayList<NewsItem> getItems(SQLiteDatabase db){
        ArrayList<NewsItem> data = new ArrayList<>();
        NewsItem temp;

        //SQLite query
        String query = "SELECT * FROM news ORDER BY _id DESC";
        Cursor cursor = db.rawQuery(query, null);

        //Loop over all results and add it to the return array
        while(cursor.moveToNext()) {
            int number = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION));
            String url = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_URL));
            temp = new NewsItem(number, url, title, description);
            data.add(temp);
        }
        cursor.close();
        return data;
    }

    //Checks if the item already exists in the db
    public boolean checkForItem(SQLiteDatabase db, String title, String description) {

        //SQLite query
        String query = "SELECT * FROM news WHERE title=? OR description=?";
        Cursor cur = db.rawQuery(query, new String[] {title, description});
        return (cur.moveToNext());
    }

    //Inserts and item into the db
    public boolean insertItem(SQLiteDatabase db, NewsItem item) {
        //Check if content already exists.
        if(!checkForItem(db, item.returnHeader(), item.returnDescription())){
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry._ID, countData(db) + 1);
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, item.returnHeader());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, item.returnDescription());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_URL, item.returnLink());

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
            lastAddedID = newRowId;
            return true; //Item was inserted
        }
        return false; //Insertion was NOT inserted
    }

    //Returns the number of elements in the db
    public int countData(SQLiteDatabase db){
        int count;

        String query = "SELECT * FROM news";
        Cursor cur = db.rawQuery(query, null);
        count = cur.getCount();

        return count;
    }

    public static final class FeedReaderContract {
        // To prevent someone from accidentally instantiating the contract class,
        // make the constructor private.
        private FeedReaderContract() {}

        /* Inner class that defines the table contents */
        public static class FeedEntry implements BaseColumns {
            public static final String TABLE_NAME = "news";
            public static final String COLUMN_NAME_TITLE = "title";
            public static final String COLUMN_NAME_DESCRIPTION = "description";
            public static final String COLUMN_NAME_URL = "url";
        }

        //SQL creation query
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                        FeedEntry._ID + " INTEGER PRIMARY KEY," +
                        FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                        FeedEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                        FeedEntry.COLUMN_NAME_URL + " TEXT)";

        //SQL deletion query
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    }



}
