package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.StringUtils;

public class MapaViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<ParadaBairro>> paradas;
    public LiveData<List<ParadaSugestao>> paradasSugeridas;
    public LiveData<List<PontoInteresse>> pois;
    public MutableLiveData<Location> localAtual;
    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;

    ParadaBairro parada;
    public ParadaSugestao paradaNova;
    public Bitmap foto;
    public LiveData<List<ItinerarioPartidaDestino>> itinerarios;
    public LiveData<List<BairroCidade>> bairros;

    public Bitmap fotoParada;
    public BairroCidade bairro;

    public ParadaSugestao getParadaNova() {
        return paradaNova;
    }

    public void setParadaNova(ParadaSugestao paradaNova) {
        this.paradaNova = paradaNova;

        if(paradaNova.getImagem() != null){
            File foto = new File(getApplication().getFilesDir(), paradaNova.getImagem());

            if(foto.exists() && foto.canRead()){
                this.fotoParada = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.fotoParada = null;
        }
    }

    public Bitmap getFotoParada() {
        return fotoParada;
    }

    public void setFotoParada(Bitmap foto) {
        this.fotoParada = foto;
    }

    public ParadaBairro getParada() {
        return parada;
    }

    public void setParada(ParadaBairro parada) {
        this.parada = parada;
        itinerarios = appDatabase.itinerarioDAO().listarTodosAtivosPorParadaComBairroEHorario(parada.getParada().getId(),
                DateTimeFormat.forPattern("HH:mm:ss").print(new DateTime()));

        if(parada.getParada().getImagem() != null){
            File foto = new File(getApplication().getFilesDir(), parada.getParada().getImagem());

            if(foto.exists() && foto.canRead()){
                this.foto = BitmapFactory.decodeFile(foto.getAbsolutePath());
            }
        } else{
            this.foto = null;
        }

    }

    public void setItinerario(String itinerario) {
        this.paradas = appDatabase.paradaItinerarioDAO().listarParadasAtivasPorItinerarioComBairro(itinerario);
    }

    public LiveData<List<ParadaBairro>> getParadas() {
        return paradas;
    }

    public void setParadas(LiveData<List<ParadaBairro>> paradas) {
        this.paradas = paradas;
    }

    public MapaViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        paradas = appDatabase.paradaDAO().listarTodosAtivosComBairro();
        paradasSugeridas = appDatabase.paradaSugestaoDAO().listarTodosAtivos();
        pois = appDatabase.pontoInteresseDAO().listarTodosAtivos();
        bairros = appDatabase.bairroDAO().listarTodosComCidade();
        paradaNova = new ParadaSugestao();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());

        localAtual = new MutableLiveData<>();
        localAtual.postValue(new Location(LocationManager.GPS_PROVIDER));
    }

    public void iniciarAtualizacoesPosicao(){
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {

                    if(location.getAccuracy() <= 20){
                        localAtual.postValue(location);

                        if(localAtual.getValue() != null){
                            localAtual.getValue().setLatitude(localAtual.getValue().getLatitude());
                            localAtual.getValue().setLongitude(localAtual.getValue().getLongitude());
                        }

                    }

                }
            }
        };
    }

    public void salvarParada(){

        paradaNova.setBairro(bairro.getBairro().getId());

        if(foto != null){
            salvarFoto();
        }

        if(paradaNova.valida(paradaNova)){
            add(paradaNova);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarParada(){

        if(bairro != null){
            paradaNova.setBairro(bairro.getBairro().getId());
        }

        if(foto != null){
            salvarFoto();
        }

        if(paradaNova.valida(paradaNova)){
            edit(paradaNova);
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

    // adicionar

    public void add(final ParadaSugestao parada) {

        parada.setDataCadastro(new DateTime());
        parada.setUltimaAlteracao(new DateTime());
        parada.setEnviado(false);
        parada.setSlug(StringUtils.toSlug(parada.getNome()));

        parada.setBairro(bairro.getBairro().getId());

        new addAsyncTask(appDatabase).execute(parada);
    }

    private static class addAsyncTask extends AsyncTask<ParadaSugestao, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ParadaSugestao... params) {
            db.paradaSugestaoDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    // editar

    public static void edit(final ParadaSugestao parada, Context context) {

        if(appDatabase == null){
            appDatabase = AppDatabase.getAppDatabase(context.getApplicationContext());
        }

        new editAsyncTask(appDatabase).execute(parada);
    }

    public void edit(final ParadaSugestao parada) {

        parada.setUltimaAlteracao(new DateTime());
        parada.setEnviado(false);
        parada.setSlug(StringUtils.toSlug(parada.getNome()));

        new editAsyncTask(appDatabase).execute(parada);
    }

    private static class editAsyncTask extends AsyncTask<ParadaSugestao, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final ParadaSugestao... params) {
            db.paradaSugestaoDAO().editar((params[0]));
            return null;
        }

    }

    // fim editar

}
