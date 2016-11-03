package com.example.diaryapp;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class ListDiaryItemsActivity extends ActionBarActivity implements OnItemClickListener, OnItemLongClickListener {
	
	private ListView diaryListView;
	private DiaryListAdapter mDiaryAdapter;
	private DBHelper db;
	private ArrayList<ModelDiary> diaryArrayList;
	private ModelDiary diary;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);
		db = new DBHelper(this);
		
		diaryListView = (ListView)findViewById(R.id.listView1);
		
		registerForContextMenu(diaryListView);
		diaryListView.setOnItemClickListener(this); 
		diaryListView.setOnItemLongClickListener(this);
		
		diaryArrayList = new ArrayList<ModelDiary>();
		diaryArrayList = db.getDiaryItems();
		
		if (diaryArrayList != null) {
			if (diaryArrayList.size() != 0) {
//				Log.e("Nikola", "size of diaryItems: " + diaryArrayList.size());
				mDiaryAdapter = new DiaryListAdapter(this, diaryArrayList);
				diaryListView.setAdapter(mDiaryAdapter);
			} else {
				Toast.makeText(this, "No Records in diary!",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_add){
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.listView1){
			getMenuInflater().inflate(R.menu.delete_list_item, menu);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		// TODO Auto-generated method stub
		diary = (ModelDiary)adapter.getItemAtPosition(position);
		if(diary != null){
			Intent intent = new Intent(this, MainActivity.class);
//			Log.e("Nikola", "sending diary object: " + diary.getId() + "\n" + diary.getMood().getMood()
//					+ "\n" + diary.getDescription() + "\n" + diary.getImagePath());
			intent.putExtra("com.example.diaryapp.selectedItem", diary);
			startActivity(intent);			
		}
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	      switch(item.getItemId()) {
	          case R.id.menu_item_delete:
	        	  diary = (ModelDiary)mDiaryAdapter.getItem(info.position);
	        	  if(diary != null){
//	        		  Log.e("Nikola", "Delete diary with id: " + diary.id);
	        		  db.deleteDiaryItem(diary);
	        		  mDiaryAdapter.remove(diary);
	        		  mDiaryAdapter.notifyDataSetChanged();	        	        		  
	        	  }
	        	  return true;
	          default:
	                return super.onContextItemSelected(item);
	      }
	}

}
