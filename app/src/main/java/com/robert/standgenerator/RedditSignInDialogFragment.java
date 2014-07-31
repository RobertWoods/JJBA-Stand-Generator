package com.robert.standgenerator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.robert.myapplication.R;

/**
 * Created by Robert on 7/30/2014.
 */
public class RedditSignInDialogFragment extends DialogFragment {

    OnDialogResult dialogResult;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setView()
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_signin,null);
        final EditText user = (EditText) view.findViewById(R.id.username);
        final EditText pass = (EditText) view.findViewById(R.id.password);
        final MyActivity activity = (MyActivity) getActivity();

        builder.setView(view)
                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.password = pass.getText().toString();
                        activity.user = user.getText().toString();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RedditSignInDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void setDialogResult(OnDialogResult dialogResult){
        dialogResult = dialogResult;
    }

    public interface OnDialogResult{
        void finish(String result);
    }


}
