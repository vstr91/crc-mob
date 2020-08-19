package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityTarifasSecoesBinding;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.adapter.TarifaSecaoAdapter;
import br.com.vostre.circular.viewModel.TarifasSecoesViewModel;

public class TarifasSecoesActivity extends BaseActivity {

    ActivityTarifasSecoesBinding binding;
    TarifasSecoesViewModel viewModel;

    ExpandableListView listItinerarios;
    List<ItinerarioPartidaDestino> itinerarios;
    TarifaSecaoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tarifas_secoes);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(TarifasSecoesViewModel.class);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Tarifas Detalhadas");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listItinerarios = binding.listItinerariosTarifas;

        viewModel.carregarItinerarios();
        viewModel.itinerarios.observe(this, itinerariosObserver);

        //carregarLista();

    }

    private void carregarLista(final List<ItinerarioPartidaDestino> itinerarios) {

        if(itinerarios != null){
            final List<String> titulos = new ArrayList<>();

            for(ItinerarioPartidaDestino i : itinerarios){
                titulos.add(i.getNomeCompleto());
            }

            adapter = new TarifaSecaoAdapter(itinerarios, titulos,this);

            listItinerarios.setAdapter(adapter);

            listItinerarios.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
//                    Toast.makeText(getApplicationContext(),
//                            titulos.get(groupPosition) + " List Expanded.",
//                            Toast.LENGTH_SHORT).show();
                }
            });

            listItinerarios.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                @Override
                public void onGroupCollapse(int groupPosition) {
//                    Toast.makeText(getApplicationContext(),
//                            titulos.get(groupPosition) + " List Collapsed.",
//                            Toast.LENGTH_SHORT).show();

                }
            });

            listItinerarios.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
//                    Toast.makeText(
//                            getApplicationContext(),
//                            titulos.get(groupPosition)
//                                    + " -> "
//                                    + itinerarios.get(
//                                    groupPosition).getSecoes().get(
//                                    childPosition), Toast.LENGTH_SHORT
//                    ).show();
                    return false;
                }
            });

        }

    }

    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {

            carregarLista(itinerarios);

            //adapter.itinerarios = itinerarios;
            //adapter.notifyDataSetChanged();
        }
    };

    public void onBtnSalvarClick(View v){

        itinerarios = viewModel.itinerarios.getValue();
        List<SecaoItinerario> secoes = new ArrayList<>();

        for(ItinerarioPartidaDestino i : itinerarios){

            for(SecaoItinerario s : i.getSecoes()){

                if(s.getNovaTarifa() != null && s.getNovaTarifa() != s.getTarifa()){
                    secoes.add(s);
                }

            }

        }

        viewModel.atualizar(secoes, getApplicationContext());

//        if(itis.size() > 0 || !binding.editTextTarifa.getText().toString().isEmpty()){
//            Double tarifa = Double.parseDouble(binding.editTextTarifa.getText().toString().replace(".", "").replace(",", "."));
//            viewModel.edit(itis, tarifa);
//
//            for(ItinerarioPartidaDestino i : itinerarios){
//                i.setSelecionado(false);
//            }
//
//            binding.editTextTarifa.setText("");
//            adapter.itinerarios = itinerarios;
//            adapter.notifyDataSetChanged();
//
//            Toast.makeText(getApplicationContext(), "Tarifas alteradas!", Toast.LENGTH_SHORT).show();
//
//        } else{
//            Toast.makeText(getApplicationContext(), "Ao menos um itinerário deve ser selecionado e a tarifa deve ser informada.", Toast.LENGTH_SHORT).show();
//        }


    }

    @BindingAdapter("android:text")
    public static void setText(EditText view, Double valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();

        if(valor != null){
            view.setText(nf.format(valor));
        }

    }

    @InverseBindingAdapter(attribute = "android:text")
    public static Double getText(TextView view) {

        if(view.getText().toString().equals("null") || view.getText().toString().equals("")){
            return 0.0;
        } else{

            try{
                String valor = view.getText().toString();
                valor = valor.replace(Currency.getInstance(Locale.getDefault()).getCurrencyCode(), "");
                valor = valor.replace(".", "");
                valor = valor.replace(",", ".");
                Double d = Double.parseDouble(valor);
                return d;
            } catch(NumberFormatException e){
                return 0.0;
            }

        }


    }

}
