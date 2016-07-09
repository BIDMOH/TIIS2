package mobile.tiis.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import mobile.tiis.app.R;

/**
 * Created by issy on 7/7/16.
 */
public class ChildRegisterReportFragment extends android.support.v4.app.Fragment{

    //Table Layout to be used to loop the list of the children information in
    private TableLayout childRegisterTable;

    public static ChildRegisterReportFragment newInstance(int position) {
        ChildRegisterReportFragment f = new ChildRegisterReportFragment();
        Bundle b = new Bundle();
//        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_child_register, null);
        setUpView(root);

        return root;
    }

    public void setUpView(View v){
        childRegisterTable      = (TableLayout) v.findViewById(R.id.child_register_table);
    }


}
