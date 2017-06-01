package com.warrior.hangsu.administrator.mangaeasywatch.translate;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.warrior.hangsu.administrator.mangaeasywatch.R;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.ToastUtil;
import com.warrior.hangsu.administrator.mangaeasywatch.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.mangaeasywatch.volley.VolleyTool;

import java.util.HashMap;

/**
 * /storage/sdcard0/reptile/one-piece
 * <p/>
 * Created by Administrator on 2016/4/4.
 */
public class TranslateActivity extends Activity implements OnClickListener {
    private RelativeLayout translateRL;
    private TextView translateResultTv;
    private EditText translateET;
    private Button okBtn, cleanBtn;
    private ClipboardManager clip;//复制文本用
    private int queryWordCount = 0;
    private String lastQueryWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translat);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initUI();
        showKeyBroad();
//        queryWordCount = SharedPreferencesUtils.getIntSharedPreferencesData
//                (this, Globle.mangaPath + "queryWordCount");
    }

    @Override
    protected void onResume() {
        super.onResume();
        recover();
    }

    private void initUI() {
        translateRL = (RelativeLayout) findViewById(R.id.translate_rl);
        translateET = (EditText) findViewById(R.id.translate_et);
        translateResultTv = (TextView) findViewById(R.id.translate_result_tv);
        cleanBtn = (Button) findViewById(R.id.clean_btn);
        okBtn = (Button) findViewById(R.id.ok_btn);

        translateET.setOnKeyListener(new View.OnKeyListener() {
                                         @Override
                                         public boolean onKey(View v, int keyCode, KeyEvent event) {
                                             if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                                 queryWord();
                                                 return true;
                                             } else {
                                                 return false;
                                             }

                                         }
                                     }

        );

        cleanBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
    }


    public void showKeyBroad() {
        // 自动弹出键盘
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(translateET, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void closeKeyBroad() {
        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(translateET, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(translateET.getWindowToken(), 0);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_ENTER) {
//            if (!TextUtils.isEmpty(translateET.getText().toString())) {
//                translation(translateET.getText().toString());
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    private void translation(String word) {

        clip.setText(word);
        String url = Globle.YOUDAO + word;
        HashMap<String, String> params = new HashMap<String, String>();
        VolleyCallBack<YoudaoResponse> callback = new VolleyCallBack<YoudaoResponse>() {

            @Override
            public void loadSucceed(YoudaoResponse result) {
                if (null != result && result.getErrorCode() == 0) {
                    YoudaoResponse.BasicBean item = result.getBasic();
                    String t = "";
                    if (null != item) {
                        for (int i = 0; i < item.getExplains().size(); i++) {
                            t = t + item.getExplains().get(i) + ";";
                        }

                        translateResultTv.setText(result.getQuery() + " [" + item.getPhonetic() + "]: " + "\n" + t);
                    } else {
                        translateResultTv.setText("没查到该词");
                    }
                } else {
                    translateResultTv.setText("没查到");
                }
            }

            @Override
            public void loadFailed(VolleyError error) {
                ToastUtil.tipShort(TranslateActivity.this, "error" + error);
            }
        };
        VolleyTool.getInstance(this).requestData(Request.Method.GET,
                TranslateActivity.this, url, params,
                YoudaoResponse.class, callback);

    }


    private void save() {
        SharedPreferencesUtils.setSharedPreferencesData
                (TranslateActivity.this, Globle.mangaPath + "queryWordCount", queryWordCount);
        ToastUtil.tipShort(TranslateActivity.this, "" + queryWordCount);
    }

    private void recover() {
        queryWordCount = SharedPreferencesUtils.getIntSharedPreferencesData
                (this, Globle.mangaPath + "queryWordCount");
        ToastUtil.tipShort(TranslateActivity.this, "恢复" + queryWordCount);
    }

    //    @Override
//    protected void onPause() {
//        super.onPause();
//        save();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        save();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        save();
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        recover();
//    }
    private void queryWord() {
        if (!TextUtils.isEmpty(translateET.getText().toString()) &&
                !lastQueryWord.equals(translateET.getText().toString())) {
            closeKeyBroad();
            queryWordCount++;
            save();
            translation(translateET.getText().toString());
            lastQueryWord = translateET.getText().toString();
        } else if (lastQueryWord.equals(translateET.getText().toString())) {
            clip.setText(lastQueryWord);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                queryWord();
                break;
            case R.id.clean_btn:
                showKeyBroad();
                translateET.setText("");
                break;
        }
    }
}
