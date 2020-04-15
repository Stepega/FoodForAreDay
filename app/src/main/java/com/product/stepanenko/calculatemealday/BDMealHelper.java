package com.product.stepanenko.calculatemealday;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/**
 * Class для работы с бд 17.05.2017.
 */

public class BDMealHelper extends SQLiteOpenHelper
{
    static final String MEAL_TABLE = "mealTable";
    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_PROTEIN = "protein";
    static final String COLUMN_FAT = "fat";
    static final String COLUMN_CARBOHYDRATES = "carbohydrates";
    static final String COLUMN_CALORIES = "calories";
    static final String COLUMN_FAVORITE = "favorite";
    static final String COLUMN_CATEGORY = "category";
    static final String COLUMN_PROBABILITY = "probability";

    static final String HISTORY_TABLE = "historyTable";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_BREAKFAST = "breakfast";
    static final String COLUMN_BREAKFASTTWO = "breakfastTwo";
    static final String COLUMN_LUNCH = "lunch";
    static final String COLUMN_DINNER = "dinner";
    static final String COLUMN_DINNERTWO = "dinnerTwo";
    static final String COLUMN_VEGETABLE = "vegetable";

    public BDMealHelper (Context context)
    {
        super (context, "BDMeal",null,1);  //конструктор суперкласса
    }
    @Override
    public void onCreate (SQLiteDatabase db) //создаем таблицу с полями
    {
        db.execSQL("create table mealTable("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "protein real,"
                + "fat real,"
                + "carbohydrates real,"
                + "calories real,"
                + "favorite integer,"
                + "category text,"
                + "probability integer" + ");");
        db.execSQL("create table historyTable("
                + "id integer primary key autoincrement,"
                + "date text,"
                + "breakfast text,"
                + "breakfastTwo text,"
                + "lunch text,"
                + "dinner text,"
                + "dinnerTwo text,"
                + "vegetable text" + ");");
        AddBaseMeal();
        for(int i = 0; i < contentList.size();i++)
        {
            db.insert(MEAL_TABLE,null,contentList.get(i));
        }


    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {}
    static public ContentValues createContentValuesMealTable(String name, String protein, String fat, String carbohydrates,
                                                     String calories, String category, Integer favorite, Integer probability)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_PROTEIN,Float.parseFloat(protein));
        contentValues.put(COLUMN_FAT,Float.parseFloat(fat));
        contentValues.put(COLUMN_CARBOHYDRATES, Float.parseFloat(carbohydrates));
        contentValues.put(COLUMN_CALORIES,Float.parseFloat(calories));
        contentValues.put(COLUMN_FAVORITE,favorite);
        contentValues.put(COLUMN_CATEGORY,category);
        contentValues.put(COLUMN_PROBABILITY,probability);
        return contentValues;
    }

    public ArrayList<ContentValues> contentList = new ArrayList<>();
    public void AddBaseMeal()
    {
        contentList.add(createContentValuesMealTable("Баклажаны","1.2","0.1","5.1","24","Овощи",0,4));
        contentList.add(createContentValuesMealTable("Бобы","6","0.1","8.5","60","Овощи",0,4));
        contentList.add(createContentValuesMealTable("Кабачки","0.6","0.3","4.9","23","Овощи",0,4));
        contentList.add(createContentValuesMealTable("Капуста белокаченная","1.8","0.1","4.7","27","Овощи",0,4));
        contentList.add(createContentValuesMealTable("Картофель","2","0.4","16.3","80","Овощи",0,4));
        contentList.add(createContentValuesMealTable("Ананас","0.4","0.0","11.8","48","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Апельсин","0.9","0.0","8.4","37","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Бананы","1.5","0.0","22.0","94","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Баранина","16.3","15.3","0.0","202","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Брусника","0.7","0.0","8.6","37","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Ветчина","22.6","20.9","0.0","278","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Виноград","1.0","1.0","18.0","85","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Вишня","1.5","0.0","73.0","298","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Геркулес","13.1","6.2","65.7","371","Крупы",0,4));
        contentList.add(createContentValuesMealTable("Говядина","18.9","12.4","0.0","187","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Горбуша","21.0","7.0","0.0","147","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Грейпфрут","0.9","0.0","7.3","32","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Груша","2.3","0.0","62.1","257","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Индейка","21.6","12.0","0.8","197","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Кальмар","18.0","0.3","0.0","74","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Клюква","0.5","0.0","4.8","21","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Крупа гречневая","12.6","2.6","68.0","345","Крупы",0,4));
        contentList.add(createContentValuesMealTable("Крупа пшеничная","12.7","1.1","70.6","343","Крупы",0,4));
        contentList.add(createContentValuesMealTable("Крупа овсяная","12.0","6.0","67.0","370","Крупы",0,4));
        contentList.add(createContentValuesMealTable("Макаронные изделия","11.0","0.9","74.2","348","Крупы",0,4));
        contentList.add(createContentValuesMealTable("Мед","0.8","0.0","80.3","324","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Минтай","15.9","0.7","0.0","69","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Рис","8.0","1.0","76.0","345","Крупы",0,4));
        contentList.add(createContentValuesMealTable("Свинина","16.4","27.8","0.0","315","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Семга","20.8","15.1","0.0","219","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Творог","16.7","9.0","1.3","153","Молочка",0,4));
        contentList.add(createContentValuesMealTable("Яйцо куриное","12.7","11.5","0.7","157","Молочка",0,4));
        contentList.add(createContentValuesMealTable("Сыр Голландский","26","26.8","0","352","Молочка",0,4));
        contentList.add(createContentValuesMealTable("Абрикос","0.9","0.1","9.0","44","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Фасоль","3","0.3","3","31","Овощи",0,4));
        contentList.add(createContentValuesMealTable("Яблоки","0.3","0.36","8","44","Фрукты",0,4));
        contentList.add(createContentValuesMealTable("Молоко","2.8","2.5","4.7","52","Молочка",0,4));
        contentList.add(createContentValuesMealTable("Кефир","3","2.5","3.8","30","Молочка",0,4));
        contentList.add(createContentValuesMealTable("Брынза","18","20.1","0","260","Молочка",0,4));
        contentList.add(createContentValuesMealTable("Скумбрия","19.6","14.7","0","211","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Креветка","18.9","2.2","0","95","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Морская капуста","0.9","0.2","0","5","Овощи",0,4));
        contentList.add(createContentValuesMealTable("Печень говяжья","17.9","3.7","0","105","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Сердце кур","15.8","10.3","0.8","159","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Куриное филе","22.2","1.9","0","130","Мясо",0,4));
        contentList.add(createContentValuesMealTable("Рис Бурый","7.4","1.8","72.9","337","Крупы",0,4));
        contentList.add(createContentValuesMealTable("Рис красный Рубин","7.5","3.0","68","330","Крупы",0,4));
        contentList.add(createContentValuesMealTable("Ячменная крупа","10","1.3","71.7","324","Крупы",0,4));
        contentList.add(createContentValuesMealTable("Шампиньоны","3","0.5","0.1","20","Овощи",0,4));
        contentList.add(createContentValuesMealTable("Опята","2.2","1.2","0.5","18","Овощи",0,4));
        contentList.add(createContentValuesMealTable("Маслята","2.4","0.7","1.7","19","Овощи",0,4));


    }


}
