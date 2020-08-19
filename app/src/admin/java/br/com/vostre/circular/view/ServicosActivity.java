package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityServicosBinding;
import br.com.vostre.circular.model.Servico;
import br.com.vostre.circular.utils.DialogUtils;
import br.com.vostre.circular.view.adapter.ServicoAdapter;
import br.com.vostre.circular.view.form.FormServico;
import br.com.vostre.circular.viewModel.ServicosViewModel;

public class ServicosActivity extends BaseActivity {

    ActivityServicosBinding binding;
    ServicosViewModel viewModel;

    RecyclerView listServicos;
    List<Servico> servicos;
    ServicoAdapter adapter;

    FormServico formServico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_servicos);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ServicosViewModel.class);
        viewModel.servicos.observe(this, servicosObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Serviços");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listServicos = binding.listServicos;

        adapter = new ServicoAdapter(servicos, this);

        listServicos.setAdapter(adapter);

    }

    public void onFabClick(View v){
        FormServico formServico = new FormServico();
        formServico.flagInicioEdicao = false;
        formServico.show(getSupportFragmentManager(), "formServico");
//        viewModel.retorno.observe(this, retornoObserver);
    }

    Observer<List<Servico>> servicosObserver = new Observer<List<Servico>>() {
        @Override
        public void onChanged(List<Servico> servicos) {
            adapter.servicos = servicos;
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        formServico = (FormServico) DialogUtils.getOpenedDialog(this);

        if (requestCode == FormServico.PICK_IMAGE) {

            if (data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    viewModel.foto = BitmapFactory.decodeStream(inputStream);
                    formServico.exibeImagem();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


        }

    }

}
