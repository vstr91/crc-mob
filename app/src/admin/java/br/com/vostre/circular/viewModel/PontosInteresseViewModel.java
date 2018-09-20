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
import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.utils.StringUtils;

public class PontosInteresseViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<PontoInteresse>> pontosInteresse;
    public PontoInteresse pontoInteresse;

    public LiveData<List<ParadaBairro>> paradas;

    public boolean centralizaMapa = true;

    public FusedLocationProviderClient mFusedLocationClient;
    public LocationCallback mLocationCallback;
    public MutableLiveData<Location> localAtual;

    public Bitmap foto;

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

    public LiveData<List<PontoInteresse>> getPontosInteresse() {
        return pontosInteresse;
    }

    public void setPontosInteresse(LiveData<List<PontoInteresse>> pontosInteresse) {
        this.pontosInteresse = pontosInteresse;
    }

    public PontoInteresse getPontoInteresse() {
        return pontoInteresse;
    }

    public void setPontoInteresse(PontoInteresse pontoInteresse) {
        this.pontoInteresse = pontoInteresse;
        foto = BitmapFactory.decodeFile(pontoInteresse.getImagem());
    }

    public LiveData<List<ParadaBairro>> getParadas() {
        return paradas;
    }

    public void setParadas(LiveData<List<ParadaBairro>> paradas) {
        this.paradas = paradas;
    }

    public PontosInteresseViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        pontoInteresse = new PontoInteresse();
        pontosInteresse = appDatabase.pontoInteresseDAO().listarTodos();
        paradas = appDatabase.paradaDAO().listarTodosAtivosComBairro();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());
        localAtual = new MutableLiveData<>();
        localAtual.setValue(new Location(LocationManager.GPS_PROVIDER));
    }

    public void salvarPontoInteresse(){

        if(foto != null){
            salvarFoto();
        }

        if(pontoInteresse.valida(pontoInteresse)){
            add(pontoInteresse);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarPontoInteresse(){

        if(foto != null){
            salvarFoto();
        }

        if(pontoInteresse.valida(pontoInteresse)){
            edit(pontoInteresse);
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

                    if(pontoInteresse.getImagem() != null && !pontoInteresse.getImagem().isEmpty()){
                        File fotoAntiga = new File(getApplication().getFilesDir(), pontoInteresse.getImagem());

                        if(fotoAntiga.exists() && fotoAntiga.canWrite() && fotoAntiga.getName() != file.getName()){
                            fotoAntiga.delete();
                        }
                    }

                    pontoInteresse.setImagem(file.getName());
                    pontoInteresse.setImagemEnviada(false);
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

    public void add(final PontoInteresse pontoInteresse) {

        pontoInteresse.setDataCadastro(new DateTime());
        pontoInteresse.setUltimaAlteracao(new DateTime());
        pontoInteresse.setEnviado(false);
        pontoInteresse.setSlug(StringUtils.toSlug(pontoInteresse.getNome()));

        if(pontoInteresse.getDataFinal().isBefore(pontoInteresse.getDataInicial()) || pontoInteresse.getDataFinal().isBeforeNow()){
            pontoInteresse.setDataFinal(null);
        }

        new addAsyncTask(appDatabase).execute(pontoInteresse);
    }

    private static class addAsyncTask extends AsyncTask<PontoInteresse, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final PontoInteresse... params) {
            db.pontoInteresseDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    // editar

    public static void edit(final PontoInteresse pontoInteresse, Context context) {

        if(appDatabase == null){
            appDatabase = AppDatabase.getAppDatabase(context.getApplicationContext());
        }

        if(pontoInteresse.getDataFinal().isBefore(pontoInteresse.getDataInicial()) || pontoInteresse.getDataFinal().isBeforeNow()){
            pontoInteresse.setDataFinal(null);
        }

        new editAsyncTask(appDatabase).execute(pontoInteresse);
    }

    public void edit(final PontoInteresse pontoInteresse) {

        pontoInteresse.setUltimaAlteracao(new DateTime());
        pontoInteresse.setEnviado(false);
        pontoInteresse.setSlug(StringUtils.toSlug(pontoInteresse.getNome()));

        new editAsyncTask(appDatabase).execute(pontoInteresse);
    }

    private static class editAsyncTask extends AsyncTask<PontoInteresse, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final PontoInteresse... params) {
            db.pontoInteresseDAO().editar((params[0]));
            return null;
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

                    if(location.getAccuracy() <= 10){
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
