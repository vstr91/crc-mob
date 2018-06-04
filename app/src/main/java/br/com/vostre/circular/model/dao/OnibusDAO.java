package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Onibus;

@Dao
public interface OnibusDAO {

    @Query("SELECT * FROM onibus")
    LiveData<List<Onibus>> listarTodos();

    @Query("SELECT * FROM onibus WHERE ativo = 1")
    LiveData<List<Onibus>> listarTodosAtivos();

    @Query("SELECT * FROM onibus WHERE ativo = 1 AND empresa = :empresa")
    LiveData<List<Onibus>> listarTodosAtivosPorEmpresa(String empresa);

    @Query("SELECT * FROM onibus WHERE id IN (:ids)")
    List<Onibus> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM onibus WHERE id = :id")
    LiveData<Onibus> carregar(String id);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserirTodos(Onibus... onibus);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserir(Onibus onibus);

    @Update
    void editar(Onibus onibus);

    @Delete
    void deletar(Onibus onibus);

}
