package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;
import java.util.UUID;

import br.com.vostre.circular.model.FeedbackItinerario;
import br.com.vostre.circular.model.Tpr;
import br.com.vostre.circular.model.Tpr2;
import br.com.vostre.circular.model.pojo.TrechoPartidaDestino;
import br.com.vostre.circular.utils.StringUtils;

@Dao
public interface TemporariasDAO {

    @RawQuery()
    String atualizaTemporaria(SimpleSQLiteQuery query);

    @Query("UPDATE tpr SET ultima_alteracao = strftime('%s', datetime('now')), enviado = 0, ativo = 0 WHERE idItinerario = :itinerario")
    void invalidaTemporariaPorItinerario(String itinerario);

    @Query("UPDATE tpr2 SET ultima_alteracao = strftime('%s', datetime('now')), enviado = 0, ativo = 0 WHERE idItinerario = :itinerario")
    void invalidaTemporaria2PorItinerario(String itinerario);

    @Query("INSERT INTO tpr (id, sigla, tarifa, distanciaAcumuladaInicial, distanciaAcumulada, tempo, acessivel, empresa, observacao, mostraRua, idItinerario, " +
            "idBairroPartida, bairroPartida, cidadePartida, idBairroDestino, bairroDestino, cidadeDestino, " +
            "distanciaTrechoMetros, tempoTrecho, tarifaTrecho, inicio, fim, aliasBairroPartida, aliasCidadePartida, aliasBairroDestino, aliasCidadeDestino, ativo, enviado, data_cadastro, ultima_alteracao) " +
            "                                  SELECT lower(hex( randomblob(4)) || '-' || hex( randomblob(2)) || '-' || '4' || " +
            "                                   substr( hex( randomblob(2)), 2) || '-' || substr('AB89', 1 + (abs(random()) % 4) , 1)  || " +
            "                                   substr(hex(randomblob(2)), 2) || '-' || hex(randomblob(6))), " +
            "i.sigla, i.tarifa," +
            "                 IFNULL((  " +
            "                                   SELECT pi2.distanciaAcumulada FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario  " +
            "                                   INNER JOIN bairro b2 ON b2.id = p2.bairro  " +
            "                                   WHERE pi2.ordem =  (  " +
            "                                   SELECT MAX(pi2.ordem) FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
            "                                   INNER JOIN bairro b2 ON b2.id = p2.bairro  " +
            "                                   WHERE b2.id = ( SELECT b2.id FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
            "                                                   INNER JOIN bairro b2 ON b2.id = p2.bairro  " +
            "                                                   WHERE pi2.ordem = (SELECT MAX(pi.ordem)) " +
            "                                                   ) " +
            "                                  ) " +
            "                                  ), 0) AS 'distanciaAcumuladaInicial', " +
            "                  IFNULL(( " +
            "                                   SELECT pi2.distanciaAcumulada FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
            "                                   INNER JOIN bairro b2 ON b2.id = p2.bairro  " +
            "                                   WHERE pi2.ordem =  (  " +
            "                                   SELECT MAX(pi2.ordem) FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
            "                                   INNER JOIN bairro b2 ON b2.id = p2.bairro  " +
            "                                   WHERE b2.id = ( SELECT b2.id FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
            "                                                   INNER JOIN bairro b2 ON b2.id = p2.bairro  " +
            "                                                   WHERE pi2.ordem = (SELECT MAX(pi.ordem)+1) " +
            "                                                   ) " +
            "                                  ) " +
            "                                  ) - ( " +
            "                                   SELECT pi2.distanciaAcumulada FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
            "                                   INNER JOIN bairro b2 ON b2.id = p2.bairro  " +
            "                                   WHERE pi2.ordem =  (  " +
            "                                   SELECT MAX(pi2.ordem) FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
            "                                   INNER JOIN bairro b2 ON b2.id = p2.bairro  " +
            "                                   WHERE b2.id = ( SELECT b2.id FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
            "                                                   INNER JOIN bairro b2 ON b2.id = p2.bairro  " +
            "                                                   WHERE pi2.ordem = (SELECT MAX(pi.ordem))  " +
            "                                                   ) " +
            "                                  ) " +
            "                                  ), 0) AS 'distanciaAcumulada',  " +

            "                 i.tempo AS 'tempo', i.acessivel, i.empresa, i.observacao, i.mostraRuas, " +
            "                                  i.id, " +
            "                                  b.id as 'idBairroPartida', b.nome AS 'bairroPartida', c.nome AS 'cidadePartida', " +
            "                                  (  " +
            "                                   SELECT b2.id FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
            "                                   INNER JOIN bairro b2 ON b2.id = p2.bairro " +
            "                                   WHERE pi2.ordem = (SELECT MAX(pi.ordem)+1)  " +
            "                                  ) AS 'idBairroDestino', " +
            "                                  (" +
            "                                   SELECT b2.nome FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
            "                                   INNER JOIN bairro b2 ON b2.id = p2.bairro " +
            "                                   WHERE pi2.ordem = (SELECT MAX(pi.ordem)+1) " +
            "                                  ) AS 'bairroDestino', " +
            "                                  (  " +
            "                                   SELECT c2.nome FROM parada_itinerario pi2 INNER JOIN parada p2 ON p2.id = pi2.parada AND pi2.itinerario = pi.itinerario " +
            "                                   INNER JOIN bairro b2 ON b2.id = p2.bairro INNER JOIN cidade c2 ON c2.id = b2.cidade  " +
            "                                   WHERE pi2.ordem = (SELECT MAX(pi.ordem)+1)  " +
            "                                  ) AS 'cidadeDestino', " +

            "                                   IFNULL(SUM(pi.distanciaSeguinteMetros), 0) AS 'distanciaTrechoMetros', " +
            "                                   IFNULL(SUM(pi.tempoSeguinte), 0) AS 'tempoTrecho', IFNULL(SUM(pi.valorSeguinte), 0) AS 'tarifaTrecho', " +
            "                                  MIN(pi.ordem) AS 'inicio', MAX(pi.ordem) AS 'fim', i.aliasBairroPartida, i.aliasCidadePartida, " +
            "                                  i.aliasBairroDestino, i.aliasCidadeDestino, 1, 0, strftime('%s', datetime('now')), strftime('%s', datetime('now')) " +

            "                                   FROM parada_itinerario pi INNER JOIN  " +
            "                                       parada p ON p.id = pi.parada INNER JOIN " +
            "                                       bairro b ON b.id = p.bairro INNER JOIN " +
            "                                       cidade c ON c.id = b.cidade INNER JOIN " +
            "                                       itinerario i ON i.id = pi.itinerario " +

            "                                   WHERE pi.ativo = 1 AND i.ativo = 1 AND pi.itinerario = :itinerario " +
            "                                   GROUP BY i.id, b.id, b.nome, c.nome, i.tarifa " +
            "                                   HAVING idBairroDestino != '' " +
            "                                   ORDER BY pi.itinerario, pi.ordem;")
    void atualizaTemporariaPorItinerario(String itinerario);

