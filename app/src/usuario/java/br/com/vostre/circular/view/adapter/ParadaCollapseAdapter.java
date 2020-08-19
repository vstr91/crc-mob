package br.com.vostre.circular.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaParadasBinding;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.viewHolder.ParadaViewHolder;

public class ParadaCollapseAdapter extends BaseExpandableListAdapter {

    public List<BairroCidade> bairros;
    AppCompatActivity ctx;
    public String bairroAtual;
    List<String> titulos;

    public ParadaCollapseAdapter(List<BairroCidade> bairros, AppCompatActivity context){
        this.bairros = bairros;
        ctx = context;
        bairroAtual = "";
    }

    @Override
    public int getGroupCount() {

        if(bairros != null){
            return bairros.size();
        } else{
            return 0;
        }

    }

    @Override
    public int getChildrenCount(int i) {

        if(bairros != null && bairros.get(i) != null){
            return bairros.get(i).getParadas().size();
        } else{
            return 0;
        }


    }

    @Override
    public Object getGroup(int i) {
        return bairros.get(i);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return bairros.get(listPosition).getParadas().get(expandedListPosition);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        BairroCidade ba = (BairroCidade) getGroup(listPosition);

        String listTitle = ba.getBairro().getNome();

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.linha_paradas_titulo, null);
        }

        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.textViewNomeBairro);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        return convertView;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ParadaBairro parada = (ParadaBairro) getChild(listPosition, expandedListPosition);

        final String expandedListText = parada.getParada().getNome();

        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaParadasBinding itemBinding =
                LinhaParadasBinding.inflate(layoutInflater, parent, false);

        itemBinding.setParada(parada);

        return new ParadaViewHolder(itemBinding, ctx).itemView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
