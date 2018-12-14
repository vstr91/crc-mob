package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.SecaoItinerarioParada;

@Dao
public interface SecaoItinerarioDAO {

    @Query("SELECT * FROM secao_itinerario")
    LiveData<List<SecaoItinerario>> listarTodos();

    @Query("SELECT * FROM secao_itinerario")
    List<SecaoItinerario> listarTodosSync();

    @Query("SELECT * FROM secao_itinerario WHERE itinerario = :itinerario")
    LiveData<List<SecaoItinerario>> listarTodosPorItinerario(String itinerario);

    @Query("SELECT si.*, pa.id AS 'idPartida', pa.nome AS 'nomePartida', bp.nome AS 'nomeBairroPartida', " +
            "cp.nome AS 'nomeCidadePartida', pd.id AS 'idPartida', pd.nome AS 'nomePartida', " +
            "bd.nome AS 'nomeBairroPartida', cd.nome AS 'nomeCidadePartida' " +
            "FROM secao_itinerario si INNER JOIN " +
            "     itinerario i ON i.id = si.itinerario INNER JOIN " +
            "     parada pa ON pa.id = si.paradaInicial INNER JOIN " +
            "     bairro bp ON bp.id = pa.bairro INNER JOIN " +
            "     cidade cp ON cp.id = bp.cidade INNER JOIN " +
            "     parada pd ON pd.id = si.paradaFinal INNER JOIN " +
            "     bairro bd ON bd.id = pd.bairro INNER JOIN " +
            "     cidade cd ON cd.id = bd.cidade " +
            " WHERE i. id = :itinerario")
    LiveData<List<SecaoItinerarioParada>> listarTodosPorItinerarioComParada(String itinerario);

    @Query("SELECT si.*, pa.id AS 'idPartida', pa.nome AS 'nomePartida', bp.nome AS 'nomeBairroPartida', " +
            "cp.nome AS 'nomeCidadePartida', pd.id AS 'idPartida', pd.nome AS 'nomePartida', " +
            "bd.nome AS 'nomeBairroPartida', cd.nome AS 'nomeCidadePartida' " +
            "FROM secao_itinerario si INNER JOIN " +
            "     itinerario i ON i.id = si.itinerario INNER JOIN " +
            "     parada pa ON pa.id = si.paradaInicial INNER JOIN " +
            "     bairro bp ON bp.id = pa.bairro INNER JOIN " +
            "     cidade cp ON cp.id = bp.cidade INNER JOIN " +
            "     parada pd ON pd.id = si.paradaFinal INNER JOIN " +
            "     bairro bd ON bd.id = pd.bairro INNER JOIN " +
            "     cidade cd ON cd.id = bd.cidade " +
            " WHERE i. id = :itinerario")
    List<SecaoItinerarioParada> listarTodosPorItinerarioComParadaSync(String itinerario);

    @Query("SELECT * FROM secao_itinerario WHERE ativo = 1")
    List<SecaoItinerario> listarTodosAtivos();

    @Query("SELECT * FROM secao_itinerario WHERE enviado = 0")
    List<SecaoItinerario> listarTodosAEnviar();

    @Query("SELECT * FROM secao_itinerario WHERE id IN (:ids)")
    List<SecaoItinerario> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM secao_itinerario WHERE id = :id")
    LiveData<SecaoItinerario> carregarPorId(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<SecaoItinerario> secaoItinerarioes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(SecaoItinerario secaoItinerario);

    @Update
    void editar(SecaoItinerario secaoItinerario);

    @Delete
    void deletar(SecaoItinerario secaoItinerario);

    @Query("DELETE FROM secao_itinerario")
    void deletarTodos();

}
