package com.product.stepanenko.calculatemealday;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryMeal extends AppCompatActivity {
    BDMealHelper bdMealHelper;
    ArrayList<String> nameMealList;
    ArrayAdapter<String> adapterList;
    ListView bdList;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("История питания");
        setSupportActionBar(toolbar);

        bdList = (ListView) findViewById(R.id.mealListView);
        registerForContextMenu(bdList);

        bdMealHelper = new BDMealHelper(this);
        nameMealList = new ArrayList<>();
        SQLiteDatabase bdMealRead = bdMealHelper.getReadableDatabase();
        cursor = bdMealRead.query(BDMealHelper.HISTORY_TABLE, null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            int dateColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_DATE);
            do {
                nameMealList.add(cursor.getString(dateColIndex));
            }while (cursor.moveToNext());
        }
        cursor.close();
        bdMealRead.close();
        adapterList = new ArrayAdapter(this,android.R.layout.simple_list_item_1,nameMealList);
        bdList.setAdapter(adapterList);


        bdList.setOnItemClickListener(new AdapterView.OnItemClickListener() { //переназначили клик на элемент списка
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String element = adapterList.getItem(position);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HistoryMeal.this);

                SQLiteDatabase bdMealRead = bdMealHelper.getReadableDatabase();
                cursor = bdMealRead.query(BDMealHelper.HISTORY_TABLE, null,BDMealHelper.COLUMN_DATE + "= ?",new String[] {element}
                        ,null,null,null);
                if(cursor.moveToFirst())
                {
                    int dateIndexColumn = cursor.getColumnIndex(BDMealHelper.COLUMN_DATE);
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
                    String[] breakfastTwoSplit = breakfastTwo.split(";");
                    String[] lunchSplit = lunch.split(";");
                    String[] dinnerSplit = dinner.split(";");
                    String[] dinnerTwoSplit = dinnerTwo.split(";");


                    String outDayMeal = "Завтрак: \n" +  breakfastSplit[1] + " " + breakfastSplit[2] + " Грамм\n" +
                            breakfastSplit[3] + " " + breakfastSplit[4] + " Грамм\n" +
                            "\nВторой завтрак: \n" +  breakfastTwoSplit[1] + " " + breakfastTwoSplit[2] + " Грамм\n" +
                            breakfastTwoSplit[3] + " " + breakfastTwoSplit[4] + " Грамм\n" +
                            "\nОбед: \n" +  lunchSplit[1] + " " + lunchSplit[2] + " Грамм\n" +
                            lunchSplit[3] + " " + lunchSplit[4] + " Грамм\n" +
                            "\nУжин: \n" +  dinnerSplit[1] + " " + dinnerSplit[2] + " Грамм\n" +
                            dinnerSplit[3] + " " + dinnerSplit[4] + " Грамм\n" +
                            "\nВторой ужин: \n" +  dinnerTwoSplit[1] + " " + dinnerTwoSplit[2] + " Грамм\n" +
                            dinnerTwoSplit[3] + " " + dinnerTwoSplit[4] + " Грамм\n" +
                            "\nОвощи: \n" + vegetable + " Грамм";

                    dialogBuilder.setTitle(cursor.getString(dateIndexColumn));
                    dialogBuilder.setMessage(outDayMeal);
                    dialogBuilder.setCancelable(false);
                    dialogBuilder.setPositiveButton("Хорошо",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                    AlertDialog alert = dialogBuilder.create();
                    alert.show();
                }



            }
        });
    }
}


