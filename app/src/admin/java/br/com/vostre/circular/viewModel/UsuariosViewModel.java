package br.com.vostre.circular.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Parametro;
import br.com.vostre.circular.model.Usuario;
import br.com.vostre.circular.model.dao.AppDatabase;
import br.com.vostre.circular.utils.StringUtils;

public class UsuariosViewModel extends AndroidViewModel {

    private AppDatabase appDatabase;

    public LiveData<List<Usuario>> usuarios;
    public Usuario usuario;

    public LiveData<List<Usuario>> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(LiveData<List<Usuario>> usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public UsuariosViewModel(Application app){
        super(app);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
        usuario = new Usuario();
        usuarios = appDatabase.usuarioDAO().listarTodos();
    }

    public void salvarUsuario(){

        if(usuario.valida(usuario)){
            add(usuario);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    public void editarUsuario(){

        if(usuario.valida(usuario)){
            edit(usuario);
        } else{
            System.out.println("Faltou algo a ser digitado!");
        }

    }

    // adicionar

    public void add(final Usuario usuario) {

        usuario.setDataCadastro(new DateTime());
        usuario.setUltimaAlteracao(new DateTime());
        usuario.setEnviado(false);

        new addAsyncTask(appDatabase).execute(usuario);
    }

    private static class addAsyncTask extends AsyncTask<Usuario, Void, Void> {

        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Usuario... params) {
            db.usuarioDAO().inserir((params[0]));
            return null;
        }

    }

    // fim adicionar

    // editar

    public void edit(final Usuario usuario) {

        usuario.setUltimaAlteracao(new DateTime());
        usuario.setEnviado(false);

        new editAsyncTask(appDatabase).execute(usuario);
    }

    private static class editAsyncTask extends AsyncTask<Usuario, Void, Void> {

        private AppDatabase db;

        editAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Usuario... params) {
            db.usuarioDAO().editar((params[0]));
            return null;
        }

    }

    // fim editar

}
