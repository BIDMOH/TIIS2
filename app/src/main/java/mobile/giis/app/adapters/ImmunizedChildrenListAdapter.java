package mobile.giis.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mobile.giis.app.R;
import mobile.giis.app.base.BackboneActivity;
import mobile.giis.app.entity.ModelImmunizedChild;

/**
 * Created by issymac on 10/02/16.
 */
public class ImmunizedChildrenListAdapter extends BaseAdapter{

    public Context context;

    LayoutInflater inflator;

    List<ModelImmunizedChild> items;


    public ImmunizedChildrenListAdapter(Context context, List<ModelImmunizedChild> items) {
        this.items      = items;
        inflator        = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        public TextView tvName,tvLastname,tvVaccine;
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
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;

        final ModelImmunizedChild item = items.get(position);
        if (rowView == null) {
            rowView = inflator.inflate(R.layout.imm_children_list_item, null);

            viewHolder = new ViewHolder();

            viewHolder.tvName = (TextView) rowView.findViewById(R.id.names);
            viewHolder.tvLastname = (TextView)rowView.findViewById(R.id.lastnames);
            viewHolder.tvVaccine = (TextView)rowView.findViewById(R.id.vaccines);

            rowView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.tvName.setText(item.getName());
        viewHolder.tvName.setTypeface(BackboneActivity.Rosario_Regular);
        viewHolder.tvLastname.setText(item.getOutreach());
        viewHolder.tvLastname.setTypeface(BackboneActivity.Rosario_Regular);
        viewHolder.tvVaccine.setTypeface(BackboneActivity.Rosario_Regular);
        try {
            viewHolder.tvVaccine.setText(item.getVaccine());
        }catch (Exception e){e.printStackTrace();}

        return rowView;
    }

}
