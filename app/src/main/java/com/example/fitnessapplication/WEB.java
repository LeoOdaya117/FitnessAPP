package com.example.fitnessapplication;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WEB extends AppCompatActivity implements NoInternetFragment.RetryListener, Plans.OnPlanSelectedListener  {
    String tab;

    public static WebView webView; // Changed to class-level variable
    private boolean buttonClicked = false;

    public String websiteurl = URLManager.MY_URL;
    private NoInternetFragment noInternetFragment;

    private PopupWindow popupWindow;

    private boolean dismiss = false;
    private boolean subscriptionAlertdismiss = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // Initialize NoInternetFragment
        noInternetFragment = NoInternetFragment.newInstance("param1", "param2");
        noInternetFragment.setRetryListener(this);

        // Find the WebView by its id and initialize it
        webView = findViewById(R.id.myWeb);


        if (!isNetworkAvailable()) {
            loadFragment(noInternetFragment);
        } else {
            // Show the WebView
            webView.setVisibility(View.VISIBLE);
            loadInitialUrl();
        }
        webView.setVisibility(View.INVISIBLE);
        loadFragment(new Report());
//        loadInitialUrl();
        String loadURLa = websiteurl + "/gym_website/user/";
        webView.loadUrl(loadURLa + "loadingpage.php");

        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Enable DOM Storage (for storing cookies)
        webSettings.setDomStorageEnabled(true);

        // Enable third-party cookies
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptThirdPartyCookies(webView, true);

        // Set a WebViewClient to handle page navigation
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Show a toast or perform any action to inform the user about the error
                loadFragment(noInternetFragment);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // Ignore SSL certificate errors
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!buttonClicked) {
                    webView.loadUrl("javascript:(function() { document.querySelector('button').click(); })()");
                    buttonClicked = true;
                }

                // Check the URL and take appropriate actions
                if (url != null && url.endsWith("logout.php")) {
                    // Navigate back to MainActivity
                    Intent intent = new Intent(WEB.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Finish the current activity to prevent going back to it
                }
                if (url != null && url.endsWith("index.php")) {
                    String username = getIntent().getStringExtra("username");

                    checkDietPlanRecord();
                    checkUsersSubscription(username);

                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                // Check if the clicked URL contains a specific class name
                if (url.contains("type=plan")) {
                    webView.setVisibility(View.GONE);

//                    Toast.makeText(WEB.this, "Clicked on a clickable element", Toast.LENGTH_SHORT).show();
                    return true; // Indicate that the URL has been handled
                }

                else {
                    // Load the URL in the WebView
                    view.loadUrl(url);
                    return false; // Indicate that the WebView should handle the URL
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Handle item selection by updating the TextView with the item title
            tab = (String) item.getTitle();
            String currentUrl = webView.getUrl();
            String loadURL = websiteurl + "/gym_website/user/";

            TextView headerTextView = findViewById(R.id.header_text);
            switch (tab) {
                case "Plans":
                    headerTextView.setText("Plans");

                    webView.loadUrl(loadURL + "loadingpage.php");
                    loadFragment(new Plans());

                    webView.setVisibility(View.GONE);

                    return true;
                case "Discover":
                    webView.setVisibility(View.VISIBLE);
                    headerTextView.setText("Discover");

                    if (!currentUrl.endsWith("discover.php")) {
                        webView.loadUrl(loadURL + "loadingpage.php");

                        webView.loadUrl(loadURL + "discover.php");
                    }
                    return true;
                case "Report":
                    webView.setVisibility(View.INVISIBLE);

                    loadFragment(new Report());
                    headerTextView.setText("Report");
//                    if (!currentUrl.endsWith("index.php")) {
//                        webView.loadUrl(loadURL + "loadingpage.php");
//
//                        webView.loadUrl(loadURL + "index.php");
//                    }
                    return true;
                case "Settings":

                    Intent intent = new Intent(WEB.this, Settings.class);
                    intent.putExtra("username", getIntent().getStringExtra("username")); // Pass the username value
                    startActivity(intent);

//                    if (!currentUrl.endsWith("user_profile.php")) {
//                        webView.loadUrl(loadURL + "user_profile.php");
//                    }
                    return true;
                default:
                    return false;
            }
        });



    }

    private void checkDietPlanRecord() {
        String username = getIntent().getStringExtra("username");
        String mainUrl = URLManager.MY_URL;
        String url = mainUrl+ "/Gym_Website/user/API/check_user_plans.php?email=" + username;

        OkHttpClient client = new OkHttpClient();

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
//                            Toast.makeText(WEB.this, responseBody, Toast.LENGTH_SHORT).show();
                            if(responseBody.equals("No") && dismiss == false){
                                showPopupWindow();
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
        String loadURL = websiteurl + "/gym_website/user/";
        webView.loadUrl(loadURL + "loadingpage.php");
        webView.setVisibility(View.GONE);
        webView.loadUrl(url);
        webView.setVisibility(View.VISIBLE);

    }


    @Override
    public void onBackPressed() {
        String currentUrl = webView.getUrl();

        // Check if the current URL ends with pages-login.html
        if (currentUrl != null && currentUrl.endsWith("pages-login.html")) {
            // Navigate back to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish the current activity to prevent going back to it
            return;
        }

        // Check if the current URL ends with index.php
        if (currentUrl != null && currentUrl.endsWith("index.php")) {
            // Disable the back button
            return;
        }

        if (currentUrl.endsWith("workoutplan_noheader.php")) {
            webView.setVisibility(View.GONE);

        }
        if (currentUrl.endsWith("dietplans_noheader.php")) {
            webView.setVisibility(View.GONE);

        }

        // If WebView can go back, navigate back in its history
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // If WebView cannot go back, handle the back button press normally
            super.onBackPressed();
        }
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
        String url = websiteurl+ "/gym_website/user/login.php";

        url = url + username + password;
        webView.loadUrl(url);
    }

    public static void logout() {
        // Load your initial URL in the WebView
        String websiteurl = URLManager.MY_URL;
        String url = websiteurl+ "/gym_website/user/logout.php";
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

    private void checkUsersSubscription(String username) {
        OkHttpClient client = new OkHttpClient();

        // Construct the URL with the username
        String url = websiteurl + "/Gym_Website/user/fetch_membership_user.php?Username=" + username;

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
}
