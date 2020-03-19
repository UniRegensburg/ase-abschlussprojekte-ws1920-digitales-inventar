package com.example.digitalesinventar;

import java.io.Serializable;

//DataModel for the Firebase-entries to be set to a custom ListAdapter
public class DataModelItemList implements Serializable {
    String itemName;
    String itemLocation;
    String itemCategory;
    String buyDate;
    double value;
    boolean isChecked;
    long timestamp; //as ID

    public DataModelItemList() {

    }
    public DataModelItemList(String itemName, String itemCategory, String itemLocation, String buyDate, double value, boolean isChecked, long timestamp) {
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemLocation = itemLocation;
        this.buyDate = buyDate;
        this.value = value;
        this.isChecked = isChecked;
        this.timestamp = timestamp;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public String getItemBuyDate() {
        return buyDate;
    }

    public double getItemValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    //for logging
    public String itemToString() {
        return "Item: " + getItemName() + ", hinzugefügt am: " + getTimestamp() + ", in der Kategorie: " + getItemCategory() + ", abgelegt an diesem Ort: " + getItemLocation() + ".";
    }
}
