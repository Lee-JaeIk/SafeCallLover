package com.example.sangwook.jsonproject.tutorial;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sangwook.jsonproject.R;

public class LayoutTwo extends Fragment {

    public static Fragment newInstance(Context context) {
        LayoutTwo f = new LayoutTwo();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.layout_two, null);
        TextView textView = (TextView) root.findViewById(R.id.tv_two);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "addi.ttf");
        textView.setTypeface(typeface);
        return root;
    }

}