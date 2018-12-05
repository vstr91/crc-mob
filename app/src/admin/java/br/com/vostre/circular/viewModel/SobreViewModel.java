package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.utils.StringUtils;

public class SobreViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    public LiveData<ParametroInterno> parametros;

    public SobreViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        parametros = appDatabase.parametroInternoDAO().carregarInformacoes();
    }

}
