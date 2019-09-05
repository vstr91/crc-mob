package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;

@Dao
public interface ParadaSugestaoDAO {

    @Query("SELECT * FROM parada_sugestao")
    LiveData<List<ParadaSugestao>> listarTodos();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado ORDER BY p.data_cadastro DESC")
    LiveData<List<ParadaSugestaoBairro>> listarTodosComBairro();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado, u.nome AS usuario FROM parada_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado LEFT JOIN usuario u ON u.id = p.usuario_cadastro WHERE p.status = 0 ORDER BY p.data_cadastro DESC")
    LiveData<List<ParadaSugestaoBairro>> listarTodosPendentesComBairro();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado, u.nome AS usuario FROM parada_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado LEFT JOIN usuario u ON u.id = p.usuario_cadastro WHERE p.status = 1 ORDER BY p.data_cadastro DESC")
    LiveData<List<ParadaSugestaoBairro>> listarTodosAceitosComBairro();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado, u.nome AS usuario FROM parada_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado LEFT JOIN usuario u ON u.id = p.usuario_cadastro WHERE p.status = 2 ORDER BY p.data_cadastro DESC")
    LiveData<List<ParadaSugestaoBairro>> listarTodosRejeitadosComBairro();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.ativo = 1 AND p.usuario_cadastro = :id ORDER BY p.data_cadastro DESC")
    LiveData<List<ParadaSugestaoBairro>> listarTodosComBairroPorUsuario(String id);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.status = 0 AND p.ativo = 1 AND p.usuario_cadastro = :id " +
            "ORDER BY p.data_cadastro DESC")
    LiveData<List<ParadaSugestaoBairro>> listarTodosPendentesComBairroPorUsuario(String id);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.status = 1 AND p.ativo = 1 AND p.usuario_cadastro = :id " +
            "ORDER BY p.data_cadastro DESC")
    LiveData<List<ParadaSugestaoBairro>> listarTodosAceitosComBairroPorUsuario(String id);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.status = 2 AND p.ativo = 1 AND p.usuario_cadastro = :id " +
            "ORDER BY p.data_cadastro DESC")
    LiveData<List<ParadaSugestaoBairro>> listarTodosRejeitadosComBairroPorUsuario(String id);

    @Query("SELECT * FROM parada_sugestao WHERE enviado = 0")
    List<ParadaSugestao> listarTodosAEnviar();

    @Query("SELECT * FROM parada_sugestao WHERE imagemEnviada = 0 AND imagem IS NOT NULL")
    List<ParadaSugestao> listarTodosImagemAEnviar();

    @Query("SELECT * FROM parada_sugestao WHERE ativo = 1")
    LiveData<List<ParadaSugestao>> listarTodosAtivos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<ParadaSugestao> paradas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(ParadaSugestao parada);

    @Update
    void editar(ParadaSugestao parada);

    @Delete
    void deletar(ParadaSugestao parada);

    @Query("DELETE FROM parada_sugestao")
    void deletarTodos();

    @Query("DELETE FROM parada_sugestao WHERE status <> 0 AND usuario_cadastro = :id")
    void deletarTodosNaoPendentesPorUsuarioLogado(String id);

    @Query("DELETE FROM parada_sugestao WHERE ativo = 0")
    void deletarInativos();

}
