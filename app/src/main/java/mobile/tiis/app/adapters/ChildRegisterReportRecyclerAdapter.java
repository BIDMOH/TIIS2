package mobile.tiis.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mobile.tiis.app.R;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.util.ViewChildRegisterInfoRow;


/**
 * Created by Coze on 7/26/13.
 */
public class ChildRegisterReportRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG=ChildRegisterReportRecyclerAdapter.class.getSimpleName();
    private Context mContext;
    private SimpleDateFormat ft,ft2;

    List<ViewChildRegisterInfoRow> items;



    public ChildRegisterReportRecyclerAdapter( List<ViewChildRegisterInfoRow> items){
        this.items = items;
        ft = new SimpleDateFormat("dd-MM");
        ft2 = new SimpleDateFormat("dd-MM-yyyy");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sn;
        TextView date;
        TextView dateOfBirth;
        TextView childsName;
        TextView domicile;
        TextView gender;
        TextView mothersName;
        TextView mothers_hiv_status;
        TextView mothers_tt2_status;
        TextView childCumulativeSn;
        TextView childRegistrationYear;
        TextView bcg,opv0,opv1,opv2,opv3,dtp1,dtp2,dtp3,pcv1,pcv2,pcv3,rota1,rota2,rubella1,rubella2;
        public ViewHolder(View convertView) {
            super(convertView);
            sn = ((TextView)convertView.findViewById(R.id.sn));
            date = ((TextView) convertView.findViewById(R.id.date));
            dateOfBirth = ((TextView) convertView.findViewById(R.id.date_of_birth));
            childsName = ((TextView) convertView.findViewById(R.id.childsName));
            gender = ((TextView) convertView.findViewById(R.id.gender));
            mothersName = ((TextView) convertView.findViewById(R.id.mothers_name));
            mothers_hiv_status = ((TextView) convertView.findViewById(R.id.mothers_hiv_status));
            mothers_tt2_status = ((TextView) convertView.findViewById(R.id.mothers_tt2_status));
            domicile = ((TextView) convertView.findViewById(R.id.domicile));
            childCumulativeSn = ((TextView) convertView.findViewById(R.id.child_cumulative_sn));
            childRegistrationYear = ((TextView) convertView.findViewById(R.id.child_registration_year));
            bcg = ((TextView) convertView.findViewById(R.id.bcg));
            opv0 = ((TextView) convertView.findViewById(R.id.opv0));
            opv1 = ((TextView) convertView.findViewById(R.id.opv1));
            opv2 = ((TextView) convertView.findViewById(R.id.opv2));
            opv3 = ((TextView) convertView.findViewById(R.id.opv3));
            dtp1 = ((TextView) convertView.findViewById(R.id.dtp1));
            dtp2 = ((TextView) convertView.findViewById(R.id.dtp2));
            dtp3 = ((TextView) convertView.findViewById(R.id.dtp3));
            pcv1 = ((TextView) convertView.findViewById(R.id.pcv1));
            pcv2 = ((TextView) convertView.findViewById(R.id.pcv2));
            pcv3 = ((TextView) convertView.findViewById(R.id.pcv3));
            rota1 = ((TextView) convertView.findViewById(R.id.rota1));
            rota2 = ((TextView) convertView.findViewById(R.id.rota2));
            rubella1 = ((TextView) convertView.findViewById(R.id.rubella1));
            rubella2 = ((TextView) convertView.findViewById(R.id.rubella2));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        mContext=parent.getContext();
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.child_register_list_item, null);
        ViewHolder holder = new ViewHolder(view);
        return  holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ViewHolder holder = (ViewHolder)viewHolder;
        ViewChildRegisterInfoRow a = items.get(position);
        holder.sn.setText(a.sn+"");

        if(a.OPV0!=null) {
            Date scheduled_date = BackboneActivity.dateParser(a.OPV0);
            holder.date.setText(ft2.format(scheduled_date));
        }else if(a.OPV1!=null){
            Date date = BackboneActivity.dateParser(a.OPV1);
            holder.date.setText(ft2.format(date));
        }
        if(a.birthdate!=null) {
            Date birth_date = BackboneActivity.dateParser(a.birthdate);
            holder.dateOfBirth.setText(ft2.format(birth_date));
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

        holder.childsName.setText(name);
        holder.domicile.setText(a.domicile);
        holder.gender.setText(a.gender.equals("true")?"ME":"KE");
        holder.mothersName.setText(a.motherFirstName+" "+a.motherLastName);
        holder.mothers_hiv_status.setText(a.motherHivStatus);
        holder.mothers_tt2_status.setText(a.motherTT2Status);

        holder.childCumulativeSn.setText(a.childCumulativeSn);
        holder.childRegistrationYear.setText(a.childRegistrationYear);

        if(a.bcg!=null) {
            Date date = BackboneActivity.dateParser(a.bcg);
            holder.bcg.setText(ft.format(date));
        }

        if(a.OPV0!=null) {
            Date date = BackboneActivity.dateParser(a.OPV0);
            holder.opv0.setText(ft.format(date));
        }
        if(a.OPV1!=null) {
            Date date = BackboneActivity.dateParser(a.OPV1);
            holder.opv1.setText(ft.format(date));
        }

        if(a.OPV2!=null) {
            Date date = BackboneActivity.dateParser(a.OPV2);
            holder.opv2.setText(ft.format(date));
        }

        if(a.OPV3!=null) {
            Date date = BackboneActivity.dateParser(a.OPV3);
            holder.opv3.setText(ft.format(date));
        }

        if(a.DTP1!=null) {
            Date date = BackboneActivity.dateParser(a.DTP1);
            holder.dtp1.setText(ft.format(date));
        }

        if(a.DTP2!=null) {
            Date date = BackboneActivity.dateParser(a.DTP2);
            holder.dtp2.setText(ft.format(date));
        }

        if(a.DTP3!=null) {
            Date date = BackboneActivity.dateParser(a.DTP3);
            holder.dtp3.setText(ft.format(date));
        }

        if(a.PCV1!=null) {
            Date date = BackboneActivity.dateParser(a.PCV1);
            holder.pcv1.setText(ft.format(date));
        }

        if(a.PCV2!=null) {
            Date date = BackboneActivity.dateParser(a.PCV2);
            holder.pcv2.setText(ft.format(date));
        }

        if(a.PCV3!=null) {
            Date date = BackboneActivity.dateParser(a.PCV3);
            holder.pcv3.setText(ft.format(date));
        }

        if(a.Rota1!=null) {
            Date date = BackboneActivity.dateParser(a.Rota1);
            holder.rota1.setText(ft.format(date));
        }


        if(a.Rota2!=null) {
            Date date = BackboneActivity.dateParser(a.Rota2);
            holder.rota2.setText(ft.format(date));
        }

        if(a.measlesRubella1!=null) {
            Date date = BackboneActivity.dateParser(a.measlesRubella1);
            holder.rubella1.setText(ft.format(date));
        }
        if(a.measlesRubella2!=null) {
            Date date = BackboneActivity.dateParser(a.measlesRubella2);
            holder.rubella2.setText(ft.format(date));
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
