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

@Dao
public interface HorarioItinerarioDAO {

    @Query("SELECT * FROM horario_itinerario")
    LiveData<List<HorarioItinerario>> listarTodos();

    @Query("SELECT * FROM horario_itinerario WHERE ativo = 1")
    List<HorarioItinerario> listarTodosAtivos();

    @Query("SELECT * FROM horario_itinerario WHERE id IN (:ids)")
    List<HorarioItinerario> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM horario_itinerario WHERE id = :id")
    LiveData<HorarioItinerario> carregarPorId(String id);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserirTodos(HorarioItinerario... horarioItinerarioes);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserir(HorarioItinerario horarioItinerario);

    @Update
    void editar(HorarioItinerario horarioItinerario);

    @Delete
    void deletar(HorarioItinerario horarioItinerario);

}
