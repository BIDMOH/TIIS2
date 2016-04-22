package mobile.tiis.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobile.tiis.app.R;

/**
 * Created by issymac on 16/12/15.
 */
public class SettingsFragment extends android.support.v4.app.Fragment{

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_settings, null);
        setUpView(root);

        return root;
    }

    public void setUpView(View v){

    }

}