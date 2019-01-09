/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanql.commonlibrary.util;

import android.annotation.SuppressLint;
import android.widget.Toast;

/**
 * Toast工具类
 */
public class ToastUtils {

    private static Toast mToast;

    public static void showSingleToast(int resId) {
        getSingleToast(resId, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleToast(String text) {
        getSingleToast(text, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleLongToast(int resId) {
        getSingleToast(resId, Toast.LENGTH_LONG).show();
    }

    public static void showSingleLongToast(String text) {
        getSingleToast(text, Toast.LENGTH_LONG).show();
    }

    public static void showToast(int resId) {
        getToast(resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String text) {
        getToast(text, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(int resId) {
        getToast(resId, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(String text) {
        getToast(text, Toast.LENGTH_LONG).show();
    }

    private static Toast getSingleToast(int resId, int duration) {
        return getSingleToast(getString(resId), duration);
    }

    private static Toast getToast(int resId, int duration) {
        return getToast(getString(resId), duration);
    }

    public static String getString(int resId) {
        return UIUtils.getContext().getResources().getString(resId);
    }

    @SuppressLint("ShowToast")
    private static Toast getSingleToast(String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(UIUtils.getContext(), text, duration);
        } else {
            mToast.setText(text);
        }
        return mToast;
    }

    private static Toast getToast(String text, int duration) {
        return Toast.makeText(UIUtils.getContext(), text, duration);
    }
}
