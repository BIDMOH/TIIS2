package mobile.tiis.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import mobile.tiis.app.R;
import mobile.tiis.app.base.BackboneActivity;
import mobile.tiis.app.entity.NewChartDataTable;
import mobile.tiis.app.util.ViewChildRegisterInfoRow;

/**
 * Created by issy on 7/15/16.
 */
public class ChildRegisterReportListAdapter extends BaseAdapter {

    LayoutInflater inflator;
    List<ViewChildRegisterInfoRow> items;

    public ChildRegisterReportListAdapter(Context context, List<ViewChildRegisterInfoRow> newItems) {
        this.items      = newItems;
        inflator        = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = inflator.inflate(R.layout.child_register_list_item, null);
        ViewChildRegisterInfoRow a = items.get(i);

        ((TextView)convertView.findViewById(R.id.sn)).setText(a.sn+"");
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM");
        SimpleDateFormat ft2 = new SimpleDateFormat("dd-MM-yyyy");

        if(a.OPV0!=null) {
            Date scheduled_date = BackboneActivity.dateParser(a.OPV0);
            ((TextView) convertView.findViewById(R.id.date)).setText(ft2.format(scheduled_date));
        }else if(a.OPV1!=null){
            Date date = BackboneActivity.dateParser(a.OPV1);
            ((TextView) convertView.findViewById(R.id.date)).setText(ft2.format(date));
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

        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).sn;
    }
}
