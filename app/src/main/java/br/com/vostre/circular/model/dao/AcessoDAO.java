package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.model.Pais;

@Dao
public interface AcessoDAO {

    @Query("SELECT * FROM acesso ORDER BY dataValidacao DESC")
    LiveData<List<Acesso>> listarTodos();

    @Query("SELECT * FROM acesso ORDER BY dataValidacao DESC")
    List<Acesso> listarTodosSync();

    @Query("SELECT COUNT(identificadorUnico) FROM acesso")
    LiveData<Integer> contarTodos();

    @Query("SELECT COUNT(identificadorUnico) FROM acesso WHERE dataValidacao ")
    LiveData<Integer> contarTodosDoDia(DateTime dia);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Acesso> acessos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Acesso acesso);

    @Update
    void editar(Acesso acesso);

    @Delete
    void deletar(Acesso acesso);

    @Query("DELETE FROM acesso")
    void deletarTodos();

}
