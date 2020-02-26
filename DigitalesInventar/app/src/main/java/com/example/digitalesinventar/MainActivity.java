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
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    //UI-ELEMENTS --- NOTE: wird später dann noch ausgelagert in eigenstaendiges Fragment
    Button firstCat;
    Button secondCat;
    Button thirdCat;
    Button fourthCat;
    Button fifthCat;
    Button sixthCat;
    Toolbar toolbar;
    FloatingActionButton plusButton;
    Button[] catButtonArray = new Button[6];//will need to be reworked for custom category buttons
    //HELPERS
    int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Choose authentication providers //v1
        List<AuthUI.IdpConfig> providers = Arrays.asList(
          new AuthUI.IdpConfig.EmailBuilder().build());//,
        //new AuthUI.IdpConfig.GoogleBuilder().build());

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
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DatabaseActivity.getDataFromDatabase();
        DatabaseActivity.getCategoriesFromDatabase();

        plusButton = findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MainActivity", "plusButton clicked");
                launchNewItemActivity();
            }
        });
        setupCategories();
    }

    private void setupCategories() {
        firstCat = findViewById(R.id.cat1);
        secondCat = findViewById(R.id.cat2);
        thirdCat = findViewById(R.id.cat3);
        fourthCat = findViewById(R.id.cat4);
        fifthCat= findViewById(R.id.cat5);
        sixthCat = findViewById(R.id.cat6);
        //Log.i("MainActivity", "btn.add: "+firstCat.toString());
        catButtonArray[0] = firstCat;
        catButtonArray[1] = secondCat;
        catButtonArray[2] = thirdCat;
        catButtonArray[3] = fourthCat;
        catButtonArray[4] = fifthCat;
        catButtonArray[5] = sixthCat;
        //set width of Buttons
        screenWidth = UIhelper.screenWidth(getWindowManager());
        //firstCat.setHeight(screenWidth/3);
        secondCat.setWidth(screenWidth/3);
        //secondCat.setHeight(screenWidth/3);
        firstCat.setWidth(screenWidth/3);
        //thirdCat.setHeight(screenWidth/3);
        thirdCat.setWidth(screenWidth/3);
        //fourthCat.setHeight(screenWidth/3);
        fourthCat.setWidth(screenWidth/3);
        //fifthCat.setHeight(screenWidth/3);
        fifthCat.setWidth(screenWidth/3);
        //sixthCat.setHeight(screenWidth/3);
        sixthCat.setWidth(screenWidth/3);

        for (final Button cat : catButtonArray){
            cat.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Log.i("CatMainActivity", "btn: "+cat.getText().toString());
                    launchCategorySearchActivity(cat.getText().toString());
                }
            });
        }
    }

    //onClick action for plusButton --> launches newItemActivity
    private void launchNewItemActivity() {
        Log.i("MainActivity", "launchNewItemActivity called");
        Intent intent = new Intent(this, NewItemActivity.class);
        Log.i("MainActivity", "intent to start newItemActivity created");
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
