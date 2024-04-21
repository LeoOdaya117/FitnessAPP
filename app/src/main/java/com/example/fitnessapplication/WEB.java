package com.example.fitnessapplication;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.getBoolean;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WEB extends AppCompatActivity implements NoInternetFragment.RetryListener, Plans.OnPlanSelectedListener  {
    String tab;

    public static WebView webView; // Changed to class-level variable
    private boolean buttonClicked = false;

    public String websiteurl = URLManager.MY_URL;
    private NoInternetFragment noInternetFragment;

    private PopupWindow popupWindow;

    private boolean dismiss = false;
    private boolean subscriptionAlertdismiss = false;

    private boolean member = false;


    private OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        client = new OkHttpClient();
        String username = UserDataManager.getInstance(WEB.this).getEmail();

        // Initialize NoInternetFragment
        noInternetFragment = NoInternetFragment.newInstance("param1", "param2");
        noInternetFragment.setRetryListener(this);

        // Find the WebView by its id and initialize it
        webView = findViewById(R.id.myWeb);


//        if (!isNetworkAvailable()) {
//            loadFragment(noInternetFragment);
//        } else {
//            // Show the WebView
//            webView.setVisibility(View.VISIBLE);
//            loadInitialUrl();
//        }
        webView.setVisibility(View.INVISIBLE);
        loadFragment(new Report());
//        loadInitialUrl();


//        webView.loadUrl(websiteurl + "/gym_website/user/index.php");

        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Enable DOM Storage (for storing cookies)
        webSettings.setDomStorageEnabled(true);

        // Enable third-party cookies
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptThirdPartyCookies(webView, true);

        // Set a WebViewClient to handle page navigation

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            TextView headerTextView = findViewById(R.id.header_text);
            tab = (String) item.getTitle();

            // Get the current fragment
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

            switch (tab) {
                case "Plans":
                    if (!(currentFragment instanceof Plans)) {
                        headerTextView.setText("Plans");
                        checkUserPlan(username);
                        if (member) {
                            replaceFragment(fragmentManager, new Plans());
                        } else {
                            showCustomDialog("Alert!", "Please activate your subscription to access Plans.");
                        }
                    }
                    return true;

                case "Discover":
                    if (!(currentFragment instanceof Discover)) {
                        headerTextView.setText("Discover");
                        replaceFragment(fragmentManager, new Discover());
                    }
                    return true;

                case "Report":
                    if (!(currentFragment instanceof Report)) {
                        headerTextView.setText("Report");
                        replaceFragment(fragmentManager, new Report());
                    }
                    return true;

                case "Settings":
                    if (!(currentFragment instanceof SettingsFragment)) {
                        headerTextView.setText("Settings");
                        replaceFragment(fragmentManager, new SettingsFragment());
                    }
                    return true;

                default:
                    return false;
            }
        });



    }

    private AlertDialog dialog;

    private void showCustomDialog(String title,String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WEB.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.bmr_message);
        Button okButton = dialogView.findViewById(R.id.ok_button);

        titleTextView.setText(title);
        messageTextView.setText(message);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissCustomDialog(); // Dismiss the dialog when the OK button is clicked
            }
        });

        builder.setView(dialogView);

        dialog = builder.create();
        dialog.show();
    }

    private void dismissCustomDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void checkDietPlanRecord(String username) {
        String mainUrl = URLManager.MY_URL;
        String url = mainUrl+ "/User/api/check_user_plans.php?email=" + username;


        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Show error message or perform any UI update
                        Toast.makeText(WEB.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    // Handle successful response
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Show the response in a Toast or perform any UI update
//                            Toast.makeText(WEB.this, responseBody + "\n USERNAME: " + username, Toast.LENGTH_SHORT).show();

                            if(responseBody.equals("No")){
                                showPopupWindow();
                                dismiss = true;
                            }
                            else{
                                dismiss = true;
                            }

                        }
                    });
                } else {
                    // Handle unsuccessful response
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Show error message or perform any UI update
                            Toast.makeText(WEB.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private void showPopupWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WEB.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_diet_and_workout, null);
        builder.setView(dialogView);

        Button setupButton = dialogView.findViewById(R.id.setupButton);
        Button closeButton = dialogView.findViewById(R.id.dismissButton);

        AlertDialog dialog = builder.create();

        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss = true;
                dialog.dismiss(); // Dismiss the dialog when the OK button is clicked
                Intent intent = new Intent(WEB.this, SignupGender.class);
                intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
                startActivity(intent);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss = true;
                dialog.dismiss(); // Dismiss the dialog when the close button is clicked
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dismiss = true;
            }
        });

        dialog.show();
    }


    // Add this method if you want to dismiss the popup window
    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    // Method to show the PlansFragment
    @Override
    public void onPlanSelected(String url) {
        String loadURL = websiteurl + "/User/";
//        webView.loadUrl(loadURL + "loadingpage.php");
//        webView.setVisibility(View.GONE);
//        webView.loadUrl(url);
//        webView.setVisibility(View.VISIBLE);

    }
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof Report) {
            // If the current fragment is Report, do nothing (prevent navigating back)
            return;
        }
        super.onBackPressed();


    }

    private void navigateToReportFragment() {
        // Replace the current fragment with the Report fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, new Report());
        ft.commit();
    }


