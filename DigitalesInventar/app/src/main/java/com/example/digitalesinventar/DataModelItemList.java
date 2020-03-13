package com.example.digitalesinventar;

import java.util.Date;

//DataModel for the Firebase-entries to be set to a custom ListAdapter
public class DataModelItemList implements Cloneable{
    String itemName;
    String itemLocation;
    String itemCategory;
    Date buyDate;
    double value;
    String borrowState;
    String borrowPerson;
    Date borrowDate;
    long timestamp; //as ID

    public DataModelItemList() {

    }
    public DataModelItemList(String itemName, String itemCategory, String itemLocation, Date buyDate, double value, String borrowState, String borrowPerson, Date borrowDate,long timestamp) {
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemLocation = itemLocation;
        this.buyDate = buyDate;
        this.value = value;
        this.borrowState = borrowState;
        this.borrowPerson = borrowPerson;
        this.borrowDate = borrowDate;
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
