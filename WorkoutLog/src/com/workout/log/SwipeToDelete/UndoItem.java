package com.workout.log.SwipeToDelete;

import android.os.Parcel;
import android.os.Parcelable;

public class UndoItem implements Parcelable {
	   public Object[] items;
	   public int[] itemPosition;

	   public UndoItem(Object[] items, int[] itemPosition) {
	       this.items = items;
	       this.itemPosition = itemPosition;
	   }

	   protected UndoItem(Parcel in) {}

	   public int describeContents() {
	       return 0;
	   }

	   public void writeToParcel(Parcel dest, int flags) {}

	   @SuppressWarnings("rawtypes")
	   public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
	       public UndoItem createFromParcel(Parcel in) {
	           return new UndoItem(in);
	       }

	       public UndoItem[] newArray(int size) {
	           return new UndoItem[size];
	       }
	   };
}