package mobile.tiis.app.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;

import fr.ganfra.materialspinner.MaterialSpinner;
import mobile.tiis.app.ChildDetailsActivity;
import mobile.tiis.app.R;
import mobile.tiis.app.adapters.AdapterGridDataSearchResult;
import mobile.tiis.app.adapters.SingleTextViewAdapter;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.entity.Birthplace;
import mobile.tiis.app.entity.Child;
import mobile.tiis.app.entity.HealthFacility;
import mobile.tiis.app.entity.Place;
import mobile.tiis.app.entity.Status;

/**
 *  Created by issymac on 12/12/15.
 */

public class SearchChildFragment extends android.support.v4.app.Fragment implements DatePickerDialog.OnDateSetListener{

    public ListView lvChildrenSearchResults;

    public MaterialEditText metFName, metMName, metSurname, metMotFname, metMotSname, metBarcode, metDOBFrom, metDOBTo;

    public MaterialSpinner placeSpiner, healthFacilitySpinner, villageSpinner, statusSpinner;

    public RelativeLayout childListLayout;

    public RelativeLayout childNotFoundLayout;

    String barcode, firstname, firstname2, surname, motherfirstname, mothersurname, dobfrom, dobto, placeodbirthId, healthfacility,villagename, status;

    Date dateFrom, dateTo;

    String currentText;
//    public SearchResultListAdapter adapter;

    List<Child> children;
    List<Place> placeList;
    List<Birthplace> birthplaceList;
    List<HealthFacility> healthFacilityList;
    List<Status> statusList;

    public DatabaseHandler mydb;
    private Timer timer = new Timer();
    private final long DELAY = 1000; // in ms
    AdapterGridDataSearchResult adapter;
    boolean dobFromIsActive = false;
    boolean isBegginingDateSelected = false;
    boolean isEndingDateSelected = false;

    View listviewFooter;
    ImageView previous, next;
    RelativeLayout prevLayout, nextLayout;
    public static String currentCount = "0";
    public boolean autorefreshTriggered = false;
    ProgressBar pbar;

    CardView searchOutsideFacility;

    AlertDialog.Builder alertDialogBuilder;

    ImageButton searchBtn;

    String childidToParse;
    ArrayList<Child> childrensrv = new ArrayList<>();
    boolean serverdata = false;

    public int currentChildPosition = -1;
    Button previousBtn, nextBtn, searchOutsideFacilityButton;

    CardView previousCard, forwardCard;

    public View onCreateView(LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_search_child, null);
        setUpView(root);

        previous        = (ImageView) root.findViewById(R.id.previous_10_contents);
        next            = (ImageView) root.findViewById(R.id.next_10_contents);
        prevLayout      = (RelativeLayout) root.findViewById(R.id.prev_rl);
        nextLayout      = (RelativeLayout) root.findViewById(R.id.next_rl);
        previousBtn     = (Button) root.findViewById(R.id.previous_btn);
        nextBtn         = (Button) root.findViewById(R.id.next_btn);
        previousCard    = (CardView) root.findViewById(R.id.previous_card);
        forwardCard     = (CardView) root.findViewById(R.id.forward_card);

//        nextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int count = Integer.parseInt(currentCount);
//                count = count+10;
//                currentCount = count+"";
//                new getChildren().execute(currentCount);
//            }
//        });

        nextLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(currentCount);
                count = count+10;
                currentCount = count+"";
                new getChildren().execute(currentCount);
            }
        });

