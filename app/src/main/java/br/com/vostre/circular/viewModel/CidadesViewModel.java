package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.CidadeDAO;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.utils.StringUtils;

public class CidadesViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<CidadeEstado>> cidades;
    public CidadeEstado cidade;

    public LiveData<List<Estado>> estados;
    public Estado estado;

    public Bitmap brasao;

    public Bitmap getBrasao() {
        return brasao;
    }

    public void setBrasao(Bitmap brasao) {
        this.brasao = brasao;
    }

    public LiveData<List<CidadeEstado>> getCidades() {
        return cidades;
    }

    public void setCidades(LiveData<List<CidadeEstado>> cidades) {
        this.cidades = cidades;
    }

    public CidadeEstado getCidade() {
        return cidade;
    }

    public void setCidade(CidadeEstado cidade) {
        this.cidade = cidade;
        brasao = BitmapFactory.decodeFile(cidade.getCidade().getBrasao());
    }

    public CidadesViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        cidade = new CidadeEstado();
        cidades = appDatabase.cidadeDAO().listarTodosComEstado();

        estados = new MutableLiveData<>();
        estados = appDatabase.estadoDAO().listarTodos();

    }

    public void salvarCidade(){

        cidade.getCidade().setEstado(estado.getId());

        if(brasao != null){
            salvarBrasao();
        }

        if(cidade.getCidade().valida(cidade.getCidade())){
            add(cidade);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    private void salvarBrasao() {
        FileOutputStream fos = null;
        File file = new File(getApplication().getFilesDir(),  UUID.randomUUID().toString()+".png");

        try {
            fos = new FileOutputStream(file);
            brasao.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();

                    if(cidade.getCidade().getBrasao() != null && !cidade.getCidade().getBrasao().isEmpty()){
                        File brasaoAntigo = new File(getApplication().getFilesDir(), cidade.getCidade().getBrasao());

                        if(brasaoAntigo.exists() && brasaoAntigo.canWrite() && brasaoAntigo.getName() != file.getName()){
                            brasaoAntigo.delete();
                        }
                    }

                    cidade.getCidade().setBrasao(file.getName());
                    cidade.getCidade().setImagemEnviada(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void editarCidade(){

        cidade.getCidade().setEstado(estado.getId());

        if(brasao != null){
            salvarBrasao();
        }

        if(cidade.getCidade().valida(cidade.getCidade())){
            edit(cidade);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    // adicionar

    public void add(final CidadeEstado cidade) {

        cidade.getCidade().setDataCadastro(new DateTime());
        cidade.getCidade().setUltimaAlteracao(new DateTime());
        cidade.getCidade().setEnviado(false);
        cidade.getCidade().setSlug(StringUtils.toSlug(cidade.getCidade().getNome()));

        // se estado relacionado estiver programado para data apos a programacao do cidade,
        // altera a data de programacao do cidade para ficar igual e evitar erros de
        // registro nao encontrado
        if((estado.getProgramadoPara() != null && cidade.getCidade().getProgramadoPara() == null) ||
                (estado.getProgramadoPara() != null && cidade.getCidade().getProgramadoPara() != null &&
                        estado.getProgramadoPara().isAfter(cidade.getCidade().getProgramadoPara()))){
            cidade.getCidade().setProgramadoPara(estado.getProgramadoPara());
        }

        cidade.getCidade().setEstado(estado.getId());

        new addAsyncTask(appDatabase).execute(cidade);
    }

    private static class addAsyncTask extends AsyncTask<CidadeEstado, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final CidadeEstado... params) {
            db.cidadeDAO().inserir((params[0].getCidade()));
            return null;
        }

    }

    // fim adicionar

    // editar

    public static void edit(final Cidade cidade) {

        new editCidadeAsyncTask(appDatabase).execute(cidade);
    }

    public void edit(final CidadeEstado cidade) {

        cidade.getCidade().setUltimaAlteracao(new DateTime());
        cidade.getCidade().setEnviado(false);
        cidade.getCidade().setSlug(StringUtils.toSlug(cidade.getCidade().getNome()));

        // se estado relacionado estiver programado para data apos a programacao do cidade,
        // altera a data de programacao do cidade para ficar igual e evitar erros de
        // registro nao encontrado
        if((estado.getProgramadoPara() != null && cidade.getCidade().getProgramadoPara() == null) ||
                (estado.getProgramadoPara() != null && cidade.getCidade().getProgramadoPara() != null &&
                        estado.getProgramadoPara().isAfter(cidade.getCidade().getProgramadoPara()))){
            cidade.getCidade().setProgramadoPara(estado.getProgramadoPara());
        }

        new editAsyncTask(appDatabase).execute(cidade);
    }

    private static class editCidadeAsyncTask extends AsyncTask<Cidade, Void, Void> {

        private AppDatabase db;

        editCidadeAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Cidade... params) {
            db.cidadeDAO().editar((params[0]));
            return null;
        }

    }

    private static class editAsyncTask extends AsyncTask<CidadeEstado, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final CidadeEstado... params) {
            db.cidadeDAO().editar((params[0].getCidade()));
            return null;
        }

    }

    // fim editar

}
