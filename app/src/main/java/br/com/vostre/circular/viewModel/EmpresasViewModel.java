package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.model.dao.PaisDAO;
import br.com.vostre.circular.utils.StringUtils;

public class EmpresasViewModel extends AndroidViewModel {

    private static AppDatabase appDatabase;

    public LiveData<List<Empresa>> empresas;
    public Empresa empresa;

    public Bitmap logo;

    public LiveData<List<Empresa>> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(LiveData<List<Empresa>> empresas) {
        this.empresas = empresas;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }

    public EmpresasViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        empresa = new Empresa();
        empresas = appDatabase.empresaDAO().listarTodos();
    }

    public void salvarEmpresa(){

        if(logo != null){
            salvarLogo();
        }

        if(empresa.valida(empresa)){
            add(empresa);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarEmpresa(){

        if(logo != null){
            salvarLogo();
        }

        if(empresa.valida(empresa)){
            edit(empresa);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    private void salvarLogo() {
        FileOutputStream fos = null;
        File file = new File(getApplication().getFilesDir(),  UUID.randomUUID().toString()+".png");

        try {
            fos = new FileOutputStream(file);
            logo.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();

                    if(empresa.getLogo() != null && !empresa.getLogo().isEmpty()){
                        File logoAntiga = new File(getApplication().getFilesDir(), empresa.getLogo());

                        if(logoAntiga.exists() && logoAntiga.canWrite() && logoAntiga.getName() != file.getName()){
                            logoAntiga.delete();
                        }
                    }

                    empresa.setLogo(file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // adicionar

    public void add(final Empresa empresa) {

        empresa.setDataCadastro(new DateTime());
        empresa.setUltimaAlteracao(new DateTime());
        empresa.setEnviado(false);
        empresa.setSlug(StringUtils.toSlug(empresa.getNome()));

        new addAsyncTask(appDatabase).execute(empresa);
    }

    private static class addAsyncTask extends AsyncTask<Empresa, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Empresa... params) {
            db.empresaDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    // editar

    public static void editEmpresa(final Empresa empresa) {

        new editEmpresaAsyncTask(appDatabase).execute(empresa);
    }

    private static class editEmpresaAsyncTask extends AsyncTask<Empresa, Void, Void> {

        private AppDatabase db;

        editEmpresaAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Empresa... params) {
            db.empresaDAO().editar((params[0]));
            return null;
        }

    }

    public void edit(final Empresa empresa) {

        empresa.setUltimaAlteracao(new DateTime());
        empresa.setEnviado(false);
        empresa.setSlug(StringUtils.toSlug(empresa.getNome()));

        new editAsyncTask(appDatabase).execute(empresa);
    }

    private static class editAsyncTask extends AsyncTask<Empresa, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Empresa... params) {
            db.empresaDAO().editar((params[0]));
            return null;
        }

    }

    // fim editar

}
