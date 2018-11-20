package application.android.com.expencestracker.Model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.HashMap;

import application.android.com.expencestracker.DBImp.UserTableImp;
import application.android.com.expencestracker.R;
import application.android.com.expencestracker.loginActivity;

public class UserSessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    Resources res;

    private static int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SqlDemoPerf";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    private String KEY_EMAIL;
    private String KEY_PWD;
    private String KEY_USERNAME;
    private String KEY_USERID;

    UserTableImp _userTable;

    public UserSessionManager(Context context) {
        this._context = context;
        this.pref = this._context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.editor = this.pref.edit();
        this._userTable = new UserTableImp(context);
        res = _context.getResources();
        KEY_EMAIL = res.getString(R.string.KEY_EMAIL);
        KEY_PWD = res.getString(R.string.KEY_PWD);
        KEY_USERNAME = res.getString(R.string.KEY_USERNAME);
        KEY_USERID = res.getString(R.string.KEY_USERID);
    }

    public void createUserLoginSession(String email, String pwd) {
        this.editor.putBoolean(IS_USER_LOGIN, true);
        this.editor.putString(KEY_EMAIL, email);
        this.editor.putString(KEY_PWD, pwd);
        this.editor.commit();

/*        if (!this.CheckLogIn()) {
            this.logoutUser();
        }*/
    }

    // Check the log in information.
    public boolean CheckLogIn() {
        boolean is_login = this.pref.getBoolean(IS_USER_LOGIN, false);
        if (is_login) {
            String user_email = this.pref.getString(KEY_EMAIL, "");
            String user_pwd = this.pref.getString(KEY_PWD, "");
            User user = this._userTable.getUser(user_email);
            if (user != null && user_pwd.equals(user.getPassword())) {
                this.editor.putString(KEY_USERNAME, user.getUsername());
                this.editor.putInt(KEY_USERID, user.getUserId());
                this.editor.commit();
                return true;
            }
        }
        return false;
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> usr = new HashMap<String, String>();
        usr.put(KEY_EMAIL, this.pref.getString(KEY_EMAIL, null));
        usr.put(KEY_PWD, this.pref.getString(KEY_PWD, null));
        usr.put(KEY_USERNAME, this.pref.getString(KEY_USERNAME, null));
        usr.put(KEY_USERID, String.format("%d", this.pref.getInt(KEY_USERID, -1)) );
        // Return to the caller.
        return usr;
    }

    public void logoutUser() {
        this.editor.clear();
        this.editor.commit();

        // Now Start the log in Activity again.
        Intent i = new Intent(this._context, loginActivity.class);
        // Closing all the activities.
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new flag.
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Now start the new activity.
        this._context.startActivity(i);
    }
}
