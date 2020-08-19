package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.utils.StringUtils;

public class BairrosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<BairroCidade>> bairros;
    public BairroCidade bairro;

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

    public BairrosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());


    }
}
