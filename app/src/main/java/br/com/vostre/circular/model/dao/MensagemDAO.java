package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Pais;

@Dao
public interface MensagemDAO {

    @Query("SELECT * FROM mensagem")
    LiveData<List<Mensagem>> listarTodos();

    @Query("SELECT * FROM mensagem WHERE ativo = 1")
    List<Mensagem> listarTodosAtivos();

    @Query("SELECT * FROM mensagem WHERE enviado = 0")
    List<Mensagem> listarTodosAEnviar();

    @Query("SELECT * FROM mensagem WHERE id IN (:ids)")
    List<Mensagem> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM mensagem WHERE id = :id")
    LiveData<Mensagem> carregarPorId(String id);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserirTodos(Mensagem... mensagens);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserir(Mensagem mensagem);

    @Update
    void editar(Mensagem mensagem);

    @Delete
    void deletar(Mensagem mensagem);

}