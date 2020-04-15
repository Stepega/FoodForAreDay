package com.product.stepanenko.calculatemealday;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditMealActivity extends AppCompatActivity {


    String[] dataSpinner = {"Нет","Да"};
    int tempSpinnerPosition = 0;
    BDMealHelper bdMealHelper;
    SQLiteDatabase bdMealHelperWrite;
    EditText editTextName, editTextProtein, editTextFat, editTextCarbo, editTextCal,editTextCategory;
    Cursor cursor;
    Intent intentget;
    int tempID;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_new_meal);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
             intentget = getIntent();

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setTitle("Изменить продукт");
            setSupportActionBar(toolbar);

            bdMealHelper = new BDMealHelper(this);

            editTextName = (EditText) findViewById(R.id.nameMealAdd);
            editTextProtein = (EditText) findViewById(R.id.proteinMealAdd);
            editTextFat = (EditText) findViewById(R.id.fatMealAdd);
            editTextCarbo = (EditText) findViewById(R.id.corboMealAdd);
            editTextCal = (EditText) findViewById(R.id.caloriesMealAdd);
            editTextCategory = (EditText) findViewById(R.id.categoryMealAdd);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dataSpinner);
            spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            Spinner spinner = (Spinner) findViewById(R.id.favoriteSpinner);
            spinner.setAdapter(spinnerAdapter);
            spinner.setPrompt("Хотите добавить продукт в избранное?");

            bdMealHelperWrite = bdMealHelper.getWritableDatabase();
            cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME + " = ?",
                    new String[] {intentget.getStringExtra(BDMealHelper.COLUMN_NAME)},null,null,null);
            if (cursor.moveToFirst())
            {
                editTextCategory.setText(cursor.getString(cursor.getColumnIndex(BDMealHelper.COLUMN_CATEGORY)));
                editTextName.setText(cursor.getString(cursor.getColumnIndex(BDMealHelper.COLUMN_NAME)));
                editTextCal.setText(cursor.getString(cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES)));
                editTextCarbo.setText(cursor.getString(cursor.getColumnIndex(BDMealHelper.COLUMN_CARBOHYDRATES)));
                editTextFat.setText(cursor.getString(cursor.getColumnIndex(BDMealHelper.COLUMN_FAT)));
                editTextProtein.setText(cursor.getString(cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN)));
                spinner.setSelection(cursor.getInt(cursor.getColumnIndex(BDMealHelper.COLUMN_FAVORITE)));
                tempID = cursor.getInt(cursor.getColumnIndex(BDMealHelper.COLUMN_ID));
            }
            cursor.close();
            bdMealHelperWrite.close();

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    tempSpinnerPosition = position;
                    ((TextView) parent.getChildAt(0)).setTextSize(20);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            }
            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.menu_add_meal, menu);
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_save_add:
                        return editMealINDataBase();
                    case R.id.action_close_add:
                        this.finish();
                        return true;
                    default:
                        return super.onOptionsItemSelected(item);
                }
        }

    private boolean editMealINDataBase()
    {
        String nameAdd, proteinAdd, fatAdd, corboAdd, caloriesAdd, categoryAdd;
        nameAdd = editTextName.getText().toString();
        proteinAdd = editTextProtein.getText().toString();
        fatAdd = editTextFat.getText().toString();
        corboAdd = editTextCarbo.getText().toString();
        caloriesAdd = editTextCal.getText().toString();
        categoryAdd = editTextCategory.getText().toString();
        if (nameAdd.matches("") || proteinAdd.matches("") || fatAdd.matches("") || corboAdd.matches("")||
                caloriesAdd.matches("") || categoryAdd.matches(""))
        {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return true;
        }

        ContentValues contentValues;
        contentValues = BDMealHelper.createContentValuesMealTable(
                nameAdd,proteinAdd,fatAdd,corboAdd,caloriesAdd, categoryAdd, tempSpinnerPosition, 4);
        bdMealHelperWrite = bdMealHelper.getWritableDatabase();
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME + "= ?",
                new  String[] {nameAdd},null,null,null);
        if (cursor.moveToFirst())
        {
            if (tempID != cursor.getInt(cursor.getColumnIndex(BDMealHelper.COLUMN_ID)))
            {
                Toast.makeText(this, "Продукт с таким названием уже существует, введите другой", Toast.LENGTH_SHORT).show();
                cursor.close();
                bdMealHelperWrite.close();
                return true;
            }
        }
        bdMealHelperWrite.update(BDMealHelper.MEAL_TABLE,contentValues,BDMealHelper.COLUMN_ID + " = " + Integer.toString(tempID),
                null);
        bdMealHelperWrite.close();
        cursor.close();
        this.finish();
        return true;
    }
}

