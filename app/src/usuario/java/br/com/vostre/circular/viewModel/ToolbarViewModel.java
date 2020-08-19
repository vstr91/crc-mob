package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.ParametroInterno;
import br.com.vostre.circular.model.dao.AppDatabase;

public class ToolbarViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;
    public LiveData<List<Mensagem>> mensagensNaoLidas;

    public ToolbarViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        mensagensNaoLidas = appDatabase.mensagemDAO().listarTodosNaoLidos();
    }

}
