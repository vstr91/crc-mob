package br.com.vostre.circular.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityImagensParadasBinding;
import br.com.vostre.circular.databinding.ActivityPaisesBinding;
import br.com.vostre.circular.listener.ParadaSugestaoListener;
import br.com.vostre.circular.model.ImagemParada;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.pojo.ImagemParadaBairro;
import br.com.vostre.circular.model.pojo.ParadaSugestaoBairro;
import br.com.vostre.circular.view.adapter.ImagemParadaAdapter;
import br.com.vostre.circular.view.adapter.PaisAdapter;
import br.com.vostre.circular.view.form.FormPais;
import br.com.vostre.circular.viewModel.ImagensParadasViewModel;
import br.com.vostre.circular.viewModel.PaisesViewModel;

public class ImagensParadasActivity extends BaseActivity implements ParadaSugestaoListener {

    ActivityImagensParadasBinding binding;
    ImagensParadasViewModel viewModel;

    RecyclerView listImagens;
    List<ImagemParadaBairro> imagensParadas;
    ImagemParadaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_imagens_paradas);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(ImagensParadasViewModel.class);
        viewModel.imagensParadas.observe(this, imagensParadasObserver);

        binding.setView(this);
        //binding.setViewModel(viewModel);
        setTitle("Fotos Paradas");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        listImagens = binding.listImagens;

        adapter = new ImagemParadaAdapter(imagensParadas, this, this);

        listImagens.setAdapter(adapter);

    }

    @Override
    public void onSelected(String id, int acao) {

        ImagemParadaBairro imagemParada = new ImagemParadaBairro();
        ImagemParada ip = new ImagemParada();
        ip.setId(id);
        imagemParada.setImagemParada(ip);

        final ImagemParadaBairro p = viewModel.imagensParadas.getValue().get(viewModel.imagensParadas.getValue().indexOf(imagemParada));

        switch(acao){
            case 0:
                // reiniciar
                viewModel.reiniciarSugestao(p.getImagemParada());
                break;
            case 1:
                // aceitou
                viewModel.aceitaSugestao(p.getImagemParada());
                Toast.makeText(getApplicationContext(), "Aceitou", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                // rejeitou
                viewModel.rejeitaSugestao(p.getImagemParada());
                Toast.makeText(getApplicationContext(), "Rejeitou", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    Observer<List<ImagemParadaBairro>> imagensParadasObserver = new Observer<List<ImagemParadaBairro>>() {
        @Override
        public void onChanged(List<ImagemParadaBairro> imagensParadas) {
            adapter.imagens = imagensParadas;
            adapter.notifyDataSetChanged();
        }
    };

}
