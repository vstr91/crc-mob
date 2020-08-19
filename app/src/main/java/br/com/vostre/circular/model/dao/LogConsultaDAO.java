package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.log.LogItinerario;
import br.com.vostre.circular.model.log.LogParada;

@Dao
public interface LogConsultaDAO {

    @Query("SELECT * FROM log_itinerario ORDER BY data_cadastro")
    LiveData<List<LogItinerario>> listarTodosItinerarios();

    @Query("SELECT * FROM log_parada ORDER BY data_cadastro")
    LiveData<List<LogParada>> listarTodasParadas();

    @Query("SELECT * FROM log_itinerario WHERE enviado = 0")
    List<LogItinerario> listarTodosItinerariosAEnviar();

    @Query("SELECT * FROM log_parada WHERE enviado = 0")
    List<LogParada> listarTodasParadasAEnviar();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodosItinerarios(List<LogItinerario> logs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodasParadas(List<LogParada> logs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirItinerario(LogItinerario log);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirParada(LogParada log);

    @Update
    void editarItinerario(LogItinerario log);

    @Update
    void editarParada(LogParada log);

    @Delete
    void deletarItinerario(LogItinerario log);

    @Delete
    void deletarParada(LogParada log);

    @Query("DELETE FROM log_itinerario")
    void deletarTodosItinerarios();

    @Query("DELETE FROM log_parada")
    void deletarTodasParadas();

    @Query("DELETE FROM log_itinerario WHERE enviado = 1")
    void deletarItinerariosEnviados();

    @Query("DELETE FROM log_parada WHERE enviado = 1")
    void deletarParadasEnviadas();

}
