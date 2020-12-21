package br.com.vostre.circular.model.dao;

import android.media.Image;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.HistoricoParada;
import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.ImagemParadaBairro;

@Dao
public interface ImagemParadaDAO {

    @Query("SELECT * FROM imagem_parada")
    LiveData<List<ImagemParada>> listarTodos();

    @Query("SELECT * FROM imagem_parada WHERE ativo = 1 AND status = 1 AND parada = :parada")
    LiveData<List<ImagemParada>> listarTodosAtivosPorParada(String parada);

    @Query("SELECT ip.*, p.id AS idParada, p.nome AS nomeParada, p.sentido AS sentidoParada, b.id AS idBairro, b.nome AS nomeBairro, c.nome AS nomeCidade, e.sigla AS siglaEstado " +
            "FROM imagem_parada ip INNER JOIN parada p ON p.id = ip.parada INNER JOIN " +
            "bairro b ON b.id = p.bairro INNER JOIN cidade c ON c.id = b.cidade INNER JOIN estado e ON e.id = c.estado " +
            "ORDER BY ip.status, p.nome")
    LiveData<List<ImagemParadaBairro>> listarTodosPorStatus();

    @Query("SELECT * FROM imagem_parada")
    List<ImagemParada> listarTodosSync();

    @Query("SELECT * FROM imagem_parada WHERE imagemEnviada = 0 AND imagem IS NOT NULL")
    List<ImagemParada> listarTodosImagemAEnviar();

    @Query("SELECT * FROM imagem_parada WHERE enviado = 0")
    List<ImagemParada> listarTodosAEnviar();

    @Query("SELECT * FROM imagem_parada WHERE ativo = 1")
    LiveData<List<ImagemParada>> listarTodosAtivos();

    @Query("SELECT * FROM imagem_parada WHERE parada = :parada")
    LiveData<List<ImagemParada>> carregarPorParada(String parada);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<ImagemParada> paradas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(ImagemParada parada);

    @Update
    void editar(ImagemParada parada);

    @Delete
    void deletar(ImagemParada parada);

    @Query("DELETE FROM imagem_parada")
    void deletarTodos();

    @Query("DELETE FROM imagem_parada WHERE ativo = 0")
    void deletarInativos();

}
