package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.HistoricoItinerario;

@Dao
public interface HistoricoItinerarioDAO {

    @Query("SELECT * FROM historico_itinerario")
    LiveData<List<HistoricoItinerario>> listarTodos();

    @Query("SELECT * FROM historico_itinerario")
    List<HistoricoItinerario> listarTodosSync();

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

    @Query("DELETE FROM historico_itinerario WHERE ativo = 0")
    void deletarInativos();

}
