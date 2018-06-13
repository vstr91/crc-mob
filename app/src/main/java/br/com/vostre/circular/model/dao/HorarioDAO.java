package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Horario;

@Dao
public interface HorarioDAO {

    @Query("SELECT * FROM horario")
    LiveData<List<Horario>> listarTodos();

    @Query("SELECT * FROM horario WHERE ativo = 1")
    List<Horario> listarTodosAtivos();

    @Query("SELECT * FROM horario WHERE enviado = 0")
    List<Horario> listarTodosAEnviar();

    @Query("SELECT * FROM horario WHERE id IN (:ids)")
    List<Horario> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM horario WHERE id = :id")
    LiveData<Horario> carregarPorId(String id);

    @Query("SELECT * FROM horario WHERE nome LIKE :nome LIMIT 1")
    Horario encontrarPorNome(String nome);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserirTodos(Horario... horarioes);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserir(Horario horario);

    @Update
    void editar(Horario horario);

    @Delete
    void deletar(Horario horario);

}
