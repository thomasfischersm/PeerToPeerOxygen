package com.playposse.peertopeeroxygen.android.student;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.data.clientactions.GetPublicDomainsClientAction;
import com.playposse.peertopeeroxygen.android.data.clientactions.SubscribeToPrivateDomainClientAction;
import com.playposse.peertopeeroxygen.android.data.missions.MissionDataManager;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CombinedDomainBeans;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An {@link android.app.Activity} that asks the user to select a domain.
 */
public class StudentDomainSelectionActivity
        extends StudentParentActivity
        implements GetPublicDomainsClientAction.Callback {

    private final static String LOG_CAT = StudentDomainSelectionActivity.class.getSimpleName();

    private LinearLayout rootView;
    private TextView usersDomainTextView;
    private TextView selectPublicDomainTextView;
    private EditText invitationCodeEditText;
    private TextView createPrivateDomainLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_domain_selection);
        super.onCreate(savedInstanceState);

        rootView = (LinearLayout) findViewById(R.id.rootView);
        usersDomainTextView = (TextView) findViewById(R.id.usersDomainTextView);
        selectPublicDomainTextView = (TextView) findViewById(R.id.selectPublicDomainTextView);
        invitationCodeEditText = (EditText) findViewById(R.id.invitationCodeEditText);
        createPrivateDomainLink = (TextView) findViewById(R.id.createPrivateDomainLink);

        invitationCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    invitationCodeEditText.clearFocus();
                    rootView.requestFocus();

                    switchToPrivateDomain();
                }
            }
        });

        invitationCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    switchToPrivateDomain();
                    return true;
                }
                return false;
            }
        });

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_CAT, "Got touch");
                invitationCodeEditText.clearFocus();
                rootView.requestFocus();

                InputMethodManager imm =
                        (InputMethodManager) getApplicationContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        createPrivateDomainLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                startActivity(new Intent(context, StudentCreatePrivateDomainActivity.class));
            }
        });

        showLoadingProgress();
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        super.receiveData(dataRepository);

        dataServiceConnection.getLocalBinder().getPublicDomains(this);
    }

    @Override
    public void onResult(CombinedDomainBeans combinedDomainBeans) {
        dismissLoadingProgress();

        Set<Long> subscribedDomainIds = OxygenSharedPreferences.getSubscribedDomainIds(this);

        // Add buttons for subscribed domains.
        int index = rootView.indexOfChild(usersDomainTextView) + 1;
        clearDomainButtons(index);
        List<DomainBean> subscribedDomainBeans = combinedDomainBeans.getSubscribedBeans();
        if (subscribedDomainBeans == null) {
            subscribedDomainBeans = new ArrayList<>();
        }
        for (DomainBean domainBean : subscribedDomainBeans) {
            addDomainButton(index++, domainBean);
        }
        usersDomainTextView.setVisibility(
                subscribedDomainBeans.size() == 0 ? View.GONE : View.VISIBLE);

        // Add buttons for public domains.
        index = rootView.indexOfChild(selectPublicDomainTextView) + 1;
        clearDomainButtons(index);
        List<DomainBean> publicDomainBeans = combinedDomainBeans.getPublicBeans();
        if (publicDomainBeans != null) {
            for (DomainBean domainBean : publicDomainBeans) {
                if (!subscribedDomainIds.contains(domainBean.getId())) {
                    addDomainButton(index++, domainBean);
                }
            }
        }
    }

    /**
     * Clears all the {@link Button}s from the rootView starting at the specified index until it
     * encounters a child that is not a button.
     */
    private void clearDomainButtons(int index) {
        while (index < rootView.getChildCount()) {
            View view = rootView.getChildAt(index);
            if (view instanceof Button) {
                rootView.removeViewAt(index);
            } else {
                break;
            }
        }
    }

    private void addDomainButton(int index, final DomainBean domainBean) {
        Button button = new Button(this);
        button.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setText(domainBean.getName());
        rootView.addView(button, index);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToPublicDomain(domainBean);
            }
        });
    }

    private void switchToPrivateDomain() {
        if (dataServiceConnection == null) {
            return;
        }

        if (StringUtil.isEmpty(invitationCodeEditText)) {
            return;
        }

        showLoadingProgress();
        String invitationCode = invitationCodeEditText.getText().toString();
        dataServiceConnection.getLocalBinder().subscribeToPrivateDomain(
                invitationCode,
                new SubscribeToPrivateDomainClientAction.Callback() {
                    @Override
                    public void onResult(UserBean userBean) {
                        dismissLoadingProgress();
                        switchToDomain(userBean.getDomainId());
                    }

                    @Override
                    public void onError() {
                        dismissLoadingProgress();
                        ToastUtil.sendToast(
                                getApplicationContext(),
                                R.string.invitation_code_not_found_toast);
                    }
                });
    }

    private void switchToPublicDomain(DomainBean domainBean) {
        if (dataServiceConnection != null) {
            dataServiceConnection
                    .getLocalBinder()
                    .subscribeToPublicDomain(domainBean.getId(), null);
        }

        switchToDomain(domainBean.getId());
    }

    private void switchToDomain(Long domainId) {
        MissionDataManager.switchToDomainAsync(
                domainId,
                getApplicationContext(),
                dataServiceConnection.getLocalBinder());

        startActivity(new Intent(getApplicationContext(), StudentMainActivity.class));
    }
}
