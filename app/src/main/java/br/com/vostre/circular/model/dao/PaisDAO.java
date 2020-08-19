package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Pais;

@Dao
public interface PaisDAO {

    @Query("SELECT * FROM pais ORDER BY nome")
    LiveData<List<Pais>> listarTodos();

    @Query("SELECT * FROM pais ORDER BY nome")
    List<Pais> listarTodosSync();

    @Query("SELECT * FROM pais WHERE ativo = 1 ORDER BY nome")
    LiveData<List<Pais>> listarTodosAtivos();

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Pais pais);

    @Update
    void editar(Pais pais);

    @Delete
    void deletar(Pais pais);

    @Query("DELETE FROM pais")
    void deletarTodos();

    @Query("DELETE FROM pais WHERE ativo = 0")
    void deletarInativos();

}
