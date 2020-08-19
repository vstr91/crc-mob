package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaItinerario;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaItinerarioBairro;

@Dao
public interface ParadaItinerarioDAO {

    @Query("SELECT * FROM parada_itinerario")
    List<ParadaItinerario> listarTodosSync();

    @Query("SELECT pi.*, pi.itinerario AS idItinerario, p.id AS idParada, p.nome AS nomeParada, " +
            "p.latitude AS latitude, p.longitude AS longitude, " +
            "b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade " +
            "WHERE p.ativo = 1 AND pi.itinerario = :itinerario")
    LiveData<List<ParadaItinerarioBairro>> listarTodosPorItinerarioComBairro(String itinerario);

    @Query("SELECT pi.*, pi.itinerario AS idItinerario, p.id AS idParada, p.nome AS nomeParada, " +
            "p.latitude AS latitude, p.longitude AS longitude, " +
            "b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade " +
            "WHERE p.ativo = 1 AND pi.itinerario = :itinerario AND pi.ativo = 1 ORDER BY pi.ordem")
    List<ParadaItinerarioBairro> listarTodosPorItinerarioComBairroSync(String itinerario);

    @Query("SELECT pi.*, p.id AS idParada, p.nome AS nomeParada, p.latitude AS latitude, p.longitude AS longitude, " +
            "b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade " +
            "WHERE pi.ativo = 1 AND pi.itinerario = :itinerario AND pi.ativo = 1 ORDER BY pi.ordem")
    LiveData<List<ParadaItinerarioBairro>> listarTodosAtivosPorItinerarioComBairro(String itinerario);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado " +
            "WHERE pi.ativo = 1 AND pi.itinerario = :itinerario AND pi.ativo = 1 ORDER BY pi.ordem")
    LiveData<List<ParadaBairro>> listarParadasAtivasPorItinerarioComBairro(String itinerario);

    @Query("SELECT p.*, MIN(pi.ordem), MAX(pi.ordem) " +
            "FROM parada_itinerario pi INNER JOIN " +
            "     parada p ON p.id = pi.parada " +
            "WHERE itinerario = :itinerario " +
            "AND pi.ativo = 1 " +
            "AND p.ativo = 1 " +
            "GROUP BY p.rua " +
            "ORDER BY pi.itinerario, pi.ordem")
    List<Parada> listarParadasAtivasPorItinerarioERuaSync(String itinerario);

    @Query("SELECT p.id, /*p.nome,*/ p.latitude, p.longitude/*, p.imagem, p.cep, p.rua, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado*/ " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado " +
            "WHERE pi.ativo = 1 AND pi.itinerario = :itinerario AND pi.ativo = 1 ORDER BY pi.ordem")
    LiveData<List<ParadaBairro>> listarParadasAtivasPorItinerarioComBairroSimplificado(String itinerario);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado " +
            "WHERE pi.ativo = 1 AND pi.itinerario IN(" +
            "SELECT DISTINCT i.id " +
            "            FROM itinerario i INNER JOIN " +
            "                 parada_itinerario pi ON pi.itinerario = i.id INNER JOIN" +
            "                 parada p ON p.id = pi.parada INNER JOIN" +
            "                 parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN" +
            "                 parada pd ON pd.id = pi2.parada" +
            "            WHERE p.bairro = :partida AND pd.bairro = :destino " +
            "            AND pi2.ordem > pi.ordem " +
            "            ORDER BY i.id" +
            ") AND pi.ativo = 1 ORDER BY pi.ordem")
    LiveData<List<ParadaBairro>> listarParadasAtivasPorItinerarioComBairroTrecho(String partida, String destino);

    @Query("SELECT p.id, /*p.nome,*/ p.latitude, p.longitude/*, p.imagem, p.cep, p.rua, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado*/ " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado " +
            "WHERE pi.ativo = 1 AND pi.itinerario IN(" +
            "SELECT DISTINCT i.id " +
            "            FROM itinerario i INNER JOIN " +
            "                 parada_itinerario pi ON pi.itinerario = i.id INNER JOIN" +
            "                 parada p ON p.id = pi.parada INNER JOIN" +
            "                 parada_itinerario pi2 ON pi2.itinerario = i.id INNER JOIN" +
            "                 parada pd ON pd.id = pi2.parada" +
            "            WHERE p.bairro = :partida AND pd.bairro = :destino " +
            "            AND pi2.ordem > pi.ordem " +
            "            ORDER BY i.id" +
            ") AND pi.ativo = 1 ORDER BY pi.ordem")
    LiveData<List<ParadaBairro>> listarParadasAtivasPorItinerarioComBairroTrechoSimplificado(String partida, String destino);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado " +
            "WHERE pi.ativo = 1 AND pi.itinerario = :itinerario AND pi.ativo = 1 ORDER BY pi.ordem")
    List<ParadaBairro> listarParadasAtivasPorItinerarioComBairroSync(String itinerario);

    @Query("SELECT p.id, p.nome, p.latitude, p.longitude, b.nome AS nomeBairro, c.nome AS nomeCidade, e.nome AS nomeEstado, " +
            "e.sigla AS siglaEstado " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado " +
            "WHERE pi.ativo = 1 AND pi.itinerario = :itinerario AND pi.ativo = 1 ORDER BY pi.ordem")
    List<ParadaBairro> listarParadasAtivasPorItinerarioComBairroSimplificadoSync(String itinerario);

    @Query("SELECT pi.* "+
            "FROM parada_itinerario pi " +
            "WHERE pi.ativo = 1 AND pi.itinerario = :itinerario " +
            "AND pi.ordem < (SELECT pi2.ordem FROM parada_itinerario pi2 WHERE pi2.parada = :paradaLimite " +
            "AND pi2.itinerario = pi.itinerario) ORDER BY pi.ordem")
    List<ParadaItinerario> listarParadasAtivasPorItinerarioComBairroSync(String itinerario, String paradaLimite);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado " +
            "WHERE pi.ativo = 1 AND pi.itinerario = :itinerario AND p.id <> :parada ORDER BY pi.ordem")
    LiveData<List<ParadaBairro>> listarParadasAtivasPorItinerarioComBairroSemParadaInicial(String itinerario, String parada);

    @Query("SELECT i.* FROM parada_itinerario pi INNER JOIN itinerario i ON i.id = pi.itinerario " +
            "WHERE pi.parada = :parada AND pi.ativo = 1 AND i.ativo = 1")
    List<Itinerario> listarTodosAtivosPorParada(String parada);

    @Query("SELECT * FROM parada_itinerario WHERE enviado = 0")
    List<ParadaItinerario> listarTodosAEnviar();

    @Query("SELECT pi.* FROM parada_itinerario pi WHERE pi.parada = :parada AND pi.itinerario = :itinerario")
    ParadaItinerario checaDuplicidade(String parada, String itinerario);

    @Query("UPDATE parada_itinerario SET ativo = 0, ultima_alteracao = datetime('now'), enviado = 0 WHERE itinerario = :itinerario")
    void invalidaTodosPorItinerario(String itinerario);

    @Query("SELECT pi.*, pi.itinerario AS idItinerario, p.id AS idParada, p.nome AS nomeParada, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade " +
            "FROM parada_itinerario pi " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade " +
            "WHERE p.ativo = 1 AND p.id = :parada AND pi.itinerario = :itinerario")
    ParadaItinerarioBairro carregar(String parada, String itinerario);

    @Query("SELECT pi.* " +
            "FROM parada_itinerario pi " +
            "WHERE pi.ativo = 1 AND pi.parada = :parada AND pi.itinerario = :itinerario")
    ParadaItinerario carregarParadaItinerario(String parada, String itinerario);

    @Query("SELECT * FROM parada_itinerario pi " +
            "WHERE pi.itinerario = :itinerario " +
            "AND pi.ordem >= " +
            "(" +
            "SELECT pi2.ordem FROM parada_itinerario pi2 INNER JOIN itinerario i ON i.id = pi2.itinerario INNER JOIN parada p ON p.id = pi2.parada " +
            "WHERE p.bairro = :partida AND pi.ativo = 1 " +
            "AND i.id = :itinerario ORDER BY pi2.ordem DESC LIMIT 1" +
            ")" +
            "AND pi.ordem < " +
            "(" +
            "SELECT pi2.ordem FROM parada_itinerario pi2 INNER JOIN itinerario i ON i.id = pi2.itinerario INNER JOIN parada p ON p.id = pi2.parada " +
            "WHERE p.bairro = :destino AND pi.ativo = 1 " +
            "AND i.id = :itinerario ORDER BY pi2.ordem DESC LIMIT 1" +
            ")" +
            "ORDER BY pi.ordem")
    List<ParadaItinerario> listarTrechosIntervalo(String itinerario, String partida, String destino);

    @Query("SELECT DISTINCT p.rua" +
            "    FROM parada_itinerario pi" +
            "    INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado" +
            "    WHERE pi.ativo = 1 AND pi.itinerario = :itinerario AND pi.ativo = 1 ORDER BY pi.ordem")
    LiveData<List<ParadaBairro>> listarRuasPorItinerario(String itinerario);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<ParadaItinerario> paradasItinerarios);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(ParadaItinerario paradaItinerario);

    @Update
    void editar(ParadaItinerario paradaItinerario);

    @Delete
    void deletar(ParadaItinerario paradaItinerario);

    @Query("DELETE FROM parada_itinerario")
    void deletarTodos();

    @Query("DELETE FROM parada_itinerario WHERE ativo = 0")
    void deletarInativos();

}
