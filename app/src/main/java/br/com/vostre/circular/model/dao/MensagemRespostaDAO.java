package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.MensagemResposta;

@Dao
public interface MensagemRespostaDAO {

    @Query("SELECT * FROM mensagem_resposta")
    LiveData<List<MensagemResposta>> listarTodos();

    @Query("SELECT * FROM mensagem_resposta WHERE ativo = 1")
    List<MensagemResposta> listarTodosAtivos();

    @Query("SELECT * FROM mensagem_resposta WHERE enviado = 0")
    List<MensagemResposta> listarTodosAEnviar();

    @Query("SELECT * FROM mensagem_resposta WHERE mensagem = :id")
    LiveData<List<MensagemResposta>> carregarTodosPorIdMensagem(String id);

    @Query("SELECT * FROM mensagem_resposta WHERE id = :id")
    LiveData<MensagemResposta> carregarPorId(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<MensagemResposta> mensagensRespostas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(MensagemResposta mensagemResposta);

    @Update
    void editar(MensagemResposta mensagemResposta);

    @Delete
    void deletar(MensagemResposta mensagemResposta);

    @Query("DELETE FROM mensagem_resposta")
    void deletarTodos();

    @Query("DELETE FROM mensagem_resposta WHERE ativo = 0")
    void deletarInativos();

}
