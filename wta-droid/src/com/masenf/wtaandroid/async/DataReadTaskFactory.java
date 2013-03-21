package com.masenf.wtaandroid.async;

import java.util.ArrayList;

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
	public DataReadTaskFactory(DataReadCallback cb) {
		this.cb = cb;
		this.d = WtaDatastore.getReadableInstance();
		this.ef = new EntryListFactory();
	}
	public DataReadTask postQuerySet(DataQuery... q) {
		DataReadTask t = new DataReadTask(cb);
		// allow concurrent reads
		t.executeOnExecutor(DataReadTask.THREAD_POOL_EXECUTOR, q);
		return t;
	}
	public DataQuery getTagsAndLocations(final String tag) {
		return new DataQuery() {
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
	}
	public DataReadTask getFavorites() {
		return getContents(WtaDatastore.TAG_FAVORITES);
	}
	public DataReadTask getContents(String tag) {
		return postQuerySet(getTagsAndLocations(tag));
	}
	public DataReadTask isFavorite(final int stop_id) {
		DataQuery q = new DataQuery() {
			@Override
			public EntryList execute() {
				EntryList result = null;
				if (d.hasTag(stop_id, WtaDatastore.TAG_FAVORITES)) {
					result = ef.fromLocationCursor(d.getStop(stop_id));
					Log.v("",stop_id + " is a favorite");
				}
				return result;
			}
		};
		return postQuerySet(q);
	}
	/*
	public DataReadTask getStop(int stop_id)

	*/
}
