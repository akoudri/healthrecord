package com.akoudri.healthrecord.app;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Ali Koudri on 25/04/14.
 */
public class AddSizeFragment extends DialogFragment {

    private Button addBtn, cancelBtn;

    public static AddSizeFragment newInstance()
    {
        return new AddSizeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //FIXME: use internationalization
        getDialog().setTitle("Set Size");
        View dialog = inflater.inflate(R.layout.fragment_add_size, container, false);
        addBtn = (Button) dialog.findViewById(R.id.add_size_btn);
        cancelBtn = (Button) dialog.findViewById(R.id.cancel_size_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                dismiss();
            }
        });
        return dialog;
    }
}
