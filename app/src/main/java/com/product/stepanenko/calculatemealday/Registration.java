package com.product.stepanenko.calculatemealday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.SharedPreferences.Editor;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Registration extends AppCompatActivity {

    String[] dataSpinner = {"Похудеть","Сохранить вес","Нарастить мышцы"};
    int tempSpinnerPosition = 0;
    int tempMalePosition = 0;
    int tempLifePosition = 0;
    String[] dataLife = {"сидячий образ жизни,\n сидячая работа"
            ,"легкая активность, \nнемного дневной активности"
            ,"средняя активность, \nтренировки 3-5 раз в неделю)"
            ,"высокая активность,\n тяжелые тренировки 6-7 раз в неделю"
            ,"экстремально-высокая активность,\n ежедневные тренировки"};
    EditText edText;
    public SharedPreferences fileRegData;
    static final String FILE_NAME_REGDATA = "RegistrationData";
    static final String NAME_REGDATA = "name";
    static final String AGE_REGDATA = "nage";
    static final String GROWTH_REGDATA = "growth";
    static final String WEIGHT_REGDATA = "weight";
    static final String GOAL_REGDATA = "goal";
    static final String MALE_REGDATA = "male";
    static final String LIFE_REGDATA = "life";
    static final String COUNTWEIGHT_REGDATA = "countWeight";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Регистрация");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataSpinner);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.goalSpinner);
        spinner.setAdapter(adapterSpinner);
        spinner.setSelection(0);
        spinner.setPrompt("Выберите вашу цель");
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
        ArrayAdapter<String> adapterSpinnerLife = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataLife);
        adapterSpinnerLife.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinnerLife = (Spinner) findViewById(R.id.lifeSpinner);
        spinnerLife.setAdapter(adapterSpinnerLife);
        spinnerLife.setSelection(0);
        spinnerLife.setPrompt("Выберите ваш образ жизни");
        spinnerLife.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tempLifePosition = position;
                ((TextView) parent.getChildAt(0)).setTextSize(20);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RadioGroup radioGroupMale = (RadioGroup) findViewById(R.id.radio_group_registration);
        RadioButton radioButton = (RadioButton) findViewById(R.id.radio_button_man);
        radioButton.setChecked(true);
        radioGroupMale.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.radio_button_man:
                        tempMalePosition = 0;
                        break;
                    case R.id.radio_button_woman:
                        tempMalePosition = 1;
                        break;
                }
            }
        });

    }

    void savedText(String keyWrite, String dataWrite)
    {
        fileRegData = getSharedPreferences(FILE_NAME_REGDATA,MODE_PRIVATE);
        Editor editor = fileRegData.edit();
        editor.putString(keyWrite,dataWrite);
        editor.commit();
    }
    void loadText(String keyOut)
    {
        fileRegData = getSharedPreferences(FILE_NAME_REGDATA,MODE_PRIVATE);
        String savedText = fileRegData.getString(keyOut, "");
        Toast.makeText(this, savedText, Toast.LENGTH_SHORT).show();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save_registration:
                String name, age,growth,weight;
                edText = (EditText) findViewById(R.id.nameView);
                name = edText.getText().toString();
                edText = (EditText) findViewById(R.id.ageView);
                age = edText.getText().toString();
                edText = (EditText) findViewById(R.id.growthView);
                growth = edText.getText().toString();
                edText = (EditText) findViewById(R.id.weightView);
                weight = edText.getText().toString();

                if (name.matches("") || age.matches("") || growth.matches("") || weight.matches(""))
                {
                    Toast.makeText(this,"Заполните все поля", Toast.LENGTH_SHORT).show();
                    return true;
                }
                float writeLife = 1.35f;
                switch (tempLifePosition)
                {
                    case 0:
                        writeLife = 1.2f;
                        break;
                    case 1:
                        writeLife = 1.35f;
                        break;
                    case 2:
                        writeLife = 1.55f;
                        break;
                    case 3:
                        writeLife = 1.75f;
                        break;
                    case 4:
                        writeLife = 1.95f;
                        break;
                }
                savedText(NAME_REGDATA,name);
                savedText(AGE_REGDATA,age);
                savedText(GROWTH_REGDATA, growth);
                savedText(WEIGHT_REGDATA,weight);
                savedText(GOAL_REGDATA, Integer.toString(tempSpinnerPosition)); //кидаем значение из спиннера
                savedText(MALE_REGDATA, Integer.toString(tempMalePosition));
                savedText(LIFE_REGDATA, Float.toString(writeLife));
                savedText(COUNTWEIGHT_REGDATA,"0");
                Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
