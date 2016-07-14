package mobile.tiis.app.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mobile.tiis.app.ChildDetailsActivity;
import mobile.tiis.app.R;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.util.ViewAppointmentRow;
import mobile.tiis.app.util.ViewChildRegisterInfoRow;

/**
 * Created by issy on 7/7/16.
 */
public class ChildRegisterReportFragment extends android.support.v4.app.Fragment{

    //Table Layout to be used to loop the list of the children information in
    private TableLayout childRegisterTable;

    public String currentCount = "0";
    private BackboneApplication app;
    private DatabaseHandler this_database;
    private ArrayList<ViewChildRegisterInfoRow> var;
    private LayoutInflater inflater;

    public static ChildRegisterReportFragment newInstance(int position) {
        ChildRegisterReportFragment f = new ChildRegisterReportFragment();
        Bundle b = new Bundle();
//        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_child_register, null);
        app = (BackboneApplication) this.getActivity().getApplication();
        this_database = app.getDatabaseInstance();
        this.inflater = inflater;
        setUpView(root);


        new filterList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return root;
    }

    public void setUpView(View v){
        childRegisterTable      = (TableLayout) v.findViewById(R.id.child_register_table);
    }


    public  class filterList extends AsyncTask<String, Void, Integer> {

        ArrayList<ViewChildRegisterInfoRow> mVar;

