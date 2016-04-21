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

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mobile.tiis.app.R;
import mobile.tiis.app.adapters.AdapterImmunizations;
import mobile.tiis.app.base.BackboneApplication;
import mobile.tiis.app.database.DatabaseHandler;
import mobile.tiis.app.entity.ChartDataModel;

/**
 * Created by Rubin on 5/1/2015.
 */
public class FragmentImmunizations extends DialogFragment {

    ListView lvImmunizations;
    String dataFromDataPicker;
    DatabaseHandler mydb;
    TextView tvDate;
    Button btnBack;
    List<ChartDataModel> listImmun;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.immunizations,
                container, false);
        lvImmunizations = (ListView)view.findViewById(R.id.list_with_children);
        tvDate = (TextView)view.findViewById(R.id.tv_date);
        btnBack = (Button)view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentImmunizations.this).commit();
            }
        });
        BackboneApplication app = (BackboneApplication) getActivity().getApplication();
        mydb = app.getDatabaseInstance();
        listImmun = mydb.getImmunizations(dataFromDataPicker,app);
        if(listImmun.isEmpty()){
            Toast.makeText(getActivity(),"There is no data to be shown",Toast.LENGTH_LONG).show();
        }else {
            AdapterImmunizations adapter = new AdapterImmunizations(getActivity(), R.layout.item_listview_immunizations, listImmun);
            lvImmunizations.setAdapter(adapter);
        }


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        String text ="";
        try {
            date = format.parse(dataFromDataPicker);
            format = new SimpleDateFormat("dd MMM yyyy");
              text = format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvDate.setText("Date: "+text);
        return view;
    }
    public void setDataChoosen(String dataChoosen) {
        dataFromDataPicker = dataChoosen;
    }

}
