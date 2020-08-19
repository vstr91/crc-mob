package br.com.vostre.circular.view.form;

import android.app.Application;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormAcessoBinding;
import br.com.vostre.circular.model.Acesso;
import br.com.vostre.circular.model.pojo.AcessoTotal;
import br.com.vostre.circular.view.adapter.AcessoDetalheAdapter;
import br.com.vostre.circular.viewModel.AcessosViewModel;

public class FormAcesso extends FormBase {

    FormAcessoBinding binding;

    AcessosViewModel viewModel;

    RecyclerView listAcessos;
    List<Acesso> acessos;
    AcessoDetalheAdapter adapter;

    AcessoTotal acesso;

    static Application ctx;

    String dia;

    public Application getCtx() {
        return ctx;
    }

    public void setCtx(Application ctx) {
        this.ctx = ctx;
    }

    public AcessoTotal getAcesso() {
        return acesso;
    }

    public void setAcesso(AcessoTotal acesso) {
        this.acesso = acesso;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.form_pais, container, false);
//
//        if(this.getDialog() != null){
//            this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        }
//
//        return view;

        binding = DataBindingUtil.inflate(
                inflater, R.layout.form_acesso, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(AcessosViewModel.class);

        binding.setAcesso(acesso);
        viewModel.setDia(dia);
        viewModel.setAcesso(acesso);
        viewModel.acessosDetalhe.observe(this, acessosObserver);

        listAcessos = binding.listAcessos;

        adapter = new AcessoDetalheAdapter(acessos, (AppCompatActivity) getActivity());

        listAcessos.setAdapter(adapter);

        binding.setView(this);
        binding.setViewModel(viewModel);

        return binding.getRoot();

    }

    public void onClickFechar(View v){
        dismiss();
    }

    Observer<List<Acesso>> acessosObserver = new Observer<List<Acesso>>() {
        @Override
        public void onChanged(List<Acesso> acessos) {
            adapter.acessos = acessos;
            adapter.notifyDataSetChanged();
        }
    };

}
