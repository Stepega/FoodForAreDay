package com.product.stepanenko.calculatemealday;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MealFavoriteActivity extends AppCompatActivity {

    BDMealHelper bdMealHelper;
    ArrayList<String> nameMealList;
    ArrayAdapter<String> adapterList;
    ListView bdList;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        bdList = (ListView) findViewById(R.id.mealListView);
        registerForContextMenu(bdList);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//Сделать автообновление после изменения позиции!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Избранное");
        setSupportActionBar(toolbar);

       registerForContextMenu(bdList);

        bdMealHelper = new BDMealHelper(this);

        bdList = (ListView) findViewById(R.id.mealListView);
        bdMealHelper = new BDMealHelper(this);
        nameMealList = new ArrayList<>();
        SQLiteDatabase bdMealRead = bdMealHelper.getReadableDatabase();
        cursor = bdMealRead.query("mealTable",null, BDMealHelper.COLUMN_FAVORITE + "=" + "1",null,null,null,null);
        if(cursor.moveToFirst())
        {
            int nameColIndex = cursor.getColumnIndex("name");
            do {
                nameMealList.add(cursor.getString(nameColIndex));
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

                MealDialog dialogFragment = MealDialog.newInstance(element);
                dialogFragment.show(getFragmentManager(),"");
            }
        });
    }
    public void  onBackPressed()
    {
        bdMealHelper.close();
        this.finish();
    }
    //Создаем меню на тулбаре
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meal_favorite, menu);
        return true;
    }
//Обрабатываем нажатие кнопок на тулбаре
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_favorite_sel:
                intent = new Intent(this, MealActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.action_add:
                intent = new Intent(this, AddNewMealActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Настраиваем контекст меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.edit_meal_contextmenu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String selectArg = adapterList.getItem(info.position);
        switch (item.getItemId()){
            case R.id.rename_contextitem:
                MealEditNameDialog nameDialog = MealEditNameDialog.newInstance(selectArg);
                nameDialog.show(getFragmentManager(),"");
                return true;
            case R.id.edit_contextitem:
                Intent intent = new Intent(this, EditMealActivity.class);
                intent.putExtra(BDMealHelper.COLUMN_NAME,selectArg);
                startActivity(intent);
                return true;
            case R.id.delete_contextitem:
                SQLiteDatabase bdMealHelperWrite = bdMealHelper.getWritableDatabase();
                bdMealHelperWrite.delete(BDMealHelper.MEAL_TABLE,BDMealHelper.COLUMN_NAME + "= ?", new String[] {selectArg});
                bdMealHelperWrite.close();

                adapterList.remove(selectArg);
                bdList.setAdapter(adapterList);
                return true;
            default: return true;

        }
    }
}
