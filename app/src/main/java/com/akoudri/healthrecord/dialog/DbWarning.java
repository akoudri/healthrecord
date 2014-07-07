package com.akoudri.healthrecord.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.akoudri.healthrecord.app.R;

/**
 * Created by Ali Koudri on 25/04/14.
 */
public class DbWarning extends DialogFragment {

    private Button okBtn;

    public static DbWarning newInstance()
    {
        return new DbWarning();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.warning);
        View dialog = inflater.inflate(R.layout.fragment_db_warning, container, false);
        okBtn = (Button) dialog.findViewById(R.id.db_warning_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return dialog;
    }
}
