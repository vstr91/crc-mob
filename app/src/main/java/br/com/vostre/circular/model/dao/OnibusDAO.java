package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Onibus;

@Dao
public interface OnibusDAO {

    @Query("SELECT * FROM onibus")
    LiveData<List<Onibus>> listarTodos();

    @Query("SELECT * FROM onibus WHERE ativo = 1")
    LiveData<List<Onibus>> listarTodosAtivos();

    @Query("SELECT * FROM onibus WHERE enviado = 0")
    List<Onibus> listarTodosAEnviar();

    @Query("SELECT * FROM onibus WHERE ativo = 1 AND empresa = :empresa")
    LiveData<List<Onibus>> listarTodosAtivosPorEmpresa(String empresa);

    @Query("SELECT * FROM onibus WHERE id IN (:ids)")
    List<Onibus> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM onibus WHERE id = :id")
    LiveData<Onibus> carregar(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Onibus> onibus);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Onibus onibus);

    @Update
    void editar(Onibus onibus);

    @Delete
    void deletar(Onibus onibus);

    @Query("DELETE FROM onibus")
    void deletarTodos();

    @Query("DELETE FROM onibus WHERE ativo = 0")
    void deletarInativos();

}
