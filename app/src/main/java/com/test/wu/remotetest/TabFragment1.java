package com.test.wu.remotetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TabFragment1 extends Fragment {

    TextView textView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        final Button connectButton = (Button) view.findViewById(R.id.ConnectButton);

        textView = (TextView) view.findViewById(R.id.textView);
        Log.d("TF1", connectButton.getText().toString());
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RemoteActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
