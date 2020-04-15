package com.product.stepanenko.calculatemealday;

/**
 * Created by Серега on 04.06.2017.
 */

public class Item {

    String name;
    String weight;

    Item(String n, String w){
        this.name=n + ":";
        this.weight=w + " грамм";
    }

    //Всякие гетеры и сеттеры
    public String getName() {
        return name.replace(":","");
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getWeight() {
        return weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }

}