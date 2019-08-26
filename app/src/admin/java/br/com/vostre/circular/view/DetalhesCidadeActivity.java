package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalhesCidadeBinding;
import br.com.vostre.circular.databinding.ActivityDetalhesEmpresaBinding;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Cidade;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.model.Onibus;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.view.adapter.BairroAdapter;
import br.com.vostre.circular.view.adapter.ItinerarioAdapter;
import br.com.vostre.circular.view.adapter.OnibusAdapter;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.view.form.FormOnibus;
import br.com.vostre.circular.viewModel.BairrosViewModel;
import br.com.vostre.circular.viewModel.DetalhesCidadeViewModel;
import br.com.vostre.circular.viewModel.DetalhesEmpresaViewModel;

public class DetalhesCidadeActivity extends BaseActivity {

    ActivityDetalhesCidadeBinding binding;
    DetalhesCidadeViewModel viewModel;

    RecyclerView listBairros;
    List<BairroCidade> bairros;
    BairroAdapter adapter;

    AppCompatActivity ctx;

    static int PICK_FILE_CSV = 704;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhes_cidade);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(DetalhesCidadeViewModel.class);

        viewModel.setCidade(getIntent().getStringExtra("cidade"));
        ctx = this;

        viewModel.cidade.observe(this, cidadeObserver);

        binding.setView(this);
        setTitle("Detalhes Cidade");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listBairros = binding.listBairros;

        adapter = new BairroAdapter(bairros, this);

        listBairros.setAdapter(adapter);
    }

    public void onFabClick(View v){

        FormBairro formBairro = new FormBairro();
        formBairro.setCtx(getApplication());
        formBairro.setCidadeDetalhada(viewModel.cidade.getValue());
        formBairro.flagInicioEdicao = false;
        formBairro.show(getSupportFragmentManager(), "formBairro");

    }

    public void onFabLoteClick(View v){

        Intent intentFile = new Intent();
                intentFile.setType("text/*");
                intentFile.setAction(Intent.ACTION_GET_CONTENT);
                this.startActivityForResult(Intent.createChooser(intentFile, "Escolha o arquivo de dados"), PICK_FILE_CSV);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_FILE_CSV) {

            if(data != null){
                try {
                    InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());

                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder dados = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        String[] dado = line.split(",");

                        String nome = dado[0];

                        if(nome != null && !nome.isEmpty()){
                            Bairro bairro = new Bairro();
                            bairro.setNome(nome);
                            bairro.setCidade(viewModel.cidade.getValue().getCidade().getId());
                            bairro.setAtivo(true);
                            BairrosViewModel.addEstatico(bairro, getApplicationContext());
                        }



                    }

                    Toast.makeText(getApplicationContext(), "Finalizou!", Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    Observer<List<BairroCidade>> bairrosObserver = new Observer<List<BairroCidade>>() {
        @Override
        public void onChanged(List<BairroCidade> bairros) {
            adapter.bairros = bairros;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<CidadeEstado> cidadeObserver = new Observer<CidadeEstado>() {
        @Override
        public void onChanged(CidadeEstado cidade) {

            if(cidade != null){
                binding.textViewCidade.setText(cidade.getCidade().getNome());
                viewModel.carregarBairros(cidade.getCidade().getId());
                viewModel.bairros.observe(ctx, bairrosObserver);
            }

        }
    };

}
