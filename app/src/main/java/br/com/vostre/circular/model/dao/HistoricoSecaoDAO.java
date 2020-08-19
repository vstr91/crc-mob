package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.HistoricoSecao;

@Dao
public interface HistoricoSecaoDAO {

    @Query("SELECT * FROM historico_secao")
    LiveData<List<HistoricoSecao>> listarTodos();

    @Query("SELECT * FROM historico_secao")
    List<HistoricoSecao> listarTodosSync();

    @Query("SELECT * FROM historico_secao WHERE enviado = 0")
    List<HistoricoSecao> listarTodosAEnviar();

    @Query("SELECT * FROM historico_secao WHERE ativo = 1")
    LiveData<List<HistoricoSecao>> listarTodosAtivos();

    @Query("SELECT * FROM historico_secao WHERE secao = :secao")
    LiveData<List<HistoricoSecao>> carregarPorSecao(String secao);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<HistoricoSecao> secoes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(HistoricoSecao secao);

    @Update
    void editar(HistoricoSecao secao);

    @Delete
    void deletar(HistoricoSecao secao);

    @Query("DELETE FROM historico_secao")
    void deletarTodos();

    @Query("DELETE FROM historico_secao WHERE ativo = 0")
    void deletarInativos();

}
