package com.example.digitalesinventar;

//DataModel for the Firebase-entries to be set to a custom ListAdapter
public class DataModelItemList implements Cloneable{
    String itemName;
    String itemLocation;
    String itemCategory;
    long timestamp;

    public DataModelItemList(String itemName, String itemCategory, String itemLocation, long timestamp) {
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemLocation = itemLocation;
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

    public long getTimestamp() {
        return timestamp;
    }

    public String itemToString() {
        return "Item: " + getItemName() + ", hinzugefügt am: " + getTimestamp() + ", in der Kategorie: " + getItemCategory() + ", abgelegt an diesem Ort: " + getItemLocation() + ".";
    }

    @Override
    public DataModelItemList clone()
    {
        DataModelItemList clone;
        try
        {
            clone = (DataModelItemList) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new Error();
        }

        return clone;
    }

}
