package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.pojo.CidadeEstado;

@Dao
public interface EmpresaDAO {

    @Query("SELECT * FROM empresa ORDER BY nome")
    LiveData<List<Empresa>> listarTodos();

    @Query("SELECT * FROM empresa ORDER BY nome")
    List<Empresa> listarTodosSync();

    @Query("SELECT * FROM empresa WHERE ativo = 1 ORDER BY nome")
    LiveData<List<Empresa>> listarTodosAtivos();

    @Query("SELECT * FROM empresa WHERE enviado = 0")
    List<Empresa> listarTodosAEnviar();

    @Query("SELECT * FROM empresa WHERE imagemEnviada = 0 AND logo IS NOT NULL")
    List<Empresa> listarTodosImagemAEnviar();

    @Query("SELECT * FROM empresa WHERE id = :id")
    LiveData<Empresa> carregar(String id);

    @Query("SELECT * FROM empresa WHERE id IN (:ids)")
    List<Empresa> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM empresa WHERE nome LIKE :nome LIMIT 1")
    Empresa encontrarPorNome(String nome);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Empresa> empresas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Empresa empresa);

    @Update
    void editar(Empresa empresa);

    @Delete
    void deletar(Empresa empresa);

    @Query("DELETE FROM empresa")
    void deletarTodos();

}
