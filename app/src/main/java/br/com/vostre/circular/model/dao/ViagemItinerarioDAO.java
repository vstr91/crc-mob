package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.ViagemItinerario;

@Dao
public interface ViagemItinerarioDAO {

    @Query("SELECT * FROM viagem_itinerario")
    LiveData<List<ViagemItinerario>> listarTodos();

    @Query("SELECT * FROM viagem_itinerario WHERE itinerario = :itinerario AND ativo = 1")
    LiveData<List<ViagemItinerario>> listarTodosPorItinerario(String itinerario);

    @Query("SELECT * FROM viagem_itinerario WHERE enviado = 0")
    List<ViagemItinerario> listarTodosAEnviar();

    @Query("SELECT * FROM viagem_itinerario WHERE ativo = 1")
    LiveData<List<ViagemItinerario>> listarTodosAtivos();

    @Query("SELECT * FROM viagem_itinerario WHERE trajetoEnviado = 0 AND trajeto IS NOT NULL")
    List<ViagemItinerario> listarTodosArquivoAEnviar();

    @Query("SELECT * FROM viagem_itinerario WHERE itinerario = :itinerario")
    LiveData<List<ViagemItinerario>> carregarPorItinerario(String itinerario);

    @Query("SELECT * FROM viagem_itinerario WHERE id = :id")
    ViagemItinerario carregar(String id);

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

    @Query("UPDATE viagem_itinerario SET ultima_alteracao = datetime('now'), enviado = 0, trajetoEnviado = 0")
    void marcaTodosParaEnvio();

}
