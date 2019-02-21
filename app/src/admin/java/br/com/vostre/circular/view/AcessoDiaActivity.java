package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityAcessoDiaBinding;
import br.com.vostre.circular.databinding.ActivityDetalheAcessoBinding;
import br.com.vostre.circular.model.pojo.AcessoDia;
import br.com.vostre.circular.model.pojo.AcessoTotal;
import br.com.vostre.circular.view.adapter.AcessoAdapter;
import br.com.vostre.circular.view.adapter.AcessoDiaAdapter;
import br.com.vostre.circular.viewModel.AcessosViewModel;

public class AcessoDiaActivity extends BaseActivity {

    ActivityAcessoDiaBinding binding;

    AcessosViewModel viewModel;

    RecyclerView listAcessos;
    List<AcessoDia> acessos;
    AcessoDiaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_acesso_dia);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Detalhes Acessos");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        viewModel = ViewModelProviders.of(this).get(AcessosViewModel.class);

        viewModel.acessosDia.observe(this, acessosObserver);

        listAcessos = binding.listAcessos;

        adapter = new AcessoDiaAdapter(acessos, this);

        listAcessos.setAdapter(adapter);
    }

    Observer<List<AcessoDia>> acessosObserver = new Observer<List<AcessoDia>>() {
        @Override
        public void onChanged(List<AcessoDia> acessos) {
            adapter.acessos = acessos;
            adapter.notifyDataSetChanged();
        }
    };

}
