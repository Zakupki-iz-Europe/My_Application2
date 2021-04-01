package com.example.myapplication2;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class YesNoDialogFragment extends DialogFragment {

    public static final String ARG_TITLE = "YesNoDialog.Title";
    public static final String ARG_MESSAGE = "YesNoDialog.Message";

    private YesNoDialogFragmentListener listener;

    public interface YesNoDialogFragmentListener {
        /**
         *
         * @param resultCode - Activity.RESULT_OK, Activity.RESULT_CANCEL
         * @param data
         */
        public void onYesNoResultDialog(int resultCode, @Nullable Intent data);
    }

    public YesNoDialogFragment() {

    }

    public void setOnYesNoDialogFragmentListener(YesNoDialogFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        String message = args.getString(ARG_MESSAGE);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buttonOkClick();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buttonNoClick();
                    }
                })
                .create();
    }

    private void buttonOkClick() {
        int resultCode = Activity.RESULT_OK;

        Bundle bundle = new Bundle();
        bundle.putString("key1", "Value 1");
        bundle.putString("key2", "Value 2");
        Intent data  = new Intent().putExtras(bundle);

        // Open this DialogFragment from an Activity.
        if(this.listener != null)  {
            this.listener.onYesNoResultDialog(Activity.RESULT_OK, data);
        }
        // Open this DialogFragment from another Fragment.
        else {
            // Send result to your TargetFragment.
            // See (Your) TargetFragment.onActivityResult()
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);
        }
    }

    private void buttonNoClick() {
        int resultCode = Activity.RESULT_CANCELED;
        Intent data  = null;

        // Open this DialogFragment from an Activity.
        if(this.listener != null)  {
            this.listener.onYesNoResultDialog(resultCode, data);
        }
        // Open this DialogFragment from another Fragment.
        else {
            // Send result to your TargetFragment.
            // See (Your) TargetFragment.onActivityResult()
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);
        }
    }

}