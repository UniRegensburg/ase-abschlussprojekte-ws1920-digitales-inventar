package com.example.digitalesinventar;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123; //wieso
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String userID = "defaultEmptyID";
    public static ArrayList<String> categories = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupMainMenu();

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


        //Set default values for categories
        categories.add("Unterhaltungselektronik");
        categories.add("Haushaltsgegenstände");
        categories.add("Einrichtung");
        categories.add("Hobby");
        categories.add("Werkzeug");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private void launchSignInFailedActivity() {
        Log.i("MainActivity", "launchNewItemActivity called");
        Intent intent = new Intent(this, SignInFailedActivity.class);
        Log.i("MainActivity", "intent to start newItemActivity created");
        startActivity(intent);
    }

    //sets and initializes UI for MainActivity
    public void setupMainMenu() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DatabaseActivity.getDataFromDatabase();

        FloatingActionButton plusButton = findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MainActivity", "plusButton clicked");
                launchNewItemActivity();
            }
        });
    }

    //onClick action for plusButton --> launches newItemActivity
    private void launchNewItemActivity() {
        Log.i("MainActivity", "launchNewItemActivity called");
        Intent intent = new Intent(this, NewItemActivity.class);
        Log.i("MainActivity", "intent to start newItemActivity created");
        startActivity(intent);
    }

}
