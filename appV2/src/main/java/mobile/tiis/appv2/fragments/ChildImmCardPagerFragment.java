package mobile.tiis.appv2.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;

import mobile.tiis.appv2.CustomViews.NestedListView;
import mobile.tiis.appv2.R;
import mobile.tiis.appv2.adapters.ImmunizationCardAdapter;
import mobile.tiis.appv2.base.BackboneActivity;
import mobile.tiis.appv2.base.BackboneApplication;
import mobile.tiis.appv2.database.DatabaseHandler;
import mobile.tiis.appv2.entity.Child;
import mobile.tiis.appv2.entity.ImmunizationCardItem;
import mobile.tiis.appv2.CustomViews.NestedListView;
import mobile.tiis.appv2.util.BackgroundThread;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

/**
 * Created by issymac on 26/01/16.
 */
public class ChildImmCardPagerFragment extends RxFragment {
    private static final String TAG = ChildImmCardPagerFragment.class.getSimpleName();

    private BackboneApplication app;

    private static final String CHILD_OBJECT = "child";

    private DatabaseHandler mydb;

    private NestedListView immCardList;

    private Child currentChild;

    private String childId;

    private ImmunizationCardAdapter adapter;

    private ArrayList<ImmunizationCardItem> immunizationCardList;

    private TextView cardTitle, vacDoseTitle, vacLotTitle, healthFacTitle, vacDateTitle, doneCheckboxTitle, reasonTitle;

    RelativeLayout immListEmptyState;
    private Looper backgroundLooper;

    public static ChildImmCardPagerFragment newInstance(Child currentChild) {
        ChildImmCardPagerFragment f = new ChildImmCardPagerFragment();
        Bundle b                    = new Bundle();
        b                           .putSerializable(CHILD_OBJECT, currentChild);
        f                           .setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentChild     = (Child) getArguments().getSerializable(CHILD_OBJECT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v;
        v = (ViewGroup) inflater.inflate(R.layout.fragment_child_imm_card, null);

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();

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
        if (currentChild != null) {
            childId = currentChild.getId();
        }

        Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                // Do some long running operation
                immunizationCardList = mydb.getImmunizationCard(childId);
                return Observable.just(true);
            }
        })// Run on a background thread
                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread()).compose(this.<Boolean>bindToLifecycle())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        if (immunizationCardList.size() > 0){
                            immListEmptyState.setVisibility(View.GONE);
                            adapter = new ImmunizationCardAdapter(getActivity(), immunizationCardList);
                            immCardList.setAdapter(adapter);
                        }else{
                            immListEmptyState.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onNext(Boolean string) {
                        Log.d(TAG, "onNext(" + string + ")");
                    }
                });
    }

    private void initListeners(){

    }

}
