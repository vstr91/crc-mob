package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.PontoInteresse;

@Dao
public interface PontoInteresseDAO {

    @Query("SELECT * FROM ponto_interesse")
    LiveData<List<PontoInteresse>> listarTodos();

    @Query("SELECT * FROM ponto_interesse")
    List<PontoInteresse> listarTodosSync();

    @Query("SELECT * FROM ponto_interesse WHERE ativo = 1")
    LiveData<List<PontoInteresse>> listarTodosAtivos();

    @Query("SELECT * FROM ponto_interesse WHERE enviado = 0")
    List<PontoInteresse> listarTodosAEnviar();

    @Query("SELECT * FROM ponto_interesse WHERE imagemEnviada = 0 AND imagem IS NOT NULL")
    List<PontoInteresse> listarTodosImagemAEnviar();

    @Query("SELECT p.* FROM ponto_interesse p WHERE (latitude >= :minLat AND latitude <= :maxLat) " +
            "AND (longitude >= :minLng AND longitude <= :maxLng)")
    LiveData<List<PontoInteresse>> listarTodosAtivosProximos(double minLat, double maxLat, double minLng, double maxLng);

    @Query("SELECT * FROM ponto_interesse WHERE id IN (:ids)")
    List<PontoInteresse> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM ponto_interesse WHERE nome LIKE :nome LIMIT 1")
    PontoInteresse encontrarPorNome(String nome);

    @Query("SELECT * FROM ponto_interesse WHERE slug LIKE :slug LIMIT 1")
    PontoInteresse encontrarPorSlug(String slug);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<PontoInteresse> pontosInteresse);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(PontoInteresse pontoInteresse);

    @Update
    void editar(PontoInteresse pontoInteresse);

    @Delete
    void deletar(PontoInteresse pontoInteresse);

    @Query("DELETE FROM ponto_interesse")
    void deletarTodos();

}
