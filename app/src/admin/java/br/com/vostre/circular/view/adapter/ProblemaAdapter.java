package br.com.vostre.circular.view.adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.databinding.LinhaProblemasBinding;
import br.com.vostre.circular.model.pojo.ProblemaTipo;
import br.com.vostre.circular.view.viewHolder.ProblemaViewHolder;

public class ProblemaAdapter extends RecyclerView.Adapter<ProblemaViewHolder> {

    public List<ProblemaTipo> problemas;
    AppCompatActivity ctx;

    public ProblemaAdapter(List<ProblemaTipo> problemas, AppCompatActivity context){
        this.problemas = problemas;
        ctx = context;
    }

    @Override
    public ProblemaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaProblemasBinding itemBinding =
                LinhaProblemasBinding.inflate(layoutInflater, parent, false);
        return new ProblemaViewHolder(itemBinding, ctx);
    }

    @Override
    public void onBindViewHolder(ProblemaViewHolder holder, int position) {
        ProblemaTipo problema = problemas.get(position);
        holder.bind(problema);
    }

    @Override
    public int getItemCount() {

        if(problemas == null){
            return 0;
        } else{
            return problemas.size();
        }


    }
}
