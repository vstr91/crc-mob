package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityFavoritosBinding;
import br.com.vostre.circular.databinding.ActivityMensagensBinding;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioFavoritoAdapter;
import br.com.vostre.circular.view.adapter.MensagemAdapter;
import br.com.vostre.circular.view.adapter.ParadaAdapter;
import br.com.vostre.circular.view.adapter.ParadaFavoritaAdapter;
import br.com.vostre.circular.view.form.FormMensagem;
import br.com.vostre.circular.viewModel.FavoritosViewModel;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;
import br.com.vostre.circular.viewModel.MensagensViewModel;
import br.com.vostre.circular.viewModel.ParadasViewModel;

public class FavoritosActivity extends BaseActivity {

    ActivityFavoritosBinding binding;
    FavoritosViewModel viewModel;

    RecyclerView listParadas;
    List<ParadaBairro> paradas;
    ParadaFavoritaAdapter adapterParada;

    RecyclerView listItinerarios;
    List<ItinerarioPartidaDestino> itinerarios;
    ItinerarioFavoritoAdapter adapterItinerarios;

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favoritos);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(FavoritosViewModel.class);
        viewModel.paradas.observe(this, paradasObserver);
        viewModel.itinerarios.observe(this, itinerariosObserver);

        binding.setView(this);
        setTitle("Favoritos");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listParadas = binding.listParadas;

        adapterParada = new ParadaFavoritaAdapter(paradas, this);

        listParadas.setAdapter(adapterParada);

        listItinerarios = binding.listItinerarios;

        adapterItinerarios = new ItinerarioFavoritoAdapter(itinerarios, this);

        listItinerarios.setAdapter(adapterItinerarios);

        tabHost = binding.tabs;
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Itinerários");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Itinerários");
        tabHost.addTab(spec);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Paradas");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Paradas");
        tabHost.addTab(spec2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        boolean retorno = super.onCreateOptionsMenu(menu);

        if(menu != null){
            menu.getItem(0).setVisible(false);
        }

        return retorno;
    }

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {

            if(paradas.size() > 0){
                adapterParada.paradas = paradas;
            } else{
                adapterParada.paradas = new ArrayList<>();
            }

            adapterParada.notifyDataSetChanged();
        }
    };

    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {

            if(itinerarios.size() > 0){
                adapterItinerarios.itinerarios = itinerarios;
            } else{
                adapterItinerarios.itinerarios = new ArrayList<>();
            }

            adapterItinerarios.notifyDataSetChanged();
        }
    };

}
