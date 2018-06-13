package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;

@Dao
public interface BairroDAO {

    @Query("SELECT * FROM bairro")
    LiveData<List<Bairro>> listarTodos();

    @Query("SELECT * FROM bairro WHERE ativo = 1")
    List<Bairro> listarTodosAtivos();

    @Query("SELECT * FROM bairro WHERE enviado = 0")
    List<Bairro> listarTodosAEnviar();

    @Query("SELECT b.*, c.id AS idCidade, c.nome AS nomeCidade, e.id AS idEstado, e.nome AS nomeEstado, e.sigla AS siglaEstado FROM bairro b " +
            "INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado")
    LiveData<List<BairroCidade>> listarTodosComCidade();

    @Query("SELECT * FROM bairro WHERE id IN (:ids)")
    List<Bairro> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM bairro WHERE nome LIKE :nome LIMIT 1")
    Bairro encontrarPorNome(String nome);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserirTodos(Bairro... bairros);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserir(Bairro bairro);

    @Update
    void editar(Bairro bairro);

    @Delete
    void deletar(Bairro bairro);

}
