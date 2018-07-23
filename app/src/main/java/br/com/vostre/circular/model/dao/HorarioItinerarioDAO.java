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
            "FROM horario h LEFT JOIN horario_itinerario hi ON hi.horario = h.id AND hi.itinerario = :itinerario " +
            "WHERE h.ativo = 1 AND (hi.ativo = 1 OR hi.ativo IS NULL) AND (hi.itinerario = :itinerario OR hi.itinerario IS NULL) ORDER BY h.nome")
    LiveData<List<HorarioItinerarioNome>> listarTodosAtivosPorItinerario(String itinerario);

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario " +
            "FROM horario h INNER JOIN horario_itinerario hi ON hi.horario = h.id AND hi.itinerario = :itinerario " +
            "WHERE h.ativo = 1 AND (domingo = 1 OR segunda = 1 OR terca = 1 " +
            "OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1) " +
            "AND hi.ativo = 1 AND hi.itinerario = :itinerario ORDER BY h.nome")
    LiveData<List<HorarioItinerarioNome>> listarApenasAtivosPorItinerario(String itinerario);

    @Query("SELECT hi.* FROM horario_itinerario hi WHERE hi.horario = :horario AND hi.itinerario = :itinerario")
    HorarioItinerario checaDuplicidade(String horario, String itinerario);

    @Query("UPDATE parada_itinerario SET ativo = 0 WHERE itinerario = :itinerario")
    void invalidaTodosPorItinerario(String itinerario);

    @Query("SELECT * FROM horario_itinerario WHERE id IN (:ids)")
    List<HorarioItinerario> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM horario_itinerario WHERE id = :id")
    LiveData<HorarioItinerario> carregarPorId(String id);

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario FROM horario_itinerario hi " +
            "INNER JOIN horario h ON h.id = hi.horario WHERE itinerario IN (SELECT pi.itinerario " +
            "FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE p.bairro = :idPartida AND pi.ordem = 1) " +
            "AND p.bairro = :idDestino AND pi.ordem > 1) " +
            "AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= :hora AND hi.ativo = 1 " +
            " ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1")
    LiveData<HorarioItinerarioNome> carregarProximoPorPartidaEDestino(String idPartida, String idDestino,
                                                                  String hora);

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario FROM horario_itinerario hi " +
            "INNER JOIN horario h ON h.id = hi.horario WHERE itinerario IN (SELECT pi.itinerario " +
            "FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE p.bairro = :idPartida AND pi.ordem = 1) " +
            "AND p.bairro = :idDestino AND pi.ordem > 1) " +
            "AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= :hora AND hi.ativo = 1 " +
            " ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1")
    LiveData<HorarioItinerarioNome> carregarPrimeiroPorPartidaEDestino(String idPartida, String idDestino,
                                                                  String hora);

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario FROM horario_itinerario hi " +
            "INNER JOIN horario h ON h.id = hi.horario WHERE itinerario IN (SELECT pi.itinerario " +
            "FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE p.bairro = :idPartida AND pi.ordem = 1) " +
            "AND p.bairro = :idDestino AND pi.ordem > 1) " +
            "AND TIME(h.nome/1000, 'unixepoch', 'localtime') > :hora AND hi.ativo = 1 " +
            " ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1")
    LiveData<HorarioItinerarioNome> carregarSeguintePorPartidaEDestino(String idPartida, String idDestino,
                                                                      String hora);

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario FROM horario_itinerario hi " +
            "INNER JOIN horario h ON h.id = hi.horario WHERE itinerario IN (SELECT pi.itinerario " +
            "FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE p.bairro = :idPartida AND pi.ordem = 1) " +
            "AND p.bairro = :idDestino AND pi.ordem > 1) " +
            "AND TIME(h.nome/1000, 'unixepoch', 'localtime') < :hora AND hi.ativo = 1 " +
            " ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1")
    LiveData<HorarioItinerarioNome> carregarAnteriorPorPartidaEDestino(String idPartida, String idDestino,
                                                                       String hora);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<HorarioItinerario> horarioItinerarios);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(HorarioItinerario horarioItinerario);

    @Update
    void editar(HorarioItinerario horarioItinerario);

    @Delete
    void deletar(HorarioItinerario horarioItinerario);

    @Query("DELETE FROM horario_itinerario")
    void deletarTodos();

}
