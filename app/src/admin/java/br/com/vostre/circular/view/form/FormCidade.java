package br.com.vostre.circular.view.form;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormCidadeBinding;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.pojo.CidadeEstado;
import br.com.vostre.circular.view.adapter.EstadoAdapterSpinner;
import br.com.vostre.circular.viewModel.CidadesViewModel;

public class FormCidade extends FormBase {

    FormCidadeBinding binding;
    Calendar data;

    TextView textViewProgramado;
    Button btnTrocar;

    ImageView imageViewBrasao;
    Button btnTrocarBrasao;

    CidadesViewModel viewModel;

    CidadeEstado cidade;
    public Boolean flagInicioEdicao;
    EstadoAdapterSpinner adapter;

    static Application ctx;

    public static final int PICK_IMAGE = 300;

    public CidadeEstado getCidade() {
        return cidade;
    }

    public void setCidade(CidadeEstado cidade) {
        this.cidade = cidade;
    }

    public static Application getCtx() {
        return ctx;
    }

    public static void setCtx(Application ctx) {
        FormCidade.ctx = ctx;
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
                inflater, R.layout.form_cidade, container, false);
        super.onCreate(savedInstanceState);
        binding.setView(this);

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        imageViewBrasao = binding.imageView;
        btnTrocarBrasao = binding.btnTrocarBrasao;

        textViewProgramado.setVisibility(View.GONE);
        btnTrocar.setVisibility(View.GONE);

        imageViewBrasao.setVisibility(View.GONE);
        btnTrocarBrasao.setVisibility(View.GONE);

        viewModel = ViewModelProviders.of(this.getActivity()).get(CidadesViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(cidade != null){
            viewModel.cidade = cidade;

            if(cidade.getCidade().getBrasao() != null){
                File brasao = new File(getContext().getApplicationContext().getFilesDir(),  cidade.getCidade().getBrasao());//new File(ctx.getApplicationContext().getFilesDir(), cidade.getCidade().getBrasao());

                System.out.println("CAMINHO: "+brasao.getAbsolutePath()+" | EXISTE: "+brasao.exists()+" | LE: "+brasao.canRead());

                if(brasao.exists() && brasao.canRead()){
                    Bitmap bmp = BitmapFactory.decodeFile(brasao.getAbsolutePath());
                    viewModel.setBrasao(bmp);
                    exibeBrasao();
                } else{
                    ocultaBrasao();
                }
            }

            flagInicioEdicao = true;
        }

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        if(viewModel.cidade.getCidade().getProgramadoPara() == null){
            textViewProgramado.setVisibility(View.GONE);
            btnTrocar.setVisibility(View.GONE);
        } else{
            exibeDataEscolhida();
        }

        viewModel.estados.observe(this, estadosObserver);

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        if(cidade != null){
            viewModel.editarCidade();
        } else{
            viewModel.salvarCidade();
        }

        dismiss();
    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickTrocar(View v){
        FormCalendario formCalendario = new FormCalendario();
        formCalendario.setParent(this);
        formCalendario.setDataAnterior(viewModel.cidade.getCidade().getProgramadoPara().toCalendar(null));
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    public void onSwitchProgramadoChange(CompoundButton btn, boolean ativo){

        if(flagInicioEdicao && cidade.getCidade().getProgramadoPara() != null){
            flagInicioEdicao = false;
            return;
        }

        if(ativo){
            FormCalendario formCalendario = new FormCalendario();
            formCalendario.setParent(this);

            if(viewModel.cidade.getCidade().getProgramadoPara() != null){
                formCalendario.setDataAnterior(viewModel.cidade.getCidade().getProgramadoPara().toCalendar(null));
            }

            formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
        } else{
            ocultaDataEscolhida();
        }

    }

    @Override
    public void setData(Calendar umaData) {
        viewModel.cidade.getCidade().setProgramadoPara(new DateTime(umaData,
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))));

        if(viewModel.cidade.getCidade().getProgramadoPara() == null){
            ocultaDataEscolhida();
        } else{
            exibeDataEscolhida();
        }

    }

    private void ocultaDataEscolhida(){
        binding.switchProgramado.setChecked(false);
        textViewProgramado.setVisibility(View.GONE);
        textViewProgramado.setText("");
        btnTrocar.setVisibility(View.GONE);
        viewModel.cidade.getCidade().setProgramadoPara(null);
    }

    private void exibeDataEscolhida(){

        textViewProgramado.setText(DateTimeFormat
                .forPattern("dd/MM/yy HH:mm").print(viewModel.cidade.getCidade().getProgramadoPara()));

        textViewProgramado.setVisibility(View.VISIBLE);
        btnTrocar.setVisibility(View.VISIBLE);
    }

    private void ocultaBrasao(){
        imageViewBrasao.setVisibility(View.GONE);
        btnTrocarBrasao.setVisibility(View.GONE);
        viewModel.brasao = null;
        viewModel.cidade.getCidade().setBrasao(null);
        binding.btnBrasao.setVisibility(View.VISIBLE);
    }

    public void exibeBrasao(){
        imageViewBrasao.setImageBitmap(viewModel.brasao);
        imageViewBrasao.invalidate();
        imageViewBrasao.setVisibility(View.VISIBLE);
        btnTrocarBrasao.setVisibility(View.VISIBLE);
        binding.btnBrasao.setVisibility(View.GONE);
    }

    @BindingAdapter("entries")
    public static void setSpinnerEntries(Spinner spinner, LiveData<List<Estado>> estados){

        if(estados.getValue() != null){
            EstadoAdapterSpinner adapter = new EstadoAdapterSpinner(ctx, R.layout.linha_estados_spinner, R.id.textViewNome, estados.getValue());
            spinner.setAdapter(adapter);
        }

    }

    public void setSpinnerEntries(Spinner spinner, List<Estado> estados){

        if(estados != null){
            EstadoAdapterSpinner adapter = new EstadoAdapterSpinner(ctx, R.layout.linha_estados_spinner, R.id.textViewNome, estados);
            spinner.setAdapter(adapter);

            if(cidade != null){
                Estado estado = new Estado();
                estado.setId(cidade.getCidade().getEstado());
                int i = viewModel.estados.getValue().indexOf(estado);
                binding.spinnerEstado.setSelection(i);
            }

        }

    }

    public void onItemSelectedSpinnerEstado(AdapterView<?> adapterView, View view, int i, long l){

        if(viewModel.estados.getValue() != null){
            viewModel.estado = viewModel.estados.getValue().get(i);
        }

    }

    public void onClickBtnBrasao(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent, "Escolha a imagem do brasão"), PICK_IMAGE);
    }

    @BindingAdapter("srcCompat")
    public static void setImagemBrasao(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }

    }

    Observer<List<Estado>> estadosObserver = new Observer<List<Estado>>() {
        @Override
        public void onChanged(List<Estado> estados) {
            setSpinnerEntries(binding.spinnerEstado, estados);
        }
    };

}
