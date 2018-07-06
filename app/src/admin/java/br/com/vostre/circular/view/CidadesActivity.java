package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityCidadesBinding;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.utils.DialogUtils;
import br.com.vostre.circular.view.adapter.CidadeAdapter;

public class CidadesActivity extends BaseActivity {

    ActivityCidadesBinding binding;
    CidadesViewModel viewModel;

    RecyclerView listCidades;
    List<CidadeEstado> cidades;
    CidadeAdapter adapter;

    public FormCidade formCidade;

    public FormCidade getFormCidade() {
        return formCidade;
    }

    public void setFormCidade(FormCidade formCidade) {
        this.formCidade = formCidade;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cidades);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(CidadesViewModel.class);
        viewModel.cidades.observe(this, cidadesObserver);

        binding.setView(this);
        setTitle("Cidades");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listCidades = binding.listCidades;

        adapter = new CidadeAdapter(cidades, this);

        listCidades.setAdapter(adapter);
    }

    public void onFabClick(View v) {
        formCidade = new FormCidade();
        formCidade.setCtx(getApplication());
        formCidade.flagInicioEdicao = false;
        formCidade.show(getSupportFragmentManager(), "formCidade");
    }

    Observer<List<CidadeEstado>> cidadesObserver = new Observer<List<CidadeEstado>>() {
        @Override
        public void onChanged(List<CidadeEstado> cidades) {
            adapter.cidades = cidades;
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        formCidade = (FormCidade) DialogUtils.getOpenedDialog(this);

        if (requestCode == FormCidade.PICK_IMAGE) {

            if (data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    viewModel.brasao = BitmapFactory.decodeStream(inputStream);
                    formCidade.exibeBrasao();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


        }

    }

}
