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
import br.com.vostre.circular.model.Problema;
import br.com.vostre.circular.model.pojo.ProblemaTipo;

@Dao
public interface ProblemaDAO {

    @Query("SELECT * FROM problema ORDER BY data_cadastro DESC")
    LiveData<List<Problema>> listarTodos();

    @Query("SELECT * FROM problema WHERE imagemEnviada = 0 AND imagem IS NOT NULL")
    List<Problema> listarTodosImagemAEnviar();

    @Query("SELECT * FROM problema WHERE ativo = 1 ORDER BY data_cadastro DESC")
    List<Problema> listarTodosAtivos();

    @Query("SELECT p.*, t.nome AS tipo, u.nome FROM problema p INNER JOIN tipo_problema t ON t.id = p.tipoProblema LEFT JOIN usuario u ON u.id = p.usuario_cadastro WHERE situacao = 0 ORDER BY data_cadastro DESC")
    LiveData<List<ProblemaTipo>> listarTodosAbertos();

    @Query("SELECT p.*, t.nome AS tipo, u.nome FROM problema p INNER JOIN tipo_problema t ON t.id = p.tipoProblema LEFT JOIN usuario u ON u.id = p.usuario_cadastro WHERE situacao IN (1,2) ORDER BY data_cadastro DESC")
    LiveData<List<ProblemaTipo>> listarTodosResolvidos();

    @Query("SELECT * FROM problema WHERE enviado = 0")
    List<Problema> listarTodosAEnviar();

    @Query("SELECT * FROM problema WHERE id = :id")
    LiveData<Problema> carregarPorId(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Problema> problemas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Problema problema);

    @Update
    void editar(Problema problema);

    @Delete
    void deletar(Problema problema);

    @Query("DELETE FROM problema")
    void deletarTodos();

}