package com.ay.whatstheword;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author manish.s
 * 
 */
public class CategoryGrid extends ArrayAdapter<Category> {

	Context context;
	int layoutResourceId;
	ArrayList<Category> data = new ArrayList<Category>();

	public CategoryGrid(Context context, int layoutResourceId,
			ArrayList<Category> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		RecordHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RecordHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
			holder.imageItem = (ImageView) row.findViewById(R.id.item_image);

			row.setTag(holder);
		} else {
			holder = (RecordHolder) row.getTag();
		}

		Category item = data.get(position);
		holder.imageItem.setImageResource(item.getImageID());
		holder.txtTitle.setText(item.getName());
		GradientDrawable bgShape = (GradientDrawable)holder.imageItem.getBackground();
//		Shape circle = (Shape) wordView.findViewById(R.id.circle_bg);
		int parseColor = Color.parseColor(item.getColor());
		bgShape.setColor(parseColor);
		
		
		return row;

	}
	
	


	


	static class RecordHolder {
		TextView txtTitle;
		ImageView imageItem;

	}
}