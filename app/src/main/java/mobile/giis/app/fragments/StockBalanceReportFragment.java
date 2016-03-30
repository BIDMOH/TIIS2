package mobile.giis.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import mobile.giis.app.R;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;

/**
 * Created by issymac on 31/03/16.
 */
public class StockBalanceReportFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;

    private TextView region, district, healthFacility, reportingPeriod, title;

    BackboneApplication app;

    private ProgressBar progressBar;

    private DatabaseHandler mydb;

    private TableLayout defaultersTable;

    private String hf_id, child_id, birthplacestr, villagestr, hfstr, statusstr, gender_val, birthdate_val;

    public static DefaultersReportFragment newInstance(int position) {
        DefaultersReportFragment f = new DefaultersReportFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        app = (BackboneApplication) this.getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rowview;
        rowview = inflater.inflate(R.layout.fragment_defaulters, null);
        prepareUIElements(rowview);
        mydb = app.getDatabaseInstance();
        healthFacility.setText(mydb.getHealthCenterName(app.getLOGGED_IN_USER_HF_ID()));

        return rowview;
    }

    public void prepareUIElements(View v){
        region          = (TextView) v.findViewById(R.id.region_title);
        district        = (TextView) v.findViewById(R.id.district_title);
        healthFacility  = (TextView) v.findViewById(R.id.hf_value);
        reportingPeriod = (TextView) v.findViewById(R.id.period_title);
        title           = (TextView) v.findViewById(R.id.the_title);
        progressBar     = (ProgressBar) v.findViewById(R.id.progres_bar);
        defaultersTable = (TableLayout) v.findViewById(R.id.defaulter_table);
    }
}
