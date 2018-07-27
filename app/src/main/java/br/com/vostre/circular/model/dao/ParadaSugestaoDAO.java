package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.ParadaSugestao;
import br.com.vostre.circular.model.pojo.ParadaBairro;

@Dao
public interface ParadaSugestaoDAO {

    @Query("SELECT * FROM parada_sugestao")
    LiveData<List<ParadaSugestao>> listarTodos();

    @Query("SELECT * FROM parada_sugestao WHERE enviado = 0")
    List<ParadaSugestao> listarTodosAEnviar();

    @Query("SELECT * FROM parada_sugestao WHERE ativo = 1")
    LiveData<List<ParadaSugestao>> listarTodosAtivos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<ParadaSugestao> paradas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(ParadaSugestao parada);

    @Update
    void editar(ParadaSugestao parada);

    @Delete
    void deletar(ParadaSugestao parada);

    @Query("DELETE FROM parada_sugestao")
    void deletarTodos();

}
