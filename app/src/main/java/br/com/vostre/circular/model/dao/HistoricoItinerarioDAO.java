package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.HistoricoParada;

@Dao
public interface HistoricoItinerarioDAO {

    @Query("SELECT * FROM historico_itinerario")
    LiveData<List<HistoricoItinerario>> listarTodos();

    @Query("SELECT * FROM historico_itinerario WHERE enviado = 0")
    List<HistoricoItinerario> listarTodosAEnviar();

    @Query("SELECT * FROM historico_itinerario WHERE ativo = 1")
    LiveData<List<HistoricoItinerario>> listarTodosAtivos();

    @Query("SELECT * FROM historico_itinerario WHERE itinerario = :itinerario")
    LiveData<List<HistoricoItinerario>> carregarPorItinerario(String itinerario);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<HistoricoItinerario> itinerarios);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(HistoricoItinerario itinerario);

    @Update
    void editar(HistoricoItinerario itinerario);

    @Delete
    void deletar(HistoricoItinerario itinerario);

    @Query("DELETE FROM historico_itinerario")
    void deletarTodos();

}
