package mobile.tiis.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobile.tiis.app.R;

/**
 * Created by issymac on 27/01/16.
 */
public class VaccinateChildFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.vaccine_administer, null);
        setUpView(root);

        return root;
    }

    public void setUpView(View v){

    }

}
