package com.product.stepanenko.calculatemealday;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


public class MainActivity extends AppCompatActivity {
    SharedPreferences fileRegData;
    Intent registration;
    Toolbar toolbar;
    final Random randomGenerate = new Random();
    ListView listViewBreackfast, listViewBreackfast2, listViewLunch, listViewDinner, listViewDinner2;
    BDMealHelper bdMealHelper;
    SimpleDateFormat formatForDateNow;


    boolean cheked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Food for a day");
        setSupportActionBar(toolbar);

        bdMealHelper = new BDMealHelper(this);


        fileRegData = getSharedPreferences(Registration.FILE_NAME_REGDATA, MODE_PRIVATE);
        if(fileRegData.getString(Registration.NAME_REGDATA,"").matches(""))
        {
            registration = new Intent(this, Registration.class);
            startActivity(registration);
            this.finish();
        }else
        {

            SQLiteDatabase checkbdMealHelper = bdMealHelper.getWritableDatabase();

            formatForDateNow = new SimpleDateFormat("yyyy.MM.dd");
            String historyRead = formatForDateNow.format( new Date());

            Cursor cursor = checkbdMealHelper.query(BDMealHelper.HISTORY_TABLE,null,BDMealHelper.COLUMN_DATE +"= ?",
                    new String[] {historyRead},null,null,null);
            if (cursor.moveToFirst())
            {
                loadDayRation();
                Toast.makeText(this, "Данные загружены с БД", Toast.LENGTH_SHORT).show();
            }
            else calculateDayRation(false);
        }





    }


    //Setting toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.main_menu_allmeal:
                Intent intent = new Intent(this, MealActivity.class);
                startActivity(intent);
                return true;
            case R.id.main_menu_profile:
                registration = new Intent(this, Profile.class);
                startActivity(registration);
                return true;
            case R.id.main_menu_refresh:
                calculateDayRation(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    String loadText(String keyOut)
    {
        String savedText = fileRegData.getString(keyOut, "");
        return  savedText;
    }

    private void calculateDayRation( boolean refreshDB)
    {
        ArrayList<Item> dataLV = new ArrayList<>();
        ArrayList<Item> dataLVBR2 = new ArrayList<>();
        ArrayList<Item> dataLVLCH = new ArrayList<>();
        ArrayList<Item> dataLVDN = new ArrayList<>();
        ArrayList<Item> dataLVDN2 = new ArrayList<>();

        String name = loadText(Registration.NAME_REGDATA);
        int age = Integer.parseInt(loadText(Registration.AGE_REGDATA));
        int weight = Integer.parseInt(loadText(Registration.WEIGHT_REGDATA));
        int goal = Integer.parseInt(loadText(Registration.GOAL_REGDATA));
        int male = Integer.parseInt(loadText(Registration.MALE_REGDATA));
        int growth = Integer.parseInt(loadText(Registration.GROWTH_REGDATA));
        float life = Float.parseFloat(loadText(Registration.LIFE_REGDATA));
        float todayProtein = 0, todayFat = 0, todayCarbo = 0;
        float eatingTodayProtein = 0, eatingTodayCal = 0;
        TextView textViewHeader = (TextView) findViewById(R.id.mainHeader);
        TextView textViewHeaderPFC = (TextView) findViewById(R.id.mainPFC);
        TextView textViewBreackfast = (TextView) findViewById(R.id.main_breack);
        TextView textViewBreackfastTwo = (TextView) findViewById(R.id.main_breack2);
        TextView textViewLunch = (TextView) findViewById(R.id.main_lunch);
        TextView textViewDinner = (TextView) findViewById(R.id.main_dinner);
        TextView textViewDinnerTwo = (TextView) findViewById(R.id.main_dinner2);

        listViewBreackfast = (ListView) findViewById(R.id.ListViewBR);
        listViewBreackfast2 = (ListView) findViewById(R.id.ListViewBRTwo);
        listViewLunch = (ListView) findViewById(R.id.ListViewLunch);
        listViewDinner = (ListView) findViewById(R.id.ListViewDinner);
        listViewDinner2 = (ListView) findViewById(R.id.ListViewDinnerTwo);

        Float bmr = 0f;
        switch (male)
        {
            case 0:
                bmr = ((9.99f*weight)+(6.25f*growth)-(4.92f*age)+5)*life;
                break;
            case 1:
                bmr = ((9.99f*weight)+(6.25f*growth)-(4.92f*age)-161)*life;
                break;
            default:
                Toast.makeText(this, "Ошибка \nОбновите регистрационные данные", Toast.LENGTH_SHORT).show();
                registration = new Intent(this, Registration.class);
                startActivity(registration);
                this.finish();
                break;
        }
        switch (goal)
        {
            case 0:
                bmr = bmr - bmr * 0.1f;
                todayProtein = (bmr * 0.18f)/4;
                todayFat = (bmr * 0.32f)/9;
                todayCarbo = (bmr - (todayProtein*4 + todayFat*9))/4;
                break;
            case 1:
                bmr = bmr;
                todayProtein = (bmr * 0.18f)/4;
                todayFat = (bmr * 0.3f)/9;
                todayCarbo = (bmr * 0.52f)/4;
                break;
            case 2:
                bmr = bmr + bmr * 0.1f;
                todayProtein = (bmr * 0.2f)/4;
                todayFat = (bmr * 0.3f)/9;
                todayCarbo = (bmr * 0.5f)/4;
                break;
            default:
                Toast.makeText(this, "Ошибка \nОбновите регистрационные данные", Toast.LENGTH_SHORT).show();
                registration = new Intent(this, Registration.class);
                startActivity(registration);
                this.finish();
                break;
        }
        textViewHeader.setText("Сегодня нужно скушать " + Integer.toString(Math.round(bmr)) + " ккал");
        textViewHeaderPFC.setText("Белков: "+Integer.toString(Math.round(todayProtein))+", Жиров: "+Integer.toString(Math.round(todayFat))+", Углеводов: "+Integer.toString(Math.round(todayCarbo)));
        float bmrBreakfast = bmr * 0.25f;
        float bmrBreakfastTwo = bmr * 0.1f;
        float bmrLunch = bmr * 0.4f;
        float bmrDinner = bmr * 0.2f;
        float bmrDinnerTwo = bmr * 0.05f;
        textViewBreackfast.setText("Завтрак: "+ Integer.toString(Math.round(bmrBreakfast)) +" ккал.");
        textViewBreackfastTwo.setText("Второй Завтрак: "+ Integer.toString(Math.round(bmrBreakfastTwo)) +" ккал.");
        textViewLunch.setText("Обед: "+ Integer.toString(Math.round(bmrLunch)) +" ккал.");
        textViewDinner.setText("Ужин: "+ Integer.toString(Math.round(bmrDinner)) +" ккал.");
        textViewDinnerTwo.setText("Второй Ужин: "+ Integer.toString(Math.round(bmrDinnerTwo)) +" ккал.");
//завтрак1
        SQLiteDatabase bdMealHelperWrite = bdMealHelper.getWritableDatabase();
        ContentValues contentHistory = new ContentValues();


        formatForDateNow = new SimpleDateFormat("yyyy.MM.dd");
        String historyWrite = formatForDateNow.format( new Date());
        contentHistory.put(BDMealHelper.COLUMN_DATE,historyWrite);

        Cursor cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_CATEGORY +"= ?",
                new String[] {"Крупы"},null,null,null);
        ArrayList<String> productRandom = new ArrayList<>();
        boolean chekedFavorite = true;
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int favoriteColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAVORITE);
            do {
                if (cursor.getInt(favoriteColIndex) == 1) {
                    productRandom.add(cursor.getString(nameColIndexx));
                    chekedFavorite = false;
                }
            }while (cursor.moveToNext());
        }
        if (chekedFavorite)
        {
            if (cursor.moveToFirst())
            {
                int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
                do {
                    productRandom.add(cursor.getString(nameColIndexx));
                }while (cursor.moveToNext());
            }
        }
        int randomCount = randomGenerate.nextInt(productRandom.size());
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME+ "= ?"
                ,new String[] {productRandom.get(randomCount)},null,null,null);
        String nameMeal;
        float proteinMeal, fatMeal, carboMeal, ccalMeal, weightMeal;
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int proteinColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN);
            int fatColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAT);
            int carboColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CARBOHYDRATES);
            int ccalColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);

            nameMeal = cursor.getString(nameColIndexx);
            proteinMeal = cursor.getFloat(proteinColIndex);
            carboMeal = cursor.getFloat(carboColIndex);
            ccalMeal = cursor.getFloat(ccalColIndex);
            weightMeal = (todayCarbo * 0.25f * 100)/carboMeal;
            eatingTodayProtein = (proteinMeal * weightMeal)/100;
            eatingTodayCal = eatingTodayCal + (ccalMeal * weightMeal)/100;
            dataLV.add(new Item(nameMeal, Integer.toString(Math.round(weightMeal))));
            historyWrite = ";" + nameMeal + ";" + Integer.toString(Math.round(weightMeal));
        }
