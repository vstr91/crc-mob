package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.model.SecaoItinerario;
import br.com.vostre.circular.databinding.ActivitySecoesItinerarioBinding;
import br.com.vostre.circular.view.adapter.SecaoItinerarioAdapter;
import br.com.vostre.circular.view.form.FormSecao;
import br.com.vostre.circular.viewModel.SecoesItinerarioViewModel;

public class SecoesItinerarioActivity extends BaseActivity {

    ActivitySecoesItinerarioBinding binding;
    SecoesItinerarioViewModel viewModel;

    RecyclerView listSecoes;
    List<SecaoItinerario> secoes;
    SecaoItinerarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_secoes_itinerario);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(SecoesItinerarioViewModel.class);

        Itinerario itinerario = new Itinerario();
        itinerario.setId(getIntent().getStringExtra("itinerario"));
        viewModel.setItinerario(itinerario);

        viewModel.secoes.observe(this, secoesObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Seções Itinerário");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listSecoes = binding.listSecoes;

        adapter = new SecaoItinerarioAdapter(secoes, this);

        listSecoes.setAdapter(adapter);

    }

    public void onFabClick(View v){
        FormSecao formSecao = new FormSecao();
        formSecao.flagInicioEdicao = false;
        formSecao.setCtx(getApplication());
        formSecao.show(getSupportFragmentManager(), "formSecao");
    }

    Observer<List<SecaoItinerario>> secoesObserver = new Observer<List<SecaoItinerario>>() {
        @Override
        public void onChanged(List<SecaoItinerario> secoes) {
            adapter.secoes = secoes;
            adapter.notifyDataSetChanged();
        }
    };
}
