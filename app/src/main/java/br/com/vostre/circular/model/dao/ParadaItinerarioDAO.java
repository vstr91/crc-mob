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

import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;

@Dao
public interface ParadaItinerarioDAO {

    @Query("SELECT pi.*, pi.itinerario AS idItinerario, p.id AS idParada, p.nome AS nomeParada, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade " +
            "WHERE p.ativo = 1 AND pi.itinerario = :itinerario")
    LiveData<List<ParadaItinerarioBairro>> listarTodosPorItinerarioComBairro(String itinerario);

    @Query("SELECT pi.*, p.id AS idParada, p.nome AS nomeParada, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade " +
            "WHERE pi.ativo = 1 AND pi.itinerario = :itinerario ORDER BY pi.ordem")
    LiveData<List<ParadaItinerarioBairro>> listarTodosAtivosPorItinerarioComBairro(String itinerario);

    @Query("SELECT i.* FROM parada_itinerario pi INNER JOIN itinerario i ON i.id = pi.itinerario " +
            "WHERE pi.parada = :parada AND pi.ativo = 1 AND i.ativo = 1")
    List<Itinerario> listarTodosAtivosPorParada(String parada);

    @Query("SELECT * FROM parada_itinerario WHERE enviado = 0")
    List<ParadaItinerario> listarTodosAEnviar();

    @Query("SELECT pi.* FROM parada_itinerario pi WHERE pi.parada = :parada AND pi.itinerario = :itinerario")
    ParadaItinerario checaDuplicidade(String parada, String itinerario);

    @Query("UPDATE parada_itinerario SET ativo = 0 WHERE itinerario = :itinerario")
    void invalidaTodosPorItinerario(String itinerario);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserirTodos(ParadaItinerario... paradasItinerarios);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserir(ParadaItinerario paradaItinerario);

    @Update
    void editar(ParadaItinerario paradaItinerario);

    @Delete
    void deletar(ParadaItinerario paradaItinerario);

}