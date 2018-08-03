package br.com.vostre.circular.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.joda.time.DateTime;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.StringUtils;

public class ParadasViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<CidadeEstado> cidade;

    public LiveData<List<CidadeEstado>> cidades;

    public LiveData<List<ParadaBairro>> paradas;

    public BairroCidade bairro;

    ParadaBairro parada;

    public Bitmap foto;

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public ParadaBairro getParada() {
        return parada;
    }

    public void setParada(ParadaBairro parada) {
        this.parada = parada;
        foto = BitmapFactory.decodeFile(parada.getParada().getImagem());
    }

    public LiveData<CidadeEstado> getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = appDatabase.cidadeDAO().carregar(cidade);
        paradas = appDatabase.paradaDAO().listarTodosComBairroPorCidade(cidade);
    }

    public ParadasViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        cidades = appDatabase.cidadeDAO().listarTodosAtivasComEstado();
        cidade = appDatabase.cidadeDAO().carregar("");
        paradas = appDatabase.paradaDAO().listarTodosComBairroPorCidade("");
    }

    public void salvarParada(){

        parada.getParada().setBairro(bairro.getBairro().getId());

        if(foto != null){
            salvarFoto();
        }

        if(parada.getParada().valida(parada.getParada())){
            add(parada.getParada());
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarParada(){

        if(bairro != null){
            parada.getParada().setBairro(bairro.getBairro().getId());
        }

        if(foto != null){
            salvarFoto();
        }

        if(parada.getParada().valida(parada.getParada())){
            edit(parada.getParada());
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    private void salvarFoto() {
        FileOutputStream fos = null;
        File file = new File(getApplication().getFilesDir(),  UUID.randomUUID().toString()+".png");

        try {
            fos = new FileOutputStream(file);
            foto.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();

                    if(parada.getParada().getImagem() != null && !parada.getParada().getImagem().isEmpty()){
                        File fotoAntiga = new File(getApplication().getFilesDir(), parada.getParada().getImagem());

                        if(fotoAntiga.exists() && fotoAntiga.canWrite() && fotoAntiga.getName() != file.getName()){
                            fotoAntiga.delete();
                        }
                    }

                    parada.getParada().setImagem(file.getName());
                    parada.getParada().setImagemEnviada(false);
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

    public void add(final Parada parada) {

        parada.setDataCadastro(new DateTime());
        parada.setUltimaAlteracao(new DateTime());
        parada.setEnviado(false);
        parada.setSlug(StringUtils.toSlug(parada.getNome()));

        // se bairro relacionado estiver programado para data apos a programacao da parada,
        // altera a data de programacao da parada para ficar igual e evitar erros de
        // registro nao encontrado
        if((parada.getProgramadoPara() != null && parada.getProgramadoPara() == null) ||
                (bairro.getBairro().getProgramadoPara() != null && parada.getProgramadoPara() != null
                        && bairro.getBairro().getProgramadoPara().isAfter(parada.getProgramadoPara()))){
            parada.setProgramadoPara(bairro.getBairro().getProgramadoPara());
        }

        parada.setBairro(bairro.getBairro().getId());

        new addAsyncTask(appDatabase).execute(parada);
    }

    private static class addAsyncTask extends AsyncTask<Parada, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Parada... params) {
            db.paradaDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    // editar

    public static void edit(final Parada parada, Context context) {

        if(appDatabase == null){
            appDatabase = AppDatabase.getAppDatabase(context.getApplicationContext());
        }

        new editAsyncTask(appDatabase).execute(parada);
    }

    public void edit(final Parada parada) {

        parada.setUltimaAlteracao(new DateTime());
        parada.setEnviado(false);
        parada.setSlug(StringUtils.toSlug(parada.getNome()));

        // se bairro relacionado estiver programado para data apos a programacao da parada,
        // altera a data de programacao da parada para ficar igual e evitar erros de
        // registro nao encontrado
        if(bairro != null && ((bairro.getBairro().getProgramadoPara() != null && parada.getProgramadoPara() == null) ||
                (bairro.getBairro().getProgramadoPara() != null && parada.getProgramadoPara() != null
                        && bairro.getBairro().getProgramadoPara().isAfter(parada.getProgramadoPara())))){
            parada.setProgramadoPara(bairro.getBairro().getProgramadoPara());
        }

        new editAsyncTask(appDatabase).execute(parada);
    }

    private static class editAsyncTask extends AsyncTask<Parada, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Parada... params) {
            db.paradaDAO().editar((params[0]));
            return null;
        }

    }

    // fim editar

}
