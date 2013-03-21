package com.masenf.wtaandroid.async;

import android.util.Log;

import com.masenf.core.async.DataReadTask;
import com.masenf.core.async.callbacks.DataReadCallback;
import com.masenf.core.data.DataQuery;
import com.masenf.core.data.EntryList;
import com.masenf.wtaandroid.data.EntryListFactory;
import com.masenf.wtaandroid.data.WtaDatastore;

public class DataReadTaskFactory {
	
	private DataReadCallback cb;
	private WtaDatastore d;
	private EntryListFactory ef;
	public DataReadTaskFactory(WtaDatastore d, DataReadCallback cb) {
		this.cb = cb;
		this.d = d;
		this.ef = new EntryListFactory();
	}
	public DataReadTask getTagsAndLocations(final String tag) {
		DataReadTask t = new DataReadTask(cb);
		DataQuery q = new DataQuery() {
			@Override
			public EntryList execute() {
				EntryList tags = ef.fromTagCursor(d.getSubTags(tag));
				EntryList locations = ef.fromLocationCursor(d.getLocations(tag));
				EntryList result;
				if (tags == null)
					result = locations;
				else if (locations == null)
					result = tags;
				else {
					tags.addAll(locations);
					result = tags;
				}
				return result;
			}
		};
		t.execute(q);
		return t;
	}
	public DataReadTask getFavorites() {
		return getTagsAndLocations(WtaDatastore.TAG_FAVORITES);
	}
	public DataReadTask isFavorite(final int stop_id) {
		DataReadTask t = new DataReadTask(cb);
		DataQuery q = new DataQuery() {
			@Override
			public EntryList execute() {
				EntryList result = null;
				if (d.isFavorite(stop_id)) {
					result = ef.fromLocationCursor(d.getStop(stop_id));
					Log.v("",stop_id + " is a favorite");
				}
				return result;
			}
		};
		t.execute(q);
		return t;
	}
	/*
	public DataReadTask getStop(int stop_id)

	*/
}
