package com.akoudri.healthrecord.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.akoudri.healthrecord.app.R;

/**
 * Created by Ali Koudri on 31/07/14.
 */
public class SmsMessageDialog extends DialogFragment {

    private TextView tv;
    private Button b;
    private String phoneNumber;

    public static SmsMessageDialog newInstance(String phoneNumber)
    {
        SmsMessageDialog d = new SmsMessageDialog();
        Bundle args = new Bundle();
        args.putString("phoneNumber", phoneNumber);
        d.setArguments(args);
        return d;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneNumber = getArguments().getString("phoneNumber");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.dialog_sms_message, container, false);
        tv = (TextView) v.findViewById(R.id.sms_message);
        b = (Button) v.findViewById(R.id.send_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = tv.getText().toString();
                //Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"+phoneNumber));
                //intent.putExtra("sms_body", message);
                //startActivity(intent);
                SmsManager manager = SmsManager.getDefault();
                manager.sendTextMessage(phoneNumber, null, message, null, null);
                dismiss();
            }
        });
        return v;
    }
}
