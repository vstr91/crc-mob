package br.com.vostre.circular.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityEmpresasBinding;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.utils.DialogUtils;
import br.com.vostre.circular.view.adapter.EmpresaAdapter;
import br.com.vostre.circular.view.form.FormEmpresa;
import br.com.vostre.circular.viewModel.EmpresasViewModel;

public class EmpresasActivity extends BaseActivity {

    ActivityEmpresasBinding binding;
    EmpresasViewModel viewModel;

    RecyclerView listEmpresas;
    List<Empresa> empresas;
    EmpresaAdapter adapter;

    FormEmpresa formEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_empresas);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(EmpresasViewModel.class);
        viewModel.empresas.observe(this, empresasObserver);

        binding.setView(this);
        setTitle("Empresas");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listEmpresas = binding.listEmpresas;

        adapter = new EmpresaAdapter(empresas, this);

        listEmpresas.setAdapter(adapter);
        binding.editTextFiltro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if(s.length() > 0){
                    adapter.getFilter().filter(s);
                //}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void onFabClick(View v){
        formEmpresa = new FormEmpresa();
        formEmpresa.flagInicioEdicao = false;
        formEmpresa.setCtx(getApplication());
        formEmpresa.show(getSupportFragmentManager(), "formEmpresa");
    }

    Observer<List<Empresa>> empresasObserver = new Observer<List<Empresa>>() {
        @Override
        public void onChanged(List<Empresa> empresas) {
            adapter.empresas = empresas;
            adapter.empresasOriginal = empresas;
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        formEmpresa = (FormEmpresa) DialogUtils.getOpenedDialog(this);

        if (requestCode == FormEmpresa.PICK_IMAGE) {

            if (data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    viewModel.logo = BitmapFactory.decodeStream(inputStream);
                    formEmpresa.exibeLogo();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


        }

    }

}
