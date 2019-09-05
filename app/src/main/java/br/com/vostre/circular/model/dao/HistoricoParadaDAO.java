package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.HistoricoParada;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;

@Dao
public interface HistoricoParadaDAO {

    @Query("SELECT * FROM historico_parada")
    LiveData<List<HistoricoParada>> listarTodos();

    @Query("SELECT * FROM historico_parada WHERE enviado = 0")
    List<HistoricoParada> listarTodosAEnviar();

    @Query("SELECT * FROM historico_parada WHERE ativo = 1")
    LiveData<List<HistoricoParada>> listarTodosAtivos();

    @Query("SELECT * FROM historico_parada WHERE parada = :parada")
    LiveData<List<HistoricoParada>> carregarPorParada(String parada);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<HistoricoParada> paradas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(HistoricoParada parada);

    @Update
    void editar(HistoricoParada parada);

    @Delete
    void deletar(HistoricoParada parada);

    @Query("DELETE FROM historico_parada")
    void deletarTodos();

    @Query("DELETE FROM historico_parada WHERE ativo = 0")
    void deletarInativos();

}
