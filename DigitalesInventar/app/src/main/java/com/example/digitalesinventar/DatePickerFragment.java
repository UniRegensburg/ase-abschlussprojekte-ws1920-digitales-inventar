package com.example.digitalesinventar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
	implements DatePickerDialog.OnDateSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
		dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
		return  dialog;
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
//do sth
		month += 1;
		try {
		NewItemActivity.getDateView().setText(day + "." + month + "." + year);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			EditItemActivity.getDateView().setText(day + "." + month + "." + year);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}