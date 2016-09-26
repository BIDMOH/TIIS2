package mobile.tiis.appV2.DatabaseModals;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Ilakoze on 5/01/2015.
 */
public class Modal implements Serializable {
    private static final String TAG = Modal.class.getSimpleName();
    public Modal setModel(Cursor cursor,Modal modal){
        int counter = cursor.getColumnCount();
        for(int i=0;i<counter;i++){
            String columnName=cursor.getColumnName(i);
            columnName=columnName.toLowerCase();
            StringBuilder columnNameSb = new StringBuilder();
            columnNameSb.append(columnName);
            columnNameSb.setCharAt(0, Character.toUpperCase(columnNameSb.charAt(0)));
            columnName = columnNameSb.toString();

            for(Method method: modal.getClass().getMethods())
            {
                if(method.getName().equals("set"+columnName))
                {
                    final Class<?> c = method.getParameterTypes()[0];
                    final String name = (c.isArray()? c.getComponentType() : c).getName();
                    if(name.equals("java.lang.String")) {
                        try {
                            method.invoke(modal, cursor.getString(i));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }else if(name.equals("long")){
                        try {
                            method.invoke(modal, cursor.getLong(i));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }else if (name.equals("int")){
                        try {
                            method.invoke(modal, cursor.getInt(i));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }else if (name.equals("double")){
                        try {
                            method.invoke(modal, cursor.getDouble(i));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

        }
        return modal;
    }

    public JSONObject getjson(Modal modal){
        JSONObject jsonObject = new JSONObject();
        for(Method method:modal.getClass().getMethods())
        {
            if(method.getName().startsWith("get") && !method.getName().equals("getClass")&&!method.getName().equals("getjson"))
            {
                String field = (method.getName().replaceFirst("get","")).toLowerCase();
                try {
                    Log.d("MEthod name", field);
                    jsonObject.put(field,method.invoke(modal));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
        return jsonObject;
    }
}
