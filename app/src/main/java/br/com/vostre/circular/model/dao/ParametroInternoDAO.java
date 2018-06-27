package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Parametro;
import br.com.vostre.circular.model.ParametroInterno;

@Dao
public interface ParametroInternoDAO {

    @Query("SELECT * FROM parametro_interno LIMIT 1")
    LiveData<List<ParametroInterno>> listarTodos();

    @Query("SELECT * FROM parametro_interno WHERE id = :id")
    LiveData<ParametroInterno> carregarPorId(String id);

    @Query("SELECT * FROM parametro_interno WHERE id = 1")
    ParametroInterno carregar();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(ParametroInterno parametro);

    @Update
    void editar(ParametroInterno parametro);

}
