package mobile.giis.app.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mobile.giis.app.CustomViews.NestedListView;
import mobile.giis.app.R;
import mobile.giis.app.adapters.ImmunizationCardAdapter;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.base.BackboneApplication;
import mobile.giis.app.database.DatabaseHandler;
import mobile.giis.app.database.SQLHandler;
import mobile.giis.app.entity.ImmunizationCardItem;

/**
 * Created by issymac on 26/01/16.
 */
public class ChildImmCardPagerFragment extends Fragment {

    private BackboneApplication app;

    private DatabaseHandler mydb;

    private NestedListView immCardList;

    private static final String VALUE = "barcode";

    private String barcode = "";

    private String childId;

    private ImmunizationCardAdapter adapter;

    private ArrayList<ImmunizationCardItem> immunizationCardList;

    private TextView cardTitle, vacDoseTitle, vacLotTitle, healthFacTitle, vacDateTitle, doneCheckboxTitle, reasonTitle;

    RelativeLayout immListEmptyState;

    public static ChildImmCardPagerFragment newInstance(String handlerBarcode) {
        ChildImmCardPagerFragment f = new ChildImmCardPagerFragment();
        Bundle b                    = new Bundle();
        b                           .putString(VALUE, handlerBarcode);
        f                           .setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcode = getArguments().getString(VALUE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v;
        v = (ViewGroup) inflater.inflate(R.layout.fragment_child_imm_card, null);

        app = (BackboneApplication) this.getActivity().getApplication();
        initViews(v);

        initDb();
        renderViews();
        initListeners();

        return v;
    }

    private void initViews(View v){
        immCardList         = (NestedListView) v.findViewById(R.id.imm_list);
        cardTitle           = (TextView) v.findViewById(R.id.card_title);
        cardTitle           .setTypeface(BackboneActivity.Rosario_Regular);
        vacDoseTitle        = (TextView) v.findViewById(R.id.imm_card_vacc_dose_title);
        vacDoseTitle        .setTypeface(BackboneActivity.Rosario_Regular);
        vacLotTitle         = (TextView) v.findViewById(R.id.imm_card_vacc_lot_title);
        vacLotTitle         .setTypeface(BackboneActivity.Rosario_Regular);
        healthFacTitle      = (TextView) v.findViewById(R.id.imm_health_fac_title);
        healthFacTitle      .setTypeface(BackboneActivity.Rosario_Regular);
        vacDateTitle        = (TextView) v.findViewById(R.id.imm_card_vacc_date_title);
        vacDateTitle        .setTypeface(BackboneActivity.Rosario_Regular);
        doneCheckboxTitle   = (TextView) v.findViewById(R.id.imm_card_done_chk_title);
        doneCheckboxTitle   .setTypeface(BackboneActivity.Rosario_Regular);
        reasonTitle         = (TextView) v.findViewById(R.id.imm_card_reason_title);
        reasonTitle         .setTypeface(BackboneActivity.Rosario_Regular);

        immListEmptyState   = (RelativeLayout) v.findViewById(R.id.imm_list_empty_state);
        immListEmptyState.setVisibility(View.GONE);

    }

    private void initDb(){
        mydb = app.getDatabaseInstance();
    }

    private void renderViews(){
        if(barcode != null){
            Cursor getChildIdCursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE " + SQLHandler.ChildColumns.BARCODE_ID + "=?",
                    new String[]{String.valueOf(barcode)});
            if (getChildIdCursor != null && getChildIdCursor.getCount() > 0) {
                getChildIdCursor.moveToFirst();
                childId = getChildIdCursor.getString(getChildIdCursor.getColumnIndex(SQLHandler.ChildColumns.ID));
            }

            immunizationCardList = mydb.getImmunizationCard(childId);
            if (immunizationCardList.size() > 0){
                immListEmptyState.setVisibility(View.GONE);
                adapter = new ImmunizationCardAdapter(this.getActivity(), immunizationCardList);
                immCardList.setAdapter(adapter);
            }else{
                immListEmptyState.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initListeners(){

    }

}
