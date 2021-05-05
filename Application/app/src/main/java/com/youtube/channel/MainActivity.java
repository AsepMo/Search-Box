package com.youtube.channel;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.animation.Animator;

import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.net.URLEncoder;
import java.util.regex.Pattern;
import java.util.ArrayList;

import com.youtube.engine.widget.SearchBox;
import com.youtube.engine.widget.SearchBox.MenuListener;
import com.youtube.engine.widget.SearchBox.SearchListener;
import com.youtube.engine.widget.SearchBox.ThumbnailListener;
import com.youtube.engine.widget.SearchResult;
import com.youtube.engine.widget.Speakerbox;

import com.youtube.channel.api.YoutubeApi;
import com.youtube.channel.fragments.YouTubeSearchFragment;
import com.youtube.channel.config.EndPoints;
import com.youtube.channel.dialogs.ActionBottomDialogFragment;
import android.telephony.TelephonyManager;

public class MainActivity extends FragmentActivity implements ActionBottomDialogFragment.ItemClickListener {

    private boolean isSearch;
    private boolean startSearching = false;
    private boolean stopSearching = false;
	private SearchBox search;
    private Speakerbox speakerbox;
    public YouTubeSearchFragment getYouTubeSearch() {
        return new YouTubeSearchFragment();
    }

    @Override 
    public void onItemClick(String item) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

		/*Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
         toolbar.setNavigationIcon(R.mipmap.ic_launcher);
         toolbar.setNavigationOnClickListener(new View.OnClickListener(){
         @Override
         public void onClick(View v) {
         getSearch();
         }
         });
         setSupportActionBar(toolbar);*/

        search = (SearchBox) findViewById(R.id.action_search);
        search.setBackground(R.color.colorPrimary);
        search.enableVoiceRecognition(this);

        /* for (int i = 0; i < 10; i++) {
         SearchResult result = new SearchResult("Result : " + Integer.toString(i), null);
         search.addSearchable(result);
         }*/


        search.addSearchable(new SearchResult("Horror Korea Movie Sub Indo", "https://i.ytimg.com/vi/ByWGevHwrZo/mqdefault.jpg"));
        search.addSearchable(new SearchResult("Psycho", "https://i.ytimg.com/vi/z8I-PJeDZKw/mqdefault.jpg"));
        search.addSearchable(new SearchResult("Ria Vlog"));
        search.addSearchable(new SearchResult("Alvin and the Chipmunks_The Road Chip - Simon's Best Moments (Sub Indo)", "https://i.ytimg.com/vi/kBIC7IB2wiQ/mqdefault.jpg"));
        search.addSearchable(new SearchResult("AsepMo Story", "https://i.ytimg.com/vi/aOb7C9fgN0g/mqdefault.jpg"));
        search.setMenuListener(new MenuListener(){

                @Override
                public void onMenuClick() {
                    ActionBottomDialogFragment addPhotoBottomDialogFragment = ActionBottomDialogFragment.newInstance();
                    addPhotoBottomDialogFragment.show(getSupportFragmentManager(), ActionBottomDialogFragment.TAG); 
                }

            });
        speakerbox = new Speakerbox(getApplication());
        speakerbox.setActivity(MainActivity.this);

        // Test calling play() immediately (before TTS initialization is complete).
        speakerbox.play(search.getSearchText());
        

