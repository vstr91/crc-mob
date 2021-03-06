package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.Parametro;

@Dao
public interface ParametroDAO {

    @Query("SELECT * FROM parametro")
    LiveData<List<Parametro>> listarTodos();

    @Query("SELECT * FROM parametro")
    List<Parametro> listarTodosSync();

    @Query("SELECT * FROM parametro WHERE ativo = 1")
    List<Parametro> listarTodosAtivos();

    @Query("SELECT * FROM parametro WHERE enviado = 0")
    List<Parametro> listarTodosAEnviar();

    @Query("SELECT * FROM parametro WHERE id IN (:ids)")
    List<Parametro> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM parametro WHERE id = :id")
    LiveData<Parametro> carregarPorId(String id);

    @Query("SELECT valor FROM parametro WHERE slug = :slug")
    String carregarPorSlug(String slug);

    @Query("SELECT * FROM parametro WHERE nome LIKE :nome LIMIT 1")
    Parametro encontrarPorNome(String nome);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Parametro> parametros);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Parametro parametro);

    @Update
    void editar(Parametro parametro);

    @Delete
    void deletar(Parametro parametro);

    @Query("DELETE FROM parametro")
    void deletarTodos();

}
