package com.example.digitalesinventar;

import android.text.InputFilter;
import android.text.Spanned;

import java.text.SimpleDateFormat;
import java.util.Date;

//helper class for input controlling
public class InputChecker {
	//these inputs are not allowed in editText for ItemName, location,
	static String blockCharacterSet = "\n";

	//input filter to avoid userinput problems
	public static InputFilter filter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
															 Spanned dest, int dstart, int dend) {

			if ((source != null) && blockCharacterSet.contains("" + source)) {
				try {
					NewItemActivity.hideKeyboard();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					EditItemActivity.hideKeyboard();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "";
			}
			return null;
		}
	};

	//method to avoid no input given by the user
	public static boolean checkEmptyInput(String input) {
		if (input.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	//date formatter
	public static Date formattedDate(DataModelItemList item) {
		long itemTs = Long.parseLong(String.valueOf(item.getTimestamp()));
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		Date resultdate = new Date(itemTs);
		return resultdate;
	}
}
