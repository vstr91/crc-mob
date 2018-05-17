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
import br.com.vostre.circular.utils.StringUtils;

public class CidadesViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<Cidade>> cidades;
    public Cidade cidade;

    public LiveData<List<Estado>> estados;
    public Estado estado;

    public Bitmap brasao;

    public Bitmap getBrasao() {
        return brasao;
    }

    public void setBrasao(Bitmap brasao) {
        this.brasao = brasao;
    }

    public LiveData<List<Cidade>> getCidades() {
        return cidades;
    }

    public void setCidades(LiveData<List<Cidade>> cidades) {
        this.cidades = cidades;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
        brasao = BitmapFactory.decodeFile(cidade.getBrasao());
    }

    public CidadesViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        cidade = new Cidade();
        cidades = appDatabase.cidadeDAO().listarTodos();

        estados = new MutableLiveData<>();
        estados = appDatabase.estadoDAO().listarTodos();

    }

    public void salvarCidade(){

        cidade.setEstado(estado.getId());

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
                    cidade.setBrasao(file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }





        if(cidade.valida(cidade)){
            add(cidade);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarCidade(){

        cidade.setEstado(estado.getId());

        if(cidade.valida(cidade)){
            edit(cidade);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    // adicionar

    public void add(final Cidade cidade) {

        cidade.setDataCadastro(new DateTime());
        cidade.setUltimaAlteracao(new DateTime());
        cidade.setEnviado(false);
        cidade.setSlug(StringUtils.toSlug(cidade.getNome()));

        // se estado relacionado estiver programado para data apos a programacao do cidade,
        // altera a data de programacao do cidade para ficar igual e evitar erros de
        // registro nao encontrado
        if((estado.getProgramadoPara() != null && cidade.getProgramadoPara() == null) ||
                (estado.getProgramadoPara() != null && cidade.getProgramadoPara() != null && estado.getProgramadoPara().isAfter(cidade.getProgramadoPara()))){
            cidade.setProgramadoPara(estado.getProgramadoPara());
        }

        cidade.setEstado(estado.getId());

        new addAsyncTask(appDatabase).execute(cidade);
    }

    private static class addAsyncTask extends AsyncTask<Cidade, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Cidade... params) {
            db.cidadeDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    // editar

    public void edit(final Cidade cidade) {

        cidade.setUltimaAlteracao(new DateTime());
        cidade.setEnviado(false);
        cidade.setSlug(StringUtils.toSlug(cidade.getNome()));

        // se estado relacionado estiver programado para data apos a programacao do cidade,
        // altera a data de programacao do cidade para ficar igual e evitar erros de
        // registro nao encontrado
        if((estado.getProgramadoPara() != null && cidade.getProgramadoPara() == null) ||
                (estado.getProgramadoPara() != null && cidade.getProgramadoPara() != null && estado.getProgramadoPara().isAfter(cidade.getProgramadoPara()))){
            cidade.setProgramadoPara(estado.getProgramadoPara());
        }

        new editAsyncTask(appDatabase).execute(cidade);
    }

    private static class editAsyncTask extends AsyncTask<Cidade, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Cidade... params) {
            db.cidadeDAO().editar((params[0]));
            return null;
        }

    }

    // fim editar

}
