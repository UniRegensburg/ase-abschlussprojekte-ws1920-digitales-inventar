package com.example.digitalesinventar;

//DataModel for the Firebase-entries to be set to a custom ListAdapter
public class DataModelItemList {
    String itemName;
    long timestamp;

    public DataModelItemList (String itemName, long timestamp) {
        this.itemName = itemName;
        this.timestamp = timestamp;
        //to be extended with other attributes in upcoming releases..
    }

    public String getItemName() {
        return itemName;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
