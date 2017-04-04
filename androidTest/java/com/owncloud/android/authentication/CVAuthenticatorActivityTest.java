package com.owncloud.android.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.web.webdriver.DriverAtoms;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import com.owncloud.android.MainApp;
import com.owncloud.android.R;
import com.owncloud.android.ui.adapter.AccountListAdapter;
import com.owncloud.android.ui.adapter.AccountListItem;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.getText;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertTrue;

/**
 * Created by alejandro on 03/04/17.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class CVAuthenticatorActivityTest {

    public static final String EXTRA_ACTION = "ACTION";
    public static final String EXTRA_ACCOUNT = "ACCOUNT";

    private static final int WAIT_LOGIN = 5000;

    private static final String ERROR_MESSAGE = "Activity not finished";
    private static final String RESULT_CODE = "mResultCode";

    @Rule
    public ActivityTestRule<AuthenticatorActivity> mActivityRule = new ActivityTestRule<AuthenticatorActivity>(
            AuthenticatorActivity.class) {

        @Override
        protected Intent getActivityIntent() {

            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, AuthenticatorActivity.class);
            result.putExtra(EXTRA_ACTION, AuthenticatorActivity.ACTION_CREATE);
            result.putExtra(EXTRA_ACCOUNT, "");
            return result;
        }

        @Override
        protected void afterActivityLaunched() {
            // Technically we do not need to do this - WebViewActivity has javascript turned on.
            // Other WebViews in your app may have javascript turned off, however since the only way
            // to automate WebViews is through javascript, it must be enabled.
            onWebView().forceJavascriptEnabled();
        }


    };



    @Before
    public void init(){
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        /*Point[] coordinates = new Point[4];
        coordinates[0] = new Point(248, 1020);
        coordinates[1] = new Point(248, 429);
        coordinates[2] = new Point(796, 1020);
        coordinates[3] = new Point(796, 429);*/

        final Context context=InstrumentationRegistry.getInstrumentation()
                .getTargetContext();


        /*final Account mAccount=AccountUtils.getCurrentOwnCloudAccount(context);
        if(mAccount!=null) {
            AccountManager am = (AccountManager) InstrumentationRegistry.getInstrumentation()
                    .getTargetContext().getSystemService(context.ACCOUNT_SERVICE);
            am.removeAccount(mAccount, new AccountManagerCallback<Boolean>() {
                @Override
                public void run(AccountManagerFuture<Boolean> future) {
                    if (future.isDone()) {
                        // after remove account
                        Account account = new Account(mAccount.name, MainApp.getAccountType());

                        if (AccountUtils.getCurrentOwnCloudAccount(context) == null) {
                            String accountName = "";
                            Account[] accounts = AccountManager.get(context).getAccountsByType(MainApp.getAccountType());
                            if (accounts.length != 0) {
                                assertTrue("Error ", false);
                            }

                        }


                    }
                }
            }, new Handler(Looper.getMainLooper()));
        }*/
        try {
            if (!uiDevice.isScreenOn()) {
                uiDevice.wakeUp();
                //uiDevice.swipe(coordinates, 10);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void check_login()
            throws InterruptedException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Bundle arguments = InstrumentationRegistry.getArguments();

        // Get values passed
        String testUser = arguments.getString("TEST_USER");
        String testPassword = arguments.getString("TEST_PASSWORD");
        String testServerURL = arguments.getString("TEST_SERVER_URL");

        onWebView().withContextualElement(findElement(Locator.CLASS_NAME,"logo-clarovideo"))
                .perform(webClick());

        Thread.sleep(WAIT_LOGIN);
        // Check that the Activity ends after clicking
        onWebView().withContextualElement(findElement(Locator.NAME,"username"))
                .perform(DriverAtoms.webKeys(testUser));

        onWebView().withContextualElement(findElement(Locator.NAME,"password"))
                .perform(DriverAtoms.webKeys(testPassword));

        onWebView().withContextualElement(findElement(Locator.CLASS_NAME,"button"))
                .perform(webClick());

        Thread.sleep(WAIT_LOGIN);

        //onWebView().check(webMatches(getText(), containsString("incorrecto")));

        Field f = Activity.class.getDeclaredField(RESULT_CODE);
        f.setAccessible(true);
        int mResultCode = f.getInt(mActivityRule.getActivity());

        assertTrue(ERROR_MESSAGE, mResultCode == Activity.RESULT_OK);

    }




}
