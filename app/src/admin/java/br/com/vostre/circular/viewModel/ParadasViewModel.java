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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.Parada;
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

public class ParadasViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<ParadaBairro>> paradas;
    public ParadaBairro parada;

    public LiveData<List<BairroCidade>> bairros;
    public BairroCidade bairro;

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

    public LiveData<List<BairroCidade>> getBairros() {
        return bairros;
    }

    public void setBairros(LiveData<List<BairroCidade>> bairros) {
        this.bairros = bairros;
    }

    public BairroCidade getBairro() {
        return bairro;
    }

    public void setBairro(BairroCidade bairro) {
        this.bairro = bairro;
    }

    public ParadasViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        parada = new ParadaBairro();
        paradas = appDatabase.paradaDAO().listarTodosComBairro();

        bairros = new MutableLiveData<>();
        bairros = appDatabase.bairroDAO().listarTodosComCidade();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());
        localAtual = new MutableLiveData<>();
        localAtual.setValue(new Location(LocationManager.GPS_PROVIDER));

        retorno = new MutableLiveData<>();
        retorno.setValue(-1);
    }

    public void salvarParada(){

        parada.getParada().setBairro(bairro.getBairro().getId());

        if(foto != null){
            salvarFoto();
        }

        if(parada.getParada().valida(parada.getParada())){
            add(parada.getParada());
        } else{
            retorno.setValue(0);
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

    public void buscarRua(final Parada parada){
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("https://nominatim.openstreetmap.org/")
                .build();

        CircularAPI api = retrofit.create(CircularAPI.class);

        Call<String> call = api.carregaRua("json", parada.getLatitude(), parada.getLongitude(), 18, 3);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {
                    JSONObject obj = new JSONObject(response.body());

                    if(obj != null){
                        JSONObject dadosRua = obj.getJSONObject("address");

                        String rua = dadosRua.optString("road", "");
                        String cep = dadosRua.optString("postcode", "");

                        System.out.println("RUA: "+rua+", "+cep);

                        parada.setRua(rua);
                        parada.setCep(cep);

                        edit(parada);

                    } else{
                        System.out.println("RUA: ERRO > "+response.code());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplication().getApplicationContext(), "Erro ao buscar rua. "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