    @Query("INSERT INTO tpr2 (id, idItinerario, distanciaMetros, idBairroPartida, idBairroDestino, distanciaTrechoMetros, flagTrecho, ativo, enviado, data_cadastro, ultima_alteracao) " +
            "                                  SELECT DISTINCT lower(hex( randomblob(4)) || '-' || hex( randomblob(2)) || '-' || '4' || " +
            "                                   substr( hex( randomblob(2)), 2) || '-' || substr('AB89', 1 + (abs(random()) % 4) , 1)  || " +
            "                                   substr(hex(randomblob(2)), 2) || '-' || hex(randomblob(6))), t1.idItinerario, 0,  " +
            "                                         t1.idBairroPartida,  " +
            "                                         t2.idBairroDestino, " +

            "                                         t2.distanciaAcumuladaInicial + t2.distanciaAcumulada - t1.distanciaAcumuladaInicial, " +
            "                                         CASE WHEN (t1.inicio = 1 AND (  " +
            "                                                    t2.idBairroDestino = (  " +
            "                                                                             SELECT p3.bairro " +
            "                                                                               FROM parada_itinerario pi3 " +
            "                                                                                    INNER JOIN " +
            "                                                                                    parada p3 ON p3.id = pi3.parada " +
            "                                                                              WHERE pi3.itinerario = t1.idItinerario " +
            "                                                                              ORDER BY pi3.ordem DESC " +
            "                                                                              LIMIT 1 " +
            "                                                                         ) ) " +
            "                                             ) THEN 0 ELSE 1 END, 1, 0, strftime('%s', datetime('now')), strftime('%s', datetime('now')) " +
            "                                    FROM tpr t1 INNER JOIN " +
            "                                         tpr t2 ON t1.idItinerario = t2.idItinerario AND  " +
            "                                                   t1.fim <= t2.fim WHERE t1.ativo = 1 AND t2.ativo = 1 AND (t1.idItinerario = :itinerario AND t2.idItinerario = :itinerario) " +
            "                                   ORDER BY t1.idItinerario,  " +
            "                                            t1.inicio, " +
            "                                            t2.inicio")
    void atualizaTemporaria2PorItinerario(String itinerario);

    @Query("SELECT * FROM tpr")
    LiveData<List<Tpr>> listarTodosTemp1();

    @Query("SELECT * FROM tpr2")
    LiveData<List<Tpr2>> listarTodosTemp2();

    @Query("SELECT t.*, bp.nome AS bairroPartida, cp.nome AS cidadePartida, bd.nome AS bairroDestino, cd.nome AS cidadeDestino " +
            "FROM tpr2 t INNER JOIN bairro bp ON bp.id = t.idBairroPartida INNER JOIN cidade cp ON cp.id = bp.cidade INNER JOIN " +
            "bairro bd ON bd.id = t.idBairroDestino INNER JOIN cidade cd ON cd.id = bd.cidade " +
            "WHERE t.ativo = 1 " +
            "AND t.idItinerario = :itinerario ORDER BY bp.nome, bd.nome")
    LiveData<List<TrechoPartidaDestino>> listarTodosTemp2PorItinerario(String itinerario);

    @Query("SELECT * FROM tpr")
    List<Tpr> listarTodosTemp1Sync();

    @Query("SELECT * FROM tpr2")
    List<Tpr2> listarTodosTemp2Sync();

    @Query("SELECT * FROM tpr WHERE enviado = 0")
    List<Tpr> listarTodosTemp1AEnviar();

    @Query("SELECT * FROM tpr2 WHERE enviado = 0")
    List<Tpr2> listarTodosTemp2AEnviar();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodosTemp1(List<Tpr> tpr);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodosTemp2(List<Tpr2> tpr2);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Tpr tpr);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Tpr2 tpr2);

    @Update
    void editar(Tpr tpr);

    @Update
    void editar(Tpr2 tpr2);

    @Delete
    void deletar(Tpr tpr);

    @Delete
    void deletar(Tpr2 tpr2);

    @Query("DELETE FROM tpr WHERE ativo = 0")
    void deletarInativos();

    @Query("DELETE FROM tpr2 WHERE ativo = 0")
    void deletarInativos2();

}
