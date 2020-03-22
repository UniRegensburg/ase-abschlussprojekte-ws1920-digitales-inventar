package com.example.digitalesinventar;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

	private static final int RC_SIGN_IN = 123; //wieso
	static FirebaseFirestore db = FirebaseFirestore.getInstance();
	public static String userID = "defaultEmptyID";
	private SearchView searchView;

	FrameLayout frameLayout;
	TabLayout tabLayout;

	Integer currentCase = 0;

	Spinner sortBySpinner;
	ArrayAdapter<String> spinnerAdapter;
	ArrayList<String> spinnerArrayLarge = new ArrayList<>();
	ArrayList<String> spinnerArraySmall = new ArrayList<>();


	//UI-ELEMENTS --- NOTE: wird später dann noch ausgelagert in eigenstaendiges Fragment
	Toolbar toolbar;
	FloatingActionButton plusButton;
	Button settingsButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		callLogin();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);

		// Get SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) menu.findItem(R.id.search_bar).getActionView();
		ComponentName componentName = new ComponentName(this, SearchActivity.class);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

		// setupSearchListener in Fragment class
		MainActivityFragment fragment = new MainActivityFragment();
		fragment.setupSearchListener(searchView);
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
		return true;
	}

	@Override
	public void onBackPressed() {
		// close search view on back button pressed
		if (!searchView.isIconified()) {
			searchView.setIconified(true);
			return;
		}
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_logout) {
			FirebaseAuth.getInstance().signOut();
			Log.i("logout", "logout clicked");
			callLogin();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RC_SIGN_IN) {
			IdpResponse response = IdpResponse.fromResultIntent(data);
			Log.d("loginTag", "second");

			if (resultCode == RESULT_OK) {
				// Successfully signed in
				Log.d("loginTag", "third");
				FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
				Log.d("loginTag", user.getUid());
				userID = user.getUid();
				setupMainMenu();
			} else {
				// Sign in failed. If response is null the user canceled the
				// sign-in flow using the back button. Otherwise check
				// response.getError().getErrorCode() and handle the error.
				// ..
				Log.d("loginTag", "error");
				Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
				handleSignInResult(task);
			}
		}
	}

	private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
		GoogleSignInAccount account = null;
		try {
			account = completedTask.getResult(ApiException.class);
		} catch (ApiException e) {
			if (e.getStatusCode() != GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
				// cancelled
				Log.d("SignIn", "cancelled");
				launchSignInFailedActivity();
			} else {
				// error
				Log.d("SingIn", "Error");
			}
		}
		if (account != null) {
			// ok
		}
	}

	private void callLogin() {
		List<AuthUI.IdpConfig> providers = Arrays.asList(
			new AuthUI.IdpConfig.EmailBuilder().build(),
			new AuthUI.IdpConfig.GoogleBuilder().build());

		// Create and launch sign-in intent
		startActivityForResult(
			AuthUI.getInstance()
				.createSignInIntentBuilder()
				.setAvailableProviders(providers)
				.build(),
			RC_SIGN_IN);
	}

	//sets and initializes UI for MainActivity
	public void setupMainMenu() {
		setContentView(R.layout.activity_main);

		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		DatabaseActivity.getDataFromDatabase();
		DatabaseActivity.getCategoriesFromDatabase();

		//initialise Button at the start
		plusButton = findViewById(R.id.plusButton);
		plusButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.i("MainActivity", "plusButton clicked");
				launchNewItemActivity();
			}
		});

		setupSortedSpinner();
		setupTabLayout();
	}

	private void setupSpinnerArrays() {
		spinnerArrayLarge.add("Neueste");
		spinnerArrayLarge.add("Älteste");
		spinnerArrayLarge.add("Name absteigend");
		spinnerArrayLarge.add("Name aufsteigend");

		spinnerArraySmall.add("Name absteigend");
		spinnerArraySmall.add("Name aufsteigend");
	}

	private void setupBigSpinner() {
		// Create an ArrayAdapter for the spinner using the string array and a default spinner layout
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, spinnerArrayLarge);
		// Specify the layout to use when the list of choices appears
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		sortBySpinner.setAdapter(spinnerAdapter);
	}

	private void setupSmallSpinner() {
		// Create an ArrayAdapter for the spinner using the string array and a default spinner layout
		spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, spinnerArraySmall);
		// Specify the layout to use when the list of choices appears
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		sortBySpinner.setAdapter(spinnerAdapter);
	}

	private void setupSortedSpinner() {
		setupSpinnerArrays();
		sortBySpinner = findViewById(R.id.spinnerSortBy);
		setupBigSpinner();
		sortBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// your code here
				//Log.i("sortSpinner", "onItemSelected:case,name: "+currentCase+","+sortBySpinner.getSelectedItem());
				switch (currentCase) {
					case 0:
						//Items
						if (sortBySpinner.getSelectedItem() == "Neueste") {
							MainActivityFragment.sortByNewest();
						} else if (sortBySpinner.getSelectedItem() == "Älteste") {
							MainActivityFragment.sortByOldest();
						} else if (sortBySpinner.getSelectedItem() == "Name absteigend") {
							MainActivityFragment.sortByNameDown();
						} else if (sortBySpinner.getSelectedItem() == "Name aufsteigend") {
							MainActivityFragment.sortByNameUp();
						}
						break;
					case 1:
						//Category
						if (sortBySpinner.getSelectedItem() == "Name absteigend") {
							CategoryFragment.sortByNameDown();
						} else if (sortBySpinner.getSelectedItem() == "Name aufsteigend") {
							CategoryFragment.sortByNameUp();
						}
						break;
					case 2:
						//Location
						if (sortBySpinner.getSelectedItem() == "Name absteigend") {
							PlaceFragment.sortByNameDown();
						} else if (sortBySpinner.getSelectedItem() == "Name aufsteigend") {
							PlaceFragment.sortByNameUp();
						}
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
	}

	private void setupTabLayout() {
		tabLayout = (TabLayout) findViewById(R.id.tabLayout);
		frameLayout = (FrameLayout) findViewById(R.id.fragment_container);

		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				Fragment fragment = null;
				switch (tab.getPosition()) {
					case 0:
						Log.i("onTabSelected", "case 0");
						currentCase = 0;
						sortBySpinner.setAdapter(null);
						setupBigSpinner();
						fragment = new MainActivityFragment();
						plusButton = findViewById(R.id.plusButton);
						plusButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								Log.i("MainActivity", "plusButton clicked");
								launchNewItemActivity();
							}
						});
						break;
					case 1:
						Log.i("onTabSelected", "case 1");
						currentCase = 1;
						sortBySpinner.setAdapter(null);
						setupSmallSpinner();
						fragment = new CategoryFragment();
						plusButton = findViewById(R.id.plusButton);
						plusButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								Log.i("MainActivity", "plusButton clicked");
								launchNewCategoryActivity();
							}
						});
						break;
					case 2:
						Log.i("onTabSelected", "case 2");
						currentCase = 2;
						sortBySpinner.setAdapter(null);
						setupSmallSpinner();
						fragment = new PlaceFragment();
						plusButton = findViewById(R.id.plusButton);
						plusButton.hide();
						break;
				}
				getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
				if (tab.getPosition() == 2) {
					plusButton = findViewById(R.id.plusButton);
					plusButton.show();
				}
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}

	//onClick action for plusButton when item is selected--> launches newItemActivity
	private void launchNewItemActivity() {
		Log.i("MainActivity", "launchNewItemActivity called");
		Intent intent = new Intent(this, NewItemActivity.class);
		Log.i("MainActivity", "intent to start newItemActivity created");
		startActivity(intent);
	}

	//onClick action for plusButton when category is selected --> launches alertDialog for Category
	private void launchNewCategoryActivity() {
		Log.i("MainActivity", "launchNewItemActivity called");
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
		alertDialog.setTitle("Kategorie hinzufügen");
		alertDialog.setMessage("Geben sie einen Namen ein");

		final EditText input = new EditText(MainActivity.this);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		alertDialog.setView(input);

		alertDialog.setPositiveButton(R.string.add,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String category = input.getText().toString();
					if (InputChecker.checkEmptyInput(category)) {
						Log.i("addCat", "input not empty");
						for (int i = 0; i < DatabaseActivity.categoryArray.size(); i++) {
							//avoid multiple entries
							if (category.equals(DatabaseActivity.categoryArray.get(i))) {
								Toast.makeText(getApplicationContext(), "Kategorie " + category + " existiert bereits!", Toast.LENGTH_SHORT).show();
								return;
							}
						}
						Log.i("addCat", "input not twice");
						DatabaseActivity.addCategory(category);
						//clear input
						input.setText("");
						//hide keyboard
						UIhelper.hideKeyboard(MainActivity.this);
						Toast.makeText(getApplicationContext(), "Kategorie " + category + " wurde erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(), "Sie müssen einen Namen eingeben", Toast.LENGTH_SHORT).show();
					}
				}
			});
		alertDialog.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		alertDialog.show();
	}


		private void launchSignInFailedActivity() {
        Log.i("MainActivity", "launchNewItemActivity called");
        Intent intent = new Intent(this, SignInFailedActivity.class);
        Log.i("MainActivity", "intent to start newItemActivity created");
        startActivity(intent);
    }

    private void launchCategorySearchActivity(String catName) {
        Log.i("MainActivity", "launchCategorySearchActivity called w/: "+catName);
        Intent intent = new Intent(this, CategorySearchActivity.class);
        intent.putExtra("catName", catName);
        Log.i("MainActivity", "intent to start CategorySearchActivity created");
        startActivity(intent);
    }


}
