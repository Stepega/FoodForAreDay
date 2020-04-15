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

public class AddNewMealActivity extends AppCompatActivity {

    String[] dataSpinner = {"Нет","Да"};
    int tempSpinnerPosition = 0;
    BDMealHelper bdMealHelper;
    SQLiteDatabase bdMealHelperWrite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_meal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Добавить новый");
        setSupportActionBar(toolbar);

        bdMealHelper = new BDMealHelper(this);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, dataSpinner);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.favoriteSpinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setPrompt("Хотите добавить продукт в избранное?");
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
            return AddNewMealINDataBase();
            case R.id.action_close_add:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void  onBackPressed()
    {
        bdMealHelper.close();
        this.finish();
    }
    private boolean AddNewMealINDataBase()
    {
        EditText editText;
        String nameAdd, proteinAdd, fatAdd, corboAdd, caloriesAdd, categoryAdd;
        editText = (EditText) findViewById(R.id.nameMealAdd);
        nameAdd = editText.getText().toString();
        editText = (EditText) findViewById(R.id.proteinMealAdd);
        proteinAdd = editText.getText().toString();
        editText = (EditText) findViewById(R.id.fatMealAdd);
        fatAdd = editText.getText().toString();
        editText = (EditText) findViewById(R.id.corboMealAdd);
        corboAdd = editText.getText().toString();
        editText = (EditText) findViewById(R.id.caloriesMealAdd);
        caloriesAdd = editText.getText().toString();
        editText = (EditText) findViewById(R.id.categoryMealAdd);
        categoryAdd = editText.getText().toString();
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
        Cursor cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME + "= ?",
                new  String[] {nameAdd},null,null,null);
        if (cursor.moveToFirst())
        {
            Toast.makeText(this, "Продукт с таким именем уже существует, введите другой", Toast.LENGTH_SHORT).show();
            cursor.close();
            bdMealHelperWrite.close();
            return true;
        }
        bdMealHelperWrite.insert(BDMealHelper.MEAL_TABLE,null,contentValues);
        bdMealHelperWrite.close();
        cursor.close();
        this.finish();
        return true;
    }
}
