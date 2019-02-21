package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.joda.time.DateTime;

import java.util.List;

import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.pojo.AcessoDia;
import br.com.vostre.circular.model.pojo.AcessoTotal;

@Dao
public interface AcessoDAO {

    @Query("SELECT * FROM acesso ORDER BY dataValidacao DESC")
    LiveData<List<Acesso>> listarTodos();

    @Query("SELECT * FROM acesso ORDER BY dataValidacao DESC")
    List<Acesso> listarTodosSync();

    @Query("SELECT COUNT(identificadorUnico) FROM acesso")
    LiveData<Integer> contarTodos();

    @Query("SELECT COUNT(id) FROM acesso WHERE DATE(dataValidacao/1000, 'unixepoch', 'localtime') = DATE('now','localtime')")
    LiveData<Integer> contarTodosDoDia();

    @Query("SELECT COUNT(id) FROM acesso WHERE DATE(dataValidacao/1000, 'unixepoch', 'localtime') = :dia")
    LiveData<Integer> contarTodosDoDia(String dia);

    @Query("SELECT identificadorUnico, COUNT(id) AS 'totalAcessos' FROM acesso WHERE DATE(dataValidacao/1000, 'unixepoch', 'localtime') = DATE('now','localtime') GROUP BY identificadorUnico ORDER BY COUNT(id) DESC")
    LiveData<List<AcessoTotal>> contarAcessosPorIdentificadorPorDia();

    @Query("SELECT identificadorUnico, COUNT(id) AS 'totalAcessos' FROM acesso WHERE DATE(dataValidacao/1000, 'unixepoch', 'localtime') = :dia GROUP BY identificadorUnico ORDER BY COUNT(id) DESC")
    LiveData<List<AcessoTotal>> contarAcessosPorIdentificadorPorDia(String dia);

    @Query("SELECT * FROM acesso WHERE identificadorUnico = :identificador ORDER BY DATE(dataValidacao/1000, 'unixepoch', 'localtime') DESC")
    LiveData<List<Acesso>> listarAcessosPorIdentificador(String identificador);

    @Query("SELECT * FROM acesso WHERE DATE(dataValidacao/1000, 'unixepoch', 'localtime') = DATE('now','localtime') " +
            "AND identificadorUnico = :identificador ORDER BY DATETIME(dataValidacao/1000, 'unixepoch', 'localtime') DESC")
    LiveData<List<Acesso>> listarAcessosPorIdentificadorPorDia(String identificador);

    @Query("SELECT * FROM acesso WHERE DATE(dataValidacao/1000, 'unixepoch', 'localtime') = :dia " +
            "AND identificadorUnico = :identificador ORDER BY DATETIME(dataValidacao/1000, 'unixepoch', 'localtime') DESC")
    LiveData<List<Acesso>> listarAcessosPorIdentificadorPorDia(String identificador, String dia);

    @Query("SELECT DATE(dataValidacao/1000, 'unixepoch', 'localtime') AS 'dia', COUNT(id) AS 'totalAcessos' " +
            "FROM acesso GROUP BY DATE(dataValidacao/1000, 'unixepoch', 'localtime') " +
            "ORDER BY DATE(dataValidacao/1000, 'unixepoch', 'localtime') DESC")
    LiveData<List<AcessoDia>> listarAcessosPorDia();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Acesso> acessos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Acesso acesso);

    @Update
    void editar(Acesso acesso);

    @Delete
    void deletar(Acesso acesso);

    @Query("DELETE FROM acesso")
    void deletarTodos();

}
