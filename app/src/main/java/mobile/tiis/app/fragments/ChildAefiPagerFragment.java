package mobile.tiis.app.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import mobile.tiis.app.CustomViews.NestedListView;
import mobile.tiis.app.R;
import mobile.tiis.app.adapters.AefiBottomListAdapter;
import mobile.tiis.app.adapters.AefiLastAppointementListAdapter;
import mobile.tiis.app.adapters.AefiTopListAdapter;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.entity.AefiListItem;
import mobile.tiis.app.entity.Child;
import mobile.tiis.app.util.BackgroundThread;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

/**
 * Created by issymac on 26/01/16.
 */
public class ChildAefiPagerFragment extends Fragment  implements DatePickerDialog.OnDateSetListener{

    private static final String TAG = ChildAefiPagerFragment.class.getSimpleName();
    private DatabaseHandler mydb;

    private String childId;

    private ArrayList<AefiListItem> aefiItems;

    private AefiListItem lastAppointementAefi;

    private ArrayList<AefiListItem> lastAppointementAefiList;

    private SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

    private AefiLastAppointementListAdapter aefiLastAppointementListAdapter;

    private Date aefiNewDate;

    private Thread thread;

    BackboneApplication app;

    NestedListView topListView, bottomListView;

    private Child currentChild;

    private static final String CHILD_OBJECT = "child";

    AefiBottomListAdapter bottomListAdapter;

    AefiTopListAdapter topListAdapter;

    RelativeLayout topListEmptyState, bottomListEmptyState;

    CheckBox chkHadAefi;

    Button btnAefiDate, save;

    MaterialEditText edtNotesAefi;
    private Looper backgroundLooper;

    public static final long getDaysDifference(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        long difference = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        return difference;
    }

    public static ChildAefiPagerFragment newInstance(Child currentChild) {
        ChildAefiPagerFragment f = new ChildAefiPagerFragment();
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
        v = (ViewGroup) inflater.inflate(R.layout.fragment_child_aefi, null);
        app = (BackboneApplication) ChildAefiPagerFragment.this.getActivity().getApplication();
        mydb = new DatabaseHandler(getActivity());

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();

        initViews(v);
        setupVariables();

        btnAefiDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDateDialogue();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAEFIData();
            }
        });

        return v;
    }

    public void selectDateDialogue(){
        Calendar now = Calendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone("GMT+0500"));
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ChildAefiPagerFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
//        final DatePicker dp = (DatePicker) d.findViewById(R.id.datePicker);
//        dp.setMaxDate(new Date().getTime());
//        final SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
        dpd.setAccentColor(Color.DKGRAY);
        dpd.setMaxDate(Calendar.getInstance());
        dpd.show(this.getActivity().getFragmentManager(), "DatePickerDialogue");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        final SimpleDateFormat ft = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        aefiNewDate = calendar.getTime();
        Date dNow = new Date();
        btnAefiDate.setText(ft.format(aefiNewDate));
