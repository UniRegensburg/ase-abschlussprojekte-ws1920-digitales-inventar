package com.example.digitalesinventar;

//DataModel for the Firebase-entries to be set to a custom ListAdapter
public class DataModelItemList {
    String itemName;
    String itemLocation;
    String itemCategory;
    long timestamp;

    public DataModelItemList(String itemName, String itemLocation, String itemCategory, long timestamp) {
        this.itemName = itemName;
        this.itemLocation = itemLocation;
        this.itemCategory = itemCategory;
        this.timestamp = timestamp;
        //to be extended with other attributes in upcoming releases..
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String itemToString() {
        return "Item: " + getItemName() + ", hinzugefügt am: " + getTimestamp() + ", in der Kategorie: " + getItemCategory() + ", abgelegt an diesem Ort: " + getItemLocation() + ".";
    }
}
