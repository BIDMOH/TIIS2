/*******************************************************************************
 * <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *   ~ Copyright (C)AIRIS Solutions 2015 TIIS App - Tanzania Immunization Information System App
 *   ~
 *   ~    Licensed under the Apache License, Version 2.0 (the "License");
 *   ~    you may not use this file except in compliance with the License.
 *   ~    You may obtain a copy of the License at
 *   ~
 *   ~        http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~    Unless required by applicable law or agreed to in writing, software
 *   ~    distributed under the License is distributed on an "AS IS" BASIS,
 *   ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~    See the License for the specific language governing permissions and
 *   ~    limitations under the License.
 *   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
 ******************************************************************************/

package mobile.tiis.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import mobile.tiis.app.R;
import mobile.tiis.app.ViewChildActivity;
import mobile.tiis.app.adapters.AdapterGridDataSearchResult;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.database.SQLHandler;
import mobile.tiis.app.entity.Child;
import mobile.tiis.app.entity.HealthFacility;

/**
 * Created by Arinela on 3/13/2015.
 */
public class FragmentSearchResultDataGrid extends DialogFragment {

    ListView lvSearchResultData;
    ArrayList<Child> listChild;
    Button btnSearchAgain;
    DatabaseHandler mydb;
    ProgressDialog add;
    int status;
    String childidToParse;
    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Intent scan = new Intent(getActivity().getApplicationContext(), ViewChildActivity.class);
            scan.putExtra("cameFromSearch", true);
            Bundle bnd = new Bundle();
            bnd.putString(BackboneApplication.CHILD_ID, childidToParse);
            scan.putExtras(bnd);

            switch (msg.what) {
                case 10:
                    add.show();
                    break;
                case 2:
                    //Toast.makeText(ScanHandlerActivity.this, "Status 2", Toast.LENGTH_SHORT).show();
                    //myHandler.sendEmptyMessage(10);
                    add.dismiss();

                    final AlertDialog ad2 = new AlertDialog.Builder(getActivity()).create();
                    ad2.setTitle(getString(R.string.error));
                    ad2.setMessage(getString(R.string.data_not_fetched));
                    ad2.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ad2.dismiss();
                        }
                    });
                    ad2.show();
                    break;
                case 3:
                    final AlertDialog ad3 = new AlertDialog.Builder(getActivity()).create();
                    ad3.setTitle(getString(R.string.error));
                    ad3.setMessage(getString(R.string.data_not_fetched));
                    ad3.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ad3.dismiss();
                        }
                    });
                    ad3.show();
                    //Toast.makeText(ScanHandlerActivity.this, "Status 3", Toast.LENGTH_SHORT).show();
                    //myHandler.sendEmptyMessage(10);
                    add.dismiss();
                    break;
                default:
                    //found=1;
                    //myHandler.sendEmptyMessage(10);
                    add.dismiss();
                    startActivity(scan);
                    //Toast.makeText(ScanHandlerActivity.this, "Child found", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_result_grid_data,
                container, false);
        btnSearchAgain = (Button) view.findViewById(R.id.btn_search_again);
        btnSearchAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentSearchResultDataGrid.this).commit();
            }
        });
        lvSearchResultData = (ListView) view.findViewById(R.id.lv_search_list_result);

        BackboneApplication app = (BackboneApplication) getActivity().getApplication();
        mydb = app.getDatabaseInstance();

        Context ctx = getActivity().getApplicationContext();
        AdapterGridDataSearchResult adapter = new AdapterGridDataSearchResult(ctx,  listChild, mydb, "0");
        lvSearchResultData.setAdapter(adapter);

        lvSearchResultData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent viewChild = new Intent(getActivity(), ViewChildActivity.class);
                viewChild.putExtra("cameFromSearch", true);
                final Bundle bnd = new Bundle();
                bnd.putString(BackboneApplication.CHILD_ID, listChild.get(position).getId());
                viewChild.putExtras(bnd);
                Cursor cursor = null;
                final int pos = position;
                cursor = mydb.getReadableDatabase().rawQuery("SELECT * FROM child WHERE ID=?", new String[]{listChild.get(position).getId()});
                if (cursor.getCount() > 0 && cursor != null) {

                    startActivity(viewChild);
                } else {
                    add = new ProgressDialog(getActivity());
                    add.setTitle(getString(R.string.searching_online));
                    add.setMessage(getString(R.string.child_not_found_locally));
                    add.setCanceledOnTouchOutside(false);
                    add.setCancelable(false);
                    add.show();

                    new Thread() {
                        @Override
                        public void run() {
                            BackboneApplication app = (BackboneApplication) getActivity().getApplication();

                            synchronized (this) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        childidToParse = listChild.get(pos).getId();
                                        myHandler.sendEmptyMessage(10);
                                        ChildSynchronization task = new ChildSynchronization();
                                        task.execute(childidToParse);

                                        //int status = app.parseChildById(listChild.get(pos).getId());
//                                            if(status!=2 && status!=3){
//                                                viewChild.putExtras(bnd);
//                                                add.dismiss();
//                                                startActivity(viewChild);
//                                            }
//                                            else{
//                                                final AlertDialog ad3 = new AlertDialog.Builder(getActivity()).create();
//                                                ad3.setTitle("Error");
//                                                ad3.setMessage("Fetching from Server failed due to network\nPlease try to again or check your connectivity.");
//                                                ad3.setButton("OK", new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialog, int which) {
//                                                        ad3.dismiss();
//                                                    }
//                                                });
//                                                ad3.show();
//                                            }
                                    }
                                });
                            }
                        }
                    }.start();


                }

            }
        });

        return view;
    }

    public void setListWithSearchChild(ArrayList<Child> children, DatabaseHandler mydb) {
        listChild = children;
        this.mydb = mydb;
    }

    private class ChildSynchronization extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            BackboneApplication application = (BackboneApplication) getActivity().getApplication();
            int parse_status = 0;
            String birthplace_id, village_id, hf_id;

            for (String id : params) {
                parse_status = application.parseChildById(id);
                if (parse_status != 2 && parse_status != 3) {
                    DatabaseHandler db = application.getDatabaseInstance();
                    parseHFIDWhenNotInDb(db, application);
                    Cursor cursor = null;
                    Log.d("child id", id);
                    cursor = db.getReadableDatabase().rawQuery("SELECT * FROM child WHERE ID=?", new String[]{id});
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        birthplace_id = cursor.getString(cursor.getColumnIndex(SQLHandler.ChildColumns.BIRTHPLACE_ID));
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
                            //application.parsePlaceByCustomHfId(hf_id);
                            //application.parsePlaceById(birthplace_id);
                            //application.parsePlaceById(village_id);
                        }

                        try {
                            if ( village_id != null) {
                                if (!village_id.equalsIgnoreCase("0")) {
                                    application.parsePlaceById(village_id);
                                }

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
            myHandler.sendEmptyMessage(result);
            //parseChildByBarcode = result;
        }
    }

    private void parseHFIDWhenNotInDb(DatabaseHandler db, BackboneApplication app){
        String hfidFoundInVaccEvOnlyAndNotInHealthFac = db.getHFIDFoundInVaccEvAndNotInHealthFac();
        if(hfidFoundInVaccEvOnlyAndNotInHealthFac != null){
            app.parseHealthFacilityThatAreInVaccEventButNotInHealthFac(hfidFoundInVaccEvOnlyAndNotInHealthFac);
        }
    }
}
