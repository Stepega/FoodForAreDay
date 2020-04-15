package com.product.stepanenko.calculatemealday;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Серега on 23.05.2017.
 */

public class MealEditNameDialog extends DialogFragment
{
    String tempName;
    BDMealHelper bdMealHelper;
    SQLiteDatabase bdMealWrite;
    String[] selectionArgs;
    Cursor cursor;
    int tempSpinnerPosition;
    int keyID;
    static MealEditNameDialog newInstance (String name)
    {
        MealEditNameDialog f = new MealEditNameDialog();
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
        selectionArgs = new String[] { tempName };

         final EditText editText = new EditText(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(layoutParams);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);






        AlertDialog.Builder activeDialog = new AlertDialog.Builder(getActivity());

            activeDialog.setTitle(tempName);
            activeDialog.setMessage("Введите новое название");
            activeDialog.setView(editText);


        activeDialog.setPositiveButton("Хорошо", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                if (editText.getText().toString().matches("")!= true)
                {
                    boolean check = true;
                    bdMealWrite = bdMealHelper.getWritableDatabase();
                    cursor = bdMealWrite.query(BDMealHelper.MEAL_TABLE, null, BDMealHelper.COLUMN_NAME + " = ?", selectionArgs, null, null, null);
                    if (cursor.moveToFirst()) {
                        int idColIndex = cursor.getColumnIndex(BDMealHelper.COLUMN_ID);
                        keyID = cursor.getInt(idColIndex);
                    }
                    cursor = bdMealWrite.query(BDMealHelper.MEAL_TABLE, null, BDMealHelper.COLUMN_NAME + "= ?",
                            new String[]{editText.getText().toString()}, null, null, null);
                    if (cursor.moveToFirst()) {
                        if (keyID != cursor.getInt(cursor.getColumnIndex(BDMealHelper.COLUMN_ID))) {
                            Toast.makeText(getActivity(), "Ошибка\nПродукт с таким названием уже существует.", Toast.LENGTH_SHORT).show();
                            cursor.close();
                            bdMealWrite.close();
                            check = false;
                        }
                    }
                    if (check)
                    {
                        ContentValues values = new ContentValues();
                        values.put(BDMealHelper.COLUMN_NAME, editText.getText().toString());
                        bdMealWrite.update(BDMealHelper.MEAL_TABLE, values, BDMealHelper.COLUMN_ID + "=" + Integer.toString(keyID), null);
                        bdMealWrite.close();
                        cursor.close();
                    }


                }
            }
        });
        activeDialog.setCancelable(false);

        return activeDialog.create();
    }
}
