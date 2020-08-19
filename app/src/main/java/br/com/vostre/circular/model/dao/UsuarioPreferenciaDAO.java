package br.com.vostre.circular.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.vostre.circular.model.UsuarioPreferencia;

@Dao
public interface UsuarioPreferenciaDAO {

    @Query("SELECT * FROM usuario_preferencia ORDER BY usuario")
    LiveData<List<UsuarioPreferencia>> listarTodos();

    @Query("SELECT * FROM usuario_preferencia WHERE enviado = 0")
    List<UsuarioPreferencia> listarTodosAEnviar();

    @Query("SELECT * FROM usuario_preferencia WHERE id = :id")
    LiveData<UsuarioPreferencia> carregarPorId(String id);

    @Query("SELECT * FROM usuario_preferencia WHERE usuario = :usuario")
    UsuarioPreferencia carregarPorUsuario(String usuario);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserirTodos(List<UsuarioPreferencia> preferencias);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(UsuarioPreferencia usuarioPreferencia);

    @Update
    void editar(UsuarioPreferencia usuarioPreferencia);

    @Delete
    void deletar(UsuarioPreferencia usuarioPreferencia);

    @Query("DELETE FROM usuario_preferencia")
    void deletarTodos();

    @Query("DELETE FROM usuario_preferencia WHERE ativo = 0")
    void deletarInativos();

}
