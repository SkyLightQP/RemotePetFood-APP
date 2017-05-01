package kr.kgaons.remotepetfood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public MainActivity main;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isNetworkOnline()) Toast.makeText(MainActivity .this,"네트워크가 연결되지 않았습니다. 다시 시도 해주세요.", Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        main = this;

        webView = (WebView) this.findViewById(R.id.web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSaveFormData(true);
        class MyWebViewClient extends WebViewClient {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        }
        webView.setWebViewClient(new MyWebViewClient());
        if(!getPreferences("RemotePetFood","URL").isEmpty()) {
            webView.loadUrl(getPreferences("RemotePetFood","URL"));
        }
        else{
            webView.loadUrl("https://www.google.co.kr");
            Toast.makeText(main, "주소가 등록되어있지 않습니다.\n설정을 통해 주소를 등록해주세요!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            LayoutInflater linf = LayoutInflater.from(this);
            final View inflator = linf.inflate(R.layout.dialog_content, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("연결할 주소 입력하기");
            builder.setView(inflator);

            final EditText e = (EditText) inflator.findViewById(R.id.et);

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!e.getText().toString().isEmpty()){
                        setPreferences("RemotePetFood","URL",e.getText().toString());
                        Toast.makeText(main, "완료! 새로고침을 눌러주세요!\n설정된 주소: " + getPreferences("RemotePetFood","URL"), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(main, "내용이 없습니다.\n현재 주소: " + getPreferences("RemotePetFood","URL"), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.show();
            return true;
        }
        if(id == R.id.refresh){
            if(!getPreferences("RemotePetFood","URL").isEmpty()) {
                webView.loadUrl(getPreferences("RemotePetFood","URL"));
            }
            else{
                webView.loadUrl("https://www.google.co.kr");
                Toast.makeText(main, "주소가 등록되어있지 않습니다.\n설정을 통해 주소를 등록해주세요!", Toast.LENGTH_SHORT).show();
            }
        }
        if(id == R.id.delete){
            SharedPreferences pref = getSharedPreferences("RemotePetFood", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("URL");
            editor.commit();
            Toast.makeText(main, "삭제 완료! 주소를 재설정 해주세요.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private String getPreferences(String name, String key){
        SharedPreferences pref = getSharedPreferences(name, MODE_PRIVATE);
        return pref.getString(key, "");
    }
    private void setPreferences(String name, String key, String value){
        SharedPreferences pref = getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }
}
