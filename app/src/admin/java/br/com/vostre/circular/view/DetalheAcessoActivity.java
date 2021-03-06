package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDashboardBinding;
import br.com.vostre.circular.databinding.ActivityDetalheAcessoBinding;
import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.model.pojo.AcessoTotal;
import br.com.vostre.circular.view.adapter.AcessoAdapter;
import br.com.vostre.circular.viewModel.AcessosViewModel;

public class DetalheAcessoActivity extends BaseActivity {

    ActivityDetalheAcessoBinding binding;

    AcessosViewModel viewModel;

    RecyclerView listAcessos;
    List<AcessoTotal> acessos;
    AcessoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhe_acesso);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Detalhes de Acesso");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        viewModel = ViewModelProviders.of(this).get(AcessosViewModel.class);

        String dia = getIntent().getStringExtra("dia");

        viewModel.carregaAcessosDia(dia);

        DateTime dt = DateTimeFormat.forPattern("YYYY-MM-dd").parseDateTime(dia);

        binding.setDia(DateTimeFormat.forPattern("dd/MM/YYYY").print(dt));

        viewModel.acessos.observe(this, acessosObserver);

        listAcessos = binding.listAcessos;

        adapter = new AcessoAdapter(acessos, this, dia);

        listAcessos.setAdapter(adapter);
    }

    Observer<List<AcessoTotal>> acessosObserver = new Observer<List<AcessoTotal>>() {
        @Override
        public void onChanged(List<AcessoTotal> acessos) {
            adapter.acessos = acessos;
            adapter.notifyDataSetChanged();
        }
    };

}
