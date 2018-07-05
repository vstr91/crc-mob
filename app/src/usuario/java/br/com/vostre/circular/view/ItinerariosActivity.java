package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityItinerariosBinding;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.adapter.CidadeAdapter;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;

public class ItinerariosActivity extends BaseActivity {

    ActivityItinerariosBinding binding;

    RecyclerView listCidades;
    CidadeAdapter adapter;

    AppCompatActivity ctx;

    ItinerariosViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_itinerarios);
        super.onCreate(savedInstanceState);
            binding.setView(this);
            setTitle("Itinerários");
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            viewModel = ViewModelProviders.of(this).get(ItinerariosViewModel.class);
            viewModel.cidades.observe(this, cidadesObserver);

            ctx = this;

            listCidades = binding.listCidades;
            adapter = new CidadeAdapter(viewModel.cidades.getValue(), this);

            listCidades.setAdapter(adapter);



    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    Observer<List<CidadeEstado>> cidadesObserver = new Observer<List<CidadeEstado>>() {
        @Override
        public void onChanged(List<CidadeEstado> cidades) {
            adapter.cidades = cidades;
            adapter.notifyDataSetChanged();
        }
    };

}
