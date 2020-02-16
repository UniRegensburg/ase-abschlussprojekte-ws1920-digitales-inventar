package com.example.digitalesinventar;

//DataModel for the Firebase-entries to be set to a custom ListAdapter
public class DataModelItemList {
    String itemName;
    String itemLocation;
    long timestamp;

    public DataModelItemList(String itemName, String itemLocation, long timestamp) {
        this.itemName = itemName;
        this.itemLocation = itemLocation;
        this.timestamp = timestamp;
        //to be extended with other attributes in upcoming releases..
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String itemToString() {
        return "Item: " + getItemName() + ", hinzugefügt am: " + getTimestamp() + ", abgelegt an diesem Ort: " + getItemLocation() + ".";
    }
}