//мясо
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_CATEGORY +"= ?",
                new String[] {"Мясо"},null,null,null);
        productRandom.clear();
        chekedFavorite = true;
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int favoriteColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAVORITE);
            do {
                if (cursor.getInt(favoriteColIndex) == 1) {
                    productRandom.add(cursor.getString(nameColIndexx));
                    chekedFavorite = false;
                }
            }while (cursor.moveToNext());
        }
        if (chekedFavorite)
        {
            if (cursor.moveToFirst())
            {
                int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
                do {
                    productRandom.add(cursor.getString(nameColIndexx));
                }while (cursor.moveToNext());
            }
        }
        randomCount = randomGenerate.nextInt(productRandom.size());
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME+ "= ?"
                ,new String[] {productRandom.get(randomCount)},null,null,null);
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int proteinColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN);
            int fatColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAT);
            int ccalColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);

            nameMeal = cursor.getString(nameColIndexx);
            proteinMeal = cursor.getFloat(proteinColIndex);
            ccalMeal = cursor.getFloat(ccalColIndex);
            weightMeal = ((todayProtein * 0.25f - eatingTodayProtein) * 100)/proteinMeal;
            eatingTodayProtein = (proteinMeal * weightMeal)/100;
            eatingTodayCal = eatingTodayCal + (ccalMeal * weightMeal)/100;
            dataLV.add(new Item(nameMeal, Integer.toString(Math.round(weightMeal))));
            historyWrite = historyWrite + ";" + nameMeal + ";" + Integer.toString(Math.round(weightMeal));
            contentHistory.put(BDMealHelper.COLUMN_BREAKFAST, historyWrite);
        }
        listViewBreackfast.setAdapter(new MyAdapterMainActivity(this,dataLV));
        listViewBreackfast.setDivider(null);


        //второй завтрак
            productRandom.clear();

        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME+ "= ?"
                ,new String[] {dataLV.get(0).getName()},null,null,null);
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int proteinColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN);
            int fatColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAT);
            int carboColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CARBOHYDRATES);
            int ccalColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);

            nameMeal = cursor.getString(nameColIndexx);
            proteinMeal = cursor.getFloat(proteinColIndex);
            carboMeal = cursor.getFloat(carboColIndex);
            ccalMeal = cursor.getFloat(ccalColIndex);
            weightMeal = (todayCarbo * 0.1f * 100)/carboMeal;
            eatingTodayProtein = (proteinMeal * weightMeal)/100;
            dataLVBR2.add(new Item(nameMeal, Integer.toString(Math.round(weightMeal))));
            eatingTodayCal = eatingTodayCal + (ccalMeal * weightMeal)/100;
            historyWrite = ";" + nameMeal + ";" + Integer.toString(Math.round(weightMeal));
        }
