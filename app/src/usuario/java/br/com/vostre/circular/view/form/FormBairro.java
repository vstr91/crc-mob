package br.com.vostre.circular.view.form;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormBairroBinding;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.adapter.BairroAdapter;
import br.com.vostre.circular.view.listener.ItemListener;
import br.com.vostre.circular.view.listener.SelectListener;
import br.com.vostre.circular.viewModel.BairrosViewModel;
import br.com.vostre.circular.viewModel.ItinerariosViewModel;

public class FormBairro extends FormBase implements SelectListener {

    FormBairroBinding binding;

    ItinerariosViewModel viewModel;

    BairroCidade bairro;

    static Application ctx;
    BairroAdapter adapter;
    RecyclerView listBairros;

    CidadeEstado cidade;
    ItemListener listener;

    public ItemListener getListener() {
        return listener;
    }

    public void setListener(ItemListener listener) {
        this.listener = listener;
    }

    public CidadeEstado getCidade() {
        return cidade;
    }

    public void setCidade(CidadeEstado cidade) {
        this.cidade = cidade;
        viewModel.setCidadePartida(cidade);
    }

    public Application getCtx() {
        return ctx;
    }

    public void setCtx(Application ctx) {
        this.ctx = ctx;
    }

    public BairroCidade getBairro() {
        return bairro;
    }

    public void setBairro(BairroCidade bairro) {
        this.bairro = bairro;
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
                inflater, R.layout.form_bairro, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(ItinerariosViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(getArguments() != null){
            Cidade umaCidade = new Cidade();
            umaCidade.setId(getArguments().getString("cidade"));
            cidade = new CidadeEstado();
            cidade.setCidade(umaCidade);

            if(viewModel.escolhaAtual == 0){
                viewModel.setCidadePartida(cidade);
                viewModel.bairros.observe(this, bairrosObserver);
                adapter = new BairroAdapter(viewModel.bairros.getValue(), ctx.getApplicationContext());
                //viewModel.escolhaAtual = 1;
            } else{
                viewModel.setCidadeDestino(cidade);
                viewModel.bairrosDestino.observe(this, bairrosObserver);
                adapter = new BairroAdapter(viewModel.bairrosDestino.getValue(), ctx.getApplicationContext());
            }

        }

        listBairros = binding.listBairros;

        adapter.setListener(this);

        listBairros.setAdapter(adapter);

        return binding.getRoot();

    }

    Observer<List<BairroCidade>> bairrosObserver = new Observer<List<BairroCidade>>() {
        @Override
        public void onChanged(List<BairroCidade> bairros) {
            adapter.bairros = bairros;
            adapter.notifyDataSetChanged();

            if(bairros.size() == 1){
                listener.onItemSelected(bairros.get(0).getBairro().getId());
                dismiss();
            }

        }
    };

    @Override
    public String onSelected(String id) {
        listener.onItemSelected(id);
        dismiss();

        return id;
    }
}
