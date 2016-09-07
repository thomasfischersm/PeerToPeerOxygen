package com.playposse.peertopeeroxygen.android.ui.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.student.QrCodeScannerFragment;
import com.playposse.peertopeeroxygen.android.util.TextFormatter;
import com.playposse.peertopeeroxygen.android.youtube.BugFreeYouTubeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

/**
 * A {@link FragmentStatePagerAdapter} that parses the instruction text to find h1 HTML tags.
 * Each H1 HTML tag receives its on page. The user sees all the headings in the tab navigation
 * and can click to the different chapters of the instruction.
 */
public class InstructionPagerAdapter extends TaggableFragmentStatePageAdapter {

    private static final String LOG_CAT = InstructionPagerAdapter.class.getSimpleName();

    private final List<Section> sections;
    private Fragment invitationFragment;
    private final String youTubeVideoId;
    private final boolean enableScanner;
    private final Context context;

    public InstructionPagerAdapter(
            FragmentManager fm,
            String instruction,
            @Nullable Fragment invitationFragment,
            String youTubeVideoId,
            boolean enableScanner,
            Context context) {

        super(fm);

        this.invitationFragment = invitationFragment;
        this.youTubeVideoId = youTubeVideoId;
        this.enableScanner = enableScanner;
        this.context = context;

        sections = parseInstruction(instruction, context);
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(LOG_CAT, "InstructionPageAdapter.getItem called for position " + position);

        if (invitationFragment != null) {
            if (position == 0) {
                return invitationFragment;
            } else {
                position -= 1;
            }
        }

        if (position < sections.size()) {
            return createInstructionFragment(position);
        } else {
            position -= sections.size();
        }

        if (youTubeVideoId != null) {
            if (position == 0) {
                return BugFreeYouTubeFragment.newInstance(youTubeVideoId);
            } else {
                position -= 1;
            }
        }

        if (enableScanner) {
            if (position == 0) {
                return createQrCodeScannerFragment();
            }
        }

        throw new RuntimeException("Unexpected fragment requested: " + position);
    }

    @Nullable
    @Override
    public String getItemTag(int position) {
        return "very-unique-" + position;
    }

    private Fragment createQrCodeScannerFragment() {
        return new QrCodeScannerFragment();
    }

    @NonNull
    private Fragment createInstructionFragment(int position) {
        InstructionFragment fragment = new InstructionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        String content = sections.get(position).getContent();
        args.putString(InstructionFragment.CONTENT_ARG, content);
        return fragment;
    }

    @Override
    public int getCount() {
        int invitationFragmentSize = (invitationFragment != null) ? 1 : 0;
        int scannerFragmentSize = enableScanner ? 1 : 0;
        int youTubeVideoSize = (youTubeVideoId != null) ? 1 : 0;
        return sections.size() + invitationFragmentSize + scannerFragmentSize + youTubeVideoSize;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if ((position == 0) && (invitationFragment != null)) {
            return context.getString(R.string.invitation_title);
        } else if (invitationFragment != null) {
            position -= 1;
        }

        if (position < sections.size()) {
            return sections.get(position).getHeading();
        } else {
            position -= sections.size();
        }

        if ((position == 0) && (youTubeVideoId != null)) {
            return context.getString(R.string.youtube_title);
        } else if (youTubeVideoId != null) {
            position -= 1;
        }

        if ((position == 0) && enableScanner) {
            return context.getString(R.string.link_buddy_title);
        } else {
            throw new RuntimeException("Requested unexpected fragment: " + position);
        }
    }

    private static List<Section> parseInstruction(@Nullable String instruction, Context context) {
        List<Section> sections = new ArrayList<>();
        if (instruction == null) {
            instruction = "";
        }

        Pattern pattern = Pattern.compile("<[hH]1>([^>]*)</[hH]1>");
        Matcher matcher = pattern.matcher(instruction);

        String heading = null;
        int contentStart = 0;
        while (matcher.find()) {
            if (heading != null) {
                int contentEnd = matcher.start();
                String content = instruction.substring(contentStart, contentEnd);
                sections.add(new Section(heading, content));
            }

            heading = matcher.group(1);
            contentStart = matcher.end();
        }

        if (heading != null) {
            String content = instruction.substring(contentStart);
            sections.add(new Section(heading, content));
        } else {
            String defaultHeading = context.getString(R.string.default_instruction_tab_label);
            sections.add(new Section(defaultHeading, instruction));
        }

        return sections;
    }

    /**
     * Data structure to represent a section of the instructions.
     */
    private static class Section {

        private final String heading;
        private final String content;

        private Section(String heading, String content) {
            this.heading = heading;
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public String getHeading() {
            return heading;
        }
    }

    /**
     * A simple {@link Fragment} that consists of a {@link TextView} that shows the content of the
     * section.
     */
    public static class InstructionFragment extends Fragment {

        protected static final String CONTENT_ARG = "content";

        @Override
        public View onCreateView(
                LayoutInflater inflater,
                ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_instruction, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.textView);
            Bundle args = getArguments();
            String content = args.getString(CONTENT_ARG);
            textView.setText(TextFormatter.format(content));
            return rootView;
        }
    }
}
