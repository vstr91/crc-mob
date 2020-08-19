package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.PontoInteresseSugestao;
import br.com.vostre.circular.model.pojo.PontoInteresseSugestaoBairro;

@Dao
public interface PontoInteresseSugestaoDAO {

    @Query("SELECT * FROM ponto_interesse_sugestao")
    LiveData<List<PontoInteresseSugestao>> listarTodos();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM ponto_interesse_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado")
    LiveData<List<PontoInteresseSugestaoBairro>> listarTodosComBairro();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado, u.nome AS usuario FROM ponto_interesse_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado LEFT JOIN usuario u ON u.id = p.usuario_cadastro WHERE p.status = 0 ORDER BY p.data_cadastro DESC")
    LiveData<List<PontoInteresseSugestaoBairro>> listarTodosPendentesComBairro();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado, u.nome AS usuario FROM ponto_interesse_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado LEFT JOIN usuario u ON u.id = p.usuario_cadastro WHERE p.status = 1 ORDER BY p.data_cadastro DESC")
    LiveData<List<PontoInteresseSugestaoBairro>> listarTodosAceitosComBairro();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado, u.nome AS usuario FROM ponto_interesse_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado LEFT JOIN usuario u ON u.id = p.usuario_cadastro WHERE p.status = 2 ORDER BY p.data_cadastro DESC")
    LiveData<List<PontoInteresseSugestaoBairro>> listarTodosRejeitadosComBairro();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM ponto_interesse_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.ativo = 1 AND p.usuario_cadastro = :id ORDER BY p.data_cadastro DESC")
    LiveData<List<PontoInteresseSugestaoBairro>> listarTodosComBairroPorUsuario(String id);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM ponto_interesse_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.status = 0 AND p.ativo = 1 AND p.usuario_cadastro = :id ORDER BY p.data_cadastro DESC")
    LiveData<List<PontoInteresseSugestaoBairro>> listarTodosPendentesComBairroPorUsuario(String id);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM ponto_interesse_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.status = 1 AND p.ativo = 1 AND p.usuario_cadastro = :id ORDER BY p.data_cadastro DESC")
    LiveData<List<PontoInteresseSugestaoBairro>> listarTodosAceitosComBairroPorUsuario(String id);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM ponto_interesse_sugestao p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.status = 2 AND p.ativo = 1 AND p.usuario_cadastro = :id ORDER BY p.data_cadastro DESC")
    LiveData<List<PontoInteresseSugestaoBairro>> listarTodosRejeitadosComBairroPorUsuario(String id);

    @Query("SELECT * FROM ponto_interesse_sugestao WHERE enviado = 0")
    List<PontoInteresseSugestao> listarTodosAEnviar();

    @Query("SELECT * FROM ponto_interesse_sugestao WHERE imagemEnviada = 0 AND imagem IS NOT NULL")
    List<PontoInteresseSugestao> listarTodosImagemAEnviar();

    @Query("SELECT * FROM ponto_interesse_sugestao WHERE ativo = 1")
    LiveData<List<PontoInteresseSugestao>> listarTodosAtivos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<PontoInteresseSugestao> pois);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(PontoInteresseSugestao poi);

    @Update
    void editar(PontoInteresseSugestao poi);

    @Delete
    void deletar(PontoInteresseSugestao poi);

    @Query("DELETE FROM ponto_interesse_sugestao")
    void deletarTodos();

    @Query("DELETE FROM ponto_interesse_sugestao WHERE status <> 0 AND usuario_cadastro = :id")
    void deletarTodosNaoPendentesPorUsuarioLogado(String id);

    @Query("DELETE FROM ponto_interesse_sugestao WHERE ativo = 0")
    void deletarInativos();

}
