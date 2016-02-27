package com.codepath.apps.mptweettweet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.codepath.apps.mptweettweet.models.User;
import com.codepath.apps.restclienttemplate.R;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ComposeDialogFragment extends DialogFragment {

    @Bind(R.id.btnClose)
    Button btnClose;

    @Bind(R.id.btnTweet)
    Button btnTweet;

    @Bind(R.id.tvCharacterCount)
    TextView tvCharacterCount;

    @Bind(R.id.tvUsername)
    TextView tvUsername;

    @Bind(R.id.etTweet)
    EditText etTweet;

    @Bind(R.id.ivProfileImage)
    RoundedImageView ivProfileImage;

    @Bind(R.id.tvReply)
    TextView tvReply;

    private String mCurrentUserName;
    private String mCurrentUserUrl;
    private User mReplyTo;
    private final int MAX_CHARACTER_COUNT = 140;
    private static final String ARG_USER_NAME = "username";
    private static final String ARG_USER_PROFILE = "profileurl";
    private static final String ARG_USER_REPLY = "reply";
    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onTweetSelected(String tweet);
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int newCount = MAX_CHARACTER_COUNT - s.length();
            tvCharacterCount.setText(String.valueOf(newCount));

            if (newCount < 0) {
                tvCharacterCount.setTextColor(getResources().getColor(R.color.redtext));
            } else {
                tvCharacterCount.setTextColor(getResources().getColor(R.color.text_dark));
            }
        }

        public void afterTextChanged(Editable s) {
        }
    };

    public static ComposeDialogFragment newInstance(String currentUserName, String currentUserUrl) {
        ComposeDialogFragment fragment = new ComposeDialogFragment();
        Bundle args = new Bundle();

        args.putString(ARG_USER_NAME, currentUserName);
        args.putString(ARG_USER_PROFILE, currentUserUrl);
        fragment.setArguments(args);

        return fragment;
    }

    public static ComposeDialogFragment newInstance(String currentUserName, String currentUserUrl, Long uid) {
        ComposeDialogFragment fragment = new ComposeDialogFragment();
        Bundle args = new Bundle();

        args.putString(ARG_USER_NAME, currentUserName);
        args.putString(ARG_USER_PROFILE, currentUserUrl);
        args.putLong(ARG_USER_REPLY, uid);
        fragment.setArguments(args);

        return fragment;
    }


    public ComposeDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentUserName = getArguments().getString(ARG_USER_NAME);
            mCurrentUserUrl = getArguments().getString(ARG_USER_PROFILE);
            long uid = getArguments().getLong(ARG_USER_REPLY);

            if (uid > 0) {
                mReplyTo = new Select().from(User.class)
                        .where("uid = ?", uid)
                        .executeSingle();
            }


        }
    }

    private void configureView() {

        if (mReplyTo != null) {
            tvReply.setText("In reply to " + mReplyTo.name);
            tvReply.setVisibility(View.VISIBLE);
            etTweet.setText("@" + mReplyTo.screenName);
            etTweet.setSelection(mReplyTo.screenName.length() + 1);
            int newCount = MAX_CHARACTER_COUNT - tvReply.length();
            tvCharacterCount.setText(String.valueOf(newCount));
        } else {
            tvReply.setVisibility(View.GONE);
        }

        tvUsername.setText(mCurrentUserName);
        Glide.with(getContext()).load(mCurrentUserUrl).placeholder(R.drawable.profile_photo_placeholder).into(ivProfileImage);

        etTweet.addTextChangedListener(mTextEditorWatcher);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTweet.getText().length() > MAX_CHARACTER_COUNT) {
                    Toast.makeText(getContext(), "To many characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mListener.onTweetSelected(etTweet.getText().toString());
                getDialog().dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compose_dialog, container, false);
        ButterKnife.bind(this, view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        this.configureView();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
