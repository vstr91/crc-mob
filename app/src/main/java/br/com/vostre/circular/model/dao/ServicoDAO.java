package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Servico;

@Dao
public interface ServicoDAO {

    @Query("SELECT * FROM servico ORDER BY nome")
    LiveData<List<Servico>> listarTodos();

    @Query("SELECT * FROM servico ORDER BY nome")
    List<Servico> listarTodosSync();

    @Query("SELECT * FROM servico WHERE ativo = 1 ORDER BY nome")
    LiveData<List<Servico>> listarTodosAtivos();

    @Query("SELECT * FROM servico WHERE enviado = 0")
    List<Servico> listarTodosAEnviar();

    @Query("SELECT * FROM servico WHERE imagemEnviada = 0 AND icone IS NOT NULL")
    List<Servico> listarTodosImagemAEnviar();

    @Query("SELECT * FROM servico WHERE id IN (:ids)")
    List<Servico> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM servico WHERE id = :id")
    LiveData<Servico> carregarPorId(String id);

    @Query("SELECT * FROM servico WHERE nome LIKE :nome LIMIT 1")
    Servico encontrarPorNome(String nome);

    @Query("SELECT * FROM servico WHERE slug LIKE :slug LIMIT 1")
    Servico encontrarPorSlug(String slug);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Servico> paises);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Servico servico);

    @Update
    void editar(Servico servico);

    @Delete
    void deletar(Servico servico);

    @Query("DELETE FROM servico")
    void deletarTodos();

    @Query("DELETE FROM servico WHERE ativo = 0")
    void deletarInativos();

}
