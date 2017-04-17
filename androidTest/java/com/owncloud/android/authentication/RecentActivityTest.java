package com.owncloud.android.authentication;

import android.accounts.Account;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import com.owncloud.android.MainApp;
import com.owncloud.android.R;
import com.owncloud.android.lib.common.OwnCloudAccount;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientManagerFactory;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.common.utils.Log_OC;
import com.owncloud.android.lib.resources.files.CreateRemoteFolderOperation;
import com.owncloud.android.ui.activity.ActivitiesListActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;


/**
 * Created by alejandro on 17/04/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RecentActivityTest {
    public static final String TAG = "RecentActivityTest";
    private static final int WAIT = 5000;

    String folderName="Test_";

    @Rule
    public ActivityTestRule<ActivitiesListActivity> mActivityRule = new ActivityTestRule<ActivitiesListActivity>(
            ActivitiesListActivity.class){

        @Override
        protected void beforeActivityLaunched() {

            super.beforeActivityLaunched();
        }


    };


    @Before
    public void created_folder() throws InterruptedException {
        final Account currentAccount = AccountUtils.getCurrentOwnCloudAccount(mActivityRule.getActivity());


                OwnCloudAccount ocAccount = null;
                try {
                    ocAccount = new OwnCloudAccount(
                            currentAccount,
                            mActivityRule.getActivity()
                    );
                    OwnCloudClient mClient = OwnCloudClientManagerFactory.getDefaultSingleton().
                            getClientFor(ocAccount, MainApp.getAppContext());
                    mClient.setOwnCloudVersion(AccountUtils.getServerVersion(currentAccount));

                    folderName+=new Date().getTime();

                    RemoteOperation getRemoteNotificationOperation = new CreateRemoteFolderOperation("/"+folderName+"/", false);

                    final RemoteOperationResult result =
                            getRemoteNotificationOperation.execute(mClient);

                    if (result.isSuccess() && result.getData() != null) {
                        final ArrayList<Object> activities = result.getData();

                        Log_OC.d(TAG, result.getLogMessage());

                    } else {
                        // show error
                        Log_OC.d(TAG, result.getLogMessage());
                    }


                } catch (com.owncloud.android.lib.common.accounts.AccountUtils.AccountNotFoundException e) {
                    Log_OC.e(TAG, "Account not found", e);
                } catch (IOException e) {
                    Log_OC.e(TAG, "IO error", e);
                } catch (OperationCanceledException e) {
                    Log_OC.e(TAG, "Operation has been canceled", e);
                } catch (AuthenticatorException e) {
                    Log_OC.e(TAG, "Authentication Exception", e);
                }

    }

    @Test
    public void reviewActivity() throws InterruptedException {

        Thread.sleep(WAIT);

        // onData(allOf(instanceOf(Activity.class)));

        onView(withId(R.id.swipe_containing_list)).perform(withCustomConstraints(swipeDown(),isDisplayingAtLeast(85)));

        Thread.sleep(WAIT);

        onView(withText(containsString(folderName))).check(matches(isDisplayed()));
    }




    public static ViewAction withCustomConstraints(final ViewAction action, final Matcher<View> constraints) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return constraints;
            }

            @Override
            public String getDescription() {
                return action.getDescription();
            }

            @Override
            public void perform(UiController uiController, View view) {
                action.perform(uiController, view);
            }
        };
    }


}