//    @Override
//    public void onBackPressed() {
//
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//
//        // Check if the current fragment is the Report fragment
//        if (currentFragment instanceof Report) {
//            // If the current fragment is Report, do nothing (prevent navigating back)
//            return;
//        }
//
//
//        String currentUrl = webView.getUrl();
//
////        // Check if the current URL ends with pages-login.html
////        if (currentUrl != null && currentUrl.endsWith("pages-login.html")) {
////            // Navigate back to MainActivity
////            Intent intent = new Intent(this, MainActivity.class);
////            startActivity(intent);
////            finish(); // Finish the current activity to prevent going back to it
////            return;
////        }
////
////        // Check if the current URL ends with index.php
////        if (currentUrl != null && currentUrl.endsWith("index.php")) {
////            // Disable the back button
////            return;
////        }
////
////        if (currentUrl.endsWith("workoutplan_noheader.php")) {
////            webView.setVisibility(View.GONE);
////
////        }
////        if (currentUrl.endsWith("dietplans_noheader.php")) {
////            webView.setVisibility(View.GONE);
////
////        }
//
////        // If WebView can go back, navigate back in its history
////        if (webView.canGoBack()) {
////            webView.goBack();
////        } else {
////            // If WebView cannot go back, handle the back button press normally
////            super.onBackPressed();
////        }
//
//        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack();
//        } else {
//            super.onBackPressed();
//        }
//
//    }

    public Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.findFragmentById(R.id.fragment_container);
    }

    private void loadFragment(Fragment fragment) {
        // Create a FragmentTransaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container with the new fragment
        transaction.replace(R.id.fragment_container, fragment, "NoInternetFragment");

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onRetry() {
        // Hide the NoInternetFragment
        loadInitialUrl();
        hideNoInternetFragment();
        webView.setVisibility(View.VISIBLE);

        loadInitialUrl();
    }


    private void loadInitialUrl() {
        // Load your initial URL in the WebView
        String websiteurl = URLManager.MY_URL;

        UserDataManager userDataManager = UserDataManager.getInstance(getApplicationContext());

        String username = "?login-username=" +userDataManager.getEmail();
        String password = "&login-password=" + userDataManager.getPassword();
//        String username = "?login-username=" + getIntent().getStringExtra("username");
//        String password = "&login-password=" + getIntent().getStringExtra("password");
        String url = websiteurl+ "/User/login.php";

        url = url + username + password;
        webView.loadUrl(url);
    }

    public static void logout() {
        // Load your initial URL in the WebView
        String websiteurl = URLManager.MY_URL;
        String url = websiteurl+ "/User/logout.php";
        webView.loadUrl(url);
    }

    private void hideNoInternetFragment() {
        // Find NoInternetFragment by its tag and remove it
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("NoInternetFragment");
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(fragment).commit();
        }
    }

    // Method to check internet connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    public  void showAlert(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private interface SubscriptionCallback {
        void onSubscriptionChecked(String subscriptionStatus);
    }

    private void checkUserPlan(String username) {

        // Construct the URL using HttpUrl.Builder for safety
        String url = websiteurl + "/User/api/check_users_subscription_date.php?email=" + username;
        Log.d("ServerResponselink", "LINK: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle failure appropriately
                e.printStackTrace();
                // Notify callback with error status

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (response.isSuccessful()) {
                        String serverResponse = responseBody.string();
                        // Log the server response
                        Log.d("ServerResponse", "Response: " + serverResponse);
                        // Pass the subscription status to the callback

                        if(serverResponse.equals("Active")){
                            member = true;

                        }
                        else{
                            member = false;
                        }
                    } else {
                        // Handle unsuccessful response
                        Log.e("ServerResponse", "Error: " + response.code());
                        // Notify callback with error status

                    }
                }
            }
        });
    }


    private void checkUsersSubscription(String username) {

        // Construct the URL with the username
        String url = websiteurl + "/User/fetch_membership_user.php?Username=" + username;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle failure appropriately
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();


                    try {

                        JSONObject jsonObject = new JSONObject(jsonData);

                        String dueDate = jsonObject.getString("dueDate");



                        // Parse dueDate to Date object
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date dueDateObj = sdf.parse(dueDate);

                        // Calculate today's date
                        Calendar today = Calendar.getInstance();
                        today.set(Calendar.HOUR_OF_DAY, 0);
                        today.set(Calendar.MINUTE, 0);
                        today.set(Calendar.SECOND, 0);
                        today.set(Calendar.MILLISECOND, 0);

                        // Calculate the difference in days between today and dueDate
                        long diffInMillis = dueDateObj.getTime() - today.getTimeInMillis();
                        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                        // Check if dueDate is today or within 3 days or has already passed
                        if ((diffInDays >= 0 && diffInDays <= 3) || diffInDays < 0) {
                            // Show AlertDialog on the UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showSubscriptionAlert();
                                }
                            });
                        }

                    } catch (JSONException | ParseException e) {

                        e.printStackTrace();

                    } catch (java.text.ParseException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    // Handle unsuccessful response
                    System.out.println("Error: " + response.code());
                }
            }
        });
    }

    private void showSubscriptionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WEB.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.subscriptionalert, null);
        builder.setView(dialogView);

        Button setupButton = dialogView.findViewById(R.id.renewButton);
        Button closeButton = dialogView.findViewById(R.id.dismissButton);

        AlertDialog dialog = builder.create();

        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscriptionAlertdismiss = true;
                dialog.dismiss(); // Dismiss the dialog when the OK button is clicked
                Intent intent = new Intent(WEB.this, Subscription.class);
                startActivity(intent);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // Dismiss the dialog when the close button is clicked
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                subscriptionAlertdismiss = true;
            }
        });

        dialog.show();
    }

    private void subscribenNow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WEB.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.subscribenow, null);
        builder.setView(dialogView);

        Button setupButton = dialogView.findViewById(R.id.renewButton);

        AlertDialog dialog = builder.create();

        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); // Dismiss the dialog when the OK button is clicked
                Intent intent = new Intent(WEB.this, Subscription.class);
                startActivity(intent);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                subscriptionAlertdismiss = true;
            }
        });

        dialog.show();
    }

    public static void replaceFragment(FragmentManager fragmentManager, Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)  // Add fragment to back stack for navigation
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String username = UserDataManager.getInstance(WEB.this).getEmail();

        checkDietPlanRecord(username);
        checkUsersSubscription(username);
        checkUserPlan(username);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel ongoing OkHttpClient calls when fragment is destroyed
        if (client != null) {
            client.dispatcher().cancelAll();
        }
    }

}
