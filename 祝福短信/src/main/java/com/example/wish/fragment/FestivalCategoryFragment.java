package com.example.wish.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.example.wish.ChooseMsgActivity;
import com.example.wish.R;
import com.example.wish.bean.Festival;
import com.example.wish.bean.FestivalLab;

/**
 * Created by huanhuan on 2016/5/15.
 */
public class FestivalCategoryFragment extends Fragment {
    private GridView mGridView;
    private ArrayAdapter<Festival> mAdapter;
    private LayoutInflater mInflater;
    public static final String ID_FESTIVAL="festival_id";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_festival_category,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mInflater=LayoutInflater.from(getActivity());
        mGridView=(GridView)view.findViewById(R.id.id_gv_fragement_festival);
        mGridView.setAdapter(mAdapter =new ArrayAdapter<Festival>(getActivity(),-1, FestivalLab.getInstance().getmFestivals()){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if(convertView==null){
                    convertView=mInflater.inflate(R.layout.item_festival,parent,false);
                }
                TextView tv = (TextView)convertView.findViewById(R.id.id_item_festival);
                tv.setText(getItem(position).getName());

                return convertView;
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChooseMsgActivity.class);
                intent.putExtra(ID_FESTIVAL,mAdapter.getItem(position).getId());
                startActivity(intent);
            }
        });
    }
}
