package br.com.vostre.circular.utils.tasks;

import android.content.Context;
import android.os.AsyncTask;

import br.com.vostre.circular.model.adapter.SyncAdapter;
import br.com.vostre.circular.model.dao.AppDatabase;

public class ImageDownloadAsyncTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase db;
    private String imagem;
    private Context ctx;

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public ImageDownloadAsyncTask(Context ctx, String imagem) {
        db = AppDatabase.getAppDatabase(ctx);
        this.imagem = imagem;
        this.ctx = ctx;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String baseUrl = db.parametroDAO().carregarPorSlug("servidor");

        SyncAdapter.imageDownload(baseUrl, imagem, ctx);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

}
