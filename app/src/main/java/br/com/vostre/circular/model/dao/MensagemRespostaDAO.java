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

    @Query("SELECT * FROM mensagem_resposta WHERE mensagem = :id")
    LiveData<List<MensagemResposta>> carregarTodosPorIdMensagem(String id);

    @Query("SELECT * FROM mensagem_resposta WHERE id = :id")
    LiveData<MensagemResposta> carregarPorId(String id);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserirTodos(MensagemResposta... mensagensRespostas);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserir(MensagemResposta mensagemResposta);

    @Update
    void editar(MensagemResposta mensagemResposta);

    @Delete
    void deletar(MensagemResposta mensagemResposta);

}
