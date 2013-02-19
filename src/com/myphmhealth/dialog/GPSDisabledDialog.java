package com.myphmhealth.dialog;

import com.myphmhealth.PHmHealthActivity;
import com.myphmhealth.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
//import android.support.v4.app.DialogFragment;


public class GPSDisabledDialog extends DialogFragment {

	public static GPSDisabledDialog newInstance(int title)
	{
		GPSDisabledDialog frag = new GPSDisabledDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.deviceaccesslocationoff)
                .setTitle(title)
                .setMessage(R.string.gps_dialog_message)
                .setPositiveButton(R.string.alert_dialog_yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((PHmHealthActivity)getActivity()).doPositiveClick();
                        }
                    }
                )
                .setNegativeButton(R.string.alert_dialog_no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((PHmHealthActivity)getActivity()).doNegativeClick();
                        }
                    }
                )
                .create();
	}
	
}