        @Override
        protected void onPreExecute() {
//            loadingBar.setVisibility(View.VISIBLE);
//            lvMonthlyPlanList.setVisibility(View.GONE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            long t = Calendar.getInstance().getTimeInMillis()/1000;
            long t1 = (t + (30 * 24 * 60 * 60));
            long t2 = (t - (30 * 24 * 60 * 60));
            String to_date =  t1+ "";
            String from_date = t2+ "";

            try {
                if (!params[2].equals("") && !params[3].equals("")) {
                    from_date = (Long.parseLong(params[2])-(24*60*60))+"";
                    Log.d("day13", "from picker : "+from_date);
                    to_date = params[3];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Cursor cursor;
            mVar = new ArrayList<>();

            DatabaseHandler mydb = app.getDatabaseInstance();

            String SQLChildRegistry = "SELECT FIRSTNAME1,FIRSTNAME2,LASTNAME1, BIRTHDATE,GENDER,MOTHER_FIRSTNAME,MOTHER_LASTNAME,\n" +
                    "\t\t(CASE \n" +
                    "\t\t\tWHEN (place.ID = '-100') \n" +
                    "\t\t\t\tTHEN child.NOTES\n" +
                    "                ELSE place.NAME \n" +
                    "\t\tEND) AS DOMICILE,\n" +
                    "\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'BCG'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS BCG,\n" +
                    "\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'OPV0'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS OPV0,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'OPV 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS OPV1,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'OPV 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS OPV2,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'OPV 3'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS OPV3,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'DTP-HepB-Hib 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS DTP1,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'DTP-HepB-Hib 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS DTP2,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'DTP-HepB-Hib 3'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS DTP3,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Rota 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS Rota1,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Rota 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS Rota2,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Measles 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS Measles1,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Measles 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS Measles2,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'PCV 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS PCV1,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'PCV 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS PCV2,\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'PCV 3'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS PCV3,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Measles Rubella 1'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS MeaslesRubella1,\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t\n" +
                    "\t\t(SELECT VACCINATION_DATE FROM vaccination_event \n" +
                    "\t\tINNER JOIN   dose  on  vaccination_event.DOSE_ID = dose.ID \n" +
                    "\t\tWHERE \n" +
                    "\t\tdose.FULLNAME = 'Measles Rubella 2'  AND vaccination_event.CHILD_ID = CHILD.ID AND VACCINATION_STATUS = \"true\") AS MeaslesRubella2\n" +
                    "\t\t\n" +
                    "\t\n" +
                    "         FROM CHILD \n" +
                    "\t\t INNER JOIN \n" +
                    "\t\t place on child.DOMICILE_ID = place.ID\n";



            Log.e("optimization", SQLChildRegistry);
            long tStart = System.currentTimeMillis();
            cursor = mydb.getReadableDatabase().rawQuery(SQLChildRegistry, null);



            Log.e("optimization", "Querying time  = elapsed total time (milliseconds): " + (System.currentTimeMillis() - tStart));

            Log.d("optimization", "Done with getting the Monthly plan data");
            if (cursor != null) {
                tStart = System.currentTimeMillis();
                Log.d("optimization", "cursor not null SIZE IS : "+cursor.getCount());
                Log.e("optimization", "Getting total size time  = elapsed total time (milliseconds): " + (System.currentTimeMillis() - tStart));


                tStart = System.currentTimeMillis();
                if (cursor.moveToFirst()) {
                    Log.d("optimization", "Moved to first item in the cursor");
                    Log.e("optimization", "moving cursor to first time  = elapsed total time (milliseconds): " + (System.currentTimeMillis() - tStart));

                    tStart = System.currentTimeMillis();
                    int counter = 0;
                    do {
                        Log.d("childregistry","BCG = "+cursor.getString(cursor.getColumnIndex("BCG")));
                        counter++;
                        ViewChildRegisterInfoRow row = new ViewChildRegisterInfoRow();
                        row.sn = counter;
                        row.childFirstName = cursor.getString(cursor.getColumnIndex("FIRSTNAME1"));
                        row.childMiddleName = cursor.getString(cursor.getColumnIndex("FIRSTNAME2"));
                        row.childSurname = cursor.getString(cursor.getColumnIndex("LASTNAME1"));
                        row.birthdate = cursor.getString(cursor.getColumnIndex("BIRTHDATE"));
                        row.gender = cursor.getString(cursor.getColumnIndex("GENDER"));
                        row.motherFirstName = cursor.getString(cursor.getColumnIndex("MOTHER_FIRSTNAME"));
                        row.motherLastName = cursor.getString(cursor.getColumnIndex("MOTHER_LASTNAME"));
                        row.domicile = cursor.getString(cursor.getColumnIndex("DOMICILE"));
                        row.bcg = cursor.getString(cursor.getColumnIndex("BCG"));
                        row.OPV0 = cursor.getString(cursor.getColumnIndex("OPV0"));
                        row.OPV1 = cursor.getString(cursor.getColumnIndex("OPV1"));
                        row.OPV2 = cursor.getString(cursor.getColumnIndex("OPV2"));
                        row.OPV0 = cursor.getString(cursor.getColumnIndex("OPV3"));
                        row.DTP1 = cursor.getString(cursor.getColumnIndex("DTP1"));
                        row.DTP2 = cursor.getString(cursor.getColumnIndex("DTP2"));
                        row.DTP3 = cursor.getString(cursor.getColumnIndex("DTP3"));
                        row.Rota1 = cursor.getString(cursor.getColumnIndex("Rota1"));
                        row.Rota2 = cursor.getString(cursor.getColumnIndex("Rota2"));
                        row.Measles1 = cursor.getString(cursor.getColumnIndex("Measles1"));
                        row.Measles2 = cursor.getString(cursor.getColumnIndex("Measles2"));
                        row.PCV1 = cursor.getString(cursor.getColumnIndex("PCV1"));
                        row.PCV2 = cursor.getString(cursor.getColumnIndex("PCV2"));
                        row.PCV3 = cursor.getString(cursor.getColumnIndex("PCV3"));
                        row.MeaslesRubella1 = cursor.getString(cursor.getColumnIndex("MeaslesRubella1"));
                        row.MeaslesRubella2 = cursor.getString(cursor.getColumnIndex("MeaslesRubella2"));

                        mVar.add(row);
                    } while (cursor.moveToNext());
                    Log.e("optimization", "Looping to get data  = elapsed total time (milliseconds): " + (System.currentTimeMillis() - tStart));


                }
                cursor.close();
            }

            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            var = mVar;
            displayChildRegisteryList(mVar);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    public void displayChildRegisteryList(ArrayList<ViewChildRegisterInfoRow> mVar) {
        ArrayList<ViewChildRegisterInfoRow> nVar = mVar;
        childRegisterTable.removeAllViews();
        for (final ViewChildRegisterInfoRow a : nVar) {

            View convertView = inflater.inflate(R.layout.child_register_table_item, null);
            ((TextView)convertView.findViewById(R.id.sn)).setText(a.sn+"");
            SimpleDateFormat ft = new SimpleDateFormat("dd-MM");
            SimpleDateFormat ft2 = new SimpleDateFormat("dd-MM-yyyy");


            if(a.OPV0!=null) {
                Date scheduled_date = BackboneActivity.dateParser(a.OPV0);
                ((TextView) convertView.findViewById(R.id.date)).setText(ft2.format(scheduled_date));
            }else if(a.OPV1!=null){
                Date date = BackboneActivity.dateParser(a.OPV1);
                ((TextView) convertView.findViewById(R.id.date)).setText(ft.format(date));
            }

            if(a.birthdate!=null) {
                Date birth_date = BackboneActivity.dateParser(a.birthdate);
                ((TextView) convertView.findViewById(R.id.tarehe_ya_kuzaliwa)).setText(ft2.format(birth_date));
            }

            String name = "";
            if(a.childFirstName!=null){
                name+=a.childFirstName;
            }
            if(a.childMiddleName!=null){
                name+=" "+a.childMiddleName;
            }
            if(a.childSurname!=null){
                name+=" "+a.childSurname;
            }


            ((TextView)convertView.findViewById(R.id.jina_la_mtoto)).setText(name);
            ((TextView)convertView.findViewById(R.id.mahali_anapoishi)).setText(a.domicile);
            ((TextView)convertView.findViewById(R.id.jinsia)).setText(a.gender.equals("true")?"ME":"KE");
            ((TextView)convertView.findViewById(R.id.jina_la_mama)).setText(a.motherFirstName+" "+a.motherLastName);

            if(a.bcg!=null) {
                Date date = BackboneActivity.dateParser(a.bcg);
                ((TextView) convertView.findViewById(R.id.bcg)).setText(ft.format(date));
            }

            if(a.OPV0!=null) {
                Date date = BackboneActivity.dateParser(a.OPV0);
                ((TextView) convertView.findViewById(R.id.opv0)).setText(ft.format(date));
            }
            if(a.OPV1!=null) {
                Date date = BackboneActivity.dateParser(a.OPV1);
                ((TextView) convertView.findViewById(R.id.opv1)).setText(ft.format(date));
            }

            if(a.OPV2!=null) {
                Date date = BackboneActivity.dateParser(a.OPV2);
                ((TextView) convertView.findViewById(R.id.opv2)).setText(ft.format(date));
            }

            if(a.OPV3!=null) {
                Date date = BackboneActivity.dateParser(a.OPV3);
                ((TextView) convertView.findViewById(R.id.opv3)).setText(ft.format(date));
            }

            if(a.DTP1!=null) {
                Date date = BackboneActivity.dateParser(a.DTP1);
                ((TextView) convertView.findViewById(R.id.dtp1)).setText(ft.format(date));
            }

            if(a.DTP2!=null) {
                Date date = BackboneActivity.dateParser(a.DTP2);
                ((TextView) convertView.findViewById(R.id.dtp2)).setText(ft.format(date));
            }

            if(a.DTP3!=null) {
                Date date = BackboneActivity.dateParser(a.DTP3);
                ((TextView) convertView.findViewById(R.id.dtp3)).setText(ft.format(date));
            }

            if(a.PCV1!=null) {
                Date date = BackboneActivity.dateParser(a.PCV1);
                ((TextView) convertView.findViewById(R.id.pcv1)).setText(ft.format(date));
            }

            if(a.PCV2!=null) {
                Date date = BackboneActivity.dateParser(a.PCV2);
                ((TextView) convertView.findViewById(R.id.pcv2)).setText(ft.format(date));
            }

            if(a.PCV3!=null) {
                Date date = BackboneActivity.dateParser(a.PCV3);
                ((TextView) convertView.findViewById(R.id.pcv3)).setText(ft.format(date));
            }

            if(a.Rota1!=null) {
                Date date = BackboneActivity.dateParser(a.Rota1);
                ((TextView) convertView.findViewById(R.id.rota1)).setText(ft.format(date));
            }


            if(a.Rota2!=null) {
                Date date = BackboneActivity.dateParser(a.Rota2);
                ((TextView) convertView.findViewById(R.id.rota2)).setText(ft.format(date));
            }

            if(a.MeaslesRubella1!=null) {
                Date date = BackboneActivity.dateParser(a.MeaslesRubella1);
                ((TextView) convertView.findViewById(R.id.rubella1)).setText(ft.format(date));
            }
            if(a.MeaslesRubella2!=null) {
                Date date = BackboneActivity.dateParser(a.MeaslesRubella2);
                ((TextView) convertView.findViewById(R.id.rubella2)).setText(ft.format(date));
            }

            childRegisterTable.addView(convertView);
        }


    }


}
