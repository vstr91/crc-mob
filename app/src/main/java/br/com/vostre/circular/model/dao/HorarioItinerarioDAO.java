package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;

@Dao
public interface HorarioItinerarioDAO {

    @Query("SELECT * FROM horario_itinerario")
    LiveData<List<HorarioItinerario>> listarTodos();

    @Query("SELECT * FROM horario_itinerario")
    List<HorarioItinerario> listarTodosSync();

    @Query("SELECT * FROM horario_itinerario WHERE ativo = 1")
    List<HorarioItinerario> listarTodosAtivos();

    @Query("SELECT COUNT(id) FROM horario_itinerario WHERE ativo = 1 AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1)")
    LiveData<Integer> contarTodosAtivos();

    @Query("SELECT * FROM horario_itinerario WHERE enviado = 0")
    List<HorarioItinerario> listarTodosAEnviar();

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario " +
            "FROM horario h LEFT JOIN horario_itinerario hi ON hi.horario = h.id AND hi.itinerario = :itinerario " +
            "WHERE h.ativo = 1 AND (hi.ativo = 1 OR hi.ativo IS NULL) AND (hi.itinerario = :itinerario OR hi.itinerario IS NULL) ORDER BY h.nome")
    LiveData<List<HorarioItinerarioNome>> listarTodosAtivosPorItinerario(String itinerario);

    @RawQuery
    String carregarProximoHorarioPorItinerario(SupportSQLiteQuery query);

    @RawQuery
    String carregarHorarioAnteriorPorItinerario(SupportSQLiteQuery query);

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario " +
            "FROM horario h INNER JOIN horario_itinerario hi ON hi.horario = h.id AND hi.itinerario = :itinerario " +
            "WHERE h.ativo = 1 AND (domingo = 1 OR segunda = 1 OR terca = 1 " +
            "OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1) " +
            "AND hi.ativo = 1 AND hi.itinerario = :itinerario ORDER BY h.nome")
    LiveData<List<HorarioItinerarioNome>> listarApenasAtivosPorItinerario(String itinerario);

