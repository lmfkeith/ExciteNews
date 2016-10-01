package com.example.keithliu.excitenews;

import android.net.Uri;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import android.os.AsyncTask;
import android.webkit.WebViewClient;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.jsoup.Jsoup;
/*
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
*/
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


public class MainActivity extends AppCompatActivity {

    private Document htmlDocument;
    private String htmlPageUrl = "http://www.excite.co.jp";
    private Elements htmlLinksList;

    private String strHtml;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView myWebView = (WebView) findViewById(R.id.webview);

        WebSettings settings = myWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");

        myWebView.getSettings().setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new MyWebViewClient());

        /* JavaScript must be enabled if you want it to work, obviously */
        myWebView.getSettings().setJavaScriptEnabled(true);

        /* Register a new JavaScript interface called HTMLOUT */
        myWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

/*
        myWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String   failingUrl) {

            }

            public void onPageFinished(WebView view, String url) {


                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });
*/

         myWebView.loadUrl(htmlPageUrl);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            htmlDocument = Jsoup.parse(strHtml);
            Element htmlNews = htmlDocument.getElementById("newsTabList01");
            if (htmlNews != null) {
                htmlLinksList = htmlNews.getElementsByClass("new");
            } else {
                htmlLinksList = null;
            }
//            for (Element ele : htmlElementList) {
//                if (ele.className() == "new") {
//                    htmlLinksList.add(ele.getElementsByTag("a").first());
//                }
//            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (htmlLinksList != null){
                String outputData = "<html><head></head><body>";
                for (Element link : htmlLinksList) {
                    Log.v("JSoupParse", link.toString());
                    outputData += "<li>" + link.getElementsByTag("a").first().text() + "</li>";
                }
                outputData += "</body></html>";

                WebView myWebView = (WebView) findViewById(R.id.webview);

                myWebView.loadData(outputData, "text/html; charset=utf-8", "utf-8");
            }
        }
    }

    private class MyWebViewClient extends WebViewClient {
        private Boolean isLoadOnce = false;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (!isLoadOnce) {
                view.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            } else {
                isLoadOnce = false;
            }

            /*
            view.loadUrl("javascript:(function() { "
                    + "window.HTMLOUT.setHtml('<html>'+"
                    + "document.getElementsByTagName('html')[0].innerHTML+'</html>');})();");
            */
        }
    }

    /* An instance of this class will be registered as a JavaScript interface */
    private class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html)
        {
            // process the html as needed by the app
            strHtml = html;

            JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
            jsoupAsyncTask.execute();
        }
    }

    /*
    class MyJavaScriptInterface
    {
        private Context ctx;

        MyJavaScriptInterface(Context ctx)
        {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showHTML(String html)
        {
            //code to use html content here
            handlerForJavascriptInterface.post(new Runnable() {
                @Override
                public void run()
                {
                    Toast toast = Toast.makeText(this, "Page has been loaded in webview. html content :"+html_, Toast.LENGTH_LONG);
                    toast.show();
                }});
        }
    }
    */
}

