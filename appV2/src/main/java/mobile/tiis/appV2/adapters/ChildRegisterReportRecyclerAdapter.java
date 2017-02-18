package mobile.tiis.appv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mobile.tiis.appv2.R;
import mobile.tiis.appv2.base.BackboneActivity;
import mobile.tiis.appv2.util.ViewChildRegisterInfoRow;


/**
 * Created by Coze on 7/26/13.
 */
public class ChildRegisterReportRecyclerAdapter extends BaseAdapter {
    private static final String TAG=ChildRegisterReportRecyclerAdapter.class.getSimpleName();
    private SimpleDateFormat ft,ft2;
    private LayoutInflater layoutInflater;

    List<ViewChildRegisterInfoRow> items;



    public ChildRegisterReportRecyclerAdapter( List<ViewChildRegisterInfoRow> items,Context context){
        this.items = items;
        ft = new SimpleDateFormat("dd-MM");
        ft2 = new SimpleDateFormat("dd-MM-yyyy");
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {
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

    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.child_register_list_item,parent,false);
            holder.sn = ((TextView)convertView.findViewById(R.id.sn));
            holder.date = ((TextView) convertView.findViewById(R.id.date));
            holder.dateOfBirth = ((TextView) convertView.findViewById(R.id.date_of_birth));
            holder.childsName = ((TextView) convertView.findViewById(R.id.childsName));
            holder. gender = ((TextView) convertView.findViewById(R.id.gender));
            holder.mothersName = ((TextView) convertView.findViewById(R.id.mothers_name));
            holder.mothers_hiv_status = ((TextView) convertView.findViewById(R.id.mothers_hiv_status));
            holder.mothers_tt2_status = ((TextView) convertView.findViewById(R.id.mothers_tt2_status));
            holder.domicile = ((TextView) convertView.findViewById(R.id.domicile));
            holder.childCumulativeSn = ((TextView) convertView.findViewById(R.id.child_cumulative_sn));
            holder.childRegistrationYear = ((TextView) convertView.findViewById(R.id.child_registration_year));
            holder.bcg = ((TextView) convertView.findViewById(R.id.bcg));
            holder.opv0 = ((TextView) convertView.findViewById(R.id.opv0));
            holder.opv1 = ((TextView) convertView.findViewById(R.id.opv1));
            holder.opv2 = ((TextView) convertView.findViewById(R.id.opv2));
            holder.opv3 = ((TextView) convertView.findViewById(R.id.opv3));
            holder.dtp1 = ((TextView) convertView.findViewById(R.id.dtp1));
            holder.dtp2 = ((TextView) convertView.findViewById(R.id.dtp2));
            holder.dtp3 = ((TextView) convertView.findViewById(R.id.dtp3));
            holder.pcv1 = ((TextView) convertView.findViewById(R.id.pcv1));
            holder.pcv2 = ((TextView) convertView.findViewById(R.id.pcv2));
            holder.pcv3 = ((TextView) convertView.findViewById(R.id.pcv3));
            holder.rota1 = ((TextView) convertView.findViewById(R.id.rota1));
            holder.rota2 = ((TextView) convertView.findViewById(R.id.rota2));
            holder.rubella1 = ((TextView) convertView.findViewById(R.id.rubella1));
            holder.rubella2 = ((TextView) convertView.findViewById(R.id.rubella2));

            convertView.setTag(holder);

        }else{
           holder = (ViewHolder)convertView.getTag();
        }

