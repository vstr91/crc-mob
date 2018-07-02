package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.ParadaBairro;

@Dao
public interface ParadaDAO {

    @Query("SELECT * FROM parada")
    LiveData<List<Parada>> listarTodos();

    @Query("SELECT * FROM parada WHERE enviado = 0")
    List<Parada> listarTodosAEnviar();

    @Query("SELECT * FROM parada WHERE imagemEnviada = 0 AND imagem IS NOT NULL")
    List<Parada> listarTodosImagemAEnviar();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado")
    LiveData<List<ParadaBairro>> listarTodosComBairro();

    @Query("SELECT p.*, b.id AS idBairro, b.nome AS nomeBairro, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, " +
            "e.nome AS nomeEstado, e.sigla AS siglaEstado FROM parada p " +
            "INNER JOIN bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN " +
            "estado e ON e.id = c.estado WHERE p.ativo = 1")
    LiveData<List<ParadaBairro>> listarTodosAtivosComBairro();

    @Query("SELECT * FROM parada WHERE ativo = 1")
    List<Parada> listarTodosAtivos();

    @Query("SELECT * FROM parada WHERE id IN (:ids)")
    List<Parada> carregarTodosPorIds(int[] ids);

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

}
