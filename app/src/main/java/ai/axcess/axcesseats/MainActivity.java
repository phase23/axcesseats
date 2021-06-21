package ai.axcess.axcesseats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private static final String ONESIGNAL_APP_ID = "070e3946-98cd-4d8e-b3f6-ce12c3257c72";
    WebView webView;
    String playerId;
    String cookieval;
    ImageView logo;
    TextView loading;
    ProgressBar progressBar;
    Handler handler;
    boolean redirect = false;
    boolean completey_loaded  = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        setContentView(R.layout.activity_main);




        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

         playerId = OneSignal.getDeviceState().getUserId();
        Log.d("player", playerId);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }



        logo = (ImageView) findViewById(R.id.llogo);
        loading = (TextView) findViewById(R.id.loadingtxt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        webView = (WebView) findViewById(R.id.web);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.addJavascriptInterface(new WebAppInterface(this), "android");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //webView.setWebViewClient(new WebViewClient());
        //WebView.setWebViewClient(new WebViewClient());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }




        webView.setWebViewClient(new WebViewClient() {

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){

                handler.proceed();

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(!completey_loaded){
                    redirect = true;
                }

               completey_loaded = false;

                webView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                completey_loaded = false;
                Log.d("compleetly loaded ", completey_loaded + "");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //Toast.makeText(getApplicationContext(), url + "\n\n", Toast.LENGTH_LONG).show();

                Log.d("WebView", url);
                super.onPageFinished(view, url);

                if(!redirect){
                    completey_loaded = true;
                }

                if(completey_loaded && !redirect) {
                    Log.d("compleetly loaded ", completey_loaded + "");
                    Log.d("compleetly loaded ",  " is completed loaded");

                    //handler = new Handler();
                    //handler.postDelayed(new Runnable() {
                     //   @Override
                     //   public void run(){


                            webView.setVisibility(View.VISIBLE);
                            logo.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);




                      //  }
                  //  }, 3000);


                } else {
                    redirect = false;


                }



                String cookies = CookieManager.getInstance().getCookie(url);
                Log.d(TAG, "All the cookies in a string:" + cookies);
                 cookieval = getCookie(url ,"cunq");
                Log.d(TAG, "my cookies: " + cookieval);

                if(cookieval != null){


                    try {
                        sendplayerid("https://axcess.ai/sendplayerid.php?cunq="+cookieval + "&player="+playerId);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }






            }




            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                //Toast.makeText(getApplicationContext(), "AJAX" + url, Toast.LENGTH_LONG).show();
                return null;
            }



        });



        webView.getSettings().setUserAgentString("Chrome/56.0.0 Mobile");
        webView.loadUrl("https://axcess.ai/eat/now");
    }



    void sendplayerid(String url) throws IOException {
        Log.d(TAG, "url:" + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        String resulting = response.body().string();

                    }//end void

                });
    }



    public String getCookie(String siteName,String CookieName){
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        if(cookies != null){
            String[] temp=cookies.split(";");
            for (String ar1 : temp ){
                if(ar1.contains(CookieName)){
                    String[] temp1=ar1.split("=");
                    CookieValue = temp1[1];
                }
            }
        }
        return CookieValue;
    }


    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

}