package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;

@Dao
public interface CidadeDAO {

    @Query("SELECT * FROM cidade ORDER BY nome")
    LiveData<List<Cidade>> listarTodos();

    @Query("SELECT * FROM cidade ORDER BY nome")
    List<Cidade> listarTodosSync();

    @Query("SELECT * FROM cidade WHERE ativo = 1 ORDER BY nome")
    LiveData<List<Cidade>> listarTodosAtivos();

    @Query("SELECT COUNT(DISTINCT c.id) FROM cidade c INNER JOIN bairro b ON b.cidade = c.id WHERE c.ativo = 1 AND b.ativo = 1")
    LiveData<Integer> contarTodosAtivos();

    @Query("SELECT c.*, e.id AS idEstado, e.nome AS nomeEstado FROM cidade c INNER JOIN estado e ON e.id = c.estado " +
            "            WHERE (SELECT COUNT(b.id) FROM bairro b WHERE b.cidade = c.id) > 0 " +
            "            AND (SELECT COUNT(pi.id) FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro " +
            "            WHERE c.id = b.cidade AND pi.ativo = 1) > 0 " +
            "AND (" +
            "    SELECT COUNT(DISTINCT hi.id) FROM horario_itinerario hi INNER JOIN itinerario i ON i.id = hi.itinerario INNER JOIN parada_itinerario pi ON pi.itinerario = i.id " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro " +
            "    WHERE b.cidade = c.id " +
            "    AND (hi.domingo = 1 OR hi.segunda = 1 OR hi.terca = 1 OR hi.quarta = 1 OR hi.quinta = 1 OR hi.sexta = 1 OR hi.sabado = 1) " +
            ") > 0 " +
            "AND c.ativo = 1 ORDER BY c.nome")
    LiveData<List<CidadeEstado>> listarTodosAtivasComEstado();

    @Query("SELECT c.*, e.id AS idEstado, e.nome AS nomeEstado FROM cidade c INNER JOIN estado e ON e.id = c.estado " +
            "WHERE (SELECT COUNT(b.id) FROM bairro b WHERE b.cidade = c.id AND b.id != :bairro) > 0 " +
            "AND (SELECT COUNT(pi.id) FROM parada_itinerario pi INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro " +
            "WHERE c.id = b.cidade AND pi.ativo = 1 AND b.id != :bairro) > 0 AND ( " +
            "    SELECT COUNT(DISTINCT hi.id) FROM horario_itinerario hi INNER JOIN itinerario i ON i.id = hi.itinerario INNER JOIN parada_itinerario pi ON pi.itinerario = i.id " +
            "INNER JOIN parada p ON p.id = pi.parada INNER JOIN bairro b ON b.id = p.bairro " +
            "    WHERE b.cidade = c.id " +
            "    AND (hi.domingo = 1 OR hi.segunda = 1 OR hi.terca = 1 OR hi.quarta = 1 OR hi.quinta = 1 OR hi.sexta = 1 OR hi.sabado = 1) " +
            ") > 0  AND c.ativo = 1 ORDER BY c.nome")
    LiveData<List<CidadeEstado>> listarTodosAtivasComEstadoFiltro(String bairro);

    @Query("SELECT * FROM cidade WHERE enviado = 0")
    List<Cidade> listarTodosAEnviar();

    @Query("SELECT * FROM cidade WHERE imagemEnviada = 0 AND brasao IS NOT NULL")
    List<Cidade> listarTodosImagemAEnviar();

    @Query("SELECT c.*, e.id AS idEstado, e.nome AS nomeEstado, (SELECT COUNT(DISTINCT id) FROM bairro b2 WHERE b2.cidade = c.id) AS 'totalBairros' " +
            "FROM cidade c INNER JOIN estado e ON e.id = c.estado ORDER BY c.nome")
    LiveData<List<CidadeEstado>> listarTodosComEstado();

    @Query("SELECT c.*, e.id AS idEstado, e.nome AS nomeEstado FROM cidade c INNER JOIN estado e ON e.id = c.estado WHERE c.id = :id")
    LiveData<CidadeEstado> carregar(String id);

    @Query("SELECT * FROM cidade WHERE id IN (:ids)")
    List<Cidade> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM cidade WHERE nome LIKE :nome LIMIT 1")
    Cidade encontrarPorNome(String nome);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Cidade> cidadees);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Cidade cidade);

    @Update
    void editar(Cidade cidade);

    @Delete
    void deletar(Cidade cidade);

    @Query("DELETE FROM cidade")
    void deletarTodos();

}
