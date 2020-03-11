package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;

@Dao
public interface ItinerarioDAO {

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ")")
    LiveData<List<ItinerarioPartidaDestino>> listarTodos();

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.id = :itinerario " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ")")
    List<ItinerarioPartidaDestino> listarTodosAtivosPorIdSync(String itinerario);

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino', (SELECT COUNT(DISTINCT hi.id) FROM horario_itinerario hi WHERE hi.itinerario = i.id " +
            "AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1)) AS 'totalHorarios' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ")")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosComTotalHorarios();

    @Query("SELECT DISTINCT i.id, " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino', (SELECT COUNT(DISTINCT hi.id) FROM horario_itinerario hi WHERE hi.itinerario = i.id " +
            "AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1) AND hi.ativo = 1) AS 'totalHorarios' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "), " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ")")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosComTotalHorariosSimplificado();

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino', (SELECT COUNT(DISTINCT hi.id) FROM horario_itinerario hi WHERE hi.itinerario = i.id " +
            "AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1)) AS 'totalHorarios' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario WHERE i.ativo = 1 AND cidadePartida == cidadeDestino " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ")")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosMunicipaisAtivosComTotalHorarios();

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino', (SELECT COUNT(DISTINCT hi.id) FROM horario_itinerario hi WHERE hi.itinerario = i.id " +
            "AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1)) AS 'totalHorarios' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario WHERE i.ativo = 1 AND cidadePartida != cidadeDestino " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ")")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosIntermunicipaisAtivosComTotalHorarios();

    @Query("SELECT * FROM itinerario")
    List<Itinerario> listarTodosSync();

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.ativo = 1 " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ")")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivos();

    @Query("SELECT DISTINCT i.*, (SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino', i.observacao, p.id AS 'idParadaReferencia', p.nome AS 'nomeParadaReferencia', " +
            "p.latitude AS 'latitudeParadaReferencia', p.longitude AS 'longitudeParadaReferencia' " +
            "FROM parada_itinerario pi INNER JOIN itinerario i ON i.id = pi.itinerario INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE (parada IN (:paradas) " +
            "AND ((SELECT parada FROM parada_itinerario pi WHERE pi.ordem = " +
            "            (SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) " +
            "NOT IN (:paradas) ) )" +
            "AND i.ativo = 1 AND pi.ativo = 1 " +
            "ORDER BY parada, pi.itinerario")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosProximosPoi(List<String> paradas);

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino', (SELECT COUNT(DISTINCT hi.id) FROM horario_itinerario hi WHERE hi.itinerario = i.id " +
            "AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1)) AS 'totalHorarios' " +
            " FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.ativo = 1 " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ")")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosComTotalHorarios();

    @Query("SELECT COUNT(DISTINCT i.id) AS 'total'" +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.ativo = 1 " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id" +
            ")")
    LiveData<Integer> contarTodosAtivos();

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino', 0 AS 'flagTrecho' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.ativo = 1")
    List<ItinerarioPartidaDestino> listarTodosAtivosSync();

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroPartida', " +
            "/*(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida',*/ " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'idBairroDestino'," +
            "/*(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino',*/ 0 AS 'flagTrecho' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.ativo = 1")
    List<ItinerarioPartidaDestino> listarTodosAtivosGraphSync();

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino', 1 AS 'flagTrecho' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.ativo = 1 ORDER BY cidadePartida, bairroPartida, cidadeDestino, bairroDestino")
    List<ItinerarioPartidaDestino> listarTodosAtivosSimplificadoSync();

    @Query("SELECT i.*, b.id AS 'idBairroPartida', p.id AS 'idPartida', p.nome AS 'nomePartida', b.nome AS 'bairroPartida', c.nome AS 'cidadePartida', " +
            "b2.id AS 'idBairroDestino', p2.id AS 'idDestino', p2.nome AS 'nomeDestino', b2.nome AS 'bairroDestino', " +
            "c2.nome AS 'cidadeDestino', 1 AS 'flagTrecho', pi.distanciaSeguinte, pi.distanciaSeguinteMetros " +
            "FROM itinerario i INNER JOIN parada_itinerario pi ON pi.itinerario = i.id INNER JOIN parada p ON p.id = pi.parada INNER JOIN " +
            "bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade " +
            "INNER JOIN parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN parada p2 ON p2.id = pi2.parada INNER JOIN bairro b2 ON b2.id = p2.bairro INNER JOIN cidade c2 ON c2.id = b2.cidade " +
            "WHERE pi2.ordem > pi.ordem " +
            "AND i.ativo = 1 " +
            "AND pi.ativo = 1 " +
            "AND pi2.ativo = 1 " +
            "ORDER BY i.id, pi.ordem, pi2.ordem")
    List<ItinerarioPartidaDestino> listarTodosAtivosTesteSync();

    @Query("SELECT i.*, " +
            "bp.id AS 'idBairroPartida', pp.id AS 'idPartida', pp.nome AS 'nomePartida', bp.nome AS 'bairroPartida', c.nome AS 'cidadePartida'," +
            "bd.id AS 'idBairroDestino', pd.id AS 'idDestino', pd.nome AS 'nomeDestino', bd.nome AS 'bairroDestino', cd.nome AS 'cidadeDestino'," +
            "(" +
            "SELECT SUM(pi3.distanciaSeguinte) " +
            "FROM parada_itinerario pi3 " +
            "WHERE pi3.itinerario = i.id " +
            "AND pi3.ordem >= pi.ordem " +
            "AND pi3.ordem < pi2.ordem" +
            ") AS 'distanciaTrecho', " +

            "(" +
            "SELECT SUM(pi3.distanciaSeguinteMetros) " +
            "FROM parada_itinerario pi3 " +
            "WHERE pi3.itinerario = i.id " +
            "AND pi3.ordem >= pi.ordem " +
            "AND pi3.ordem < pi2.ordem" +
            ") AS 'distanciaTrechoMetros', " +

            "(" +
            "SELECT sUM(pi3.tempoSeguinte) " +
            "FROM parada_itinerario pi3 " +
            "WHERE pi3.itinerario = i.id " +
            "AND pi3.ordem >= pi.ordem " +
            "AND pi3.ordem < pi2.ordem" +
            ") AS 'tempoTrecho', " +
            "(" +
            "SELECT SUM(pi3.valorSeguinte) " +
            "FROM parada_itinerario pi3 " +
            "WHERE pi3.itinerario = i.id " +
            "AND pi3.ordem >= pi.ordem " +
            "AND pi3.ordem < pi2.ordem " +
            ") AS 'tarifaTrecho'," +

            "CASE WHEN pi.ordem = 1 AND pi2.ordem = (SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) THEN 0 ELSE 1 END AS 'flagTrecho'" +
            "            FROM parada_itinerario pi INNER JOIN " +
            "                 itinerario i ON i.id = pi.itinerario INNER JOIN" +
            "                 parada pp ON pp.id = pi.parada INNER JOIN" +
            "                 bairro bp ON bp.id = pp.bairro INNER JOIN" +
            "                 cidade c ON c.id = bp.cidade INNER JOIN" +
            "                 parada_itinerario pi2 ON pi2.itinerario = i.id AND pi2.ordem > pi.ordem INNER JOIN" +
            "                 parada pd ON pd.id = pi2.parada INNER JOIN" +
            "                 bairro bd ON bd.id = pd.bairro INNER JOIN" +
            "                 cidade cd ON cd.id = bd.cidade" +
            "            WHERE pi.ativo = 1 " +
            "            AND   pi2.ativo = 1" +
            "            AND   i.ativo = 1" +
            "            AND idDestino IS NOT NULL " +
            "            ORDER BY i.id, CASE WHEN flagTrecho = 1 THEN distanciaTrechoMetros ELSE distanciaMetros END, pi.ordem, pi2.ordem")
    List<ItinerarioPartidaDestino> listarTodosAtivosTesteNovoSync();

    // QUERY ANTIGA
