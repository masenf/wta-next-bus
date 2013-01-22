package com.masenf.wtaandroid.async;

import java.util.ArrayList;

import com.masenf.wtaandroid.IGlobalProgress;
import com.masenf.wtaandroid.async.callbacks.DataReadCallback;
import com.masenf.wtaandroid.data.BaseEntry;
import com.masenf.wtaandroid.data.DataQuery;
import com.masenf.wtaandroid.data.EntryList;

public class DataReadTask extends BaseTask<DataQuery, EntryList> {

	private DataReadCallback cb;
	public DataReadTask(DataReadCallback cb, IGlobalProgress pg) {
		this.cb = cb;
		if (pg != null)
			setGlobalProgress(pg);
	}
	@Override
	protected EntryList doInBackground(DataQuery... params) {
		EntryList result = new EntryList();
		for (int i=0;i<params.length;i++)
		{
			DataQuery q = params[i];
			ArrayList<BaseEntry> intermediate = q.execute();
			if (intermediate != null)
				result.addAll(intermediate);
			if (params.length > 1)
				publishProgress(i);
		}
		return result;
	}
	@Override
	protected void onPostExecute(EntryList result) {
		cb.updateData(result);
		super.onPostExecute(result);
	}
}
