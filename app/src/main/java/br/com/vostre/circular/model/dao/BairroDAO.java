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

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.pojo.BairroCidade;

@Dao
public interface BairroDAO {

    @Query("SELECT * FROM bairro ORDER BY nome")
    LiveData<List<Bairro>> listarTodos();

    @Query("SELECT * FROM bairro ORDER BY nome")
    List<Bairro> listarTodosSync();

    @Query("SELECT * FROM bairro WHERE ativo = 1 ORDER BY nome")
    List<Bairro> listarTodosAtivos();

    @Query("SELECT * FROM bairro WHERE enviado = 0")
    List<Bairro> listarTodosAEnviar();

    @Query("SELECT b.*, c.id AS idCidade, c.nome AS nomeCidade, c.brasao AS brasao, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado FROM bairro b " +
            "INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado ORDER BY b.nome, c.nome")
    LiveData<List<BairroCidade>> listarTodosComCidade();

    @Query("SELECT b.*, c.id AS idCidade, c.nome AS nomeCidade, c.brasao AS brasao, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado FROM bairro b " +
            "INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado WHERE b.cidade = :cidade ORDER BY b.nome COLLATE LOCALIZED ASC")
    LiveData<List<BairroCidade>> listarTodosComCidadePorCidade(String cidade);

    @Query("SELECT DISTINCT b.*, c.id AS idCidade, c.nome AS nomeCidade, c.brasao AS brasao, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado FROM bairro b " +
            "INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado INNER JOIN parada p ON p.bairro = b.id INNER JOIN parada_itinerario pi ON pi.parada = p.id WHERE b.cidade = :cidade " +
            "ORDER BY b.nome")
    LiveData<List<BairroCidade>> listarTodosAtivosComCidadePorCidade(String cidade);

    @Query("SELECT DISTINCT b.*, c.id AS idCidade, c.nome AS nomeCidade, c.brasao AS brasao, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado FROM bairro b " +
            "INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado INNER JOIN parada p ON p.bairro = b.id INNER JOIN parada_itinerario pi ON pi.parada = p.id WHERE b.cidade = :cidade " +
            "ORDER BY b.nome")
    List<BairroCidade> listarTodosAtivosComCidadePorCidadeSync(String cidade);

    @Query("SELECT b.*, c.id AS idCidade, c.nome AS nomeCidade, c.brasao AS brasao, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado FROM bairro b " +
            "INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado WHERE b.cidade = :cidade AND b.id != :bairro")
    LiveData<List<BairroCidade>> listarTodosComCidadePorCidadeFiltro(String cidade, String bairro);

    @Query("SELECT DISTINCT b.*, c.id AS idCidade, c.nome AS nomeCidade, c.brasao AS brasao, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado FROM bairro b " +
            "INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado INNER JOIN parada p ON p.bairro = b.id INNER JOIN parada_itinerario pi ON pi.parada = p.id " +
            "WHERE b.cidade = :cidade AND b.id != :bairro ORDER BY b.nome")
    LiveData<List<BairroCidade>> listarTodosAtivosComCidadePorCidadeFiltro(String cidade, String bairro);

    @Query("SELECT * FROM bairro WHERE id IN (:ids)")
    List<Bairro> carregarTodosPorIds(int[] ids);

    @Query("SELECT b.*, c.id AS idCidade, c.nome AS nomeCidade, c.brasao AS brasao, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado FROM bairro b " +
            "INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado WHERE b.id LIKE :id")
    LiveData<BairroCidade> carregar(String id);

    @Query("SELECT b.*, c.id AS idCidade, c.nome AS nomeCidade, c.brasao AS brasao, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado FROM bairro b " +
            "INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado WHERE b.id LIKE :id")
    BairroCidade carregarSync(String id);

    @Query("SELECT * FROM bairro WHERE nome LIKE :nome LIMIT 1")
    Bairro encontrarPorNome(String nome);

    @Query("SELECT * FROM bairro b INNER JOIN cidade c ON c.id = b.cidade WHERE b.nome LIKE :nome AND c.nome LIKE :cidade LIMIT 1")
    Bairro encontrarPorNomeECidade(String nome, String cidade);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Bairro> bairros);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Bairro bairro);

    @Update
    void editar(Bairro bairro);

    @Delete
    void deletar(Bairro bairro);

    @Query("DELETE FROM bairro")
    void deletarTodos();

    @Query("DELETE FROM bairro WHERE ativo = 0")
    void deletarInativos();

}