//        lastAppointementAefi.setAefiDate(calendar.getTime());

    }


    public void saveAEFIData(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChildAefiPagerFragment.this.getActivity())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((AlertDialog) dialog).dismiss();
                    }
                });
        ContentValues contentValues = new ContentValues();
        if (lastAppointementAefi != null) {
            if (chkHadAefi.isChecked() != lastAppointementAefi.isAefi()) {
                lastAppointementAefi.setAefi(chkHadAefi.isChecked());
                if (chkHadAefi.isChecked())
                    contentValues.put(SQLHandler.VaccinationAppointmentColumns.AEFI, "true");
                else
                    contentValues.put(SQLHandler.VaccinationAppointmentColumns.AEFI, "false");
            }
            if (lastAppointementAefi.getAefiDate() != null) {
                if (aefiNewDate != null && aefiNewDate.compareTo(lastAppointementAefi.getAefiDate()) != 0) {
                    contentValues.put(SQLHandler.VaccinationAppointmentColumns.AEFI_DATE, BackboneActivity.stringToDateParser(aefiNewDate));
                    lastAppointementAefi.setAefiDate(aefiNewDate);
                }
            } else {
                contentValues.put(SQLHandler.VaccinationAppointmentColumns.AEFI_DATE, BackboneActivity.stringToDateParser(new Date()));
                lastAppointementAefi.setAefiDate(new Date());
            }
            if (edtNotesAefi.getText() != null && !lastAppointementAefi.getNotes().equals(edtNotesAefi.getText().toString())) {
                lastAppointementAefi.setNotes(edtNotesAefi.getText().toString());
                contentValues.put(SQLHandler.VaccinationAppointmentColumns.NOTES, edtNotesAefi.getText().toString());
            }


            if (contentValues.size() > 0) {
                lastAppointementAefi.setModifiedById(app.getLOGGED_IN_USER_ID());
                lastAppointementAefi.setModifiedOn(new Date());
                if (mydb.updateVaccinationAppointementById(contentValues, lastAppointementAefi.getAppointementId()) > 0) {
                    alertDialogBuilder.setMessage(R.string.child_change_data_saved_success);
                    thread = new Thread() {
                        @Override
                        public void run() {
                            String url = prepareUrl().toString();
                            if (!app.updateAefiAppointement(prepareUrl())) {
                                mydb.addPost(url, -1);
                                Log.d("Save Edited Child", "Error while saving edited aefi " + lastAppointementAefi.getAppointementId());
                            } else {
                            }
                        }
                    };
                    thread.start();
                }
                alertDialogBuilder.show();
                reloadAefiLists();
            }
        } else {
            alertDialogBuilder.setMessage(R.string.child_change_data_saved_error);
            alertDialogBuilder.show();
        }
    }

    private void initViews(View v) {
        topListView     = (NestedListView) v.findViewById(R.id.aefi_list_one);
        setListViewHeightBasedOnChildren(topListView);
        bottomListView  = (NestedListView) v.findViewById(R.id.aefi_list_two);
        setListViewHeightBasedOnChildren(bottomListView);

        topListEmptyState   = (RelativeLayout) v.findViewById(R.id.top_list_empty_state);
        topListEmptyState.setVisibility(View.GONE);
        bottomListEmptyState   = (RelativeLayout) v.findViewById(R.id.bottom_list_empty_state);
        bottomListEmptyState.setVisibility(View.GONE);

        edtNotesAefi    = (MaterialEditText) v.findViewById(R.id.met_notes_value);
        chkHadAefi      = (CheckBox) v.findViewById(R.id.aefi_checkbox);
        btnAefiDate     = (Button) v.findViewById(R.id.date_btn);
        save            = (Button) v.findViewById(R.id.save_button);

    }

    private void setupVariables(){
        Observable.defer(new Func0<Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call() {
                // Do some long running operation
                if (currentChild != null) {
                    childId = currentChild.getId();
                } else {
                    new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChildAefiPagerFragment.this.getActivity(), "Child not found on AEFI", Toast.LENGTH_LONG).show();
                        }
                    };
                }
                aefiItems = mydb.getAefiVaccinationAppointement(childId);
                lastAppointementAefiList = mydb.getAefiLastVaccinationAppointement(childId);
                return Observable.just(true);
            }
        })// Run on a background thread
                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        if (aefiItems != null && aefiItems.size() > 0) {
                            bottomListEmptyState.setVisibility(View.GONE);
                            bottomListAdapter = new AefiBottomListAdapter(getActivity(), aefiItems);
                            bottomListView.setAdapter(bottomListAdapter);
                        }else{
                            bottomListEmptyState.setVisibility(View.VISIBLE);
                        }

                        if (lastAppointementAefiList != null && lastAppointementAefiList.size() > 0) {
                            topListEmptyState.setVisibility(View.GONE);
                            lastAppointementAefi = lastAppointementAefiList.get(0);

                            if (lastAppointementAefi != null) {

                                topListAdapter = new AefiTopListAdapter(ChildAefiPagerFragment.this.getActivity(), lastAppointementAefiList);
                                topListView.setAdapter(topListAdapter);

                                chkHadAefi.setChecked(true);
                                if (lastAppointementAefi.getAefiDate() != null)
                                    btnAefiDate.setText(format.format(lastAppointementAefi.getAefiDate()));
                                else
                                    btnAefiDate.setText(format.format(new Date()));
                                edtNotesAefi.setText(lastAppointementAefi.getNotes());

                            }
                        }else{
                            topListEmptyState.setVisibility(View.VISIBLE);
                        }
                        aefiNewDate = new Date();
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

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void reloadAefiLists()  {
        aefiItems.clear();
        ArrayList<AefiListItem> aefiListtemp = mydb.getAefiVaccinationAppointement(childId);
        if (aefiListtemp != null && aefiListtemp.size() > 0)
            aefiItems.addAll(aefiListtemp);
        bottomListAdapter.notifyDataSetChanged();

        lastAppointementAefiList.clear();
        ArrayList<AefiListItem> lastAppointementAefiListtemp = mydb.getAefiLastVaccinationAppointement(childId);
        if (lastAppointementAefiListtemp != null && lastAppointementAefiListtemp.size() > 0) {
            lastAppointementAefiList.addAll(lastAppointementAefiListtemp);
            lastAppointementAefi = lastAppointementAefiList.get(0);
        }
        topListAdapter.notifyDataSetChanged();
    }

    private StringBuilder prepareUrl() {
        final StringBuilder webServiceUrl = new StringBuilder(BackboneApplication.WCF_URL)
                .append(BackboneApplication.VACCINATION_APPOINTMENT_MANAGMENT_SVC).append(BackboneApplication.REGISTER_CHILD_AEFI);
        webServiceUrl.append("appId=" + lastAppointementAefi.getAppointementId());
        if (lastAppointementAefi.isAefi())
            webServiceUrl.append("&aefi=" + "true");
        else
            webServiceUrl.append("&aefi=" + "true");
        webServiceUrl.append("&notes=" + lastAppointementAefi.getNotes());
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");
        if (lastAppointementAefi.getAefiDate() != null)
            try {
                webServiceUrl.append("&date=" + URLEncoder.encode(formatted.format(lastAppointementAefi.getAefiDate()), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        try {
            webServiceUrl.append("&modifiedon=" + URLEncoder.encode(formatted.format(lastAppointementAefi.getModifiedOn()), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webServiceUrl.append("&modifiedby=" + lastAppointementAefi.getModifiedById());
        return webServiceUrl;
    }

}
