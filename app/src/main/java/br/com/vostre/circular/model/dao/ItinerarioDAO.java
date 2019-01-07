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
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadeDestino' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ")")
    LiveData<List<ItinerarioPartidaDestino>> listarTodos();

    @Query("SELECT * FROM itinerario")
    List<Itinerario> listarTodosSync();

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadeDestino' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.ativo = 1 " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ")")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivos();

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'idBairroDestino'," +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadeDestino', 1 AS 'flagTrecho' " +
            "FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.ativo = 1")
    List<ItinerarioPartidaDestino> listarTodosAtivosSync();

    @Query("SELECT i.*, " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = pi2.ordem " +
            "AND pi.itinerario = i.id) AS 'idBairroPartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "WHERE pi.ordem = pi2.ordem AND pi.itinerario = i.id) AS 'nomePartida', " +
            "(SELECT pp.bairro FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "WHERE pi.ordem > pi2.ordem AND pi.itinerario = i.id) AS 'idBairroDestino', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "WHERE pi.ordem > pi2.ordem AND pi.itinerario = i.id) AS 'nomeDestino', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = pi2.ordem AND pi.itinerario = i.id) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem > pi2.ordem AND pi.itinerario = i.id) AS 'bairroDestino', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
            "WHERE pi.ordem = pi2.ordem AND pi.itinerario = i.id " +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade " +
            "WHERE pi.ordem > pi2.ordem AND pi.itinerario = i.id " +
            ") AS 'cidadeDestino', 1 AS 'flagTrecho' " +
            "FROM parada_itinerario pi2 INNER JOIN itinerario i ON i.id = pi2.itinerario " +
            "WHERE pi2.ordem < (SELECT MAX(pi3.ordem) FROM parada_itinerario pi3 WHERE pi3.itinerario = i.id) " +
            "AND i.ativo = 1 " +
            "ORDER BY i.id, pi2.ordem")
    List<ItinerarioPartidaDestino> listarTodosAtivosTesteSync();

    @Query("SELECT DISTINCT (SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
            "FROM horario_itinerario hi INNER JOIN " +
            "horario h ON h.id = hi.horario " +
            "WHERE itinerario = i.id AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= :hora " +
            "ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 ) AS 'proximoHorario', i.*, " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadeDestino' FROM parada_itinerario pit INNER JOIN itinerario i ON i.id = pit.itinerario " +
            "WHERE i.ativo = 1 AND pit.parada = :parada AND (SELECT pi.parada FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) <> :parada " +
            "ORDER BY (SELECT strftime('%H:%M', TIME(h.nome/1000, 'unixepoch', 'localtime')) " +
            "FROM horario_itinerario hi INNER JOIN horario h ON h.id = hi.horario " +
            "WHERE itinerario = i.id AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= :hora" +
            " ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1 )")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosPorParadaComBairroEHorario(String parada, String hora);

    @RawQuery(observedEntities = ItinerarioPartidaDestino.class)
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosPorParadaComBairroEHorarioCompleto(SupportSQLiteQuery query);
//    @RawQuery
//    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosPorParadaComBairroEHorarioCompleto(SupportSQLiteQuery query);

    @Query("SELECT * FROM itinerario WHERE enviado = 0")
    List<Itinerario> listarTodosAEnviar();

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadeDestino', e.nome AS nomeEmpresa FROM parada_itinerario pit INNER JOIN " +
            "itinerario i ON i.id = pit.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
            "WHERE i.ativo = 1 AND i.empresa = :empresa " +
            "ORDER BY i.ativo DESC, " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            "), " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ")")
    LiveData<List<ItinerarioPartidaDestino>> listarTodosAtivosPorEmpresa(String empresa);

    @Query("SELECT i.*, " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroDestino', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
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
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadeDestino', e.nome AS nomeEmpresa FROM parada_itinerario pit INNER JOIN " +
            "itinerario i ON i.id = pit.itinerario INNER JOIN empresa e ON e.id = i.empresa " +
            "WHERE i.id = :itinerario")
    LiveData<ItinerarioPartidaDestino> carregar(String itinerario);

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'nomeDestino'," +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id) AS 'bairroDestino'," +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
            ") AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id) AND pi.itinerario = i.id" +
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

}