//мясо

        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME+ "= ?"
                ,new String[] {dataLV.get(1).getName()},null,null,null);
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int proteinColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN);
            int fatColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAT);
            int ccalColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);

            nameMeal = cursor.getString(nameColIndexx);
            proteinMeal = cursor.getFloat(proteinColIndex);
            fatMeal = cursor.getFloat(fatColIndex);
            ccalMeal = cursor.getFloat(ccalColIndex);
            weightMeal = ((todayProtein * 0.1f - eatingTodayProtein) * 100)/proteinMeal;
            eatingTodayProtein = (proteinMeal * weightMeal)/100;
            dataLVBR2.add(new Item(nameMeal, Integer.toString(Math.round(weightMeal))));
            eatingTodayCal = eatingTodayCal + (ccalMeal * weightMeal)/100; //Общее колво ккал
            historyWrite = historyWrite + ";" + nameMeal + ";" + Integer.toString(Math.round(weightMeal));
            contentHistory.put(BDMealHelper.COLUMN_BREAKFASTTWO, historyWrite);
        }
        listViewBreackfast2.setAdapter(new MyAdapterMainActivity(this,dataLVBR2));
        listViewBreackfast2.setDivider(null);

        //Обед

        productRandom.clear();

        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_CATEGORY +"= ?",
                new String[] {"Крупы"},null,null,null);
        chekedFavorite = true;
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int favoriteColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAVORITE);
            do {
                if (cursor.getInt(favoriteColIndex) == 1) {
                    productRandom.add(cursor.getString(nameColIndexx));
                    chekedFavorite = false;
                }
            }while (cursor.moveToNext());
        }
        if (chekedFavorite)
        {
            if (cursor.moveToFirst())
            {
                int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
                do {
                    productRandom.add(cursor.getString(nameColIndexx));
                }while (cursor.moveToNext());
            }
        }
        randomCount = randomGenerate.nextInt(productRandom.size());
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME+ "= ?"
                ,new String[] {productRandom.get(randomCount)},null,null,null);
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int proteinColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN);
            int fatColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAT);
            int carboColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CARBOHYDRATES);
            int ccalColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);

            nameMeal = cursor.getString(nameColIndexx);
            proteinMeal = cursor.getFloat(proteinColIndex);
            fatMeal = cursor.getFloat(fatColIndex);
            carboMeal = cursor.getFloat(carboColIndex);
            ccalMeal = cursor.getFloat(ccalColIndex);
            weightMeal = (todayCarbo * 0.4f * 100)/carboMeal;
            eatingTodayProtein = (proteinMeal * weightMeal)/100;
            eatingTodayCal = eatingTodayCal + (ccalMeal * weightMeal)/100;
            dataLVLCH.add(new Item(nameMeal, Integer.toString(Math.round(weightMeal))));
            historyWrite = ";" + nameMeal + ";" + Integer.toString(Math.round(weightMeal));
        }
