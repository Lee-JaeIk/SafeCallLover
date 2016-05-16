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

public class LayoutThree extends Fragment {

    public static Fragment newInstance(Context context) {
        LayoutThree f = new LayoutThree();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.layout_three, null);
        TextView textView = (TextView) root.findViewById(R.id.tv_three);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "addi.ttf");
        textView.setTypeface(typeface);
        return root;
    }

}