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
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.view.adapter.MensagemAdapter;
import br.com.vostre.circular.view.form.FormMensagem;
import br.com.vostre.circular.viewModel.MensagensViewModel;

public class MensagensActivity extends BaseActivity {

    ActivityMensagensBinding binding;
    MensagensViewModel viewModel;

    RecyclerView listMensagens;
    List<Mensagem> mensagens;
    MensagemAdapter adapter;

    RecyclerView listMensagensRecebidas;
    List<Mensagem> mensagensRecebidas;
    MensagemAdapter adapterRecebidas;

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mensagens);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(MensagensViewModel.class);
        viewModel.mensagens.observe(this, mensagensObserver);
        viewModel.mensagensRecebidas.observe(this, mensagensRecebidasObserver);

        binding.setView(this);
        setTitle("Mensagens");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listMensagens = binding.listMensagens;

        adapter = new MensagemAdapter(mensagens, this);

        listMensagens.setAdapter(adapter);

        listMensagensRecebidas = binding.listMensagensRecebidas;

        adapterRecebidas = new MensagemAdapter(mensagensRecebidas, this);

        listMensagensRecebidas.setAdapter(adapterRecebidas);

        tabHost = binding.tabs;
        tabHost.setup();

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Recebidas");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Recebidas");
        tabHost.addTab(spec2);

        TabHost.TabSpec spec = tabHost.newTabSpec("Enviadas");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Enviadas");
        tabHost.addTab(spec);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(viewModel != null){
            viewModel.atualizarMensagens();
            viewModel.mensagens.observe(this, mensagensObserver);
            viewModel.mensagensRecebidas.observe(this, mensagensRecebidasObserver);
        }

    }

    public void onFabClick(View v){
        FormMensagem formMensagem = new FormMensagem();
        formMensagem.flagInicioEdicao = false;
        formMensagem.show(getSupportFragmentManager(), "formMensagem");
    }

    Observer<List<Mensagem>> mensagensObserver = new Observer<List<Mensagem>>() {
        @Override
        public void onChanged(List<Mensagem> mensagens) {
            adapter.mensagens = mensagens;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<List<Mensagem>> mensagensRecebidasObserver = new Observer<List<Mensagem>>() {
        @Override
        public void onChanged(List<Mensagem> mensagens) {
            adapterRecebidas.mensagens = mensagens;
            adapterRecebidas.notifyDataSetChanged();
        }
    };

}
