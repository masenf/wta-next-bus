package com.masenf.wtaandroid.async;

import java.util.ArrayList;

import com.masenf.core.async.DataWriteTask;
import com.masenf.core.async.callbacks.DataWriteCallback;
import com.masenf.core.data.DataUpdateQuery;
import com.masenf.wtaandroid.data.WtaDatastore;
import com.masenf.wtaandroid.data.WtaDatastore.TagEntryType;

public class DataWriteTaskFactory {
	
	private DataWriteCallback cb;
	private WtaDatastore d;
	
	public DataWriteTaskFactory(DataWriteCallback cb) {
		this.cb = cb;
		this.d = WtaDatastore.getWritableInstance();
	}
	public void close() {
		d.close();
	}
	public DataWriteTask setAlias(final int stop_id, final String alias) {
		return postQuerySet(new DataUpdateQuery() {
			@Override
			public ArrayList<Long> execute() {
				ArrayList<Long> a = new ArrayList<Long>();
				a.add((long) d.setAlias(stop_id, alias));
				return a;
			}
		});
	}
	public DataWriteTask renameTag(final int tag_id, final String newname) {
		return postQuerySet(new DataUpdateQuery() {
			@Override
			public ArrayList<Long> execute() {
				ArrayList<Long> a = new ArrayList<Long>();
				a.add((long) d.renameTag(tag_id, newname));
				return a;
			}
		});
	}
	public DataWriteTask addRecent(final int stop_id, final String name) {
		return postQuerySet(addLocation(WtaDatastore.TAG_RECENT, stop_id, name, null));
	}
	public DataWriteTask rmFavorite(int stop_id) {
		return postQuerySet(rmTagFromLocation(stop_id, WtaDatastore.TAG_FAVORITES));
	}
	public DataWriteTask addFavorite(int stop_id, String name) {
		return postQuerySet(addLocation(WtaDatastore.TAG_FAVORITES, stop_id, name, null));
	}
	
	public DataWriteTask postQuerySet(DataUpdateQuery... q) {
		DataWriteTask t = new DataWriteTask(cb);
		// try to enforce sequential writes
		t.execute(q);
		return t;
	}
	public DataUpdateQuery addLocation(final String tag, final int stop_id, final String name, final String alias) {
		return new DataUpdateQuery() {
			@Override
			public ArrayList<Long> execute() {
				ArrayList<Long> a = new ArrayList<Long>();
				a.add(d.addLocation(tag, stop_id, name, alias));
				return a;
			}
		};
	}
	public DataUpdateQuery setTag(final long fk, final String tag, final TagEntryType type, final int sort_order) {
		return new DataUpdateQuery() {
			@Override
			public ArrayList<Long> execute() {
				ArrayList<Long> a = new ArrayList<Long>();
				a.add(d.setTag(fk, tag, type, sort_order));
				return a;
			}
		};
	}
	public DataUpdateQuery addTagToLocation(final long _id, final String tag) {
		return setTag(_id, tag, TagEntryType.STOP, 10);
	}
	public DataUpdateQuery rmTagFromLocation(final int stop_id, final String tag) {
		return new DataUpdateQuery() {
			@Override
			public ArrayList<Long> execute() {
				ArrayList<Long> a = new ArrayList<Long>();
				a.add(d.rmTagFromLocation(stop_id, tag));
				return a;
			}
		};
	}
}
