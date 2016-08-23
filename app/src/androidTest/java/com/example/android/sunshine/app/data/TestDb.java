/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        getContext().deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();

    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
*/
   public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
       //                                      ^^^^^^^ is  the column in the cursor with title names which
       //                                       store the name of the columns.
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testLocationTable() {

        testLocationTableAndReturn();

    }
     public Long testLocationTableAndReturn(){
         // First step: Get reference to writable database
         mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
         SQLiteDatabase db= new WeatherDbHelper(mContext).getWritableDatabase();
         // Create ContentValues of what you want to insert
         ContentValues weatherValues=TestUtilities.createNorthPoleLocationValues();
//         ContentValues weatherValues=new ContentValues();
//         weatherValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,"845401");
//         weatherValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME,"Motihari");
//         weatherValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT,"26.647");
//         weatherValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG,"84.9089");

         // Insert ContentValues into database and get a row ID back
         Long row_id=db.insert(WeatherContract.LocationEntry.TABLE_NAME,null,weatherValues);
         assertTrue(row_id!=-1);
         //  Query the database and receive a Cursor back
         Cursor c=db.query(WeatherContract.LocationEntry.TABLE_NAME,
                 null,
                 null,
                 null,
                 null,
                 null,
                 null);

         // Move the cursor to a valid database row
         assertTrue("Query result is not giving expected values",c.moveToFirst());
         // Validate data in resulting Cursor with the original ContentValues
         // (you can use the validateCurrentRecord function in TestUtilities to validate the
         // query if you like)

         Set<Map.Entry<String,Object>> valueset=weatherValues.valueSet();
         for(Map.Entry<String,Object> entry:valueset){
             String columnname=entry.getKey();
             int idx=c.getColumnIndex(columnname);
             assertFalse("Column not found",idx==-1);
             String expectedValue=entry.getValue().toString();
             assertEquals("Entry "+ entry.getValue().toString()+"Does not match",expectedValue,c.getString(idx));
         }
         assertFalse("More than one rows in the table",c.moveToNext());

         // Finally, close the cursor and database
         c.close();
         db.close();
         /// Return the row_id of the data we inserted
         return row_id;
     }
    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {



        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.
         Long locationRowId=testLocationTableAndReturn();
        Cursor c;
        SQLiteDatabase db;

              // First step: Get reference to writable database
              mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
              WeatherDbHelper weatherDbHelper = new WeatherDbHelper(mContext);
              db = weatherDbHelper.getWritableDatabase();

              // Create ContentValues of what you want to insert
              // (you can use the createWeatherValues TestUtilities function if you wish)
              ContentValues contentValues = TestUtilities.createWeatherValues(locationRowId);
              // Insert ContentValues into database and get a row ID back
              Long row_id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, contentValues);

              // Query the database and receive a Cursor back
              c = db.query(WeatherContract.WeatherEntry.TABLE_NAME, null, null, null, null, null, null);
              // Move the cursor to a valid database row
              assertTrue("The row in the database not created properly ", c.moveToFirst());
              // Validate data in resulting Cursor with the original ContentValues
              // (you can use the validateCurrentRecord function in TestUtilities to validate the
              // query if you like)
              Set<Map.Entry<String, Object>> setValues = contentValues.valueSet();
              for (Map.Entry<String, Object> element : setValues) {
                  String keyelement = element.getKey();
                  String elementval = element.getValue().toString();
                  int idx = c.getColumnIndex(keyelement);
                  assertFalse("There is no column with the name entered in the contentValues ", idx == -1);
                  assertEquals("The value in the row returned by cursor and that in contentValue is not same ", c.getString(idx), elementval);
              }


              c.close();
              db.close();





        // Finally, close the cursor and database
    }

    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertLocation() {
        return -1L;
    }
}
