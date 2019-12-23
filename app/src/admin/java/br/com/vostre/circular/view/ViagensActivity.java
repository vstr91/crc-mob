package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityPaisesBinding;
import br.com.vostre.circular.databinding.ActivityViagensBinding;
import br.com.vostre.circular.listener.ViagemListener;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.ViagemItinerario;
import br.com.vostre.circular.view.adapter.PaisAdapter;
import br.com.vostre.circular.view.adapter.ViagemItinerarioAdapter;
import br.com.vostre.circular.view.form.FormPais;
import br.com.vostre.circular.viewModel.PaisesViewModel;
import br.com.vostre.circular.viewModel.ViagensItinerarioViewModel;

public class ViagensActivity extends BaseActivity implements ViagemListener {

    ActivityViagensBinding binding;
    ViagensItinerarioViewModel viewModel;

    RecyclerView listViagens;
    List<ViagemItinerario> viagens;
    ViagemItinerarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_viagens);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ViagensItinerarioViewModel.class);
        viewModel.setItinerario(getIntent().getStringExtra("itinerario"));
        viewModel.viagens.observe(this, viagensObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Viagens");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listViagens = binding.listViagens;

        adapter = new ViagemItinerarioAdapter(viagens, this, this);

        listViagens.setAdapter(adapter);

    }

    Observer<List<ViagemItinerario>> viagensObserver = new Observer<List<ViagemItinerario>>() {
        @Override
        public void onChanged(List<ViagemItinerario> viagens) {

            List<ViagemItinerario> vi = new ArrayList<>();

            for(ViagemItinerario v : viagens){

                if(v.getTotalPontos(getApplicationContext()) > 0 && v.getHoraInicial() != null && v.getHoraFinal() != null){
                    vi.add(v);
                }

            }

            adapter.viagens = vi;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getApplicationContext(), "Viagem excluída!", Toast.LENGTH_SHORT).show();
            } else if(retorno == 0){
                Toast.makeText(getApplicationContext(),
                        "Erro ao excluir viagem.",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    public void onSelected(String id) {
        viewModel.editarViagem(id, getApplicationContext());
        viewModel.retorno.observe(this, retornoObserver);
    }
}