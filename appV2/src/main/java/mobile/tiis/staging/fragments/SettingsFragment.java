package mobile.tiis.staging.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import mobile.tiis.staging.CustomViews.MaterialLoader;
import mobile.tiis.staging.R;
import mobile.tiis.staging.base.BackboneApplication;

/**
 * Created by issymac on 16/12/15.
 */
public class SettingsFragment extends android.support.v4.app.Fragment{


    private String currentCumulativeNo;
    private TextView currentCumulativeValue;
    private EditText cumulative_no;

    private  BackboneApplication app;
    private Button bt;
    private MaterialLoader progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_settings, null);

        progressBar = (MaterialLoader) root.findViewById(R.id.progress);
        currentCumulativeValue = (TextView)root.findViewById(R.id.current_cumulative_sn);
        cumulative_no = (EditText)root.findViewById(R.id.cumulative_no);
        app = (BackboneApplication) getActivity().getApplication();
        bt = (Button)root.findViewById(R.id.save_btn);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String no = cumulative_no.getText().toString();
                if(no.equals("")){
                    cumulative_no.setError("Please enter a valid number");
                }else{
                    new AsyncTask<Void, Void, Boolean>(){
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            bt.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected Boolean doInBackground(Void... voids) {

                            if(app.updateHealthFacilityCumulativeChildRegistrationNumber(Integer.parseInt(no))!=-999){
                                currentCumulativeNo = app.getHealthFacilityCumulativeChildRegistrationNumber();
                                return true;
                            }else{
                                return false;
                            }
                        }

                        @Override
                        protected void onPostExecute(Boolean aVoid) {
                            progressBar.setVisibility(View.GONE);
                            bt.setVisibility(View.VISIBLE);
                            if(aVoid) {
                                currentCumulativeValue.setText(currentCumulativeNo);
                            }else{
                                Toast.makeText(getActivity(),"Error saving the new Health Facility Cumulative Registration Number",Toast.LENGTH_LONG).show();
                            }
                            super.onPostExecute(aVoid);
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });


        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                currentCumulativeValue.setText("Please wait, obtaining data from the server");
            }

            @Override
            protected Void doInBackground(Void... voids) {
                currentCumulativeNo = app.getHealthFacilityCumulativeChildRegistrationNumber();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                currentCumulativeValue.setText(currentCumulativeNo);
                super.onPostExecute(aVoid);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        return root;
    }

}