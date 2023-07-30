package com.example.musicapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicapp.MyApplication;
import com.example.musicapp.R;
import com.example.musicapp.activity.MainActivity;
import com.example.musicapp.constant.GlobalFuntion;
import com.example.musicapp.databinding.FragmentFeedbackBinding;
import com.example.musicapp.model.Feedback;
import com.example.musicapp.utils.StringUtil;

public class FeedbackFragment extends Fragment {

    private FragmentFeedbackBinding fragmentFeedbackBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentFeedbackBinding = FragmentFeedbackBinding.inflate(inflater, container, false);

        fragmentFeedbackBinding.tvSendFeedback.setOnClickListener(v -> onClickSendFeedback());

        return fragmentFeedbackBinding.getRoot();
    }

    private void onClickSendFeedback() {
        if (getActivity() == null) {
            return;
        }
        MainActivity activity = (MainActivity) getActivity();

        String strName = fragmentFeedbackBinding.edtName.getText().toString();
        String strPhone = fragmentFeedbackBinding.edtPhone.getText().toString();
        String strEmail = fragmentFeedbackBinding.edtEmail.getText().toString();
        String strComment = fragmentFeedbackBinding.edtComment.getText().toString();

        if (StringUtil.isEmpty(strName)) {
            GlobalFuntion.showToastMessage(activity, getString(R.string.name_require));
        } else if (StringUtil.isEmpty(strComment)) {
            GlobalFuntion.showToastMessage(activity, getString(R.string.comment_require));
        } else {
            activity.showProgressDialog(true);
            Feedback feedback = new Feedback(strName, strPhone, strEmail, strComment);
            MyApplication.get(getActivity()).getFeedbackDatabaseReference()
                    .child(String.valueOf(System.currentTimeMillis()))
                    .setValue(feedback, (databaseError, databaseReference) -> {
                        activity.showProgressDialog(false);
                        sendFeedbackSuccess();
                    });
        }
    }

    public void sendFeedbackSuccess() {
        GlobalFuntion.hideSoftKeyboard(getActivity());
        GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_send_feedback_success));
        fragmentFeedbackBinding.edtName.setText("");
        fragmentFeedbackBinding.edtPhone.setText("");
        fragmentFeedbackBinding.edtEmail.setText("");
        fragmentFeedbackBinding.edtComment.setText("");
    }
}
