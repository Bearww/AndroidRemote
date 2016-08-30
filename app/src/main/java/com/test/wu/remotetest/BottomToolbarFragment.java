package com.test.wu.remotetest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class BottomToolbarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_toolbar_fragment, container, false);
/*
        final ImageButton cutButton = (ImageButton) view.findViewById(R.id.cutButton);
        final ImageButton copyButton = (ImageButton) view.findViewById(R.id.copyButton);
        final ImageButton selectButton = (ImageButton) view.findViewById(R.id.selectButton);
        final ImageButton pasteButton = (ImageButton) view.findViewById(R.id.pasteButton);
        final ImageButton shareButton = (ImageButton) view.findViewById(R.id.shareButton);
        final ImageButton nullButton = (ImageButton) view.findViewById(R.id.nullButton);
        final ImageButton moreButton = (ImageButton) view.findViewById(R.id.moreButton);

        cutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        pasteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        nullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
*/
        return view;
    }
}