//мясо
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_CATEGORY +"= ?",
                new String[] {"Мясо"},null,null,null);
        productRandom.clear();
        chekedFavorite = true;
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int favoriteColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAVORITE);
            do {
                if (cursor.getInt(favoriteColIndex) == 1) {
                    productRandom.add(cursor.getString(nameColIndexx));
                    chekedFavorite = false;
                }
            }while (cursor.moveToNext());
        }
        if (chekedFavorite)
        {
            if (cursor.moveToFirst())
            {
                int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
                do {
                    productRandom.add(cursor.getString(nameColIndexx));
                }while (cursor.moveToNext());
            }
        }
        randomCount = randomGenerate.nextInt(productRandom.size());
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME+ "= ?"
                ,new String[] {productRandom.get(randomCount)},null,null,null);
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int proteinColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN);
            int ccalColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);

            nameMeal = cursor.getString(nameColIndexx);
            proteinMeal = cursor.getFloat(proteinColIndex);
            ccalMeal = cursor.getFloat(ccalColIndex);
            weightMeal = ((todayProtein * 0.4f - eatingTodayProtein) * 100)/proteinMeal;
            eatingTodayProtein = (proteinMeal * weightMeal)/100;
            eatingTodayCal = eatingTodayCal + (ccalMeal * weightMeal)/100;
            dataLVLCH.add(new Item(nameMeal, Integer.toString(Math.round(weightMeal))));
            historyWrite = historyWrite + ";" + nameMeal + ";" + Integer.toString(Math.round(weightMeal));
            contentHistory.put(BDMealHelper.COLUMN_LUNCH, historyWrite);
        }
        listViewLunch.setAdapter(new MyAdapterMainActivity(this,dataLVLCH));
        listViewLunch.setDivider(null);

        //Ужин

        productRandom.clear();

        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_CATEGORY +"= ?",
                new String[] {"Крупы"},null,null,null);
        chekedFavorite = true;
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int favoriteColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAVORITE);
            do {
                if (cursor.getInt(favoriteColIndex) == 1) {
                    productRandom.add(cursor.getString(nameColIndexx));
                    chekedFavorite = false;
                }
            }while (cursor.moveToNext());
        }
        if (chekedFavorite)
        {
            if (cursor.moveToFirst())
            {
                int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
                do {
                    productRandom.add(cursor.getString(nameColIndexx));
                }while (cursor.moveToNext());
            }
        }
        randomCount = randomGenerate.nextInt(productRandom.size());
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME+ "= ?"
                ,new String[] {productRandom.get(randomCount)},null,null,null);
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int proteinColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN);
            int fatColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAT);
            int carboColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CARBOHYDRATES);
            int ccalColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);

            nameMeal = cursor.getString(nameColIndexx);
            proteinMeal = cursor.getFloat(proteinColIndex);
            fatMeal = cursor.getFloat(fatColIndex);
            carboMeal = cursor.getFloat(carboColIndex);
            ccalMeal = cursor.getFloat(ccalColIndex);
            weightMeal = (todayCarbo * 0.2f * 100)/carboMeal;
            eatingTodayProtein = (proteinMeal * weightMeal)/100;
            eatingTodayCal = eatingTodayCal + (ccalMeal * weightMeal)/100;
            dataLVDN.add(new Item(nameMeal, Integer.toString(Math.round(weightMeal))));
            historyWrite = ";" + nameMeal + ";" + Integer.toString(Math.round(weightMeal));

        }
