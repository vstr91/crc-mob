package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Mensagem;

@Dao
public interface MensagemDAO {

    @Query("SELECT * FROM mensagem WHERE servidor = 0 ORDER BY data_cadastro DESC")
    LiveData<List<Mensagem>> listarTodos();

    @Query("SELECT * FROM mensagem WHERE servidor = 1 ORDER BY data_cadastro DESC")
    LiveData<List<Mensagem>> listarTodosServidor();

    @Query("SELECT * FROM mensagem WHERE servidor = 1 ORDER BY data_cadastro DESC")
    LiveData<List<Mensagem>> listarTodosRecebidos();

    @Query("SELECT * FROM mensagem WHERE servidor = 1 AND lida = 0")
    LiveData<List<Mensagem>> listarTodosNaoLidos();

    @Query("SELECT * FROM mensagem WHERE servidor = 0 AND lida = 0")
    LiveData<List<Mensagem>> listarTodosNaoLidosServidor();

    @Query("SELECT * FROM mensagem WHERE ativo = 1 ORDER BY data_cadastro DESC")
    List<Mensagem> listarTodosAtivos();

    @Query("SELECT * FROM mensagem WHERE enviado = 0")
    List<Mensagem> listarTodosAEnviar();

    @Query("SELECT * FROM mensagem WHERE id IN (:ids)")
    List<Mensagem> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM mensagem WHERE id = :id")
    LiveData<Mensagem> carregarPorId(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Mensagem> mensagens);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Mensagem mensagem);

    @Update
    void editar(Mensagem mensagem);

    @Delete
    void deletar(Mensagem mensagem);

    @Query("DELETE FROM mensagem")
    void deletarTodos();

    @Query("DELETE FROM mensagem WHERE ativo = 0")
    void deletarInativos();

}
