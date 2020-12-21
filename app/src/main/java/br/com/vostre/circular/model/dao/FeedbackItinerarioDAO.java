package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.FeedbackItinerario;
import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.pojo.ImagemParadaBairro;

@Dao
public interface FeedbackItinerarioDAO {

    @Query("SELECT * FROM feedback_itinerario")
    LiveData<List<FeedbackItinerario>> listarTodos();

    @Query("SELECT * FROM feedback_itinerario WHERE ativo = 1 AND itinerario = :itinerario")
    LiveData<List<FeedbackItinerario>> listarTodosAtivosPorItinerario(String itinerario);

    @Query("SELECT * FROM feedback_itinerario")
    List<FeedbackItinerario> listarTodosSync();

    @Query("SELECT * FROM feedback_itinerario WHERE imagemEnviada = 0 AND imagem IS NOT NULL")
    List<FeedbackItinerario> listarTodosImagemAEnviar();

    @Query("SELECT * FROM feedback_itinerario WHERE enviado = 0")
    List<FeedbackItinerario> listarTodosAEnviar();

    @Query("SELECT * FROM feedback_itinerario WHERE ativo = 1")
    LiveData<List<FeedbackItinerario>> listarTodosAtivos();

    @Query("SELECT * FROM feedback_itinerario WHERE itinerario = :itinerario")
    LiveData<List<FeedbackItinerario>> carregarPorItinerario(String itinerario);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<FeedbackItinerario> feedbacks);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(FeedbackItinerario feedback);

    @Update
    void editar(FeedbackItinerario feedback);

    @Delete
    void deletar(FeedbackItinerario feedback);

    @Query("DELETE FROM feedback_itinerario")
    void deletarTodos();

    @Query("DELETE FROM feedback_itinerario WHERE ativo = 0")
    void deletarInativos();

}
