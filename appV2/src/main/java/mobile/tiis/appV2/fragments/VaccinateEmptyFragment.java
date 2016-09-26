package mobile.tiis.appV2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mobile.tiis.appV2.R;

/**
 * Created by issymac on 27/01/16.
 */
public class VaccinateEmptyFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.vaccinate_placeholder, null);
        setUpView(root);

        return root;
    }

    public void setUpView(View v){

    }

}
