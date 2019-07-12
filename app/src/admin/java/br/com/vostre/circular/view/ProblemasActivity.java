package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TabHost;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityMensagensBinding;
import br.com.vostre.circular.databinding.ActivityProblemasBinding;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.model.Problema;
import br.com.vostre.circular.model.pojo.ProblemaTipo;
import br.com.vostre.circular.view.adapter.MensagemAdapter;
import br.com.vostre.circular.view.adapter.ProblemaAdapter;
import br.com.vostre.circular.view.form.FormMensagem;
import br.com.vostre.circular.viewModel.MensagensViewModel;
import br.com.vostre.circular.viewModel.ProblemasViewModel;

public class ProblemasActivity extends BaseActivity {

    ActivityProblemasBinding binding;
    ProblemasViewModel viewModel;

    RecyclerView listAbertos;
    RecyclerView listResolvidos;
    List<ProblemaTipo> abertos;
    List<ProblemaTipo> resolvidos;
    ProblemaAdapter adapter;
    ProblemaAdapter adapterResolvidos;

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_problemas);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ProblemasViewModel.class);
        //viewModel.abertos.observe(this, abertosObserver);
        //viewModel.resolvidos.observe(this, resolvidosObserver);

        binding.setView(this);
        setTitle("Problemas");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listAbertos = binding.listAbertos;

        adapter = new ProblemaAdapter(abertos, this);

        listAbertos.setAdapter(adapter);

        listResolvidos = binding.listResolvidos;

        adapterResolvidos = new ProblemaAdapter(resolvidos, this);

        listResolvidos.setAdapter(adapterResolvidos);

        viewModel.abertos.observe(this, abertosObserver);
        viewModel.resolvidos.observe(this, resolvidosObserver);

        tabHost = binding.tabs;
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Em Aberto");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Em Aberto");
        tabHost.addTab(spec);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Resolvidos");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Resolvidos");
        tabHost.addTab(spec2);

    }

    Observer<List<ProblemaTipo>> abertosObserver = new Observer<List<ProblemaTipo>>() {
        @Override
        public void onChanged(List<ProblemaTipo> problemas) {
            adapter.problemas = problemas;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<List<ProblemaTipo>> resolvidosObserver = new Observer<List<ProblemaTipo>>() {
        @Override
        public void onChanged(List<ProblemaTipo> problemas) {
            adapterResolvidos.problemas = problemas;
            adapterResolvidos.notifyDataSetChanged();
        }
    };

}
