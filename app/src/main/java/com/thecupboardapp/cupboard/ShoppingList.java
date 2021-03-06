package com.thecupboardapp.cupboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kyle on 1/12/2018.
 * Edited by Jacob Strom on 3/3/2018.
 */

public class ShoppingList implements Comparable<ShoppingList> {
    private UUID mId;
    private String mFirebaseId;
    private String mName;
    private List<ShoppingListItem> mShoppingShoppingListItems;
    private Long lastModified;

    public ShoppingList() {
       mId = UUID.randomUUID();
       mName = "New Shopping List";
       mShoppingShoppingListItems = new ArrayList<ShoppingListItem>();
       lastModified = System.currentTimeMillis();
    }

    public ShoppingList(String name, List<ShoppingListItem> items) {
        mId = UUID.randomUUID();
        mName = name;
        mShoppingShoppingListItems = items;
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getFirebaseId() {
        return mFirebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        mFirebaseId = firebaseId;
    }


    public List<ShoppingListItem> getShoppingListItems() {
        return mShoppingShoppingListItems;
    }

    public void addShoppingListItem(ShoppingListItem item){
        mShoppingShoppingListItems.add(item);
    }

    public void removeShoppingListItem(UUID id) {
        int index = -1;
        for (int i = 0; i < mShoppingShoppingListItems.size(); i++) {
            if (id == mShoppingShoppingListItems.get(i).getId()) {
                index = i;
            }
        }

        if (index != -1) {
            mShoppingShoppingListItems.remove(index);
        }
    }

    public int compareTo(ShoppingList s2) {
        long l = s2.getLastModified()- this.getLastModified();
        //return Math.toIntExact(Long.parseLong(f1.getExpiration()) - Long.parseLong(f2.getExpiration()));
        //return safeLongToInt(l);
        int retVal = 0;
        if (l > 0)  retVal = 1;
        else if (l < 0) retVal = -1;
        return retVal;
    }

    /*private static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }*/

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }
    
}
