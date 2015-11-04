package com.groupon.vgudla.tclient.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.tclient.R;
import com.groupon.vgudla.tclient.activity.TimelineActivity;
import com.groupon.vgudla.tclient.listeners.OnComposeListener;

public class ComposeDialog extends DialogFragment {
    private static final int TWEET_CHARACTER_LIMIT = 140;
    EditText etCompose;
    TextView tvCharCount;
    ImageView ivSave;
    ImageView ivCancel;

    public ComposeDialog() {
        // Empty constructor is required for DialogFragment
    }

    public static ComposeDialog newInstance() {
        ComposeDialog composeDialog = new ComposeDialog();
        return composeDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Compose Tweet");
        etCompose = (EditText) view.findViewById(R.id.etCompose);
        tvCharCount = (TextView) view.findViewById(R.id.tvCharCount);

        ivSave = (ImageView) view.findViewById(R.id.ivSave);
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnComposeListener listener = (TimelineActivity)getActivity();
                listener.onFinishCompose(etCompose.getText().toString());
                dismiss();
            }
        });
        ivCancel = (ImageView) view.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tvCharCount.setText(TWEET_CHARACTER_LIMIT + " characters remaining");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCharCount.setText((TWEET_CHARACTER_LIMIT - s.length()) + " characters remaining");
            }
        });
    }
}
