package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.admin.AdminMainActivity;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.clientactions.CreatePrivateDomainClientAction;
import com.playposse.peertopeeroxygen.android.data.missions.MissionDataManager;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;

/**
 * An {@link android.app.Activity} that creates a private domain.
 */
public class StudentCreatePrivateDomainActivity
        extends StudentParentActivity
        implements CreatePrivateDomainClientAction.Callback {

    private EditText domainNameEditText;
    private EditText domainDescriptionEditText;
    private Button createPrivateDomainButton;

    private boolean isDomainCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_create_private_domain);
        super.onCreate(savedInstanceState);

        setTitle(R.string.create_private_domain_title);

        domainNameEditText = (EditText) findViewById(R.id.domainNameEditText);
        domainDescriptionEditText = (EditText) findViewById(R.id.domainDescriptionEditText);
        createPrivateDomainButton = (Button) findViewById(R.id.createPrivateDomainButton);

        createPrivateDomainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPrivateDomain();
            }
        });
    }

    private void createPrivateDomain() {
        if (!validate()) {
            return;
        }

        if (dataServiceConnection == null) {
            return;
        }

        String name = StringUtil.getCleanString(domainNameEditText);
        String description = StringUtil.getCleanString(domainDescriptionEditText);

        dataServiceConnection.getLocalBinder().createPrivateDomain(name, description, this);
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
        ToastUtil.sendToast(this, R.string.private_domain_creation_success_toast);

        MissionDataManager.switchToDomainAsync(
                domainBean.getId(),
                this,
                dataServiceConnection.getLocalBinder());

        isDomainCreated = true;
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        super.receiveData(dataRepository);

        if (isDomainCreated) {
            // Wait for the new domain to be loaded and go to the admin home activity.
            isDomainCreated = false; // Sometimes, this activity exists way past when it is shown.
            startActivity(new Intent(this, AdminMainActivity.class));
        }
    }
}
