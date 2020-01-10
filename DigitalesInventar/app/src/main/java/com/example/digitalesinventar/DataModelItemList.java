package com.example.digitalesinventar;

public class DataModelItemList {
    String itemName;
    String timestamp;

    public DataModelItemList (String itemName, String timestamp) {
        this.itemName = itemName;
        this.timestamp = timestamp;
    }

    public String getItemName() {
        return itemName;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
