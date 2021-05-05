package com.youtube.channel.fragments;

import android.support.v7.app.AlertDialog;
import android.app.Fragment;
import android.annotation.TargetApi;
import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.youtube.channel.R;
import com.youtube.channel.api.YoutubeApi;
import com.youtube.engine.widget.AdvancedWebView;
import com.youtube.channel.MainActivity;

public class YouTubeSearchFragment extends Fragment implements AdvancedWebView.Listener {

    public static String TAG = YouTubeSearchFragment.class.getSimpleName();
    private static final String EXTRA_TEXT = "text";
    public static AdvancedWebView mWebView;
    private View mCustomView;
    private int mOriginalSystemUiVisibility;
    private int mOriginalOrientation;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mFullscreenContainer;
    //private Handler mHandler;

    public static YouTubeSearchFragment searchKeyWord(String text) {
        YouTubeSearchFragment fragment = new YouTubeSearchFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_youtube_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final String keyword = getArguments().getString(EXTRA_TEXT);
        mWebView = (AdvancedWebView)view.findViewById(R.id.webview);
        mWebView.setListener(this, this);
        mWebView.setGeolocationEnabled(false);
        mWebView.setMixedContentAllowed(true);
        mWebView.setCookiesEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setThirdPartyCookiesEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                    ((MainActivity)getActivity()).setHint(url);
                    //setFabListener(url);
                    Toast.makeText(getActivity(), "Finished loading", Toast.LENGTH_SHORT).show();
                }

            });
        mWebView.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);

                    ((MainActivity)getActivity()).setText(title);
                    Toast.makeText(getActivity(), title, Toast.LENGTH_SHORT).show();
                }
                @Override
                public Bitmap getDefaultVideoPoster() {
                    if (getActivity() == null) {
                        return null;
                    }

                    return BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(),
                                                        R.drawable.video_poster);
                }

                @Override
                public void onShowCustomView(View view,
                                             WebChromeClient.CustomViewCallback callback) {
                    // if a view already exists then immediately terminate the new one
                    if (mCustomView != null) {
                        onHideCustomView();
                        return;
                    }

                    // 1. Stash the current state
                    mCustomView = view;
                    mOriginalSystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
                    mOriginalOrientation = getActivity().getRequestedOrientation();

                    // 2. Stash the custom view callback
                    mCustomViewCallback = callback;

                    // 3. Add the custom view to the view hierarchy
                    FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
                    decor.addView(mCustomView, new FrameLayout.LayoutParams(
                                      ViewGroup.LayoutParams.MATCH_PARENT,
                                      ViewGroup.LayoutParams.MATCH_PARENT));


                    // 4. Change the state of the window
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_IMMERSIVE);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }

                @Override
                public void onHideCustomView() {
                    // 1. Remove the custom view
                    FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
                    decor.removeView(mCustomView);
                    mCustomView = null;

                    // 2. Restore the state to it's original form
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);
                    getActivity().setRequestedOrientation(mOriginalOrientation);

                    // 3. Call the custom view callback
                    mCustomViewCallback.onCustomViewHidden();
                    mCustomViewCallback = null;

                }
            });

        // if no url is passed, close the activity
        if (TextUtils.isEmpty(keyword)) {
            ((MainActivity)getActivity()).finish();
        }

        if (!keyword.equals("")) {
            try {
                mWebView.loadUrl(YoutubeApi.YOUTUBE_SEARCH_WEB_PAGE + keyword);                    
            } catch (Exception e) {
                e.printStackTrace();
                Process.killProcess(Process.myPid());
            }
        } else {
            Toast.makeText(getActivity(), "Please enter some text to search.", Toast.LENGTH_SHORT).show();
        }
        mWebView.addHttpHeader("X-Requested-With", "");

        setUpWebViewDefaults(mWebView);
    }


    public void setChannelUrl(String channelId) {
        mWebView.loadUrl(YoutubeApi.YOUTUBE_WEB_URL + "channel/" + channelId);
    }

    
    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setUpWebViewDefaults(WebView webView) {
        WebSettings settings = webView.getSettings();

        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        settings.setSaveFormData(true);
        settings.setDatabaseEnabled(true);
        settings.setEnableSmoothTransition(true);
        settings.setSupportZoom(true);
        settings.setDisplayZoomControls(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setLoadWithOverviewMode(true);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mWebView.onDestroy();
        // ...
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }


    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        mWebView.setVisibility(View.INVISIBLE);
        //setFabListener(url);
    }

    @Override
    public void onPageFinished(String url) {
        mWebView.setVisibility(View.VISIBLE);
        //setFabListener(url);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        if (errorCode == -2) {
            Toast.makeText(getActivity(), "onPageError(errorCode = " + errorCode + ",  description = " + description + ",  failingUrl = " + failingUrl + ")", Toast.LENGTH_SHORT).show();

            /*new AlertDialog.Builder(getActivity())
             .setTitle(R.string.yt_error)
             .setMessage(getString(R.string.yt_error_while_loading_page) + " " + failingUrl + "(" + String.valueOf(errorCode) + " " + description + ")")
             .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(@NonNull DialogInterface dialog, @NonNull int which)
             {
             YoutubeBrowserActivity.this.finish();
             }
             }).setPositiveButton(R.string.yt_refresh, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(@NonNull DialogInterface dialog, @NonNull int which)
             {
             webView.reload();
             }
             }).show();*/
        }
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        Toast.makeText(getActivity(), "onDownloadRequested(url = " + url + ",  suggestedFilename = " + suggestedFilename + ",  mimeType = " + mimeType + ",  contentLength = " + contentLength + ",  contentDisposition = " + contentDisposition + ",  userAgent = " + userAgent + ")", Toast.LENGTH_LONG).show();

        /*if (AdvancedWebView.handleDownload(this, url, suggestedFilename)) {
         // download successfully handled
         }
         else {
         // download couldn't be handled because user has disabled download manager app on the device
         }*/
    }

    @Override
    public void onExternalPageRequest(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
        Toast.makeText(getActivity(), "onExternalPageRequest(url = " + url + ")", Toast.LENGTH_SHORT).show();
	}


}
