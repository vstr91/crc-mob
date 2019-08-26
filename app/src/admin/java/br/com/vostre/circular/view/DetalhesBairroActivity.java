package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDetalhesBairroBinding;
import br.com.vostre.circular.databinding.ActivityDetalhesCidadeBinding;
import br.com.vostre.circular.model.Bairro;
import br.com.vostre.circular.model.Parada;
import br.com.vostre.circular.model.pojo.BairroCidade;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.model.pojo.ParadaBairro;
import br.com.vostre.circular.view.adapter.BairroAdapter;
import br.com.vostre.circular.view.adapter.ParadaAdapter;
import br.com.vostre.circular.view.form.FormBairro;
import br.com.vostre.circular.viewModel.BairrosViewModel;
import br.com.vostre.circular.viewModel.DetalhesBairroViewModel;
import br.com.vostre.circular.viewModel.DetalhesCidadeViewModel;
import br.com.vostre.circular.viewModel.ParadasViewModel;

public class DetalhesBairroActivity extends BaseActivity {

    ActivityDetalhesBairroBinding binding;
    DetalhesBairroViewModel viewModel;

    RecyclerView listParadas;
    List<ParadaBairro> paradas;
    ParadaAdapter adapter;

    AppCompatActivity ctx;

    static int PICK_FILE_CSV = 704;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detalhes_bairro);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(DetalhesBairroViewModel.class);

        viewModel.setBairro(getIntent().getStringExtra("bairro"));
        ctx = this;

        viewModel.bairro.observe(this, bairroObserver);

        binding.setView(this);
        setTitle("Detalhes Bairro");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listParadas = binding.listParadas;

        adapter = new ParadaAdapter(paradas, this);

        listParadas.setAdapter(adapter);
    }

//    public void onFabClick(View v){
//
//        FormBairro formBairro = new FormBairro();
//        formBairro.setCtx(getApplication());
//        formBairro.setCidadeDetalhada(viewModel.cidade.getValue());
//        formBairro.flagInicioEdicao = false;
//        formBairro.show(getSupportFragmentManager(), "formBairro");
//
//    }

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
                    String line;
                    while ((line = r.readLine()) != null) {
                        String[] dado = line.split(",");

                        String nome = dado[0];
                        Double latitude = Double.parseDouble(dado[1]);
                        Double longitude = Double.parseDouble(dado[2]);
                        Double taxaEmbarque = Double.parseDouble(dado[3]);

                        if(nome != null && !nome.isEmpty()){
                            Parada parada = new Parada();
                            parada.setNome(nome);
                            parada.setLatitude(latitude);
                            parada.setLongitude(longitude);
                            parada.setTaxaDeEmbarque(taxaEmbarque);
                            parada.setImagemEnviada(true);
                            parada.setAtivo(true);
                            parada.setBairro(viewModel.bairro.getValue().getBairro().getId());
                            ParadasViewModel.addEstatico(parada, getApplicationContext());
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

    Observer<List<ParadaBairro>> paradasObserver = new Observer<List<ParadaBairro>>() {
        @Override
        public void onChanged(List<ParadaBairro> paradas) {
            adapter.paradas = paradas;
            adapter.notifyDataSetChanged();
        }
    };

    Observer<BairroCidade> bairroObserver = new Observer<BairroCidade>() {
        @Override
        public void onChanged(BairroCidade bairro) {

            if(bairro != null){
                binding.textViewBairro.setText(bairro.getBairro().getNome());
                viewModel.carregarParadas(bairro.getBairro().getId());
                viewModel.paradas.observe(ctx, paradasObserver);
            }

        }
    };

}
