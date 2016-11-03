package com.example.diaryapp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DiaryListAdapter extends ArrayAdapter<ModelDiary> {

	private Context context;
	private List<ModelDiary> diaryListItems;
	
	public DiaryListAdapter(Context context, List<ModelDiary> diaryListItems) {
		super(context, R.layout.activity_list_items, diaryListItems);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.diaryListItems = diaryListItems;
	}
	
	@Override
	public void add(ModelDiary diary) {
		// TODO Auto-generated method stub
		diaryListItems.add(diary);
		notifyDataSetChanged();
		super.add(diary);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return diaryListItems.size();
	}

	@Override
	public ModelDiary getItem(int position) {
		// TODO Auto-generated method stub
		return diaryListItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void remove(ModelDiary diary) {
		// TODO Auto-generated method stub
		diaryListItems.remove(diary);
		notifyDataSetChanged();
		super.remove(diary);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			
			convertView = inflater.inflate(R.layout.activity_list_items, null);
			viewHolder = new ViewHolder();
			
			viewHolder.textDescription = (TextView)convertView.findViewById(R.id.list_item_descriptionTextView);
			viewHolder.textMood = (TextView)convertView.findViewById(R.id.list_item_moodTextView);
			viewHolder.textLocation = (TextView)convertView.findViewById(R.id.list_item_locationTextView);
			viewHolder.textDate = (TextView)convertView.findViewById(R.id.list_item_dateTextView);
			
			convertView.setTag(viewHolder);
			
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		ModelDiary mDiary = (ModelDiary) getItem(position);
		viewHolder.textDescription.setText(mDiary.getDescription() + "");
		viewHolder.textMood.setText(mDiary.getMood().getMood());
		viewHolder.textLocation.setText("Location: " + mDiary.getAddress() + ", " + mDiary.getCity());
		viewHolder.textDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(mDiary.getDate()));
		
		
		return convertView;
	}
	
	private class ViewHolder {
		TextView textDescription;
		TextView textMood;
		TextView textLocation;
		TextView textDate;
	}
}