//мясо
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_CATEGORY +"= ?",
                new String[] {"Мясо"},null,null,null);
        productRandom.clear();
        chekedFavorite = true;
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int favoriteColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAVORITE);
            do {
                if (cursor.getInt(favoriteColIndex) == 1) {
                    productRandom.add(cursor.getString(nameColIndexx));
                    chekedFavorite = false;
                }
            }while (cursor.moveToNext());
        }
        if (chekedFavorite)
        {
            if (cursor.moveToFirst())
            {
                int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
                do {
                    productRandom.add(cursor.getString(nameColIndexx));
                }while (cursor.moveToNext());
            }
        }
        randomCount = randomGenerate.nextInt(productRandom.size());
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME+ "= ?"
                ,new String[] {productRandom.get(randomCount)},null,null,null);
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int proteinColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN);
            int fatColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAT);
            int ccalColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);

            nameMeal = cursor.getString(nameColIndexx);
            proteinMeal = cursor.getFloat(proteinColIndex);
            ccalMeal = cursor.getFloat(ccalColIndex);
            weightMeal = ((todayProtein * 0.2f - eatingTodayProtein) * 100)/proteinMeal;
            eatingTodayProtein = (proteinMeal * weightMeal)/100;
            eatingTodayCal = eatingTodayCal + (ccalMeal * weightMeal)/100;
            dataLVDN.add(new Item(nameMeal, Integer.toString(Math.round(weightMeal))));
            historyWrite = historyWrite + ";" + nameMeal + ";" + Integer.toString(Math.round(weightMeal));
            contentHistory.put(BDMealHelper.COLUMN_DINNER, historyWrite);
        }
        listViewDinner.setAdapter(new MyAdapterMainActivity(this,dataLVDN));
        listViewDinner.setDivider(null);

//Второй ужин
        productRandom.clear();

        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_CATEGORY +"= ?",
                new String[] {"Фрукты"},null,null,null);
        chekedFavorite = true;
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int favoriteColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAVORITE);
            do {
                if (cursor.getInt(favoriteColIndex) == 1) {
                    productRandom.add(cursor.getString(nameColIndexx));
                    chekedFavorite = false;
                }
            }while (cursor.moveToNext());
        }
        if (chekedFavorite)
        {
            if (cursor.moveToFirst())
            {
                int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
                do {
                    productRandom.add(cursor.getString(nameColIndexx));
                }while (cursor.moveToNext());
            }
        }
        randomCount = randomGenerate.nextInt(productRandom.size());
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME+ "= ?"
                ,new String[] {productRandom.get(randomCount)},null,null,null);
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int proteinColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN);
            int ccalColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);

            nameMeal = cursor.getString(nameColIndexx);
            proteinMeal = cursor.getFloat(proteinColIndex);
            ccalMeal = cursor.getFloat(ccalColIndex);
            weightMeal = (bmrDinnerTwo * 0.3f * 100)/ccalMeal;
            eatingTodayProtein = (proteinMeal * weightMeal)/100;
            eatingTodayCal = eatingTodayCal + (ccalMeal * weightMeal)/100;
            dataLVDN2.add(new Item(nameMeal, Integer.toString(Math.round(weightMeal))));
            historyWrite =";" + nameMeal + ";" + Integer.toString(Math.round(weightMeal));

        }