        search.setThumbnails("https://yt3.ggpht.com/ytc/AAUvwniXpFZbF9hWgU-51Xn6fLt1utL1h20Gt-MhNZuj=s800-c-k-c0x00ffffff-no-rj");
        //search.setThumbnails("https://us.123rf.com/450wm/sarahdesign/sarahdesign1410/sarahdesign141001946/32374889-download-button.jpg?ver=6");
        search.setThumbnailListener(new ThumbnailListener(){
                @Override
                public void onThumbnailClick() {
                    YouTubeSearchFragment yt= (YouTubeSearchFragment)getFragmentManager().findFragmentById(R.id.content_frame);
                    yt.setChannelUrl(EndPoints.YOUTUBE_CHANNEL_ID);
                }
            });
        search.setSearchListener(new SearchListener(){

                @Override
                public void onSearchOpened() {
                    //Use this to tint the screen
                }

                @Override
                public void onSearchClosed() {
                    //Use this to un-tint the screen
                }

                @Override
                public void onSearchTermChanged(String term) {
                    //React to the search term changing
                    //Called after it has updated results
                }

                @Override
                public void onSearch(String searchTerm)
                {
                    showFragment(YouTubeSearchFragment.searchKeyWord(searchTerm)); 
                }

                @Override
                public void onResultClick(SearchResult result) {
                    //React to a result being clicked
                    showFragment(YouTubeSearchFragment.searchKeyWord(result.title));
                }

                @Override
                public void onSearchCleared() {
                    //Called when the clear button is clicked
                }

            });
        search.setOverflowMenu(R.menu.menu_application);
        search.setOverflowMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_about:
                            Toast.makeText(MainActivity.this, "Clicked!", Toast.LENGTH_SHORT).show();
                            return true;
                    }
                    return false;
                }
            });
            
        if (!isNetworkAvailable()) {
            getYouTubeSearch().mWebView.onDestroy();
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("Network Error :")
                .setMessage("\nCan't Connect To Server")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Process.killProcess(Process.myPid());
                    }
                }).show();
        } else {
            if (isConnectedFast()) {
                try {
                    
                   // new UpdateTaskChecker(mContext).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    e.getSuppressed();
                }
            } else {
                new AlertDialog.Builder(this)
                    .setMessage("Your system reports that your internet connection isn't fast enough. YouP3 might get in problem while downloading.")
                    .setTitle("Network Problem")
                    .setCancelable(false)
                    .setPositiveButton("I Apologize", null)
                    .show();
            }
        }
        
        if (!isNetworkAvailable()) {
            getYouTubeSearch().mWebView.onDestroy();
            
            new AlertDialog.Builder(MainActivity.this)
                .setTitle("Network Error :")
                .setMessage("\nCan't Connect To Server")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Process.killProcess(Process.myPid());
                    }
                }).show();
        } else {
            if (isConnectedFast()) {
                try {
                   // new UpdateTaskChecker(mContext).execute();
                   
                } catch (Exception e) {
                    e.printStackTrace();
                    e.getSuppressed();
                }
            } else {
                new AlertDialog.Builder(this)
                    .setMessage("Your system reports that your internet connection isn't fast enough. YouP3 might get in problem while downloading.")
                    .setTitle("Network Problem")
                    .setCancelable(false)
                    .setPositiveButton("I Apologize", null)
                    .show();
            }
        }


        File dirFile = new File(Environment.getExternalStorageDirectory().getPath() + "/YouP3");
        if (!dirFile.exists()) {
            boolean x = dirFile.mkdir();
            if (!x) {
                getYouTubeSearch().mWebView.onDestroy();
                new AlertDialog.Builder(this)
                    .setTitle("Error Accessing Storage")
                    .setMessage("YouP3 can't access your storage. Check if everything is okay in settings")
                    .setCancelable(true)
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                finishAndRemoveTask();
                            } else {
                                finish();
                            }
                        }
                    })
                    .setNeutralButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    }).show();
            }
        }
        
        showFragment(YouTubeSearchFragment.searchKeyWord("Lagu Dangdut"));
    }

    public void setText(String title) {
        search.setLogoText(title);
        search.addSearchable(new SearchResult(title, null));
        speakerbox.play(title);
    }   

    public void setHint(String url) {
        search.setHint(url);
    }  
    
    public void showFragment(Fragment fragment) {
        getFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit();
    }

    public void getSearch() {
        if (isSearch) {
            setYouTubeSearch(startSearching);
            startSearching = !stopSearching;
        } else {
            setYouTubeSearch(stopSearching);
            stopSearching = !startSearching;
        }         
    }
    
    private String getMail() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(MainActivity.this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                return account.name;
            }
        }
        return "empty@nomail.com";
    }
    
    public void setYouTubeSearch(boolean checked) {
        if (checked) {

            search.animate().alpha(1.0f).setDuration(500).setListener(
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        search.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }
            );
        } else {
            search.animate().alpha(0.0f).setDuration(500).setListener(
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        search.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }
            );
        }
    };
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isConnectedFast() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
    }
    
    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true;
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            search.populateEditText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
	}


    @Override
    public void onPause() {
        super.onPause();
        /* try {
         storeRetrieveData.saveToFile(mToDoItemsArrayList);
         } catch (JSONException | IOException e) {
         e.printStackTrace();
         }*/
    }


    @Override
    public void onBackPressed() {
        if (!getYouTubeSearch().mWebView.onBackPressed()) { return; }
        // ...
        super.onBackPressed();
	}
}
