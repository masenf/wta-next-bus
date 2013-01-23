package com.masenf.wtaandroid.async;

import java.util.ArrayList;
import java.util.UUID;

import com.masenf.wtaandroid.async.callbacks.DataReadCallback;
import com.masenf.wtaandroid.data.BaseEntry;
import com.masenf.wtaandroid.data.DataQuery;
import com.masenf.wtaandroid.data.EntryList;
import com.masenf.wtaandroid.progress.IProgressManager;

public class DataReadTask extends BaseTask<DataQuery, EntryList> {

	private DataReadCallback cb;
	public DataReadTask(DataReadCallback cb, IProgressManager pg) {
		this.cb = cb;
		if (pg != null)
			setProgressManager(pg, UUID.randomUUID().toString());
	}
	@Override
	protected EntryList doInBackground(DataQuery... params) {
		EntryList result = new EntryList();
		// a speedup loop
		postProgressMax(50000);
		for (int i=0;i<50000;i++) {
			UUID.randomUUID();
			postProgress(i);
		}
		postProgressMax(params.length);
		for (int i=0;i<params.length;i++)
		{
			DataQuery q = params[i];
			ArrayList<BaseEntry> intermediate = q.execute();
			if (intermediate != null)
				result.addAll(intermediate);
			if (params.length > 1)
				postProgress(i);
		}
		return result;
	}
	@Override
	protected void onPostExecute(EntryList result) {
		cb.updateData(result);
		super.onPostExecute(result);
	}
}
