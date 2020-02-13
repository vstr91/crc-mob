package br.com.vostre.circular.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.LinhaItinerariosTarifaBinding;
import br.com.vostre.circular.databinding.LinhaTarifasSecoesBinding;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.viewHolder.ItinerarioTarifaViewHolder;
import br.com.vostre.circular.view.viewHolder.TarifaSecaoViewHolder;

public class TarifaSecaoAdapter extends BaseExpandableListAdapter {

    public List<ItinerarioPartidaDestino> itinerarios;
    AppCompatActivity ctx;
    List<String> titulos;
    NumberFormat nf;

    public TarifaSecaoAdapter(List<ItinerarioPartidaDestino> itinerarios, List<String> titulos,  AppCompatActivity context){
        this.itinerarios = itinerarios;
        ctx = context;
        this.titulos = titulos;

        nf = NumberFormat.getCurrencyInstance();

    }

    private String formataTarifa(Double tarifa){
        return nf.format(tarifa);
    }

//    @Override
//    public ItinerarioTarifaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater layoutInflater =
//                LayoutInflater.from(parent.getContext());
//        LinhaItinerariosTarifaBinding itemBinding =
//                LinhaItinerariosTarifaBinding.inflate(layoutInflater, parent, false);
//        return new ItinerarioTarifaViewHolder(itemBinding, ctx);
//    }
//
//    @Override
//    public void onBindViewHolder(TarifaSecaoViewHolder holder, int position) {
//        ItinerarioPartidaDestino itinerario = itinerarios.get(position);
//        holder.bind(itinerarios, secao.getItinerario());
//    }
//
//    @Override
//    public int getItemCount() {
//
//        if(itinerarios == null){
//            return 0;
//        } else{
//            return itinerarios.size();
//        }
//
//
//    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return itinerarios.get(listPosition).getSecoes().get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        SecaoItinerario secao = (SecaoItinerario) getChild(listPosition, expandedListPosition);

        final String expandedListText = secao.getNome();

        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        LinhaTarifasSecoesBinding itemBinding =
                LinhaTarifasSecoesBinding.inflate(layoutInflater, parent, false);

        itemBinding.setSecao(secao);

        return new TarifaSecaoViewHolder(itemBinding, ctx).itemView;

//        if (convertView == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) this.ctx
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.linha_tarifas_secoes, null);
//        }
//
//        TextView expandedListTextView = (TextView) convertView
//                .findViewById(R.id.textViewNome);
//        expandedListTextView.setText(expandedListText);
//
//        TextView textViewTarifa = (TextView) convertView
//                .findViewById(R.id.textViewTarifaAtual);
//        textViewTarifa.setText(formataTarifa(secao.getTarifa()));

//        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.itinerarios.get(listPosition).getSecoes().size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.itinerarios.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.itinerarios.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        ItinerarioPartidaDestino iti = (ItinerarioPartidaDestino) getGroup(listPosition);

        String listTitle = iti.getNomeCompleto();

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.ctx.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.linha_tarifas_secoes_titulo, null);
        }

        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.textViewNomeItinerario);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
