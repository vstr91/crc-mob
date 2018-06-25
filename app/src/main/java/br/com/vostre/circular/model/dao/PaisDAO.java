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

@Dao
public interface PaisDAO {

    @Query("SELECT * FROM pais")
    LiveData<List<Pais>> listarTodos();

    @Query("SELECT * FROM pais WHERE ativo = 1")
    List<Pais> listarTodosAtivos();

    @Query("SELECT * FROM pais WHERE enviado = 0")
    List<Pais> listarTodosAEnviar();

    @Query("SELECT * FROM pais WHERE id IN (:ids)")
    List<Pais> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM pais WHERE id = :id")
    LiveData<Pais> carregarPorId(String id);

    @Query("SELECT * FROM pais WHERE nome LIKE :nome LIMIT 1")
    Pais encontrarPorNome(String nome);

    @Query("SELECT * FROM pais WHERE sigla LIKE :sigla LIMIT 1")
    Pais encontrarPorSigla(String sigla);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Pais> paises);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserir(Pais pais);

    @Update
    void editar(Pais pais);

    @Delete
    void deletar(Pais pais);

    @Query("DELETE FROM pais")
    void deletarTodos();

}