//мясо
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_CATEGORY +"= ?",
                new String[] {"Молочка"},null,null,null);
        productRandom.clear();
        chekedFavorite = true;
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int favoriteColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAVORITE);
            do {
                if (cursor.getInt(favoriteColIndex) == 1) {
                    productRandom.add(cursor.getString(nameColIndexx));
                    chekedFavorite = false;
                }
            }while (cursor.moveToNext());
        }
        if (chekedFavorite)
        {
            if (cursor.moveToFirst())
            {
                int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
                do {
                    productRandom.add(cursor.getString(nameColIndexx));
                }while (cursor.moveToNext());
            }
        }
        randomCount = randomGenerate.nextInt(productRandom.size());
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_NAME+ "= ?"
                ,new String[] {productRandom.get(randomCount)},null,null,null);
        if (cursor.moveToFirst())
        {
            int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
            int proteinColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN);
            int ccalColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);

            nameMeal = cursor.getString(nameColIndexx);
            proteinMeal = cursor.getFloat(proteinColIndex);
            ccalMeal = cursor.getFloat(ccalColIndex);
            weightMeal = ((todayProtein * 0.05f - eatingTodayProtein) * 100)/proteinMeal;
            eatingTodayCal = eatingTodayCal + (ccalMeal * weightMeal)/100;
            dataLVDN2.add(new Item(nameMeal, Integer.toString(Math.round(weightMeal))));
            historyWrite = historyWrite + ";" + nameMeal + ";" + Integer.toString(Math.round(weightMeal));
            contentHistory.put(BDMealHelper.COLUMN_DINNERTWO, historyWrite);
        }
        listViewDinner2.setAdapter(new MyAdapterMainActivity(this,dataLVDN2));
        listViewDinner2.setDivider(null);

