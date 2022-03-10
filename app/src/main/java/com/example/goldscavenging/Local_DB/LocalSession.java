package com.example.goldscavenging.Local_DB;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LocalSession {

    private static final String PREF_NAME = "Data_clinic";
    private static final String TOKEN = "token";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String PASSWORD = "password";
    private static final String SHOP = "shop";
    private static final String ROLE = "role";
    private static final String isSessionCreated = "isSessionCreated";
    private static final String LASTLOGIN = "lastlogin";
    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor editor;


    public LocalSession(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, 0);
        editor = mPreferences.edit();
    }


    public void createSession(String token,String Id,String name,String phone,String shop,String role,String password) {
        editor.putBoolean(LocalSession.isSessionCreated, true);
        editor.putString(LocalSession.TOKEN,token);
        editor.putString(LocalSession.ID, Id);
        editor.putString(LocalSession.NAME,name);
        editor.putString(LocalSession.PHONE,phone);
        editor.putString(LocalSession.SHOP,shop);
        editor.putString(LocalSession.ROLE,role);
        editor.putString(LocalSession.PASSWORD,password);
        editor.apply();
        editor.commit();
    }


    public void lastDtae(String date){
        editor.putString(LocalSession.LASTLOGIN,date);
        editor.apply();
        editor.commit();
    }

    public static Boolean getIsSessionCreated() {
        return mPreferences.getBoolean(LocalSession.isSessionCreated, false);
    }



    public static String getId() {
        return mPreferences.getString(ID,"");
    }
    public static String getName() {
        return mPreferences.getString(NAME,"");
    }
    public static String getPhone() {
        return mPreferences.getString(PHONE,"");
    }
    public static String getShop() {
        return mPreferences.getString(SHOP,"");
    }
    public static String getRole() {
        return mPreferences.getString(ROLE,"");
    }
    public static String getPassword() {
        return mPreferences.getString(PASSWORD,"");
    }
    public static String getToken() {
        return mPreferences.getString(TOKEN,"");
    }
    public static String getLastlogin() {
        return mPreferences.getString(LASTLOGIN,"");
    }


    public static void clearSession() {
        editor.clear();
        editor.apply();
        editor.commit();
    }


}
