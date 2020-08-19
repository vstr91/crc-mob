package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ParadaBairro;

public class QRCodeViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    public LiveData<ParadaBairro> parada;
    public MutableLiveData<Boolean> aviso;

    public QRCodeViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        this.parada = appDatabase.paradaDAO().carregarComBairroPorUFCidadeEBairro("", "", "", "");
        aviso = new MutableLiveData<>();
    }

    public void carregaParadaQRCode(String uf, String cidade, String bairro, String parada){
        this.parada = appDatabase.paradaDAO().carregarComBairroPorUFCidadeEBairro(uf.toUpperCase(), cidade, bairro, parada);
    }

}
