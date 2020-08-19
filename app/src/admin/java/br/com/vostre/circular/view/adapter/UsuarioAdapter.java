package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaUsuariosBinding;
import br.com.vostre.circular.model.Usuario;
import br.com.vostre.circular.view.viewHolder.UsuarioViewHolder;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioViewHolder> {

    public List<Usuario> usuarios;
    AppCompatActivity ctx;

    public UsuarioAdapter(List<Usuario> usuarios, AppCompatActivity context){
        this.usuarios = usuarios;
        ctx = context;
    }

    @Override
    public UsuarioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaUsuariosBinding itemBinding =
                LinhaUsuariosBinding.inflate(layoutInflater, parent, false);
        return new UsuarioViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarios.get(position);
        holder.bind(usuario);
    }

    @Override
    public int getItemCount() {

        if(usuarios == null){
            return 0;
        } else{
            return usuarios.size();
        }


    }
}
