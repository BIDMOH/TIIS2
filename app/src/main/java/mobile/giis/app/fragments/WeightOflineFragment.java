package mobile.giis.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mobile.giis.app.R;

/**
 * Created by issymac on 14/03/16.
 */
public class WeightOflineFragment extends Fragment {

    String barcode;

    private TextView text;

    public static WeightOflineFragment newInstance(String barcode) {
        WeightOflineFragment f = new WeightOflineFragment();
        Bundle b = new Bundle();
        b.putString("barcode", barcode);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcode = getArguments().getString("barcode");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.weight_offline_fragment, null);
        setupviews(rootview);

        return rootview;
    }

    public void setupviews(View v){
    }

}
