package br.com.vostre.circular.viewModel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.pojo.AcessoDia;
import br.com.vostre.circular.model.pojo.AcessoTotal;

public class AcessosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<AcessoTotal>> acessos;
    public LiveData<List<Acesso>> acessosDetalhe;
    public LiveData<List<AcessoDia>> acessosDia;

    AcessoTotal acesso;
    String dia;

    public AcessoTotal getAcesso() {
        return acesso;
    }

    public void setAcesso(AcessoTotal acesso) {
        this.acesso = acesso;
        acessosDetalhe = appDatabase.acessoDAO().listarAcessosPorIdentificadorPorDia(acesso.getIdentificadorUnico(), dia);
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public AcessosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        acessos = appDatabase.acessoDAO().contarAcessosPorIdentificadorPorDia();
        acessosDetalhe = appDatabase.acessoDAO().listarAcessosPorIdentificadorPorDia(dia);
        acessosDia = appDatabase.acessoDAO().listarAcessosPorDia();
    }

    public void carregaAcessosDia(String dia){
        acessos = appDatabase.acessoDAO().contarAcessosPorIdentificadorPorDia(dia);
    }

}
