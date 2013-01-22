package com.masenf.wtaandroid.async;

import com.masenf.wtaandroid.IGlobalProgress;
import com.masenf.wtaandroid.async.callbacks.DataReadCallback;
import com.masenf.wtaandroid.data.DataQuery;
import com.masenf.wtaandroid.data.EntryList;
import com.masenf.wtaandroid.data.EntryListFactory;
import com.masenf.wtaandroid.data.WtaDatastore;

public class DataReadTaskFactory {
	
	private DataReadCallback cb;
	private IGlobalProgress pg;
	private WtaDatastore d;
	private EntryListFactory ef;
	public DataReadTaskFactory(WtaDatastore d, DataReadCallback cb, IGlobalProgress pg) {
		this.cb = cb;
		this.pg = pg;
		this.d = d;
		this.ef = new EntryListFactory();
	}
	public DataReadTask getTagsAndLocations(final String tag) {
		DataReadTask t = new DataReadTask(cb, pg);
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
	
	/*
	public DataReadTask getStop(int stop_id)
	public DataReadTask getFavorites();
	public DataReadTask isFavorite(int stop_id);
	public DataReadTask getRecent();
	*/
}
