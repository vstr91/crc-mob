package br.com.vostre.circular.utils.tasks;

import android.content.Context;
import android.os.AsyncTask;

import br.com.vostre.circular.model.adapter.SyncAdapter;
import br.com.vostre.circular.model.dao.AppDatabase;

public class PreferenceDownloadAsyncTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase db;
    private String id;
    private Context ctx;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PreferenceDownloadAsyncTask(Context ctx, String id) {
        db = AppDatabase.getAppDatabase(ctx);
        this.id = id;
        this.ctx = ctx;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String baseUrl = db.parametroDAO().carregarPorSlug("servidor");

        SyncAdapter.preferenceDownload(baseUrl, id, ctx);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

}
