package com.masenf.core.async;

import java.util.ArrayList;
import java.util.UUID;

import com.masenf.core.async.callbacks.DataReadCallback;
import com.masenf.core.data.BaseEntry;
import com.masenf.core.data.DataQuery;
import com.masenf.core.data.EntryList;
import com.masenf.core.progress.IProgressManager;

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
//		postProgressMax(500);
//		for (int i=0;i<500;i++) {
//			UUID.randomUUID();
//			postProgress(i);
//		}
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