//Овощи
        productRandom.clear();
        ArrayList<Float> calSred = new ArrayList<>();
        cursor = bdMealHelperWrite.query(BDMealHelper.MEAL_TABLE,null,BDMealHelper.COLUMN_CATEGORY +"= ?",
                new String[] {"Овощи"},null,null,null);
        if (cursor.moveToFirst())
        {
            int calIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);
            do {
                calSred.add(cursor.getFloat(calIndex));
            }while (cursor.moveToNext());
        }
        float sredarg = 0;
        for (float i: calSred)
        {
            sredarg = sredarg + i;
        }
        sredarg = sredarg/(calSred.size()+1);
        float result = bmr - eatingTodayCal;
        if (result < 0)
        {
            cheked = false;
        }
        result = (result/sredarg)*100;
        
        TextView vegetableOut = (TextView) findViewById(R.id.main_vegetable);
        vegetableOut.setText(Integer.toString(Math.round(Math.abs(result))));
        historyWrite =  Integer.toString(Math.round(Math.abs(result)));
        contentHistory.put(BDMealHelper.COLUMN_VEGETABLE, historyWrite);
        
        if (refreshDB)
        {
            String historyRead = formatForDateNow.format(new Date());
            cursor = bdMealHelperWrite.query(BDMealHelper.HISTORY_TABLE, null, BDMealHelper.COLUMN_DATE + "= ?",
                    new String[]{historyRead}, null, null, null);
            if (cursor.moveToFirst())
            {
                int tempId = cursor.getInt(cursor.getColumnIndex(BDMealHelper.COLUMN_ID));
                bdMealHelperWrite.update(BDMealHelper.HISTORY_TABLE, contentHistory,
                        BDMealHelper.COLUMN_ID + " = " + Integer.toString(tempId), null);
            }
        }
        else
            bdMealHelperWrite.insert(BDMealHelper.HISTORY_TABLE,null,contentHistory);
        cursor.close();
        bdMealHelperWrite.close();
    }
    private void loadDayRation()
    {
        ArrayList<Item> dataLVBR = new ArrayList<>();
        ArrayList<Item> dataLVBR2 = new ArrayList<>();
        ArrayList<Item> dataLVLCH = new ArrayList<>();
        ArrayList<Item> dataLVDN = new ArrayList<>();
        ArrayList<Item> dataLVDN2 = new ArrayList<>();

        String name = loadText(Registration.NAME_REGDATA);
        int age = Integer.parseInt(loadText(Registration.AGE_REGDATA));
        int weight = Integer.parseInt(loadText(Registration.WEIGHT_REGDATA));
        int goal = Integer.parseInt(loadText(Registration.GOAL_REGDATA));
        int male = Integer.parseInt(loadText(Registration.MALE_REGDATA));
        int growth = Integer.parseInt(loadText(Registration.GROWTH_REGDATA));
        float life = Float.parseFloat(loadText(Registration.LIFE_REGDATA));
        float todayProtein = 0, todayFat = 0, todayCarbo = 0;
        TextView textViewHeader = (TextView) findViewById(R.id.mainHeader);
        TextView textViewHeaderPFC = (TextView) findViewById(R.id.mainPFC);
        TextView textViewBreackfast = (TextView) findViewById(R.id.main_breack);
        TextView textViewBreackfastTwo = (TextView) findViewById(R.id.main_breack2);
        TextView textViewLunch = (TextView) findViewById(R.id.main_lunch);
        TextView textViewDinner = (TextView) findViewById(R.id.main_dinner);
        TextView textViewDinnerTwo = (TextView) findViewById(R.id.main_dinner2);

        listViewBreackfast = (ListView) findViewById(R.id.ListViewBR);
        listViewBreackfast2 = (ListView) findViewById(R.id.ListViewBRTwo);
        listViewLunch = (ListView) findViewById(R.id.ListViewLunch);
        listViewDinner = (ListView) findViewById(R.id.ListViewDinner);
        listViewDinner2 = (ListView) findViewById(R.id.ListViewDinnerTwo);

        Float bmr = 0f;
        switch (male)
        {
            case 0:
                bmr = ((9.99f*weight)+(6.25f*growth)-(4.92f*age)+5)*life;
                break;
            case 1:
                bmr = ((9.99f*weight)+(6.25f*growth)-(4.92f*age)-161)*life;
                break;
            default:
                Toast.makeText(this, "Ошибка \nОбновите регистрационные данные", Toast.LENGTH_SHORT).show();
                registration = new Intent(this, Registration.class);
                startActivity(registration);
                this.finish();
                break;
        }
        switch (goal)
        {
            case 0:
                bmr = bmr - bmr * 0.1f;
                todayProtein = (bmr * 0.18f)/4;
                todayFat = (bmr * 0.32f)/9;
                todayCarbo = (bmr - (todayProtein*4 + todayFat*9))/4;
                break;
            case 1:
                bmr = bmr;
                todayProtein = (bmr * 0.18f)/4;
                todayFat = (bmr * 0.3f)/9;
                todayCarbo = (bmr * 0.52f)/4;
                break;
            case 2:
                bmr = bmr + bmr * 0.1f;
                todayProtein = (bmr * 0.2f)/4;
                todayFat = (bmr * 0.3f)/9;
                todayCarbo = (bmr * 0.5f)/4;
                break;
            default:
                Toast.makeText(this, "Ошибка \nОбновите регистрационные данные", Toast.LENGTH_SHORT).show();
                registration = new Intent(this, Registration.class);
                startActivity(registration);
                this.finish();
                break;
        }
        textViewHeader.setText("Сегодня нужно скушать " + Integer.toString(Math.round(bmr)) + " ккал");
        textViewHeaderPFC.setText("Белков: "+Integer.toString(Math.round(todayProtein))+", Жиров: "+Integer.toString(Math.round(todayFat))+", Углеводов: "+Integer.toString(Math.round(todayCarbo)));
        float bmrBreakfast = bmr * 0.25f;
        float bmrBreakfastTwo = bmr * 0.1f;
        float bmrLunch = bmr * 0.4f;
        float bmrDinner = bmr * 0.2f;
        float bmrDinnerTwo = bmr * 0.05f;
        textViewBreackfast.setText("Завтрак: "+ Integer.toString(Math.round(bmrBreakfast)) +" ккал.");
        textViewBreackfastTwo.setText("Второй Завтрак: "+ Integer.toString(Math.round(bmrBreakfastTwo)) +" ккал.");
        textViewLunch.setText("Обед: "+ Integer.toString(Math.round(bmrLunch)) +" ккал.");
        textViewDinner.setText("Ужин: "+ Integer.toString(Math.round(bmrDinner)) +" ккал.");
        textViewDinnerTwo.setText("Второй Ужин: "+ Integer.toString(Math.round(bmrDinnerTwo)) +" ккал.");

        SQLiteDatabase bdMealHelperRead = bdMealHelper.getReadableDatabase();

        formatForDateNow = new SimpleDateFormat("yyyy.MM.dd");
        String historyRead = formatForDateNow.format( new Date());

//        Cursor cursor = bdMealHelperRead.query(BDMealHelper.HISTORY_TABLE,null,BDMealHelper.COLUMN_DATE +"= ?",
//                new String[] {historyRead},null,null,null);
        Cursor cursor = bdMealHelperRead.query(BDMealHelper.HISTORY_TABLE,null,BDMealHelper.COLUMN_DATE +"= ?",
                new String[] {historyRead},null,null,null);
        if (cursor.moveToFirst())
        {
            int breakfastIndexColumn = cursor.getColumnIndex(BDMealHelper.COLUMN_BREAKFAST);
            int breakfastIndexColumnTwo = cursor.getColumnIndex(BDMealHelper.COLUMN_BREAKFASTTWO);
            int lunchIndexColumn = cursor.getColumnIndex(BDMealHelper.COLUMN_LUNCH);
            int dinnerIndexColumn = cursor.getColumnIndex(BDMealHelper.COLUMN_DINNER);
            int dinnerTwoIndexColumn = cursor.getColumnIndex(BDMealHelper.COLUMN_DINNERTWO);
            int vegColumnIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_VEGETABLE);

            String breakfast = cursor.getString(breakfastIndexColumn);
            String breakfastTwo = cursor.getString(breakfastIndexColumnTwo);
            String lunch = cursor.getString(lunchIndexColumn);
            String dinner = cursor.getString(dinnerIndexColumn);
            String dinnerTwo = cursor.getString(dinnerTwoIndexColumn);
            String vegetable = cursor.getString(vegColumnIndex);

            String[] breakfastSplit = breakfast.split(";");
            dataLVBR.add(new Item(breakfastSplit[1],breakfastSplit[2]));
            dataLVBR.add(new Item(breakfastSplit[3],breakfastSplit[4]));

            String[] breakfastTwoSplit = breakfastTwo.split(";");
            dataLVBR2.add(new Item(breakfastTwoSplit[1],breakfastTwoSplit[2]));
            dataLVBR2.add(new Item(breakfastTwoSplit[3],breakfastTwoSplit[4]));

            String[] lunchSplit = lunch.split(";");
            dataLVLCH.add(new Item(lunchSplit[1],lunchSplit[2]));
            dataLVLCH.add(new Item(lunchSplit[3],lunchSplit[4]));

            String[] dinnerSplit = dinner.split(";");
            dataLVDN.add(new Item(dinnerSplit[1],dinnerSplit[2]));
            dataLVDN.add(new Item(dinnerSplit[3],dinnerSplit[4]));

            String[] dinnerTwoSplit = dinnerTwo.split(";");
            dataLVDN2.add(new Item(dinnerTwoSplit[1],dinnerTwoSplit[2]));
            dataLVDN2.add(new Item(dinnerTwoSplit[3],dinnerTwoSplit[4]));

            listViewBreackfast.setAdapter(new MyAdapterMainActivity(this,dataLVBR));
            listViewBreackfast.setDivider(null);
            listViewBreackfast2.setAdapter(new MyAdapterMainActivity(this,dataLVBR2));
            listViewBreackfast2.setDivider(null);
            listViewLunch.setAdapter(new MyAdapterMainActivity(this,dataLVLCH));
            listViewLunch.setDivider(null);
            listViewDinner.setAdapter(new MyAdapterMainActivity(this,dataLVDN));
            listViewDinner.setDivider(null);
            listViewDinner2.setAdapter(new MyAdapterMainActivity(this,dataLVDN2));
            listViewDinner2.setDivider(null);
            TextView vegetableOut = (TextView) findViewById(R.id.main_vegetable);
            vegetableOut.setText(vegetable);
        }
        cursor.close();
        bdMealHelperRead.close();


    }

}




