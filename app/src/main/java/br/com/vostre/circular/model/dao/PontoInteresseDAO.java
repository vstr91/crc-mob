package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.PontoInteresse;
import br.com.vostre.circular.model.pojo.PontoInteresseBairro;

@Dao
public interface PontoInteresseDAO {

    @Query("SELECT * FROM ponto_interesse")
    LiveData<List<PontoInteresse>> listarTodos();

    @Query("SELECT * FROM ponto_interesse")
    List<PontoInteresse> listarTodosSync();

    @Query("SELECT * FROM ponto_interesse WHERE ativo = 1")
    LiveData<List<PontoInteresse>> listarTodosAtivos();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "            e.nome AS nomeEstado, e.sigla AS siglaEstado FROM ponto_interesse p " +
            "            INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "            estado e ON e.id = c.estado")
    LiveData<List<PontoInteresseBairro>> listarTodosAtivosComBairro();

    @Query("SELECT * FROM ponto_interesse WHERE enviado = 0")
    List<PontoInteresse> listarTodosAEnviar();

    @Query("SELECT * FROM ponto_interesse WHERE imagemEnviada = 0 AND imagem IS NOT NULL")
    List<PontoInteresse> listarTodosImagemAEnviar();

    @Query("SELECT p.* FROM ponto_interesse p WHERE (latitude >= :minLat AND latitude <= :maxLat) " +
            "AND (longitude >= :minLng AND longitude <= :maxLng) AND p.ativo = 1")
    LiveData<List<PontoInteresse>> listarTodosAtivosProximos(double minLat, double maxLat, double minLng, double maxLng);

    @Query("SELECT * FROM ponto_interesse WHERE id IN (:ids)")
    List<PontoInteresse> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM ponto_interesse WHERE id = :id")
    PontoInteresse carregarSync(String id);

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM ponto_interesse p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.id = :poi")
    LiveData<PontoInteresseBairro> carregarComBairro(String poi);

    @Query("SELECT * FROM ponto_interesse WHERE nome LIKE :nome LIMIT 1")
    PontoInteresse encontrarPorNome(String nome);

    @Query("SELECT * FROM ponto_interesse WHERE slug LIKE :slug LIMIT 1")
    PontoInteresse encontrarPorSlug(String slug);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<PontoInteresse> pontosInteresse);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(PontoInteresse pontoInteresse);

    @Update
    void editar(PontoInteresse pontoInteresse);

    @Delete
    void deletar(PontoInteresse pontoInteresse);

    @Query("DELETE FROM ponto_interesse")
    void deletarTodos();

    @Query("DELETE FROM ponto_interesse WHERE ativo = 0")
    void deletarInativos();

}
