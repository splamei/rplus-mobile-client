package com.splamei.rplus.client;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.DialogInterface;

import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
{
    // Main data
    public static String myVerCode = "1003";

    // Url and Webview data
    public static String urlToLoad = "https://rhythm-plus.com"; // Full URL to load
    public static String mainUrl = "https://rhythm-plus.com"; // Must start with URL to allow loading
    public static String urlForNewTab = "auth.rhythm-plus.com"; // Must contain to open the second tab
    public static String urlForNewTabClosure = "auth.rhythm-plus.com/__/auth/handler?state="; // Must contain to close the second tab and return
    public static String webView1UserAgent = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) RhythmPlusSplameiClient/1003 Mobile Safari/537.36";
    public static String webView2UserAgent = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.6943.89 Mobile Safari/537.36";
    public static String updateUrl = "https://www.veemo.uk/net/r-plus/mobile/ver";
    public static String noticesUrl = "https://www.veemo.uk/net/r-plus/mobile/notices";

    // String data
    public static String secondTabNormalCloseMessage = "Welcome to Rhythm Plus";
    public static String secondTabLoadToastMessage = "Please wait while the sign-in page loads";



    WebView webView;
    WebView loginView;
    WebViewClient webViewClient;
    WebViewClient loginClient;

    CoordinatorLayout coordinatorLayout;

    boolean hasShownAuth = false;
    public static final String ERROR_CHANNEL_ID = "error_channel";
    public static final String MISC_CHANNEL_ID = "misc_channel";

    RequestQueue ExampleRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        android.util.Log.i("onCreate", "Client starting...");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        createChannel(this, MISC_CHANNEL_ID, "Misc", "Other notifications used by the client", NotificationManager.IMPORTANCE_DEFAULT);
        createChannel(this, ERROR_CHANNEL_ID, "Errors", "Notifications sent when errors occur", NotificationManager.IMPORTANCE_HIGH);

        ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(this, "more")
                .setShortLabel("About")
                .setLongLabel("About the client")
                .setIcon(IconCompat.createWithResource(this, R.drawable.icon))
                .setRank(0)
                .setIntent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://rhythm-plus.com")))
                .build();

        ShortcutInfoCompat licenceShortcut = new ShortcutInfoCompat.Builder(this, "licence")
                .setShortLabel("Licence")
                .setLongLabel("Licence")
                .setIcon(IconCompat.createWithResource(this, R.drawable.icon))
                .setRank(1)
                .setIntent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/splamei/rplus-mobile-client/blob/master/LICENSE")))
                .build();

        ShortcutManagerCompat.pushDynamicShortcut(this, shortcut);
        ShortcutManagerCompat.pushDynamicShortcut(this, licenceShortcut);

        ExampleRequestQueue = Volley.newRequestQueue(MainActivity.this);
        coordinatorLayout = findViewById(R.id.main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1008);
        }

        int UI_OPTIONS = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        getWindow().getDecorView().setSystemUiVisibility(UI_OPTIONS);

        android.util.Log.i("onCreate", "Starting Web View....");

        webView = findViewById(R.id.mainWeb);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadsImagesAutomatically(true);


        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setAllowFileAccess(true);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setInitialScale(1);
        webView.getSettings().setUserAgentString(webView1UserAgent);


        loginView = findViewById(R.id.loginWeb);
        loginView.setVisibility(View.GONE);
        loginView.getSettings().setJavaScriptEnabled(true);
        loginView.getSettings().setAllowContentAccess(true);
        loginView.getSettings().setUseWideViewPort(true);
        loginView.getSettings().setLoadsImagesAutomatically(true);


        loginView.getSettings().setLoadWithOverviewMode(true);
        loginView.getSettings().setDomStorageEnabled(true);
        loginView.setHorizontalScrollBarEnabled(false);
        loginView.getSettings().setDatabaseEnabled(true);
        loginView.getSettings().setBuiltInZoomControls(true);
        loginView.getSettings().setDisplayZoomControls(false);
        loginView.getSettings().setAllowFileAccess(true);
        loginView.setScrollbarFadingEnabled(false);
        loginView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        loginView.setInitialScale(1);
        loginView.getSettings().setUserAgentString(webView2UserAgent);

        webViewClient = new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if ((url.startsWith(mainUrl) && !url.contains(urlForNewTab)) || url.startsWith(urlToLoad))
                {
                    // load my page
                    return false;
                }
                else if (url.contains(urlForNewTab))
                {
                    hasShownAuth = false;

                    webView.setVisibility(View.GONE);
                    loginView.setVisibility(View.VISIBLE);
                    loginView.setWebViewClient(loginClient);

                    loginView.loadUrl(url);

                    loginView.clearHistory();

                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
                            secondTabLoadToastMessage, Snackbar.LENGTH_LONG);
                    snackbar.show();

                    return true;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                ImageView imageView = findViewById(R.id.splashImg);
                imageView.setVisibility(View.INVISIBLE);

                ImageView backImg = findViewById(R.id.backImg);
                backImg.setVisibility(View.INVISIBLE);

                webView.setVisibility(View.VISIBLE);
            }
        };

        loginClient = new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url)
            {

                if (url.contains(urlForNewTabClosure))
                {
                    webView.setVisibility(View.VISIBLE);
                    loginView.setVisibility(View.GONE);

                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
                            secondTabNormalCloseMessage, Snackbar.LENGTH_LONG);
                    snackbar.show();

                    loginView.loadUrl("about:blank");
                }
            }
        };

        webView.setWebViewClient(webViewClient);
        webView.loadUrl(urlToLoad);

        android.util.Log.i("onCreate", "Client Started. Now checking for updates...");

        String url = updateUrl;
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                if (fileExists(MainActivity.this, "checkCode.dat"))
                {
                    if (!readFile(MainActivity.this, "checkCode.dat").strip().equals(response.strip()))
                    {
                        saveToFile(MainActivity.this, "checkCode.dat", response.strip());
                        newUpdate(MainActivity.this, response.strip());
                    }
                }
                else
                {
                    saveToFile(MainActivity.this, "checkCode.dat", response);
                    newUpdate(MainActivity.this, response);
                }
            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError e)
            {
                android.util.Log.i("onCreate", "Failed to check for updates " + e);
                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                        "Failed to check for updates", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        ExampleRequestQueue.add(ExampleStringRequest);

        android.util.Log.i("onCreate", "Now checking for notices");

        String urlNotices = noticesUrl;
        StringRequest NoticesStringRequest = new StringRequest(Request.Method.GET, urlNotices, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    String regex = "[;]";
                    String[] splitNotices;

                    splitNotices = response.split(regex);

                    String seenNotices = readFile(MainActivity.this, "seenNotices.dat").strip();
                    if (!seenNotices.contains(splitNotices[3]) && !splitNotices[0].equals("NONE"))
                    {
                        saveToFile(MainActivity.this, "seenNotices.dat", splitNotices[3]);
                        showNewNotice(MainActivity.this, splitNotices[0], splitNotices[1], splitNotices[2]);
                    }
                }
                catch (Exception e)
                {
                    android.util.Log.i("onCreate", "Failed to decode notices - " + e);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
                            "Failed to decode notices", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError e)
            {
                android.util.Log.i("onCreate", "Failed to get current notices - " + e);
                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                        "Failed to get current notices", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });

        ExampleRequestQueue.add(NoticesStringRequest);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if (keyCode == KeyEvent.KEYCODE_BACK)
            {
                if (webView.getVisibility() == View.VISIBLE)
                {
                    if (webView.canGoBack())
                    {
                        webView.goBack();
                    }
                    return true;
                }
                else if (loginView.getVisibility() == View.VISIBLE)
                {
                    if (loginView.canGoBack())
                    {
                        loginView.goBack();
                    }
                    else
                    {
                        webView.setVisibility(View.VISIBLE);
                        loginView.setVisibility(View.GONE);

                        loginView.loadUrl("about:blank");
                    }
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void createChannel(Context context, final String ID, String title, String description, int importance)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null && notificationManager.getNotificationChannel(ID) == null)
        {
            NotificationChannel channel = new NotificationChannel(ID, title, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.MAGENTA);
            channel.enableVibration(true);

            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void sendNotifcation(Context context, final String ID, String title, String message, int importance, int id)
    {
        android.util.Log.i("sendNotifcation", "Sending notifcation - '" + title + "' - '" + message + "'");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(importance);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (notificationManagerCompat.areNotificationsEnabled())
        {
            notificationManagerCompat.notify(id, builder.build());
        }
    }

    public static boolean sendNotificationWithURL(Context context, final String ID, String title, String message, int importance, String url, String buttonText, int notifcationID)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        android.util.Log.i("sendNotifcationWithURL", "Sending notifcation - '" + title + "' - '" + message + "'");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(importance)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // Set the pending intent for the notification

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        if (notificationManagerCompat.areNotificationsEnabled())
        {
            notificationManagerCompat.notify(notifcationID, builder.build());

            return true;
        }

        return false;
    }

    public static void showDialogBox(Context context, String title, String text, String button1Text, String button2text, DialogInterface.OnClickListener button1Pressed, DialogInterface.OnClickListener button2Pressed)
    {
        if (button2text.isEmpty())
        {
            new MaterialAlertDialogBuilder(context)
                    .setTitle(title)
                    .setMessage(text)
                    .setPositiveButton(button1Text, button1Pressed)
                    .show();
        }
        else
        {
            new MaterialAlertDialogBuilder(context)
                    .setTitle(title)
                    .setMessage(text)
                    .setPositiveButton(button1Text, button1Pressed)
                    .setNegativeButton(button2text, button2Pressed)
                    .show();
        }
    }

    public static String readFile(Context context, String fileName)
    {
        File file = new File(context.getFilesDir(), fileName);
        StringBuilder text = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append("\n");
            }
        }
        catch (IOException e)
        {
            android.util.Log.e("readFile", "Failed to read file '" + fileName + "'! - " + e);
        }

        return text.toString();
    }

    public static void saveToFile(Context context, String fileName, String content)
    {
        try (FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE))
        {
            outputStream.write(content.getBytes());
        }
        catch (IOException e)
        {
            android.util.Log.e("saveToFile", "Failed to save file '" + fileName + "'! - " + e);
        }
    }

    public static boolean fileExists(Context context, String fileName)
    {
        File file = new File(context.getFilesDir(), fileName);
        return file.exists();
    }

    public static void newUpdate(Context context, String responce)
    {
        if (!myVerCode.contains(responce))
        {
            android.util.Log.i("newUpdate", "New update to the client! Showing user");

            showDialogBox(context, "New update", "There is a new update to the client app. It's recommended you update for the latest fixes and changes however you can optionally skip\n\nYou won't be alerted about this update again until there is a new update", "Update", "Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Toast.makeText(context, "GitHub should now open via the app or website", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/splamei/rplus-mobile-client/releases/")));
                    System.exit(0);
                }
            }, null);
        }
    }

    public static void showNewNotice(Context context, String title, String text, String url)
    {
        if (url.contains("NONE"))
        {
            showDialogBox(context, title, text, "Ok", "", null, null);
        }
        else
        {
            showDialogBox(context, title, text, "Ok", "More", null, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Toast.makeText(context, "You are now being directed to the url provided", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url)));
                }
            });
        }
    }
}
