package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.TipoProblema;

@Dao
public interface TipoProblemaDAO {

    @Query("SELECT * FROM tipo_problema ORDER BY nome")
    LiveData<List<TipoProblema>> listarTodos();

    @Query("SELECT * FROM tipo_problema ORDER BY nome")
    List<TipoProblema> listarTodosSync();

    @Query("SELECT * FROM tipo_problema WHERE ativo = 1 ORDER BY nome")
    LiveData<List<TipoProblema>> listarTodosAtivos();

    @Query("SELECT * FROM tipo_problema WHERE enviado = 0")
    List<TipoProblema> listarTodosAEnviar();

    @Query("SELECT * FROM tipo_problema WHERE id = :id")
    LiveData<TipoProblema> carregarPorId(String id);

    @Query("SELECT * FROM tipo_problema WHERE nome LIKE :nome LIMIT 1")
    TipoProblema encontrarPorNome(String nome);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<TipoProblema> tiposProblema);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(TipoProblema tipoProblema);

    @Update
    void editar(TipoProblema tipoProblema);

    @Delete
    void deletar(TipoProblema tipoProblema);

    @Query("DELETE FROM tipo_problema")
    void deletarTodos();

    @Query("DELETE FROM tipo_problema WHERE ativo = 0")
    void deletarInativos();

}