        try {
            final ViewChildRegisterInfoRow a = items.get(position);

            holder.sn.setText(a.sn + "");

            if (a.OPV0 != null && !a.OPV0.equals("null")) {
                Date scheduled_date = BackboneActivity.dateParser(a.OPV0);
                holder.date.setText(ft2.format(scheduled_date));
            } else if (a.OPV1 != null && !a.OPV1.equals("null")) {
                Date date = BackboneActivity.dateParser(a.OPV1);
                holder.date.setText(ft2.format(date));
            } else {
                holder.date.setText("");
            }
            if (a.birthdate != null) {
                Date birth_date = BackboneActivity.dateParser(a.birthdate);
                holder.dateOfBirth.setText(ft2.format(birth_date));
            }

            String name = "";
            if (a.childFirstName != null) {
                name += a.childFirstName;
            }
            if (a.childMiddleName != null) {
                name += " " + a.childMiddleName;
            }
            if (a.childSurname != null) {
                name += " " + a.childSurname;
            }

            holder.childsName.setText(name);
            holder.domicile.setText(a.domicile);
            holder.gender.setText(a.gender.equals("true") ? "ME" : "KE");
            holder.mothersName.setText(a.motherFirstName + " " + a.motherLastName);
            holder.mothers_hiv_status.setText(a.motherHivStatus);
            holder.mothers_tt2_status.setText(a.motherTT2Status);

            holder.childCumulativeSn.setText(a.childCumulativeSn.equals("0") ? "" : a.childCumulativeSn);
            holder.childRegistrationYear.setText(a.childRegistrationYear.equals("0") ? "" : a.childRegistrationYear);

            if (a.bcg != null && !a.bcg.equals("null")) {
                Date date = BackboneActivity.dateParser(a.bcg);
                holder.bcg.setText(ft.format(date));
            } else {
                holder.bcg.setText("");
            }

            if (a.OPV0 != null && !a.OPV0.equals("null")) {
                Date date = BackboneActivity.dateParser(a.OPV0);
                holder.opv0.setText(ft.format(date));
            } else {
                holder.opv0.setText("---");
            }
            if (a.OPV1 != null && !a.OPV1.equals("null")) {
                Date date = BackboneActivity.dateParser(a.OPV1);
                holder.opv1.setText(ft.format(date));
            } else {
                holder.opv1.setText("");
            }

            if (a.OPV2 != null && !a.OPV2.equals("null")) {
                Date date = BackboneActivity.dateParser(a.OPV2);
                holder.opv2.setText(ft.format(date));
            } else {
                holder.opv2.setText("");
            }

            if (a.OPV3 != null && !a.OPV3.equals("null")) {
                Date date = BackboneActivity.dateParser(a.OPV3);
                holder.opv3.setText(ft.format(date));
            } else {
                holder.opv3.setText("");
            }

            if (a.DTP1 != null && !a.DTP1.equals("null")) {
                Date date = BackboneActivity.dateParser(a.DTP1);
                holder.dtp1.setText(ft.format(date));
            } else {
                holder.dtp1.setText("");
            }

            if (a.DTP2 != null && !a.DTP2.equals("null")) {
                Date date = BackboneActivity.dateParser(a.DTP2);
                holder.dtp2.setText(ft.format(date));
            } else {
                holder.dtp2.setText("");
            }

            if (a.DTP3 != null && !a.DTP3.equals("null")) {
                Date date = BackboneActivity.dateParser(a.DTP3);
                holder.dtp3.setText(ft.format(date));
            } else {
                holder.dtp3.setText("");
            }

            if (a.PCV1 != null && !a.PCV1.equals("null")) {
                Date date = BackboneActivity.dateParser(a.PCV1);
                holder.pcv1.setText(ft.format(date));
            } else {
                holder.pcv1.setText("");
            }

            if (a.PCV2 != null && !a.PCV2.equals("null")) {
                Date date = BackboneActivity.dateParser(a.PCV2);
                holder.pcv2.setText(ft.format(date));
            } else {
                holder.pcv2.setText("");
            }

            if (a.PCV3 != null && !a.PCV3.equals("null")) {
                Date date = BackboneActivity.dateParser(a.PCV3);
                holder.pcv3.setText(ft.format(date));
            } else {
                holder.pcv3.setText("");
            }

            if (a.Rota1 != null && !a.Rota1.equals("null")) {
                Date date = BackboneActivity.dateParser(a.Rota1);
                holder.rota1.setText(ft.format(date));
            } else {
                holder.rota1.setText("");
            }


            if (a.Rota2 != null && !a.Rota2.equals("null")) {
                Date date = BackboneActivity.dateParser(a.Rota2);
                holder.rota2.setText(ft.format(date));
            } else {
                holder.rota2.setText("");
            }

            if (a.measlesRubella1 != null && !a.measlesRubella1.equals("null")) {
                Date date = BackboneActivity.dateParser(a.measlesRubella1);
                holder.rubella1.setText(ft.format(date));
            } else {
                holder.rubella1.setText("");
            }
            if (a.measlesRubella2 != null && !a.measlesRubella2.equals("null")) {
                Date date = BackboneActivity.dateParser(a.measlesRubella2);
                holder.rubella2.setText(ft.format(date));
            } else {
                holder.rubella2.setText("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView;
    }

}
