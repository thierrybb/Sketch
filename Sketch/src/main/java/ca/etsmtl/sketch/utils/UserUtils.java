package ca.etsmtl.sketch.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;

public class UserUtils {
    public static final String USER_NAME = "userName";
    public static final String USER_PASSWORD= "password";

    private static SharedPreferences createPref(Context context) {
        return new ObscuredSharedPreferences(
                context, context.getSharedPreferences("pref", Context.MODE_PRIVATE) );
    }

    public static String getUsername(Context context){
        SharedPreferences pref = createPref(context);
        return pref.getString(USER_NAME, "None");
    }

    /*
    TODO : find a better way to keep user credential!!
     */
    public static void storeUserPassword(Context context, String user, String password) {
        SharedPreferences pref = createPref(context);

        SharedPreferences.Editor edit = pref.edit();

        edit.putString(USER_NAME, user);
        edit.putString(USER_PASSWORD, password);

        edit.apply();
    }

    public static void logout(Context context) {
        SharedPreferences pref = createPref(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.remove(USER_NAME);
        edit.remove(USER_PASSWORD);
        edit.commit();
    }

    public static boolean isLogged(Context context) {
        SharedPreferences pref = createPref(context);
        return pref.contains(USER_NAME);
    }

    public static String getPassword(Context context) {
        SharedPreferences pref = createPref(context);
        return pref.getString(USER_PASSWORD, "None");
    }
}