//    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario " +
//            "FROM horario h INNER JOIN horario_itinerario hi ON hi.horario = h.id " +
//            "WHERE h.ativo = 1 AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1) " +
//            "AND hi.ativo = 1 AND hi.itinerario IN (SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
//            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
//            "WHERE p.bairro = (SELECT b.id FROM parada p INNER JOIN bairro b ON b.id = p.bairro WHERE p.id = :partida) AND pi.ordem = 1)" +
//            " AND p.bairro = (SELECT b.id FROM parada p INNER JOIN bairro b ON b.id = p.bairro WHERE p.id = :destino) AND pi.ordem > 1) " +
//            "ORDER BY h.nome")
//    List<HorarioItinerarioNome> listarApenasAtivosPorPartidaEDestinoSync(String partida, String destino);

    @Query("SELECT DISTINCT hi.*, h.id AS idHorario, h.nome AS nomeHorario, (SELECT MAX(hi2.ultima_alteracao) FROM horario_itinerario hi2 WHERE hi2.itinerario = hi.itinerario) AS ultimaAtualizacao " +
            "FROM itinerario i INNER JOIN " +
            "parada_itinerario pi ON pi.itinerario = i.id INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN " +
            "bairro bp ON bp.id = pp.bairro INNER JOIN parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN " +
            "parada pd ON pd.id = pi2.parada INNER JOIN bairro bd ON bd.id = pd.bairro INNER JOIN  " +
            "horario_itinerario hi ON hi.itinerario = i.id INNER JOIN " +
            "horario h ON h.id = hi.horario " +
            "WHERE i.ativo = 1 AND hi.ativo = 1 AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1) AND pp.id <> pd.id " +
            "AND pi.ordem < pi2.ordem " +
            "AND pi.ativo = 1 AND pi2.ativo = 1 " +
            "AND (pi.destaque = 1 OR pi.ordem = 1) " +
            "/*AND (pi2.destaque = 1 OR pi2.ordem = " +
            "                 (SELECT MAX(pi3.ordem) FROM parada_itinerario pi3 WHERE pi3.itinerario = i.id AND pi3.ativo = 1))*/ " +
            "AND bp.id = :partida AND bd.id = :destino ORDER BY h.nome")
    List<HorarioItinerarioNome> listarApenasAtivosPorPartidaEDestinoSync(String partida, String destino);

    @Query("SELECT DISTINCT hi.*, h.id AS idHorario, h.nome AS nomeHorario, (SELECT MAX(hi2.ultima_alteracao) FROM horario_itinerario hi2 WHERE hi2.itinerario = hi.itinerario) AS ultimaAtualizacao " +
            "FROM itinerario i INNER JOIN " +
            "horario_itinerario hi ON hi.itinerario = i.id INNER JOIN " +
            "horario h ON h.id = hi.horario " +
            "WHERE i.ativo = 1 AND hi.ativo = 1 AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1) AND i.id = :itinerario ORDER BY h.nome")
    List<HorarioItinerarioNome> listarApenasAtivosPorItinerarioSync(String itinerario);

    @Query("SELECT DISTINCT hi.*, h.id AS idHorario, h.nome AS nomeHorario, (SELECT MAX(hi2.ultima_alteracao) FROM horario_itinerario hi2 WHERE hi2.itinerario = hi.itinerario) AS ultimaAtualizacao " +
            "FROM itinerario i INNER JOIN " +
            "horario_itinerario hi ON hi.itinerario = i.id INNER JOIN " +
            "horario h ON h.id = hi.horario " +
            "WHERE i.ativo = 1 AND hi.ativo = 1 AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1) AND " +
            "i.id IN (:itinerarios) ORDER BY h.nome")
    List<HorarioItinerarioNome> listarApenasAtivosPorItinerarioTrechoSync(List<String> itinerarios);

    @Query("SELECT DISTINCT hi.*, h.id AS idHorario, h.nome AS nomeHorario, (SELECT MAX(hi2.ultima_alteracao) FROM horario_itinerario hi2 WHERE hi2.itinerario = hi.itinerario) AS ultimaAtualizacao " +
            "FROM horario h INNER JOIN horario_itinerario hi ON hi.horario = h.id " +
            "WHERE h.ativo = 1 AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1) " +
            "AND hi.ativo = 1 AND hi.itinerario IN (SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE p.bairro = (SELECT b.id FROM parada p INNER JOIN bairro b ON b.id = p.bairro WHERE b.id = :partida) AND pi.ordem = 1 AND pi.ativo = 1)" +
            " AND p.bairro = (SELECT b.id FROM parada p INNER JOIN bairro b ON b.id = p.bairro WHERE b.id = :destino) AND pi.ordem > 1 AND pi.ativo = 1) " +
            "AND hi.itinerario <> :itinerarioARemover " +
            "ORDER BY h.nome")
    List<HorarioItinerarioNome> listarApenasAtivosPorPartidaEDestinoFiltradoSync(String partida, String destino, String itinerarioARemover);

    @Query("SELECT DISTINCT hi.*, h.id AS idHorario, h.nome AS nomeHorario, (SELECT MAX(hi2.ultima_alteracao) FROM horario_itinerario hi2 WHERE hi2.itinerario = hi.itinerario) AS ultimaAtualizacao " +
            "FROM horario h INNER JOIN horario_itinerario hi ON hi.horario = h.id " +
            "WHERE h.ativo = 1 AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1) " +
            "AND hi.ativo = 1 AND hi.itinerario = :itinerario " +
            "AND hi.itinerario <> :itinerarioARemover " +
            "ORDER BY h.nome")
    List<HorarioItinerarioNome> listarApenasAtivosPorItinerarioFiltradoSync(String itinerario, String itinerarioARemover);

    @Query("SELECT DISTINCT i.*, " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomePartida', " +
            "(SELECT nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'nomeDestino', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'cidadeDestino', " +
            "'' AS nomeEmpresa " +
            "FROM horario h INNER JOIN horario_itinerario hi ON hi.horario = h.id INNER JOIN itinerario i ON i.id = hi.itinerario " +
            "INNER JOIN parada_itinerario pi ON pi.itinerario = i.id INNER JOIN parada_itinerario pi2 ON pi2.itinerario = i.id " +
            "INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN parada pd ON pd.id = pi2.parada " +
            "            WHERE h.ativo = 1 AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1) " +
            "            AND hi.ativo = 1 " +
            " AND pp.bairro = :partida " +
            " AND pd.bairro = :destino" +
            " AND pi2.ordem > pi.ordem " +
            " AND (pi.destaque = 1 OR pi.ordem = 1)" +
            " /*AND (pi2.destaque = 1 OR pi2.ordem = (SELECT MAX(pi3.ordem) FROM parada_itinerario pi3 WHERE pi3.itinerario = i.id AND pi3.ativo = 1))*/ " +
            "ORDER BY h.nome")
    List<ItinerarioPartidaDestino> contaItinerariosPorPartidaEDestinoSync(String partida, String destino);

    @Query("SELECT DISTINCT i.id, i.observacao, " +
            "'' AS 'nomePartida', " +
            "'' AS 'nomeDestino', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroPartida', " +
            "(SELECT b.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'bairroDestino', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MIN(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'cidadePartida', " +
            "(SELECT c.nome FROM parada_itinerario pi INNER JOIN parada pp ON pp.id = pi.parada " +
            "INNER JOIN bairro b ON b.id = pp.bairro INNER JOIN cidade c ON c.id = b.cidade WHERE pi.ordem = " +
            "(SELECT MAX(ordem) FROM parada_itinerario WHERE itinerario = i.id AND ativo = 1) AND pi.itinerario = i.id AND pi.ativo = 1) AS 'cidadeDestino', " +
            "'' AS nomeEmpresa " +
            "FROM horario h INNER JOIN horario_itinerario hi ON hi.horario = h.id INNER JOIN itinerario i ON i.id = hi.itinerario " +
            "INNER JOIN parada_itinerario pi ON pi.itinerario = i.id INNER JOIN parada_itinerario pi2 ON pi2.itinerario = i.id " +
            "INNER JOIN parada pp ON pp.id = pi.parada INNER JOIN parada pd ON pd.id = pi2.parada " +
            "            WHERE h.ativo = 1 AND (domingo = 1 OR segunda = 1 OR terca = 1 OR quarta = 1 OR quinta = 1 OR sexta = 1 OR sabado = 1) " +
            "            AND hi.ativo = 1 " +
            " AND pp.bairro = :partida " +
            " AND pd.bairro = :destino" +
            " AND pi2.ordem > pi.ordem " +
            " AND (pi.destaque = 1 OR pi.ordem = 1)" +
            " /*AND (pi2.destaque = 1 OR pi2.ordem = (SELECT MAX(pi3.ordem) FROM parada_itinerario pi3 WHERE pi3.itinerario = i.id AND pi3.ativo = 1))*/ " +
            "ORDER BY h.nome")
    List<ItinerarioPartidaDestino> contaItinerariosPorPartidaEDestinoSimplificadoSync(String partida, String destino);

    @Query("SELECT hi.* FROM horario_itinerario hi WHERE hi.horario = :horario AND hi.itinerario = :itinerario")
    HorarioItinerario checaDuplicidade(String horario, String itinerario);

    @Query("UPDATE horario_itinerario SET domingo = 0, segunda = 0, terca = 0, quarta = 0, quinta = 0, sexta = 0, sabado = 0, ultima_alteracao = datetime('now'), enviado = 0 " +
            "WHERE itinerario = :itinerario")
    void invalidaTodosPorItinerario(String itinerario);

    @Query("UPDATE horario_itinerario SET ultima_alteracao = datetime('now'), enviado = 0")
    void marcaTodosParaEnvio();

    @Query("SELECT * FROM horario_itinerario WHERE id IN (:ids)")
    List<HorarioItinerario> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM horario_itinerario WHERE id = :id")
    LiveData<HorarioItinerario> carregarPorId(String id);

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario  " +
            "FROM horario h INNER JOIN horario_itinerario hi ON hi.horario = h.id " +
            "WHERE hi.id = :id")
    HorarioItinerarioNome carregarPorIdSync(String id);

    @Query("SELECT observacao FROM horario_itinerario WHERE horario = :id AND itinerario = :itinerario")
    String carregarObservacaoPorHorario(String id, String itinerario);

    @Query("SELECT observacao FROM horario_itinerario WHERE id = :idHorarioItinerario")
    String carregarObservacaoPorHorario(String idHorarioItinerario);

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario FROM horario_itinerario hi " +
            "INNER JOIN horario h ON h.id = hi.horario WHERE itinerario IN (SELECT pi.itinerario " +
            "FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE p.bairro = :idPartida AND pi.ordem = 1 AND pi.ativo = 1) " +
            "AND p.bairro = :idDestino AND pi.ordem > 1 AND pi.ativo = 1) " +
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
    HorarioItinerarioNome carregarProximoPorPartidaEDestinoSync(String idPartida, String idDestino,
                                                                      String hora);

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario FROM horario_itinerario hi " +
            "INNER JOIN horario h ON h.id = hi.horario WHERE itinerario IN (SELECT pi.itinerario " +
            "FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE p.bairro = :idPartida AND pi.ordem = 1 AND pi.ativo = 1) " +
            "AND p.bairro = :idDestino AND pi.ordem > 1 AND pi.ativo = 1) " +
            "AND TIME(h.nome/1000, 'unixepoch', 'localtime') >= :hora AND hi.ativo = 1 " +
            " ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1")
    LiveData<HorarioItinerarioNome> carregarPrimeiroPorPartidaEDestino(String idPartida, String idDestino,
                                                                  String hora);

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario FROM horario_itinerario hi " +
            "INNER JOIN horario h ON h.id = hi.horario WHERE itinerario IN (SELECT pi.itinerario " +
            "FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE p.bairro = :idPartida AND pi.ordem = 1 AND pi.ativo = 1) " +
            "AND p.bairro = :idDestino AND pi.ordem > 1 AND pi.ativo = 1) " +
            "AND TIME(h.nome/1000, 'unixepoch', 'localtime') > :hora AND hi.ativo = 1 " +
            " ORDER BY TIME(h.nome/1000, 'unixepoch', 'localtime') LIMIT 1")
    LiveData<HorarioItinerarioNome> carregarSeguintePorPartidaEDestino(String idPartida, String idDestino,
                                                                      String hora);

    @Query("SELECT hi.*, h.id AS idHorario, h.nome AS nomeHorario FROM horario_itinerario hi " +
            "INNER JOIN horario h ON h.id = hi.horario WHERE itinerario IN (SELECT pi.itinerario " +
            "FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada WHERE itinerario IN " +
            "(SELECT pi.itinerario FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada " +
            "WHERE p.bairro = :idPartida AND pi.ordem = 1 AND pi.ativo = 1) " +
            "AND p.bairro = :idDestino AND pi.ordem > 1 AND pi.ativo = 1) " +
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

    @Query("DELETE FROM horario_itinerario WHERE ativo = 0")
    void deletarInativos();

}
