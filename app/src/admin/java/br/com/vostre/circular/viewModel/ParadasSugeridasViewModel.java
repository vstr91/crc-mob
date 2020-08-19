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
import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.HistoricoParada;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;
import br.com.vostre.circular.utils.StringUtils;

public class ParadasSugeridasViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<ParadaBairro>> paradas;
    public ParadaBairro parada;

    public PontoInteresseSugestaoBairro poi;

    public LiveData<List<ParadaSugestaoBairro>> sugeridas;
    public ParadaSugestaoBairro sugestao;

    public LiveData<List<ParadaSugestaoBairro>> aceitas;
    public LiveData<List<ParadaSugestaoBairro>> rejeitadas;

    public LiveData<List<PontoInteresseSugestaoBairro>> sugeridasPoi;
    public LiveData<List<PontoInteresseSugestaoBairro>> aceitasPoi;
    public LiveData<List<PontoInteresseSugestaoBairro>> rejeitadasPoi;

    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;
    public MutableLiveData<Location> localAtual;

    public Bitmap foto;
    public Bitmap fotoPoi;

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

    public PontoInteresseSugestaoBairro getPoi() {
        return poi;
    }

    public void setPoi(PontoInteresseSugestaoBairro poi) {
        this.poi = poi;

        fotoPoi = BitmapFactory.decodeFile(poi.getPontoInteresse().getImagem());
    }

    public ParadasSugeridasViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        parada = new ParadaBairro();
        paradas = appDatabase.paradaDAO().listarTodosComBairro();

        sugestao = new ParadaSugestaoBairro();
        sugeridas = appDatabase.paradaSugestaoDAO().listarTodosPendentesComBairro();

        aceitas = appDatabase.paradaSugestaoDAO().listarTodosAceitosComBairro();
        rejeitadas = appDatabase.paradaSugestaoDAO().listarTodosRejeitadosComBairro();

        // pois
        poi = new PontoInteresseSugestaoBairro();
        sugeridasPoi = appDatabase.pontoInteresseSugestaoDAO().listarTodosPendentesComBairro();
        aceitasPoi = appDatabase.pontoInteresseSugestaoDAO().listarTodosAceitosComBairro();
        rejeitadasPoi = appDatabase.pontoInteresseSugestaoDAO().listarTodosRejeitadosComBairro();

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

                if(p.getParada().getImagem() != null){
                    parada.setImagem(p.getParada().getImagem());
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

    public void aceitaSugestaoPoi(final PontoInteresseSugestaoBairro p){

        if(p.getPontoInteresse().getImagem() != null && !p.getPontoInteresse().getImagem().isEmpty()){
            fotoPoi = BitmapFactory.decodeFile(p.getPontoInteresse().getImagem());
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                PontoInteresse parada;

                // checa se existe poi vinculado
                if(p.getPontoInteresse().getPontoInteresse() != null && !p.getPontoInteresse().getPontoInteresse().isEmpty()){
                    parada = new PontoInteresse();
                    parada.setId(p.getPontoInteresse().getPontoInteresse());
                    parada = appDatabase.pontoInteresseDAO().carregarSync(parada.getId());
                } else{
                    parada = new PontoInteresse();
                }
                // fim da checagem


                // altera apenas os dados informados para evitar nulos e inconsistencias
                if(p.getPontoInteresse().getNome() != null && !p.getPontoInteresse().getNome().isEmpty()){
                    parada.setNome(p.getPontoInteresse().getNome());
                }

                if(p.getPontoInteresse().getBairro() != null && !p.getPontoInteresse().getBairro().isEmpty()){
                    parada.setBairro(p.getPontoInteresse().getBairro());
                }

                if(p.getPontoInteresse().getLatitude() != null){
                    parada.setLatitude(p.getPontoInteresse().getLatitude());
                }

                if(p.getPontoInteresse().getLongitude() != null){
                    parada.setLongitude(p.getPontoInteresse().getLongitude());
                }

                if(p.getPontoInteresse().getDescricao() != null){
                    parada.setDescricao(p.getPontoInteresse().getDescricao());
                }

                if(p.getPontoInteresse().getImagem() != null){
                    parada.setImagem(p.getPontoInteresse().getImagem());
                }

                if(p.getPontoInteresse().getDataInicial() != null){
                    parada.setDataInicial(p.getPontoInteresse().getDataInicial());
                }

                if(p.getPontoInteresse().getDataFinal() != null){
                    parada.setDataFinal(p.getPontoInteresse().getDataFinal());
                }

                parada.setPermanente(true);

                if(parada.valida(parada)){

                    p.getPontoInteresse().setStatus(1);
                    p.getPontoInteresse().setEnviado(false);
                    p.getPontoInteresse().setUltimaAlteracao(DateTime.now());
                    appDatabase.pontoInteresseSugestaoDAO().editar(p.getPontoInteresse());

                    if(parada.getDataCadastro() != null){
                        parada.setUsuarioUltimaAlteracao(p.getPontoInteresse().getUsuarioUltimaAlteracao());
                        editPoi(parada);
                    } else{
                        parada.setUsuarioCadastro(p.getPontoInteresse().getUsuarioCadastro());
                        addPoi(parada);
                    }

                    retorno.postValue(1);

                } else{
                    retorno.postValue(0);
                }

            }
        });


    }

    public void rejeitaSugestaoPoi(final PontoInteresseSugestaoBairro p){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                p.getPontoInteresse().setStatus(2);
                p.getPontoInteresse().setEnviado(false);
                p.getPontoInteresse().setUltimaAlteracao(DateTime.now());
                appDatabase.pontoInteresseSugestaoDAO().editar(p.getPontoInteresse());
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

    private void salvarFotoPoi() {
        FileOutputStream fos = null;
        File file = new File(getApplication().getFilesDir(),  UUID.randomUUID().toString()+".png");

        try {
            fos = new FileOutputStream(file);
            fotoPoi.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();

                    if(poi.getPontoInteresse().getImagem() != null && !poi.getPontoInteresse().getImagem().isEmpty()){
                        File fotoAntiga = new File(getApplication().getFilesDir(), poi.getPontoInteresse().getImagem());

                        if(fotoAntiga.exists() && fotoAntiga.canWrite() && fotoAntiga.getName() != file.getName()){
                            fotoAntiga.delete();
                        }
                    }

                    poi.getPontoInteresse().setImagem(file.getName());
                    poi.getPontoInteresse().setImagemEnviada(false);
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

        if(foto != null){
            salvarFoto();
        }

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

        if(foto != null){
            salvarFoto();
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

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim editar

    // adicionar poi

    public void addPoi(final PontoInteresse parada) {

        parada.setDataCadastro(new DateTime());
        parada.setUltimaAlteracao(new DateTime());
        parada.setEnviado(false);
        parada.setSlug(StringUtils.toSlug(parada.getNome()));

        if(fotoPoi != null){
            salvarFotoPoi();
        }

        new addPoiAsyncTask(appDatabase).execute(parada);
    }

    private static class addPoiAsyncTask extends AsyncTask<PontoInteresse, Void, Void> {

        private AppDatabase db;

        addPoiAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final PontoInteresse... params) {
            db.pontoInteresseDAO().inserir((params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            retorno.setValue(1);
        }

    }

    // fim adicionar

    // editar

    public static void editPoi(final PontoInteresse parada, Context context) {

        if(appDatabase == null){
            appDatabase = AppDatabase.getAppDatabase(context.getApplicationContext());
        }

        new editPoiAsyncTask(appDatabase).execute(parada);
    }

    public void editPoi(final PontoInteresse parada) {

        parada.setUltimaAlteracao(new DateTime());
        parada.setEnviado(false);
        parada.setSlug(StringUtils.toSlug(parada.getNome()));

        if(fotoPoi != null){
            salvarFotoPoi();
        }

        new editPoiAsyncTask(appDatabase).execute(parada);
    }

    private static class editPoiAsyncTask extends AsyncTask<PontoInteresse, Void, Void> {

        private AppDatabase db;

        editPoiAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final PontoInteresse... params) {
            db.pontoInteresseDAO().editar((params[0]));
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
