package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<Usuario> usuarios);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Usuario usuario);

    @Update
    void editar(Usuario usuario);

    @Delete
    void deletar(Usuario usuario);

    @Query("DELETE FROM usuario")
    void deletarTodos();

    @Query("DELETE FROM usuario WHERE ativo = 0")
    void deletarInativos();

}
