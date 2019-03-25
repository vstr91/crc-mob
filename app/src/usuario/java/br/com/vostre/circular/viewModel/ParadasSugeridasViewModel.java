package br.com.vostre.circular.viewModel;

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

import org.joda.time.DateTime;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.HistoricoParada;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.utils.StringUtils;

public class ParadasSugeridasViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<ParadaBairro>> paradas;
    public ParadaBairro parada;

    public LiveData<List<ParadaSugestaoBairro>> sugeridas;
    public ParadaSugestaoBairro sugestao;

    public LiveData<List<ParadaSugestaoBairro>> aceitas;
    public LiveData<List<ParadaSugestaoBairro>> rejeitadas;

    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;
    public MutableLiveData<Location> localAtual;

    public Bitmap foto;

    public static MutableLiveData<Integer> retorno;

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public GeoPoint getCentroMapa() {

        if(localAtual != null){
            return new GeoPoint(localAtual.getValue().getLatitude(), localAtual.getValue().getLongitude());
        } else{
            return null;
        }

    }

    public LiveData<List<ParadaBairro>> getParadas() {
        return paradas;
    }

    public void setParadas(LiveData<List<ParadaBairro>> paradas) {
        this.paradas = paradas;
    }

    public ParadaBairro getParada() {
        return parada;
    }

    public void setParada(ParadaBairro parada) {
        this.parada = parada;



        foto = BitmapFactory.decodeFile(parada.getParada().getImagem());
    }

    public ParadasSugeridasViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        parada = new ParadaBairro();
        paradas = appDatabase.paradaDAO().listarTodosComBairro();

        sugestao = new ParadaSugestaoBairro();

        sugeridas = appDatabase.paradaSugestaoDAO().listarTodosPendentesComBairroPorUsuario(PreferenceUtils.carregarUsuarioLogado(getApplication().getApplicationContext()));

        aceitas = appDatabase.paradaSugestaoDAO()
                .listarTodosAceitosComBairroPorUsuario(PreferenceUtils
                        .carregarUsuarioLogado(getApplication().getApplicationContext()));

//        aceitas = appDatabase.paradaSugestaoDAO()
//                .listarTodosComBairroPorUsuario(PreferenceUtils
//                        .carregarUsuarioLogado(getApplication().getApplicationContext()));

        rejeitadas = appDatabase.paradaSugestaoDAO().listarTodosRejeitadosComBairroPorUsuario(PreferenceUtils.carregarUsuarioLogado(getApplication().getApplicationContext()));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());
        localAtual = new MutableLiveData<>();
        localAtual.setValue(new Location(LocationManager.GPS_PROVIDER));

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void aceitaSugestao(final ParadaSugestaoBairro p){

        if(p.getParada().getImagem() != null && !p.getParada().getImagem().isEmpty()){
            foto = BitmapFactory.decodeFile(p.getParada().getImagem());
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                Parada parada;

                // checa se existe parada vinculada
                if(p.getParada().getParada() != null && !p.getParada().getParada().isEmpty()){
                    parada = new Parada();
                    parada.setId(p.getParada().getParada());
                    parada = appDatabase.paradaDAO().carregarSync(parada.getId());
                } else{
                    parada = new Parada();
                }
                // fim da checagem


                // altera apenas os dados informados para evitar nulos e inconsistencias
                if(p.getParada().getNome() != null && !p.getParada().getNome().isEmpty()){
                    parada.setNome(p.getParada().getNome());
                }

                if(p.getParada().getBairro() != null && !p.getParada().getBairro().isEmpty()){
                    parada.setBairro(p.getParada().getBairro());
                }

                if(p.getParada().getLatitude() != null){
                    parada.setLatitude(p.getParada().getLatitude());
                }

                if(p.getParada().getLongitude() != null){
                    parada.setLongitude(p.getParada().getLongitude());
                }

                if(p.getParada().getSentido() != -1){
                    parada.setSentido(p.getParada().getSentido());
                }

                if(p.getParada().getTaxaDeEmbarque() != null){
                    parada.setTaxaDeEmbarque(p.getParada().getTaxaDeEmbarque());
                }

                if(foto != null){
                    salvarFoto();
                }

                if(parada.valida(parada)){

                    p.getParada().setStatus(1);
                    p.getParada().setEnviado(false);
                    p.getParada().setUltimaAlteracao(DateTime.now());
                    appDatabase.paradaSugestaoDAO().editar(p.getParada());

                    if(parada.getDataCadastro() != null){
                        parada.setUsuarioUltimaAlteracao(p.getParada().getUsuarioUltimaAlteracao());
                        edit(parada);
                    } else{
                        parada.setUsuarioCadastro(p.getParada().getUsuarioCadastro());
                        add(parada);
                    }

                    gravarHistorico(p, parada);

                    retorno.postValue(1);

                } else{
                    retorno.postValue(0);
                }

            }
        });


    }

    public void rejeitaSugestao(final ParadaSugestaoBairro p){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                p.getParada().setStatus(2);
                p.getParada().setEnviado(false);
                p.getParada().setUltimaAlteracao(DateTime.now());
                appDatabase.paradaSugestaoDAO().editar(p.getParada());
                retorno.postValue(1);
            }
        });


    }

    private void gravarHistorico(ParadaSugestaoBairro sugestao, Parada parada){
        HistoricoParada hp = new HistoricoParada();
        hp.setParada(parada.getId());
        hp.setSugestao(sugestao.getParada().getId());
        hp.setAtivo(true);
        hp.setEnviado(false);
        hp.setDataCadastro(DateTime.now());
        hp.setUltimaAlteracao(DateTime.now());

        appDatabase.historicoParadaDAO().inserir(hp);
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

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
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

        @Override
        protected void onPostExecute(Void aVoid) {

            if(retorno != null){
                retorno.setValue(1);
            }

        }

    }

    // fim editar

    public void iniciarAtualizacoesPosicao(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    if(location.getAccuracy() <= 20){
                        localAtual.setValue(location);

                        if(localAtual.getValue() != null){
                            localAtual.getValue().setLatitude(localAtual.getValue().getLatitude());
                            localAtual.getValue().setLongitude(localAtual.getValue().getLongitude());
                        }

                    }

                }
            }
        };
    }

}
