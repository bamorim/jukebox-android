package com.pytera.jukebox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bamorim on 6/11/13.
 */
public class ExplorerListAdapter extends BaseAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    private ArrayList<Object> mData = new ArrayList<Object>();
    private LayoutInflater mInflater;

    public ExplorerListAdapter(Context context) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void addItem(final Path item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSeparator(String name) {
        mData.add(name);
        notifyDataSetChanged();
    }

    public void clear(){
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
       return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean isEnabled(int i){
        return (getItem(i).getClass() != String.class);
    }

    @Override
    public int getItemViewType(int i) {
        return (getItem(i).getClass() == String.class) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Object item = getItem(i);
        ViewHolder holder = null;
        if (view == null) {
            if(item.getClass() == String.class)
                view = (View) mInflater.inflate(R.layout.list_separator,viewGroup,false);
            else
                view = (View) mInflater.inflate(R.layout.simplerow,viewGroup,false);
            holder = new ViewHolder();
            holder.textView = (TextView) view.findViewById(R.id.rowTextView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(item.getClass() == String.class)
            holder.textView.setText((String) item);
        else
            holder.textView.setText(((Path) item).getName());
        return view;
    }
    public static class ViewHolder {
        public TextView textView;
    }
}