//        previousBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int count = Integer.parseInt(currentCount);
//                count = count - 10;
//                currentCount = count + "";
//                new getChildren().execute(currentCount);
//            }
//        });

        prevLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(currentCount);
                count = count - 10;
                currentCount = count + "";
                //new filterList().execute(currentCategory, currentCount);
                new getChildren().execute(currentCount);
            }
        });

        searchOutsideFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchingOutsideFacility();
            }
        });

        BackboneApplication app = (BackboneApplication) SearchChildFragment.this.getActivity().getApplication();
        mydb = app.getDatabaseInstance();

        placeList = mydb.getAllPlaces();
        List<String> place_names = new ArrayList<String>();
        for (Place element : placeList) {
            place_names.add(element.getName());
        }

        birthplaceList = mydb.getAllBirthplaces();
        List<String> birthplaceNames = new ArrayList<String>();
        for (Birthplace element : birthplaceList) {
            birthplaceNames.add(element.getName());
        }

        SingleTextViewAdapter spBirthOfPlace = new SingleTextViewAdapter(SearchChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, birthplaceNames);
        placeSpiner.setAdapter(spBirthOfPlace);
        placeSpiner.setSelection(0);

        SingleTextViewAdapter dataAdapter = new SingleTextViewAdapter(SearchChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, place_names);
        villageSpinner.setAdapter(dataAdapter);
        villageSpinner.setSelection(0);

        healthFacilityList = mydb.getAllHealthFacility();
        List<String> facility_name = new ArrayList<String>();
        for (HealthFacility element : healthFacilityList) {
            facility_name.add(element.getName());
        }

        SingleTextViewAdapter healthAdapter = new SingleTextViewAdapter(SearchChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, facility_name);
        healthFacilitySpinner.setAdapter(healthAdapter);
        healthFacilitySpinner.setSelection(0);

        statusList = mydb.getStatus();
        List<String> status_name = new ArrayList<String>();

        if (statusList.size() == 0) {
            statusSpinner.setEnabled(false);
        } else {
            for (Status element : statusList) {
                status_name.add(element.getName());
            }
        }

        SingleTextViewAdapter statusAdapter = new SingleTextViewAdapter(SearchChildFragment.this.getActivity(), R.layout.single_text_spinner_item_drop_down, status_name);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setSelection(3);

        new getChildren().execute("0");

        lvChildrenSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentChildPosition = i;
                if (serverdata) {
                    if (!(i == -1)){
                        childidToParse = childrensrv.get(i).getId();
                        ChildSynchronization task = new ChildSynchronization();
                        task.execute(childidToParse);
                    }
                }else{
                    if (!(i == -1)) {
                        Intent childDetailsActivity = new Intent(SearchChildFragment.this.getActivity(), ChildDetailsActivity.class);
                        childDetailsActivity.putExtra("barcode", adapter.getBarcode(i));
                        childDetailsActivity.putExtra(BackboneApplication.CHILD_ID, adapter.getChildid(i));
                        Log.e("coze", "child id is 1  : " + adapter.getChildId(i));
                        Log.e("coze", "OR child id is 2  : " + adapter.getChildid(i));
                        Log.e("coze", "BARCODE IS  : " + adapter.getBarcode(i));

                        startActivity(childDetailsActivity);
                    }
                }

            }

        });

        metFName.setOnFocusChangeListener(new GenericTextWatcher(metFName));
        metBarcode.setOnFocusChangeListener(new GenericTextWatcher(metBarcode));
        metMName.setOnFocusChangeListener(new GenericTextWatcher(metMName));
        metSurname.setOnFocusChangeListener(new GenericTextWatcher(metSurname));
        metMotFname.setOnFocusChangeListener(new GenericTextWatcher(metMotFname));
        metMotSname.setOnFocusChangeListener(new GenericTextWatcher(metMotSname));

        metDOBFrom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    dobFromIsActive = true;
                    pickDate();
                }else {
                    if(!isEndingDateSelected){
                        Toast.makeText(SearchChildFragment.this.getActivity(),
                                "Select Ending Date to be able to filter children",
                                Toast.LENGTH_LONG).show();
                    }else{
                        new getChildren().execute(currentCount);
                    }
                }
            }
        });

        metDOBTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    dobFromIsActive = false;
                    pickDate();
                }
                else{
                    if(!isBegginingDateSelected){
                        Toast.makeText(SearchChildFragment.this.getActivity(),
                                "Select From Date to be able to filter children",
                                Toast.LENGTH_LONG).show();
                    }else{
                        new getChildren().execute(currentCount);
                    }
                }
            }
        });

        return root;
    }

    public void updateList(){
        try {
            autorefreshTriggered = true;
            new getChildren().execute(currentCount);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pickDate(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                SearchChildFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setAccentColor(Color.DKGRAY);
        dpd.show(this.getActivity().getFragmentManager(), "DatePickerDialogue");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = new GregorianCalendar();
        cal.set(year, (monthOfYear + 1), dayOfMonth);
        cal.add(cal.YEAR, 1);
        cal.add(cal.DAY_OF_MONTH, -1);

        String displayDate = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        if(dobFromIsActive){
            metDOBFrom.setText(displayDate);
            dobFromIsActive = false;
            isBegginingDateSelected = true;
        }else{
            metDOBTo.setText(displayDate);
            isEndingDateSelected = true;
        }


    }

    public void setUpView(View v){

        placeSpiner             = (MaterialSpinner) v.findViewById(R.id.spin_search_place_id);
        healthFacilitySpinner   = (MaterialSpinner) v.findViewById(R.id.spin_search_health_id);
        villageSpinner          = (MaterialSpinner) v.findViewById(R.id.spin_search_village_id);
        statusSpinner           = (MaterialSpinner) v.findViewById(R.id.spin_search_status_id);

        metFName                = (MaterialEditText) v.findViewById(R.id.met_fname);
        metBarcode              = (MaterialEditText) v.findViewById(R.id.met_barcode);
        metMName                = (MaterialEditText) v.findViewById(R.id.met_mname);
        metSurname              = (MaterialEditText) v.findViewById(R.id.met_sname);
        metMotFname             = (MaterialEditText) v.findViewById(R.id.met_mother_fname);
        metMotSname             = (MaterialEditText) v.findViewById(R.id.met_mother_sname);
        metDOBFrom              = (MaterialEditText) v.findViewById(R.id.met_dop_from);
        metDOBTo                = (MaterialEditText) v.findViewById(R.id.met_dop_to);

        lvChildrenSearchResults = (ListView) v.findViewById(R.id.lv_children_list);

        childListLayout         = (RelativeLayout) v.findViewById(R.id.children_list_layout);
        childListLayout.setVisibility(View.VISIBLE);
        childNotFoundLayout     = (RelativeLayout) v.findViewById(R.id.children_not_found_layout);
        childNotFoundLayout.setVisibility(View.GONE);

        searchOutsideFacility   = (CardView) v.findViewById(R.id.search_outside_facility);

        pbar                    = (ProgressBar) v.findViewById(R.id.pbar);
        pbar.setVisibility(View.GONE);

        searchBtn = (ImageButton) v.findViewById(R.id.search_btn_child);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new getChildren().execute("0");
            }
        });

        searchOutsideFacilityButton = (Button) v.findViewById(R.id.outside_facility_search_btn);

    }

    public void emptyAllFields(){
        metFName.setText("");
        metBarcode.setText("");
        metMName.setText("");
        metSurname .setText("");
        metMotFname .setText("");
        metMotSname .setText("");
        metDOBFrom .setText("");
        metDOBTo .setText("");
        searchOutsideFacility.setVisibility(View.VISIBLE);

    }

    public void startSearchingOutsideFacility(){

        //Create dialogue to prompt syncronization
        LayoutInflater li = LayoutInflater.from(SearchChildFragment.this.getActivity());
        View promptsView = li.inflate(R.layout.custom_alert_dialogue, null);
        alertDialogBuilder = new AlertDialog.Builder(SearchChildFragment.this.getActivity());
        alertDialogBuilder.setView(promptsView);
        TextView message = (TextView) promptsView.findViewById(R.id.dialogMessage);
        message.setText("Searching Outside Facility.... Please Wait!");
        alertDialogBuilder.setCancelable(false);

        searchChildrenOffline();

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

    private class getChildren extends AsyncTask<String, Void, Integer> {

        String num = "0";

        @Override
        protected void onPreExecute() {
            pbar.setVisibility(View.VISIBLE);
            barcode     = metBarcode.getText().toString();
            firstname   = metFName.getText().toString();
            firstname2  = metMName.getText().toString();
            surname     = metSurname.getText().toString();
            motherfirstname = metMotFname.getText().toString();
            mothersurname   = metMotSname.getText().toString();

            //get the date from and date to and format them
            SimpleDateFormat fmt = new SimpleDateFormat("d/M/yyyy");
            try {
                dateFrom = fmt.parse(metDOBFrom.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                dateTo = fmt.parse(metDOBTo.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                if (placeSpiner.getSelectedItemPosition() != 0) {
                    placeodbirthId = birthplaceList.get(placeSpiner.getSelectedItemPosition() - 1).getId();
                    Log.d("Selected from spinner", placeodbirthId);
                }else{
                    placeodbirthId = "";
                }
            } catch (Exception e) {
            }

            try {
                if (villageSpinner.getSelectedItemPosition() != 0) {
                    villagename = placeList.get(villageSpinner.getSelectedItemPosition() - 1).getId();
                    Log.d("Selected from spinner", villagename);
                }else{
                    villagename = "";
                }
            } catch (Exception e) {
            }

            try {
                if (healthFacilitySpinner.getSelectedItemPosition() != 0) {
                    healthfacility = healthFacilityList.get(healthFacilitySpinner.getSelectedItemPosition() - 1).getId();
                    Log.d("Selected from spinner", healthfacility);
                }else{
                    healthfacility = "";
                }
            } catch (Exception e) {
            }

            try {
                status="";
            } catch (Exception e) {
            }

            if (currentCount.equals("0")){
                previousCard.setVisibility(View.GONE);
            }else{
                previousCard.setVisibility(View.VISIBLE);
            }


        }

        @Override
        protected Integer doInBackground(String... params) {
            int responce = 0;
            num = params[0];
            children = mydb.searchChild(barcode,
                    firstname,firstname2, motherfirstname, ((dateFrom != null) ? (dateFrom.getTime() / 1000) + "" : ""), ((dateTo != null) ? (dateTo.getTime() / 1000) + "" : ""),"", surname, mothersurname,
                    placeodbirthId, healthfacility, villagename, status, params[0]);

            return responce;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if((children == null)){
                pbar.setVisibility(View.GONE);
                lvChildrenSearchResults.setAdapter(null);
                previousCard.setVisibility(View.GONE);
                forwardCard.setVisibility(View.GONE);
            }else{
                serverdata = false;
                childListLayout.setVisibility(View.VISIBLE);
                if (autorefreshTriggered){
                    adapter.updateReceiptsList(children);
                    adapter.notifyDataSetChanged();
                }
                else {
                    adapter = new AdapterGridDataSearchResult(SearchChildFragment.this.getActivity(), children, mydb, num);
                    lvChildrenSearchResults.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                pbar.setVisibility(View.GONE);
                if (children.size() < 10){
                    forwardCard.setVisibility(View.GONE);
                }else{
                    forwardCard.setVisibility(View.VISIBLE);
                }
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {}

    }

    public void searchChildrenOffline(){

        final AlertDialog alertDialog;

        SimpleDateFormat fmt = new SimpleDateFormat("d/M/yyyy");

        dateFrom = null;
        dateTo =  null;

        try {
            dateFrom = fmt.parse(metDOBFrom.getText().toString());
            dateTo = fmt.parse(metDOBTo.getText().toString());
            Log.d("dtfrm", "NO EXEPTIONS");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd");
        String dateFromForSRV = "";
        String dateToForSRV = "";

        try {
            dateFromForSRV = formatted.format(dateFrom);
            Log.d("dtfrm", "date from for server is : "+dateFromForSRV);
        } catch (Exception e) {
            Log.d("dtfrm",e.toString());
            e.printStackTrace();
        }
        try {
            dateToForSRV = formatted.format(dateTo);
            Log.d("dtfrm", "date to for server is : "+dateToForSRV);
        } catch (Exception e) {
            Log.d("dtfrm",e.toString());
            e.printStackTrace();
        }

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        new Thread (){

            String threadDateFrom
                    ,
                    threadDateTo;

        public Thread setData(String threadDateFrom, String threadDateTo) {
            try {
                this.threadDateFrom = URLEncoder.encode(threadDateFrom, "utf-8");
                this.threadDateTo = URLEncoder.encode(threadDateTo, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return this;
        }

        @Override
        public void run() {


            synchronized (this) {
                int emptyInputDetected = 0;
                String childBarcode = null;
                if (!(metBarcode.getText().toString().equals("") || metBarcode.getText().toString().isEmpty())){
                    childBarcode = metBarcode.getText().toString();
                }else{
                    emptyInputDetected++;
                }

                String childFName  = null;
                if (!(metFName.getText().toString().equals("") || metFName.getText().toString().isEmpty())){
                    childFName = metFName.getText().toString();
                }else{
                    emptyInputDetected++;
                }

                String ChildMName = null;
                if (!(metMName.getText().toString().equals("") || metMName.getText().toString().isEmpty())){
                    ChildMName = metMName.getText().toString();
                }else{
                    emptyInputDetected++;
                }

                String motherFname = null;
                if (!(metMotFname.getText().toString().equals("") || metMotFname.getText().toString().isEmpty())) {
                    motherFname = metMotFname.getText().toString();
                }else{
                    emptyInputDetected++;
                }

                String surname = null;
                if (!(metSurname.getText().toString().equals("") || metSurname.getText().toString().isEmpty())) {
                    surname = metSurname.getText().toString();
                }else{
                    emptyInputDetected++;
                }

                String motherSName = null;
                if (!(metMotSname.getText().toString().equals("") || metMotSname.getText().toString().isEmpty())) {
                    motherSName = metMotSname.getText().toString();
                }else{
                    emptyInputDetected++;
                }

                String placeOBId = null;
                String healthFacility = null;
                String villageName = null;
                String status = null;

               if (emptyInputDetected == 6){
                   SearchChildFragment.this.getActivity().runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           Toast.makeText(SearchChildFragment.this.getActivity(), "Please Enter Search Credentials", Toast.LENGTH_LONG).show();
                           alertDialog.dismiss();
                       }
                   });
               }else {
                   BackboneApplication backbone = (BackboneApplication) SearchChildFragment.this.getActivity().getApplication();
                   childrensrv = backbone.searchChild(childBarcode, childFName, ChildMName, motherFname, null, null, null, surname,
                           motherSName, placeOBId, healthFacility, villageName, status);

                   if (childrensrv == null || childrensrv.isEmpty()) {
                       SearchChildFragment.this.getActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               alertDialog.dismiss();
                               Log.d("EBENSEARCH", "ebenezer was No where to be found");
                               //                            Toast.makeText(getApplicationContext(), "Communication was not successful,try again", Toast.LENGTH_LONG).show();
                           }
                       });
                   } else if (childrensrv.size() > 0) {
                       alertDialog.dismiss();
                       Log.d("EBENSEARCH", "ebenezer was found : " + childrensrv.get(0).getFirstname1());

                       SearchChildFragment.this.getActivity().runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               // Create and show the dialog.
                               if (childrensrv.size() > 0) {
                                   serverdata = true;
                                   childNotFoundLayout.setVisibility(View.GONE);
                                   childListLayout.setVisibility(View.VISIBLE);
                                   lvChildrenSearchResults.setVisibility(View.VISIBLE);
                                   adapter = new AdapterGridDataSearchResult(SearchChildFragment.this.getActivity(), childrensrv, mydb, currentCount);
                                   lvChildrenSearchResults.setAdapter(null);
                                   //                                adapter.replaceData(childrensrv);
                                   lvChildrenSearchResults.setAdapter(adapter);
                                   adapter.notifyDataSetChanged();

                               }
                           }
                       });
                   }
               }
            }
        }
        }.setData(dateFromForSRV, dateToForSRV).start();

    }

    //Declaration
    private class GenericTextWatcher implements View.OnFocusChangeListener{

        private Timer timer=new Timer();
        private final long DELAY = 500; // milliseconds
        private View v;
        private GenericTextWatcher(View view) {
            this.v = view;
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            MaterialEditText et = (MaterialEditText) v;
            if(b){
               currentText  =  et.getText().toString();
            }else{
                if(et.getText().toString().equals(currentText) || (et.getText().toString().length() == 0)){

                }else{
                    new getChildren().execute(currentCount);
                }
            }
        }

    }

    private class ChildSynchronization extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            BackboneApplication application = (BackboneApplication) SearchChildFragment.this.getActivity().getApplication();
            int parse_status = 0;
            String village_id, hf_id;

            for (String id : params) {
                parse_status = application.parseChildById(id);
                Log.d("parseChildCollectorbyId", parse_status+"");
                if (parse_status != 2 && parse_status != 3) {
                    DatabaseHandler db = application.getDatabaseInstance();
                    parseHFIDWhenNotInDb(db, application);
                    Cursor cursor = null;
                    Log.d("child id", id);
                    cursor = db.getReadableDatabase().rawQuery("SELECT * FROM child WHERE ID=?", new String[]{id});
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        village_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.DOMICILE_ID));
                        hf_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.HEALTH_FACILITY_ID));
                        Log.d("search hf id", hf_id);

                        int found = 0;
                        List<HealthFacility> a = db.getAllHealthFacility();
                        for (HealthFacility b : a) {
                            if (b.getId().equalsIgnoreCase(hf_id)) {
                                found = 1;
                            }
                        }

                        if (found == 0 && hf_id != null) {
                            application.parseCustomHealthFacility(hf_id);
                        }

                        try {
                            if (village_id != null || !village_id.equalsIgnoreCase("0")) {
                                application.parsePlaceById(village_id);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return parse_status;
        }

        @Override
        protected void onPostExecute(Integer result) {
            //super.onPostExecute(result);;
//            myHandler.sendEmptyMessage(result);
            //parseChildByBarcode = result;

            Intent childDetailsActivity = new Intent(SearchChildFragment.this.getActivity(), ChildDetailsActivity.class);
            childDetailsActivity.putExtra("barcode", adapter.getBarcode(currentChildPosition));
            childDetailsActivity.putExtra(BackboneApplication.CHILD_ID, adapter.getChildid(currentChildPosition));
            Log.e("coze", "child id is 1  : " + adapter.getChildId(currentChildPosition));
            Log.e("coze", "OR child id is 2  : " + adapter.getChildid(currentChildPosition));
            Log.e("coze", "BARCODE IS  : " + adapter.getBarcode(currentChildPosition));
            startActivity(childDetailsActivity);
            currentChildPosition = -1;

        }
    }

    private void parseHFIDWhenNotInDb(DatabaseHandler db, BackboneApplication app){
        String hfidFoundInVaccEvOnlyAndNotInHealthFac = db.getHFIDFoundInVaccEvAndNotInHealthFac();
        if(hfidFoundInVaccEvOnlyAndNotInHealthFac != null){
            app.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
            Log.d("parseChildCollectorbyId", "Parsed the HF");
        }
    }

}
