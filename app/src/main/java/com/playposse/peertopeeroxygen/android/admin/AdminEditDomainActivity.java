package com.playposse.peertopeeroxygen.android.admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.clientactions.SaveDomainClientAction;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;

/**
 * An {@link android.app.Activity} that allows an admin to edit the name and description of a
 * domain. It also shows the invitation code for private domains.
 */
public class AdminEditDomainActivity
        extends AdminParentActivity
        implements SaveDomainClientAction.Callback {

    private EditText domainNameEditText;
    private EditText domainDescriptionEditText;
    private TextView invitationCodeTextView;
    private Button saveDomainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_edit_domain);
        super.onCreate(savedInstanceState);

        domainNameEditText = (EditText) findViewById(R.id.domainNameEditText);
        domainDescriptionEditText = (EditText) findViewById(R.id.domainDescriptionEditText);
        invitationCodeTextView = (TextView) findViewById(R.id.invitationCodeTextView);
        saveDomainButton = (Button) findViewById(R.id.saveDomainButton);

        saveDomainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDomain();
            }
        });
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        DomainBean domainBean = dataRepository.getCompleteMissionDataBean().getDomainBean();

        domainNameEditText.setText(domainBean.getName());
        domainDescriptionEditText.setText(domainBean.getDescription());

        if (!domainBean.getPublic()) {
            String invitationCodeHint =
                    getString(R.string.private_domain_hint, domainBean.getInvitationCode());
            invitationCodeTextView.setText(Html.fromHtml(invitationCodeHint));
            invitationCodeTextView.setVisibility(View.VISIBLE);
        } else {
            invitationCodeTextView.setVisibility(View.GONE);
        }
    }

    private void saveDomain() {
        if (!validate()) {
            return;
        }

        if (dataServiceConnection == null) {
            return;
        }

        showLoadingProgress();

        String name = StringUtil.getCleanString(domainNameEditText);
        String description = StringUtil.getCleanString(domainDescriptionEditText);
        dataServiceConnection.getLocalBinder().saveDomain(name, description, this);
    }

    private boolean validate() {
        boolean isValid = true;
        if (StringUtil.isEmpty(domainNameEditText)) {
            domainNameEditText.setError(getString(R.string.empty_string_error));
            isValid = false;
        }

        if (StringUtil.isEmpty(domainDescriptionEditText)) {
            domainDescriptionEditText.setError(getString(R.string.empty_string_error));
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onResult(DomainBean domainBean) {
        dismissLoadingProgress();
        ToastUtil.sendToast(this, R.string.save_domain_success_toast);
    }

    @Override
    public void onDuplicateNameError(String domainName) {
        dismissLoadingProgress();
        ToastUtil.sendToast(this, R.string.duplicate_private_domain_name_toast, domainName);
    }
}
