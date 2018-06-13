package br.com.vostre.circular.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.com.vostre.circular.model.Usuario;

@Dao
public interface UsuarioDAO {

    @Query("SELECT * FROM usuario")
    LiveData<List<Usuario>> listarTodos();

    @Query("SELECT * FROM usuario WHERE ativo = 1")
    List<Usuario> listarTodosAtivos();

    @Query("SELECT * FROM usuario WHERE enviado = 0")
    List<Usuario> listarTodosAEnviar();

    @Query("SELECT * FROM usuario WHERE id IN (:ids)")
    List<Usuario> carregarTodosPorIds(int[] ids);

    @Query("SELECT * FROM usuario WHERE id = :id")
    LiveData<Usuario> carregarPorId(String id);

    @Query("SELECT * FROM usuario WHERE nome LIKE :nome LIMIT 1")
    Usuario encontrarPorNome(String nome);

    @Query("SELECT * FROM usuario WHERE email LIKE :email LIMIT 1")
    Usuario encontrarPorEmail(String email);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserirTodos(Usuario... usuarios);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void inserir(Usuario usuario);

    @Update
    void editar(Usuario usuario);

    @Delete
    void deletar(Usuario usuario);

}
