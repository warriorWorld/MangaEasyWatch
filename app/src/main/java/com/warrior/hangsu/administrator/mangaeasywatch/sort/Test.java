package com.warrior.hangsu.administrator.mangaeasywatch.sort;


import android.content.SyncStatusObserver;
import android.util.Log;

import com.warrior.hangsu.administrator.mangaeasywatch.utils.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/4/5.
 */
public class Test {
    public static void main(String args[]) {
        String s = "onepiece_35_13";
        String res0 = "";
        String res1 = "";
        boolean ca = true;
        String[] arr = s.split("_");
        Pattern p;
        p = Pattern.compile("^\\d*$");
        for (int i = 0; i < arr.length; i++) {
            System.out.println("match" + p.matcher(arr[i]).matches());
            if (p.matcher(arr[i]).matches()) {
                if (ca) {
                    res0 = arr[i];
                    ca = false;
                } else {
                    res1 = arr[i];
                }
            }
        }
        System.out.print(res0 + "章" + res1);
    }
}
