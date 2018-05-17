package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Cidade;

@Dao
public interface CidadeDAO {

    @Query("SELECT * FROM cidade")
    LiveData<List<Cidade>> listarTodos();

    @Query("SELECT * FROM cidade WHERE ativo = 1")
    List<Cidade> listarTodosAtivos();

    @Query("SELECT * FROM cidade WHERE id IN (:ids)")
    List<Cidade> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM cidade WHERE nome LIKE :nome LIMIT 1")
    Cidade encontrarPorNome(String nome);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserirTodos(Cidade... cidadees);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserir(Cidade cidade);

    @Update
    void editar(Cidade cidade);

    @Delete
    void deletar(Cidade cidade);

}
