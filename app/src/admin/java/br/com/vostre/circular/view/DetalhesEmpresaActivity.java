package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.databinding.ActivityDetalhesEmpresaBinding;

public class DetalhesEmpresaActivity extends BaseActivity {

    ActivityDetalhesEmpresaBinding binding;
    DetalhesEmpresaViewModel viewModel;

    RecyclerView listItinerarios;
    List<ItinerarioPartidaDestino> itinerarios;
    ItinerarioAdapter adapter;

    RecyclerView listOnibus;
    List<Onibus> onibus;
    OnibusAdapter adapterOnibus;

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhes_empresa);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(DetalhesEmpresaViewModel.class);

        viewModel.setEmpresa(getIntent().getStringExtra("empresa"));

        viewModel.empresa.observe(this, empresaObserver);
        viewModel.itinerarios.observe(this, itinerariosObserver);
        viewModel.onibus.observe(this, onibusObserver);

        binding.setView(this);
        setTitle("Detalhes Empresa");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listItinerarios = binding.listItinerarios;
        listOnibus = binding.listOnibus;

        adapter = new ItinerarioAdapter(itinerarios, this);

        listItinerarios.setAdapter(adapter);

        adapterOnibus = new OnibusAdapter(onibus, this);

        listOnibus.setAdapter(adapterOnibus);

        tabHost = binding.tabs;
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Itinerários");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Itinerários");
        tabHost.addTab(spec);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Ônibus");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Ônibus");
        tabHost.addTab(spec2);

    }

    public void onFabClick(View v){

        if(tabHost.getCurrentTab() == 0){

        } else{
            FormOnibus formOnibus = new FormOnibus();
            formOnibus.setCtx(getApplication());
            formOnibus.flagInicioEdicao = false;
            formOnibus.show(getSupportFragmentManager(), "formOnibus");
        }

    }

    Observer<List<ItinerarioPartidaDestino>> itinerariosObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
            adapter.itinerarios = itinerarios;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<List<Onibus>> onibusObserver = new Observer<List<Onibus>>() {
        @Override
        public void onChanged(List<Onibus> onibus) {
            adapterOnibus.onibus = onibus;
            adapterOnibus.notifyDataSetChanged();
        }
    };

    Observer<Empresa> empresaObserver = new Observer<Empresa>() {
        @Override
        public void onChanged(Empresa empresa) {

            if(empresa != null){
                binding.textViewEmpresa.setText(empresa.getNome());
                viewModel.carregarItinerarios(empresa.getId());
            }

        }
    };

}
