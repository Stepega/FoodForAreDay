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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Random;

public class Profile extends AppCompatActivity {
    SharedPreferences fileRegData;
    Intent registration;
    Toolbar toolbar;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Профиль");
        setSupportActionBar(toolbar);

        String name = loadText(Registration.NAME_REGDATA);
        int age = Integer.parseInt(loadText(Registration.AGE_REGDATA));
        int weight = Integer.parseInt(loadText(Registration.WEIGHT_REGDATA));
        int goal = Integer.parseInt(loadText(Registration.GOAL_REGDATA));
        int male = Integer.parseInt(loadText(Registration.MALE_REGDATA));
        int growth = Integer.parseInt(loadText(Registration.GROWTH_REGDATA));
        float life = Float.parseFloat(loadText(Registration.LIFE_REGDATA));
        int countweight = Integer.parseInt(loadText(Registration.COUNTWEIGHT_REGDATA));

        TextView textView = (TextView) findViewById(R.id.profile_title);
        textView.setText("Здравствуйте, " + name);
        editText = (EditText) findViewById(R.id.profile_edit_text);

        drawChart();


    }
    private void drawChart()
    {
        //График Рисуется!!!!Лайн
        LineData lineData = new LineData(getXAxisValues(), getDataSet());
        LineChart chart = (LineChart) findViewById(R.id.chart);
//        chart.setData(barData);
        chart.setData(lineData);
        chart.setDescription("График изменения веса");
        chart.animateXY(2000, 2000);
        chart.invalidate();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_edit_profile:
                intent = new Intent(this, EditRegData.class);
                startActivity(intent);
                return true;
            case R.id.action_history_profile:
                intent = new Intent(this, HistoryMeal.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    void savedText(String keyWrite, String dataWrite)
    {
        fileRegData = getSharedPreferences(Registration.FILE_NAME_REGDATA,MODE_PRIVATE);
        SharedPreferences.Editor editor = fileRegData.edit();
        editor.putString(keyWrite,dataWrite);
        editor.commit();
    }
    String loadText(String keyOut)
    {
        fileRegData = getSharedPreferences(Registration.FILE_NAME_REGDATA,MODE_PRIVATE);
        String savedText = fileRegData.getString(keyOut, "");
        return  savedText;
    }
    public void setData(View view)
    {
        if (editText.getText().toString().matches(""))
        {}else
        {
            ArrayList<String> weightData = new ArrayList<>();
            String weight = loadText(Registration.WEIGHT_REGDATA);
            int countweight = Integer.parseInt(loadText(Registration.COUNTWEIGHT_REGDATA));
            savedText(Registration.WEIGHT_REGDATA + Integer.toString(countweight),weight);
            savedText(Registration.WEIGHT_REGDATA, editText.getText().toString());
            savedText(Registration.COUNTWEIGHT_REGDATA, Integer.toString(countweight +1));
            editText.setText("");
            drawChart();
        }

    }


    private ArrayList<LineDataSet> getDataSet() {
        ArrayList<LineDataSet> dataSets = null;

        int countweight = Integer.parseInt(loadText(Registration.COUNTWEIGHT_REGDATA));
        ArrayList<Entry> valueSet1 = new ArrayList<>();
        for (int i = 0; i < countweight; i++)
        {
            valueSet1.add(new Entry(Integer.parseInt(loadText(Registration.WEIGHT_REGDATA + Integer.toString(i))),i));
        }
        valueSet1.add(new Entry(Integer.parseInt(loadText(Registration.WEIGHT_REGDATA)), countweight));

//        valueSet1.add(new Entry(40f, 0));
//        valueSet1.add(new Entry(60f, 1));
        LineDataSet barDataSet1 = new LineDataSet(valueSet1, "Твой Вес");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }
    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        int countweight = Integer.parseInt(loadText(Registration.COUNTWEIGHT_REGDATA));
        for (int i = 0; i < countweight; i++)
        {
            xAxis.add(Integer.toString(i + 1));
        }
        xAxis.add(Integer.toString(countweight + 1));
//        xAxis.add("JAN");
//        xAxis.add("FEB");
//        xAxis.add("MAR");
//        xAxis.add("APR");
//        xAxis.add("MAY");
//        xAxis.add("JUN");
        return xAxis;
    }

}
