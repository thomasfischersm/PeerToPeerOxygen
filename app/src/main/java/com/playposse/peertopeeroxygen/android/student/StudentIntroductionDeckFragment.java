package com.playposse.peertopeeroxygen.android.student;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.globalconfiguration.RedirectRouting;
import com.playposse.peertopeeroxygen.android.ui.GeneralImageGetter;

/**
 * A {@link Fragment} that shows introductory text to the user. It has an option to display a
 * dismiss button on the last slide (= fragment).
 */
public class StudentIntroductionDeckFragment extends Fragment {

    private static final String TEXT_RES_ID_PARAM = "slideTextResId";
    private static final String FINAL_SLIDE_PARAM = "isFinalSlide";

    private String slideText;
    private boolean isFinalSlide;

    private TextView slideTextView;
    private TextView swipeLeftHintTextView;
    private Button closeIntroductionDeckButton;

    public StudentIntroductionDeckFragment() {
        // Required empty public constructor
    }

    public static StudentIntroductionDeckFragment newInstance(
            int slideTextResId,
            boolean isFinalSlide) {

        StudentIntroductionDeckFragment fragment = new StudentIntroductionDeckFragment();
        Bundle args = new Bundle();
        args.putInt(TEXT_RES_ID_PARAM, slideTextResId);
        args.putBoolean(FINAL_SLIDE_PARAM, isFinalSlide);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            slideText = getContext().getString(getArguments().getInt(TEXT_RES_ID_PARAM));
            isFinalSlide = getArguments().getBoolean(FINAL_SLIDE_PARAM);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView =
                inflater.inflate(R.layout.fragment_student_introduction_deck, container, false);

        slideTextView = (TextView) rootView.findViewById(R.id.slideTextView);
        swipeLeftHintTextView = (TextView) rootView.findViewById(R.id.swipeLeftHintTextView);
        closeIntroductionDeckButton =
                (Button) rootView.findViewById(R.id.closeIntroductionDeckButton);

        Spanned slideTextSpanned = Html.fromHtml(
                slideText,
                new GeneralImageGetter(getContext(),
                        slideTextView.getTextSize()), null);
        slideTextView.setText(slideTextSpanned);
        swipeLeftHintTextView.setVisibility(isFinalSlide ? View.GONE : View.VISIBLE);
        closeIntroductionDeckButton.setVisibility(isFinalSlide ? View.VISIBLE : View.GONE);

        closeIntroductionDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSlidesCompleted();
            }
        });

        return rootView;
    }

    private void onSlidesCompleted() {
        OxygenSharedPreferences.setHasIntroDeckBeenShown(getContext(), true);
        RedirectRouting.onIntroductionSlidesCompleted(getContext());
    }
}