//    @Query("SELECT i.*, " +
//            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = pi2.ordem " +
//            "AND pi.itinerario = i.id) AS 'idBairroPartida', " +
//            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//            "WHERE pi.ordem = pi2.ordem AND pi.itinerario = i.id) AS 'nomePartida', " +
//            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//            "WHERE pi.ordem > pi2.ordem AND pi.itinerario = i.id) AS 'idBairroDestino', " +
//            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//            "WHERE pi.ordem > pi2.ordem AND pi.itinerario = i.id) AS 'nomeDestino', " +
//            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = pi2.ordem AND pi.itinerario = i.id) AS 'bairroPartida', " +
//            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem > pi2.ordem AND pi.itinerario = i.id) AS 'bairroDestino', " +
//            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
//            "WHERE pi.ordem = pi2.ordem AND pi.itinerario = i.id " +
//            ") AS 'cidadePartida', " +
//            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
//            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
//            "WHERE pi.ordem > pi2.ordem AND pi.itinerario = i.id " +
//            ") AS 'cidadeDestino', 1 AS 'flagTrecho' " +
//            "FROM parada_itinerario pi2 INNER JOIN itinerario i ON i.id = pi2.itinerario " +
//            "WHERE pi2.ordem < (SELECT MAX(pi3.ordem) FROM parada_itinerario pi3 WHERE pi3.itinerario = i.id) " +
//            "AND i.ativo = 1 " +
//            "ORDER BY i.id, pi2.ordem")
//    List<ItinerarioPartidaDestino> listarTodosAtivosTesteSync();

    @Query("SELECT DISTINCT (SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
            "FROM horario_itinerario hi INNER JOIN " +
            "horario h ON h.id = hi.horario " +
            "WHERE itinerario = i.id AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= :hora " +
            "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) AS 'proximoHorario', i.*, " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino' FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.ativo = 1 AND pit.parada = :parada AND (SELECT pi.parada FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) <> :parada " +
            "ORDER BY (SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
            "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
            "WHERE itinerario = i.id AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= :hora" +
            " ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 )")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosPorParadaComBairroEHorario(String parada, String hora);

    @Query("SELECT DISTINCT (SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
            "FROM horario_itinerario hi INNER JOIN " +
            "horario h ON h.id = hi.horario " +
            "WHERE itinerario = i.id AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= :hora " +
            "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) AS 'proximoHorario', i.*, " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino' FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.ativo = 1 AND i.sigla LIKE :linha " +
            "ORDER BY (SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
            "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
            "WHERE itinerario = i.id AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= :hora" +
            " ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 )")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosPorLinhaComBairroEHorario(String linha, String hora);

    @RawQuery(observedEntities = ItinerarioPartidaDestino.class)
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosPorParadaComBairroEHorarioCompleto(SupportSQLiteQuery query);

    @RawQuery(observedEntities = ItinerarioPartidaDestino.class)
    List<ItinerarioPartidaDestino> listarTodosAtivosPorParadaComBairroEHorarioCompletoSync(SupportSQLiteQuery query);

    @RawQuery(observedEntities = ItinerarioPartidaDestino.class)
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosPorLinhaComBairroEHorarioCompleto(SupportSQLiteQuery query);

    @RawQuery(observedEntities = ItinerarioPartidaDestino.class)
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosPorDestinoComBairroEHorarioCompleto(SupportSQLiteQuery query);
//    @RawQuery
//    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosPorParadaComBairroEHorarioCompleto(SupportSQLiteQuery query);

    @Query("SELECT * FROM itinerario WHERE enviado = 0")
    List<Itinerario> listarTodosAEnviar();

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino', e.nome AS nomeEmpresa, (SELECT COUNT(DISTINCT hi.id) FROM horario_itinerario hi WHERE hi.itinerario = i.id " +
            "AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1)) AS 'totalHorarios' " +
            " FROM parada_itinerario pit INNER JOIN " +
            "itinerario i ON i.id = pit.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
            "WHERE i.ativo = 1 AND i.empresa = :empresa " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ")")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosPorEmpresa(String empresa);

    @Query("SELECT i.*, " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino' " +
            "FROM itinerario i WHERE i.id IN (SELECT pi.itinerario " +
            "FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE p.bairro = :idPartida AND pi.ordem = 1) " +
            "AND p.bairro = :idDestino AND pi.ordem > 1) LIMIT 1")
    ItinerarioPartidaDestino carregarPorPartidaEDestinoSync(String idPartida, String idDestino);

    @RawQuery
    ItinerarioPartidaDestino carregarPorPartidaEDestinoComHorarioSync(SupportSQLiteQuery query);

    @RawQuery
    List<String> carregarOpcoesPorPartidaEDestinoSync(SupportSQLiteQuery query);

    @Query("SELECT DISTINCT i.id " +
            "FROM itinerario i INNER JOIN " +
            "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN" +
            "     parada p ON p.id = pi.parada INNER JOIN" +
            "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN" +
            "     parada pd ON pd.id = pi2.parada " +
            "WHERE p.bairro = :partida AND pd.bairro = :destino " +
            "AND pi2.ordem > pi.ordem " +
            "ORDER BY i.id;")
    List<String> carregarOpcoesPorPartidaEDestinoTrechoSync(String partida, String destino);

    @Query("SELECT DISTINCT i.id " +
            "FROM itinerario i INNER JOIN " +
            "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN" +
            "     parada p ON p.id = pi.parada INNER JOIN" +
            "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN" +
            "     parada pd ON pd.id = pi2.parada " +
            "WHERE pd.bairro = :destino " +
            "AND pi2.ordem > pi.ordem " +
            "AND i.id NOT IN (" +
            "                 SELECT pi2.itinerario " +
            "                 FROM parada_itinerario pi2 INNER JOIN" +
            "                      parada p ON p.id = pi2.parada" +
            "                 WHERE p.bairro = :destino" +
            "                 AND pi2.ordem = 1" +
            "            )" +
            "ORDER BY CASE WHEN p.bairro = :bairroAtual THEN -1 ELSE 0 END, i.id;")
    List<String> carregarOpcoesPorPartidaEDestinoTrechoConsultaSync(String destino, String bairroAtual);

    @Query("SELECT DISTINCT i.id " +
            "FROM itinerario i INNER JOIN " +
            "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN" +
            "     parada p ON p.id = pi.parada INNER JOIN" +
            "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN" +
            "     parada pd ON pd.id = pi2.parada " +
            "WHERE pd.bairro = :destino " +
            "AND pi2.ordem > pi.ordem " +
            "AND i.id NOT IN (" +
            "                 SELECT pi2.itinerario " +
            "                 FROM parada_itinerario pi2 INNER JOIN" +
            "                      parada p ON p.id = pi2.parada" +
            "                 WHERE p.bairro = :destino" +
            "                 AND pi2.ordem = 1" +
            "            )" +
            "ORDER BY i.id;")
    List<String> carregarOpcoesPorDestinoTrechoConsultaSync(String destino);

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "            INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "            (SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "            INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "            (SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "            INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "            (SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "            ) AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "            INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "            (SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            "            ) AS 'cidadeDestino' " +
            "FROM itinerario i INNER JOIN " +
            "     parada_itinerario pi ON pi.itinerario = i.id INNER JOIN" +
            "     parada p ON p.id = pi.parada INNER JOIN" +
            "     parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN" +
            "     parada pd ON pd.id = pi2.parada " +
            "WHERE p.bairro = :partida AND pd.bairro = :destino " +
            "AND pi2.ordem > pi.ordem " +
            "ORDER BY i.id;")
    List<ItinerarioPartidaDestino> carregarOpcoesPorPartidaEDestinoTrechoItinerarioSync(String partida, String destino);

    @Query("SELECT IFNULL(pi.valorSeguinte, pi2.valorAnterior) FROM itinerario i2 LEFT JOIN " +
            "parada_itinerario pi ON pi.itinerario = i2.id LEFT JOIN parada pp ON pp.id = pi.parada LEFT JOIN " +
            "bairro bp ON bp.id = pp.bairro LEFT JOIN " +
            "parada_itinerario pi2 ON pi2.itinerario = i2.id LEFT JOIN " +
            "parada pd ON pd.id = pi2.parada LEFT JOIN bairro bd ON bd.id = pd.bairro " +
            "WHERE i2.ativo = 1 AND pp.id <> pd.id AND pi.ordem < pi2.ordem AND i2.id = :itinerario " +
            "AND bp.id = :partida " +
            "AND bd.id = :destino ORDER BY i2.id LIMIT 1")
    Double carregarTarifaTrechoSync(String partida, String destino, String itinerario);

    @Query("SELECT DISTINCT c.*, e.id AS idEstado, e.nome AS nomeEstado " +
            "FROM itinerario i INNER JOIN parada_itinerario pit ON pit.itinerario = i.id INNER JOIN parada p ON p.id = pit.parada INNER JOIN " +
            "bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado " +
            "WHERE i.id IN (SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE p.bairro = :idPartida AND (pi.ordem = 1 OR (pi.ordem > 1 AND pi.destaque = 1)))) AND c.id <> (SELECT b.cidade FROM bairro b WHERE b.id = :idPartida)")
    LiveData<List<CidadeEstado>> carregarDestinosPorPartida(String idPartida);

//    @Query("SELECT i.*,   FROM parada_itinerario pi INNER JOIN itinerario i ON i.id = pi.itinerario " +
//            "INNER JOIN parada WHERE ativo = 1")
//    List<ItinerarioPartidaDestino> listarTodosAtivosPorEmpresa(String empresa);

    @Query("SELECT * FROM itinerario WHERE id IN (:ids)")
    List<Itinerario> carregarTodosPorIds(int[] ids);

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino', e.nome AS nomeEmpresa FROM parada_itinerario pit INNER JOIN " +
            "itinerario i ON i.id = pit.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
            "WHERE i.id = :itinerario")
    LiveData<ItinerarioPartidaDestino> carregar(String itinerario);

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1" +
            ") AS 'cidadeDestino', e.nome AS nomeEmpresa FROM parada_itinerario pit INNER JOIN " +
            "itinerario i ON i.id = pit.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
            "WHERE i.id = :itinerario")
    ItinerarioPartidaDestino carregarSync(String itinerario);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Itinerario> itinerarios);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Itinerario itinerario);

    @Update
    void editar(Itinerario itinerario);

    @Delete
    void deletar(Itinerario itinerario);

    @Query("DELETE FROM itinerario")
    void deletarTodos();

    @Query("DELETE FROM itinerario WHERE ativo = 0")
    void deletarInativos();

}
