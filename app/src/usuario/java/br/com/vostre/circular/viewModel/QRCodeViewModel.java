package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.ParadaBairro;

public class QRCodeViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    public LiveData<ParadaBairro> parada;

    public QRCodeViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        this.parada = appDatabase.paradaDAO().carregarComBairroPorUFCidadeEBairro("", "", "", "");
    }

    public void carregaParadaQRCode(String uf, String cidade, String bairro, String parada){
        this.parada = appDatabase.paradaDAO().carregarComBairroPorUFCidadeEBairro(uf.toUpperCase(), cidade, bairro, parada);
    }

}
