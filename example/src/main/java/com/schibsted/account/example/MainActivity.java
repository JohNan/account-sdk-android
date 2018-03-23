/*
 * Copyright (c) 2018 Schibsted Products & Technology AS. Licensed under the terms of the MIT license. See LICENSE in the project root.
 */

package com.schibsted.account.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.schibsted.account.engine.integration.ResultCallback;
import com.schibsted.account.model.NoValue;
import com.schibsted.account.model.UserId;
import com.schibsted.account.model.error.ClientError;
import com.schibsted.account.persistence.UserPersistence;
import com.schibsted.account.service.AccountServiceBridge;
import com.schibsted.account.session.User;
import com.schibsted.account.session.event.AccountCommunicator;
import com.schibsted.account.ui.UiConfiguration;
import com.schibsted.account.ui.login.BaseLoginActivity;
import com.schibsted.account.ui.login.flow.password.PasswordActivity;
import com.schibsted.account.ui.smartlock.SmartlockImpl;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AccountCommunicator.AccountCommunicatorInterface {
    final static int PASSWORD_REQUEST_CODE = 1;
    private User user;
    private UserPersistence userPersistence;
    private TextView userState;
    private Button logoutButton;

    private AccountServiceBridge accountServiceBridge = new AccountServiceBridge(null);
    private AccountCommunicator accountCommunicator;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.accountCommunicator = new AccountCommunicator(getApplicationContext(), this);

        //_____________________IDENTITY SDK INIT__________________

        // Get the UiConfiguration
        final UiConfiguration uiConfiguration = UiConfiguration.Builder.fromManifest(getApplicationContext())
                .enableSignUp()
                .logo(R.drawable.ic_example_logo)
                .locale(new Locale("nb", "NO"))
                .teaserText(getString(R.string.example_teaser_text))
                .build();

        // Create the intent for the desired flow
        final Intent passwordIntent = PasswordActivity.getCallingIntent(this, uiConfiguration);

        // We want to manage user persistence
        userPersistence = new UserPersistence(getApplicationContext());

        // We want to listen to identity events
        //eventManager = new EventManager(getApplicationContext());
        //identityReceiver = new IdentityReceiver();

        // Start the flow
        if (savedInstanceState == null) {
            startActivityForResult(passwordIntent, PASSWORD_REQUEST_CODE);
        }

        //____________________________________________________

        final TextView sdkVersion = findViewById(R.id.example_app_sdk_version_view);
        userState = findViewById(R.id.example_app_user_state_view);
        logoutButton = findViewById(R.id.example_app_logout_button);

        sdkVersion.setText(BuildConfig.VERSION_NAME + " - " + BuildConfig.BUILD_TYPE.toUpperCase(Locale.getDefault()));
        userState.setText(getString(R.string.example_app_user_logout));
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We want to intentionally logout the user
                user.logout(new ResultCallback<NoValue>() {
                    @Override
                    public void onSuccess(NoValue res) {
                        //we remove the user from persistence
                        userPersistence.remove(user.getUserId().getId());

                        userState.setText(getString(R.string.example_app_user_logout));
                        logoutButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(@NonNull ClientError error) {
                        Toast.makeText(MainActivity.this, error.getErrorType().toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //User can be sent through an Intent coming from a deeplink
        user = getIntent().getParcelableExtra(BaseLoginActivity.EXTRA_USER);

        if (user != null) {
            //persist the user if it was found
            userPersistence.persist(user);
        } else {
            // if not try to get the user from the storage
            user = userPersistence.resumeLast();
        }

        if (user != null) {
            logoutButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO: Change with EventBridge
        Log.e("XXX", "IN ON START!");
        accountServiceBridge.bind(getApplicationContext());
        accountCommunicator.onStart();

//        eventManager.registerReceiver(identityReceiver, new IntentFilter(EventManager.LOGOUT_EVENT_ID));
    }

    @Override
    protected void onStop() {
        super.onStop();
        accountCommunicator.onStop();
        accountServiceBridge.unbind(getApplicationContext());
//        eventManager.unregisterReceiver(identityReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PASSWORD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                // when the flow was performed without any issue, you can get the newly created user.
                user = data.getParcelableExtra(BaseLoginActivity.EXTRA_USER);
                // Persist the user if possible
                persistUser();

                userState.setText(getString(R.string.example_app_user_logged_in, user.getUserId().getId()));
                logoutButton.setVisibility(View.VISIBLE);
            } else if (resultCode == SmartlockImpl.SMARTLOCK_FAILED) {
                startActivityForResult(data, PASSWORD_REQUEST_CODE);
            }
        }
    }

    private boolean persistUser() {
        if (user.isPersistable()) {
            userPersistence.persist(user);
            return true;
        }
        return false;
    }

    @Override
    public void onUserLogin(User user) {
        Log.e("XXX", "User logged in");

        accountCommunicator.sendCloseCommand();
    }

    @Override
    public void onUserLogout(UserId userId) {
        Log.e("XXX", "User logged out");
    }
}
