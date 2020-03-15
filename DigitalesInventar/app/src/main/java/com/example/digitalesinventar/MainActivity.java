package com.example.digitalesinventar;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;

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

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123; //wieso
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String userID = "defaultEmptyID";
    private SearchView searchView;

    FrameLayout frameLayout;
    TabLayout tabLayout;

    //UI-ELEMENTS --- NOTE: wird später dann noch ausgelagert in eigenstaendiges Fragment
    Toolbar toolbar;
    FloatingActionButton plusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Choose authentication providers //v1
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
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,fragment).commit();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    //sets and initializes UI for MainActivity
    public void setupMainMenu() {
        setContentView(R.layout.activity_main);
        frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
					@Override
					public void onTabSelected(TabLayout.Tab tab) {
						Fragment fragment = null;
						switch (tab.getPosition()) {
							case 0:
								fragment = new MainActivityFragment();
								Log.i("onTabSelected", "case 0");
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
								fragment = new PlaceFragment();
								plusButton = findViewById(R.id.plusButton);
								plusButton.hide();
								break;
						}
						getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,fragment).commit();
					}

					@Override
					public void onTabUnselected(TabLayout.Tab tab) {
            if (tab.getPosition() == 2){
                plusButton = findViewById(R.id.plusButton);
                plusButton.show();
            }
					}

					@Override
					public void onTabReselected(TabLayout.Tab tab) {

					}});

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DatabaseActivity.getDataFromDatabase();
    }

    //onClick action for plusButton when item is selected--> launches newItemActivity
    private void launchNewItemActivity() {
        Log.i("MainActivity", "launchNewItemActivity called");
        Intent intent = new Intent(this, NewItemActivity.class);
        Log.i("MainActivity", "intent to start newItemActivity created");
        startActivity(intent);
    }

    //onClick action for plusButton when category is selected --> launches newItemActivity
    private void launchNewCategoryActivity() {
        Log.i("MainActivity", "launchNewItemActivity called");
        Intent intent = new Intent(this, NewCategoryActivity.class);
        Log.i("MainActivity", "intent to start newCategoryActivity created");
        startActivity(intent);
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
