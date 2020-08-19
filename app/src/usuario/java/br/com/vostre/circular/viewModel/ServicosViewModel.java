package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import androidx.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.Servico;
import br.com.vostre.circular.model.api.CircularAPI;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.ImageUtils;
import br.com.vostre.circular.utils.StringUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ServicosViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<Servico>> servicos;
    public Servico servico;

    public Bitmap foto;

    public static MutableLiveData<Integer> retorno;

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public void setServico(Servico servico) {
        this.servico = servico;



        foto = BitmapFactory.decodeFile(servico.getIcone());
    }

    public ServicosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        servico = new Servico();
        servicos = appDatabase.servicoDAO().listarTodos();

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void salvarServico(){

        if(foto != null){
            salvarFoto();
        }

        if(servico.valida(servico)){
            add(servico);
        } else{
            retorno.setValue(0);
        }

    }

    public void editarServico(){

        if(foto != null){
            salvarFoto();
        }

        if(servico.valida(servico)){
            edit(servico);
        } else{
            retorno.setValue(0);
        }

    }

    private void salvarFoto() {
        FileOutputStream fos = null;
        File file = new File(getApplication().getFilesDir(),  UUID.randomUUID().toString()+".png");

        try {
            fos = new FileOutputStream(file);
            foto = ImageUtils.scaleDown(foto, 600, true);
            foto.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();

                    if(servico.getIcone() != null && !servico.getIcone().isEmpty()){
                        File fotoAntiga = new File(getApplication().getFilesDir(), servico.getIcone());

                        if(fotoAntiga.exists() && fotoAntiga.canWrite() && fotoAntiga.getName() != file.getName()){
                            fotoAntiga.delete();
                        }
                    }

                    servico.setIcone(file.getName());
                    servico.setImagemEnviada(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @BindingAdapter("srcCompat")
    public static void setImagemFoto(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        } else{
            imageView.setBackgroundResource(R.mipmap.ic_onibus_azul);
        }

    }

    // adicionar

    public void add(final Servico servico) {

        servico.setDataCadastro(new DateTime());
        servico.setUltimaAlteracao(new DateTime());
        servico.setEnviado(false);
        servico.setSlug(StringUtils.toSlug(servico.getNome()));

        new addAsyncTask(appDatabase).execute(servico);
    }

    public static void addEstatico(final Servico servico, Context context) {

        servico.setDataCadastro(new DateTime());
        servico.setUltimaAlteracao(new DateTime());
        servico.setEnviado(false);
        servico.setSlug(StringUtils.toSlug(servico.getNome()));

        if(servico.valida(servico)){
            if(appDatabase == null){
                appDatabase = AppDatabase.getAppDatabase(context);
            }

            //System.out.println("PARADA: nome "+parada.getNome()+" | ativo: "+parada.getAtivo()+" | latitude: "+parada.getLatitude()+" | longitude: "+parada.getLongitude()+" | bairro: "+parada.getBairro());

            new addAsyncTask(appDatabase).execute(servico);
        } else{
            Toast.makeText(context, "O serviço "+servico.getNome()+" contém dados que precisam ser informados.", Toast.LENGTH_SHORT).show();
        }


    }

    private static class addAsyncTask extends AsyncTask<Servico, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Servico... params) {
            db.servicoDAO().inserir((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(retorno != null){
                retorno.setValue(1);
            }

        }

    }

    // fim adicionar

    // editar

    public static void edit(final Servico servico, Context context) {

        if(appDatabase == null){
            appDatabase = AppDatabase.getAppDatabase(context.getApplicationContext());
        }

        new editAsyncTask(appDatabase).execute(servico);
    }

    public void edit(final Servico servico) {

        servico.setUltimaAlteracao(new DateTime());
        servico.setEnviado(false);
        servico.setSlug(StringUtils.toSlug(servico.getNome()));

        new editAsyncTask(appDatabase).execute(servico);
    }

    private static class editAsyncTask extends AsyncTask<Servico, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Servico... params) {
            db.servicoDAO().editar((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(retorno != null){
                retorno.setValue(1);
            }

        }

    }

    // fim editar

}
