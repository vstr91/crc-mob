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
