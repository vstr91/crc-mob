package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.ParametroInterno;

@Dao
public interface ParametroInternoDAO {

    @Query("SELECT * FROM parametro_interno LIMIT 1")
    LiveData<List<ParametroInterno>> listarTodos();

    @Query("SELECT * FROM parametro_interno WHERE id = :id")
    LiveData<ParametroInterno> carregarPorId(String id);

    @Query("SELECT * FROM parametro_interno WHERE id = 1")
    ParametroInterno carregar();

    @Query("SELECT * FROM parametro_interno WHERE id = 1")
    LiveData<ParametroInterno> carregarInformacoes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(ParametroInterno parametro);

    @Update
    void editar(ParametroInterno parametro);

}
