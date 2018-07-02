package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Estado;

@Dao
public interface EstadoDAO {

    @Query("SELECT * FROM estado")
    LiveData<List<Estado>> listarTodos();

    @Query("SELECT * FROM estado WHERE ativo = 1")
    List<Estado> listarTodosAtivos();

    @Query("SELECT * FROM estado WHERE enviado = 0")
    List<Estado> listarTodosAEnviar();

    @Query("SELECT * FROM estado WHERE id IN (:ids)")
    List<Estado> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM estado WHERE nome LIKE :nome LIMIT 1")
    Estado encontrarPorNome(String nome);

    @Query("SELECT * FROM estado WHERE sigla LIKE :sigla LIMIT 1")
    Estado encontrarPorSigla(String sigla);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Estado> estadoes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Estado estado);

    @Update
    void editar(Estado estado);

    @Delete
    void deletar(Estado estado);

    @Query("DELETE FROM estado")
    void deletarTodos();

}
