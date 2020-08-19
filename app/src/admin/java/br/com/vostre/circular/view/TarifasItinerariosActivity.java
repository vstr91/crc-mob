package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingAdapter;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityTarifasItinerariosBinding;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.adapter.TarifaItinerarioAdapter;
import br.com.vostre.circular.viewModel.TarifasItinerariosViewModel;

public class TarifasItinerariosActivity extends BaseActivity {

    ActivityTarifasItinerariosBinding binding;
    TarifasItinerariosViewModel viewModel;

    RecyclerView listItinerarios;
    List<ItinerarioPartidaDestino> itinerarios;
    TarifaItinerarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tarifas_itinerarios);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(TarifasItinerariosViewModel.class);

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
            adapter = new TarifaItinerarioAdapter(itinerarios, this);
            adapter.setHasStableIds(true);

            listItinerarios.setAdapter(adapter);
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
        List<ItinerarioPartidaDestino> itis = new ArrayList<>();

        for(ItinerarioPartidaDestino i : itinerarios){

            if(i.getNovaTarifa() != null && i.getNovaTarifa() != i.getItinerario().getTarifa()){
                itis.add(i);
            }

        }

        viewModel.atualizar(itis, getApplicationContext());

    }

    @BindingAdapter("android:text")
    public static void setText(EditText view, Double valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();

        if(valor != null){
            view.setText(nf.format(valor));
        } else{
            view.setText("");
        }

    }

    @InverseBindingAdapter(attribute = "android:text")
    public static Double getText(EditText view) {

        if(view.getText().toString().equals("null") || view.getText().toString().equals("")){
            return 0.0;
        } else{

            try{
                String valor = view.getText().toString();
                valor = valor.replace(Currency.getInstance(Locale.getDefault()).getSymbol(), "");
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
