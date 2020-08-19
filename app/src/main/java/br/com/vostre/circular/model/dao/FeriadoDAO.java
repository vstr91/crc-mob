package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Feriado;

@Dao
public interface FeriadoDAO {

    @Query("SELECT * FROM feriado ORDER BY strftime('%Y/%m/%d', data/1000, 'unixepoch')")
    LiveData<List<Feriado>> listarTodos();

    @Query("SELECT * FROM feriado ORDER BY strftime('%Y/%m/%d', data/1000, 'unixepoch')")
    List<Feriado> listarTodosSync();

    @Query("SELECT * FROM feriado WHERE ativo = 1 ORDER BY strftime('%Y/%m/%d', data/1000, 'unixepoch')")
    LiveData<List<Feriado>> listarTodosAtivos();

    @Query("SELECT * FROM feriado WHERE enviado = 0")
    List<Feriado> listarTodosAEnviar();

    @Query("SELECT * FROM feriado WHERE id LIKE :id")
    LiveData<Feriado> carregar(String id);

    @Query("SELECT * FROM feriado WHERE id LIKE :id")
    Feriado carregarSync(String id);

    @Query("SELECT * FROM feriado WHERE nome LIKE :nome LIMIT 1")
    Feriado encontrarPorNome(String nome);

    @Query("SELECT * FROM feriado WHERE strftime('%Y-%m-%d', data/1000, 'unixepoch') LIKE :data AND ativo = 1 LIMIT 1")
    Feriado encontrarPorData(String data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Feriado> feriados);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Feriado feriado);

    @Update
    void editar(Feriado feriado);

    @Delete
    void deletar(Feriado feriado);

    @Query("DELETE FROM feriado")
    void deletarTodos();

    @Query("UPDATE feriado SET ativo = 0, ultima_alteracao = datetime('now'), enviado = 0 WHERE strftime('%Y', data/1000, 'unixepoch') = :ano")
    void deletarTodosPorAno(String ano);

    @Query("DELETE FROM feriado WHERE ativo = 0")
    void deletarInativos();

}
