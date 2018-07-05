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

    @Query("SELECT * FROM cidade WHERE ativo = 1 ORDER BY nome")
    LiveData<List<Cidade>> listarTodosAtivos();

    @Query("SELECT c.*, e.id AS idEstado, e.nome AS nomeEstado FROM cidade c INNER JOIN estado e ON e.id = c.estado ORDER BY c.nome")
    LiveData<List<CidadeEstado>> listarTodosAtivasComEstado();

    @Query("SELECT * FROM cidade WHERE enviado = 0")
    List<Cidade> listarTodosAEnviar();

    @Query("SELECT * FROM cidade WHERE imagemEnviada = 0 AND brasao IS NOT NULL")
    List<Cidade> listarTodosImagemAEnviar();

    @Query("SELECT c.*, e.id AS idEstado, e.nome AS nomeEstado FROM cidade c INNER JOIN estado e ON e.id = c.estado ORDER BY c.nome")
    LiveData<List<CidadeEstado>> listarTodosComEstado();

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
