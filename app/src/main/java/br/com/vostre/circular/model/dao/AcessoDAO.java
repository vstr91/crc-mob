package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Acesso;
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

    @Query("SELECT identificadorUnico, versao, COUNT(id) AS 'totalAcessos' FROM acesso WHERE DATE(dataValidacao/1000, 'unixepoch', 'localtime') = DATE('now','localtime') GROUP BY identificadorUnico ORDER BY COUNT(id) DESC")
    LiveData<List<AcessoTotal>> contarAcessosPorIdentificadorPorDia();

    @Query("SELECT identificadorUnico, versao, COUNT(id) AS 'totalAcessos' FROM acesso WHERE DATE(dataValidacao/1000, 'unixepoch', 'localtime') = :dia GROUP BY identificadorUnico ORDER BY COUNT(id) DESC")
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

    @Query("SELECT DATE(dataValidacao/1000, 'unixepoch', 'localtime') AS 'dia', COUNT(id) AS 'totalAcessos' " +
            "FROM acesso GROUP BY DATE(dataValidacao/1000, 'unixepoch', 'localtime') " +
            "ORDER BY DATE(dataValidacao/1000, 'unixepoch', 'localtime') DESC LIMIT :limite")
    LiveData<List<AcessoDia>> listarAcessosPorDia(int limite);

    @Query("SELECT DATE(dataValidacao/1000, 'unixepoch', 'localtime') AS 'dia', COUNT(DISTINCT identificadorUnico) AS 'totalAcessos' " +
            "FROM acesso GROUP BY DATE(dataValidacao/1000, 'unixepoch', 'localtime') " +
            "ORDER BY DATE(dataValidacao/1000, 'unixepoch', 'localtime') DESC LIMIT :limite")
    LiveData<List<AcessoDia>> listarAcessosUnicosPorDia(int limite);

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
