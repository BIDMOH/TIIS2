package mobile.giis.app.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mobile.giis.app.HomeActivityRevised;
import mobile.giis.app.R;

/**
 * Created by issymac on 11/12/15.
 */
public class DrawerListItemsAdapter extends BaseAdapter{

    public Context context;

    LayoutInflater inflator;

    private String[] objects;


    public DrawerListItemsAdapter(Context context, String[] objects) {
        this.objects = objects;
        inflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflator.inflate(R.layout.drawer_list_item, null);

            //convertView.setTag(viewHolder);

        } else {

        }

        TextView title      = (TextView) convertView.findViewById(R.id.title);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.title_image_icon);
        title.setTypeface(HomeActivityRevised.Rosario_Regular);
        title.setText(objects[position]);

        switch(position){
            case 0:
//                imageView.setImageResource(R.mipmap.home);
                imageView.setBackgroundResource(R.drawable.title_home);
                break;
            case 1:
//                imageView.setImageResource(R.drawable.ic_social_group_add);
                imageView.setBackgroundResource(R.drawable.home_register_child_icon);
                break;
            case 2:
//                imageView.setImageResource(R.drawable.ic_device_location_searching);
                imageView.setBackgroundResource(R.drawable.search_child_button);
                break;
            case 3:
//                imageView.setImageResource(R.drawable.ic_queue);
                imageView.setBackgroundResource(R.drawable.vaccination_queue_button);
                break;
            case 4:
//                imageView.setImageResource(R.drawable.ic_editor_insert_chart);
                imageView.setBackgroundResource(R.drawable.reports_button);
                break;
            case 5:
//                imageView.setImageResource(R.drawable.ic_monthly_plan);
                imageView.setBackgroundResource(R.drawable.monthly_plan_button);
                break;
            case 6:
//                imageView.setImageResource(R.drawable.ic_stock);
                imageView.setBackgroundResource(R.drawable.stock_button);
                break;
            default:
                imageView.setBackgroundResource(R.drawable.logout_button);
        }

        return convertView;
    }

}
