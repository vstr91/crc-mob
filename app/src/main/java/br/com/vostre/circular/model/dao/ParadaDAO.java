package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.ParadaBairro;

@Dao
public interface ParadaDAO {

    @Query("SELECT * FROM parada")
    LiveData<List<Parada>> listarTodos();

    @Query("SELECT * FROM parada")
    List<Parada> listarTodosSync();

    @Query("SELECT * FROM parada WHERE enviado = 0")
    List<Parada> listarTodosAEnviar();

    @Query("SELECT * FROM parada WHERE imagemEnviada = 0 AND imagem IS NOT NULL")
    List<Parada> listarTodosImagemAEnviar();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado ORDER BY c.nome, b.nome, p.nome")
    LiveData<List<ParadaBairro>> listarTodosComBairro();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE c.id = :cidade ORDER BY b.nome, p.nome")
    LiveData<List<ParadaBairro>> listarTodosComBairroPorCidade(String cidade);

    @Query("SELECT DISTINCT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado INNER JOIN parada_itinerario pi ON pi.parada = p.id WHERE c.id = :cidade ORDER BY b.nome, p.nome")
    LiveData<List<ParadaBairro>> listarTodosAtivosComBairroPorCidade(String cidade);

    @Query("SELECT DISTINCT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE b.id = :bairro ORDER BY b.nome, p.nome")
    LiveData<List<ParadaBairro>> listarTodosComBairroPorBairro(String bairro);

    @Query("SELECT DISTINCT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE b.id = :bairro ORDER BY b.nome, p.nome")
    List<ParadaBairro> listarTodosComBairroPorBairroSync(String bairro);

    @Query("SELECT DISTINCT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, " +
            "e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada_itinerario pi INNER JOIN parada p ON pi.parada = p.id " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado INNER JOIN itinerario i ON i.id = pi.itinerario " +
            "WHERE c.id = :cidade AND pi.ativo = 1 AND i.ativo = 1 AND pi.ordem < " +
            "(SELECT MAX(pi3.ordem) FROM parada_itinerario pi3 WHERE pi3.itinerario = pi.itinerario AND pi3.ativo = 1) ORDER BY b.nome, p.nome, p.sentido")
    LiveData<List<ParadaBairro>> listarTodosAtivosComBairroPorCidadeComItinerario(String cidade);

    @Query("SELECT DISTINCT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, " +
            "e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada_itinerario pi INNER JOIN parada p ON pi.parada = p.id " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado INNER JOIN itinerario i ON i.id = pi.itinerario " +
            "WHERE b.id = :bairro AND pi.ativo = 1 AND i.ativo = 1 AND pi.ordem < i.totalParadas AND p.ativo = 1 ORDER BY b.nome, p.nome, p.sentido")
    List<ParadaBairro> listarTodosAtivosComBairroPorBairroComItinerarioSync(String bairro);

    @Query("SELECT DISTINCT p.id, p.nome, p.rua, p.sentido, p.imagem, b.id AS idBairro, b.nome AS nomeBairro " +
            "FROM parada_itinerario pi INNER JOIN parada p ON pi.parada = p.id " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN itinerario i ON i.id = pi.itinerario " +
            "INNER JOIN horario_itinerario hi ON hi.itinerario = i.id " +
            "WHERE b.id = :bairro AND pi.ativo = 1 AND i.ativo = 1 AND pi.ordem < i.totalParadas AND p.ativo = 1 " +
            "ORDER BY b.nome, p.nome, p.sentido")
    List<ParadaBairro> listarTodosAtivosComBairroPorBairroComItinerarioSimplificadoSync(String bairro);

    @Query("SELECT DISTINCT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, " +
            "e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada_itinerario pi INNER JOIN parada p ON pi.parada = p.id " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado INNER JOIN itinerario i ON i.id = pi.itinerario WHERE pi.ordem < " +
            "(SELECT MAX(pi3.ordem) FROM parada_itinerario pi3 WHERE pi3.itinerario = pi.itinerario AND pi3.ativo = 1) " +
            "AND pi.ativo = 1 AND p.ativo = 1 AND i.ativo = 1 ORDER BY b.nome, p.nome")
    LiveData<List<ParadaBairro>> listarTodosAtivosComBairroComItinerario();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.ativo = 1 ORDER BY c.nome, b.nome, p.nome")
    LiveData<List<ParadaBairro>> listarTodosAtivosComBairro();

    @Query("SELECT * FROM parada WHERE ativo = 1")
    LiveData<List<Parada>> listarTodosAtivos();

    @Query("SELECT * FROM parada WHERE ativo = 1")
    List<Parada> listarTodosAtivosSync();

    @Query("SELECT COUNT(id) FROM parada WHERE ativo = 1")
    LiveData<Integer> contarTodosAtivos();

    @Query("SELECT * FROM parada WHERE id IN (:ids)")
    List<Parada> carregarTodosPorIds(int[] ids);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.id = :parada")
    LiveData<ParadaBairro> carregarComBairro(String parada);

    @Query("SELECT b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.id = :parada")
    LiveData<ParadaBairro> carregarComBairroSimplificado(String parada);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.slug = :parada AND b.slug = :bairro AND c.slug = :cidade AND e.sigla = :uf")
    LiveData<ParadaBairro> carregarComBairroPorUFCidadeEBairro(String uf, String cidade, String bairro, String parada);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.id = :parada")
    ParadaBairro carregarComBairroSync(String parada);

    @Query("SELECT DISTINCT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p INNER JOIN parada_itinerario pi ON pi.parada = p.id " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE (p.latitude >= :minLat AND p.latitude <= :maxLat) " +
            "AND (p.longitude >= :minLng AND p.longitude <= :maxLng)")
    LiveData<List<ParadaBairro>> listarTodosAtivosProximos(double minLat, double maxLat, double minLng, double maxLng);

    @Query("SELECT p.* FROM parada p WHERE p.id = :parada")
    Parada carregarSync(String parada);

    @Query("SELECT * FROM parada WHERE nome LIKE :nome LIMIT 1")
    Parada encontrarPorNome(String nome);

    @Query("SELECT * FROM parada WHERE slug LIKE :slug LIMIT 1")
    Parada encontrarPorSlug(String slug);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Parada> paradas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Parada parada);

    @Update
    void editar(Parada parada);

    @Delete
    void deletar(Parada parada);

    @Query("DELETE FROM parada")
    void deletarTodos();

    @Query("DELETE FROM parada WHERE ativo = 0")
    void deletarInativos();

}
