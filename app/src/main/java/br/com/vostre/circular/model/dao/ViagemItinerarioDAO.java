package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.ViagemItinerario;

@Dao
public interface ViagemItinerarioDAO {

    @Query("SELECT * FROM viagem_itinerario")
    LiveData<List<ViagemItinerario>> listarTodos();

    @Query("SELECT * FROM viagem_itinerario WHERE itinerario = :itinerario")
    LiveData<List<ViagemItinerario>> listarTodosPorItinerario(String itinerario);

    @Query("SELECT * FROM viagem_itinerario WHERE enviado = 0")
    List<ViagemItinerario> listarTodosAEnviar();

    @Query("SELECT * FROM viagem_itinerario WHERE ativo = 1")
    LiveData<List<ViagemItinerario>> listarTodosAtivos();

    @Query("SELECT * FROM viagem_itinerario WHERE itinerario = :itinerario")
    LiveData<List<ViagemItinerario>> carregarPorItinerario(String itinerario);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<ViagemItinerario> itinerarios);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(ViagemItinerario itinerario);

    @Update
    void editar(ViagemItinerario itinerario);

    @Delete
    void deletar(ViagemItinerario itinerario);

    @Query("DELETE FROM viagem_itinerario")
    void deletarTodos();

    @Query("DELETE FROM viagem_itinerario WHERE ativo = 0")
    void deletarInativos();

}
