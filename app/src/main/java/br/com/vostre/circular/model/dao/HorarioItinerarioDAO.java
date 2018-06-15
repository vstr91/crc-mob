package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;

@Dao
public interface HorarioItinerarioDAO {

    @Query("SELECT * FROM horario_itinerario")
    LiveData<List<HorarioItinerario>> listarTodos();

    @Query("SELECT * FROM horario_itinerario WHERE ativo = 1")
    List<HorarioItinerario> listarTodosAtivos();

    @Query("SELECT * FROM horario_itinerario WHERE enviado = 0")
    List<HorarioItinerario> listarTodosAEnviar();

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario " +
            "FROM horario h LEFT JOIN horario_itinerario hi ON hi.horario = h.id " +
            "WHERE h.ativo = 1 AND (hi.ativo = 1 OR hi.ativo IS NULL) AND (hi.itinerario = :itinerario OR hi.itinerario IS NULL) ORDER BY h.nome")
    LiveData<List<HorarioItinerarioNome>> listarTodosAtivosPorItinerario(String itinerario);

    @Query("SELECT hi.* FROM horario_itinerario hi WHERE hi.horario = :horario AND hi.itinerario = :itinerario")
    HorarioItinerario checaDuplicidade(String horario, String itinerario);

    @Query("UPDATE parada_itinerario SET ativo = 0 WHERE itinerario = :itinerario")
    void invalidaTodosPorItinerario(String itinerario);

    @Query("SELECT * FROM horario_itinerario WHERE id IN (:ids)")
    List<HorarioItinerario> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM horario_itinerario WHERE id = :id")
    LiveData<HorarioItinerario> carregarPorId(String id);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserirTodos(List<HorarioItinerario> horarioItinerarios);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserir(HorarioItinerario horarioItinerario);

    @Update
    void editar(HorarioItinerario horarioItinerario);

    @Delete
    void deletar(HorarioItinerario horarioItinerario);

    @Query("DELETE FROM horario_itinerario")
    void deletarTodos();

}
