package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Horario;

@Dao
public interface HorarioDAO {

    @Query("SELECT * FROM horario")
    LiveData<List<Horario>> listarTodos();

    @Query("SELECT * FROM horario")
    List<Horario> listarTodosSync();

    @Query("SELECT * FROM horario WHERE ativo = 1")
    LiveData<List<Horario>> listarTodosAtivos();

    @Query("SELECT * FROM horario WHERE ativo = 1")
    List<Horario> listarTodosAtivosSync();

    @Query("SELECT * FROM horario WHERE enviado = 0")
    List<Horario> listarTodosAEnviar();

    @Query("SELECT * FROM horario WHERE id IN (:ids)")
    List<Horario> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM horario WHERE id = :id")
    LiveData<Horario> carregarPorId(String id);

    @Query("SELECT * FROM horario WHERE TIME(nome/1000, 'unixepoch', 'localtime') LIKE :nome LIMIT 1")
    Horario encontrarPorNome(String nome);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Horario> horarios);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Horario horario);

    @Update
    void editar(Horario horario);

    @Delete
    void deletar(Horario horario);

    @Query("DELETE FROM horario")
    void deletarTodos();

    @Query("DELETE FROM horario WHERE ativo = 0")
    void deletarInativos();

}
