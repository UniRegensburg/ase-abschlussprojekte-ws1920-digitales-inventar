package com.example.digitalesinventar;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

	/**
	 * Some Issues:
	 * Sucheingabe löscht sich noch nicht von selber wenn die Suche abgebrochen ist
	 * Man muss irgendwie "zweimal" klicken - also erst auf die Lupe, dann verschiebt sie sich und dann nochmal um zu schreiben
	 *  -> vll gibts da auch noch was
	 *
	 * Hinweise:
	 *  - das heißt SearchView und hat ein paar ziemlich nützliche Sachen schon vorgemacht
	 *  - Es gibt so wie ich das verstanden habe auch eine "einfache" Möglichkeit Suchvorschläge zu machen
	 *
	 * **/


	//Important to handle Intent in onCreate AND onNewIntent!!
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		Log.i("SearchActivity", "handleIntent: ");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.i("handleIntent", "query: " + query);
			doMySearch(query);
		}
	}

	//different Search - probably need List here
	private void doMySearch(String query){
		Log.i("SearchActivity", "query: "+ query);
	}

}
