package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.dao.AppDatabase;

public class SobreViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    public LiveData<ParametroInterno> parametros;

    public SobreViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        parametros = appDatabase.parametroInternoDAO().carregarInformacoes();
    }

}
