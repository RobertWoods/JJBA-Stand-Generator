package com.robert.standgenerator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.robert.myapplication.R;

/**
 * Created by Robert on 7/30/2014.
 */
public class RedditSignInDialogFragment extends DialogFragment {

    OnDialogResult dialogResult;
    CheckBox saveLoginCheckBox;
    SharedPreferences loginPreferences;
    SharedPreferences.Editor loginPrefsEditor;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        View view = inflater.inflate(R.layout.dialog_signin,null);
        final EditText user = (EditText) view.findViewById(R.id.username);

        final EditText pass = (EditText) view.findViewById(R.id.password);
        final genActivity activity = (genActivity) getActivity();
        final SecurePreferences prefs = new SecurePreferences(getActivity(),"stand-preferences","keygendonkey",true);
        saveLoginCheckBox = (CheckBox) view.findViewById(R.id.saveLoginCheckbox);
        loginPreferences = activity.getSharedPreferences("stand-preferences",activity.MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        boolean saveLogin = loginPreferences.getBoolean("saveLogin",true);
        if(saveLogin==true){
            user.setText(prefs.getString("username"));
            pass.setText(prefs.getString("password"));
            saveLoginCheckBox.setChecked(true);
        }


        builder.setView(view)
                .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.password = pass.getText().toString();
                        activity.user = user.getText().toString();

                        if (saveLoginCheckBox.isChecked()) {
                            loginPrefsEditor.putBoolean("saveLogin", true);
                            prefs.put("username", user.getText().toString());
                            prefs.put("password", pass.getText().toString());
                            loginPrefsEditor.commit();
                        } else {
                            prefs.clear();
                            loginPrefsEditor.clear();
                            loginPrefsEditor.commit();
                        }
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
