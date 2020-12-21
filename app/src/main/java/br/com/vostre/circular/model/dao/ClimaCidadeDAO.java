package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.ClimaCidade;
import br.com.vostre.circular.model.pojo.BairroCidade;

@Dao
public interface ClimaCidadeDAO {

    @Query("SELECT * FROM clima_cidade ORDER BY cidade")
    LiveData<List<ClimaCidade>> listarTodos();

    @Query("SELECT * FROM clima_cidade ORDER BY cidade")
    List<ClimaCidade> listarTodosSync();

    @Query("SELECT * FROM clima_cidade WHERE ativo = 1 ORDER BY cidade")
    List<ClimaCidade> listarTodosAtivos();

    @Query("SELECT * FROM clima_cidade WHERE enviado = 0")
    List<ClimaCidade> listarTodosAEnviar();

    @Query("SELECT * FROM clima_cidade WHERE cidade = :cidade ORDER BY ultima_alteracao DESC")
    LiveData<List<ClimaCidade>> listarTodosPorCidade(String cidade);

    @Query("SELECT * FROM clima_cidade WHERE cidade = :cidade ORDER BY ultima_alteracao DESC LIMIT 1")
    LiveData<ClimaCidade> listarMaisRecentePorCidade(String cidade);

    @Query("SELECT * FROM clima_cidade WHERE id IN (:ids)")
    List<ClimaCidade> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM clima_cidade WHERE id LIKE :id")
    LiveData<BairroCidade> carregar(String id);

    @Query("SELECT * FROM clima_cidade WHERE id LIKE :id")
    BairroCidade carregarSync(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<ClimaCidade> climas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(ClimaCidade clima);

    @Update
    void editar(ClimaCidade clima);

    @Delete
    void deletar(ClimaCidade clima);

    @Query("DELETE FROM clima_cidade")
    void deletarTodos();

    @Query("DELETE FROM clima_cidade WHERE ativo = 0")
    void deletarInativos();

}
