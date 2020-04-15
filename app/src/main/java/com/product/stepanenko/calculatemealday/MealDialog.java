package com.product.stepanenko.calculatemealday;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Серега on 19.05.2017.
 */

public class MealDialog extends DialogFragment
{
    String tempName;
    BDMealHelper bdMealHelper;
    SQLiteDatabase bdMealWrite;
    String[] selectionArgs;
    Cursor cursor;
    int tempSpinnerPosition;
    int keyID;
    static MealDialog newInstance (String name)
    {
        MealDialog f = new MealDialog();
        Bundle args = new Bundle();
        args.putString("name", name);
        f.setArguments(args);

        return f;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        tempName = getArguments().getString("name");
        bdMealHelper = new BDMealHelper(getActivity());
        bdMealWrite = bdMealHelper.getWritableDatabase();
        selectionArgs = new String[] { tempName };

        String[] dataSpinner = {"Не избраное", "Избранное"};
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final Spinner spinner = new Spinner(getActivity());
        spinner.setLayoutParams(layoutParams);
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, dataSpinner);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        cursor = bdMealWrite.query(BDMealHelper.MEAL_TABLE,null,"name = ?",selectionArgs,null,null,null);
        int nameColIndexx = cursor.getColumnIndex(BDMealHelper.COLUMN_NAME);
        int proteinColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_PROTEIN);
        int fatColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAT);
        int carboColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CARBOHYDRATES);
        int favoriteColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_FAVORITE);
        int idColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_ID);
        int ccalColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_CALORIES);
        AlertDialog.Builder activeDialog = new AlertDialog.Builder(getActivity());
        if (cursor.moveToFirst())
        {
            keyID = cursor.getInt(idColIndex);
            activeDialog.setTitle(cursor.getString(nameColIndexx));
            activeDialog.setMessage("Пищевая ценность на 100гр \nБелки: " + cursor.getString(proteinColIndex) +
                    "\nЖиры: " + cursor.getString(fatColIndex) + "\nУглеводы: " + cursor.getString(carboColIndex) +
                    "\nКалорий на 100гр: " + cursor.getString(ccalColIndex));
            spinner.setAdapter(adapterSpinner);
            spinner.setSelection(cursor.getInt(favoriteColIndex));
            spinner.setPrompt("Измените тип:");
            activeDialog.setView(spinner);
            tempSpinnerPosition = cursor.getInt(favoriteColIndex);

        }
        cursor.close();


        activeDialog.setPositiveButton("Хорошо", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                ContentValues values = new ContentValues();
                values.put(BDMealHelper.COLUMN_FAVORITE,tempSpinnerPosition);
                bdMealWrite.update(BDMealHelper.MEAL_TABLE,values,BDMealHelper.COLUMN_ID + "=" + Integer.toString(keyID), null);
                bdMealWrite.close();
            }
        });
        activeDialog.setCancelable(false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tempSpinnerPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return activeDialog.create();
    }
}
