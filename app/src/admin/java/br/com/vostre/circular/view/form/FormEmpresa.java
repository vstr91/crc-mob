package br.com.vostre.circular.view.form;

import android.app.Application;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormEmpresaBinding;
import br.com.vostre.circular.model.Empresa;
import br.com.vostre.circular.viewModel.EmpresasViewModel;

public class FormEmpresa extends FormBase {

    FormEmpresaBinding binding;
    Calendar data;

    TextView textViewProgramado;
    Button btnTrocar;

    ImageView imageViewLogo;
    Button btnTrocarLogo;

    EmpresasViewModel viewModel;

    Empresa empresa;
    public Boolean flagInicioEdicao;

    static Application ctx;

    public static final int PICK_IMAGE = 300;

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public static Application getCtx() {
        return ctx;
    }

    public static void setCtx(Application ctx) {
        FormEmpresa.ctx = ctx;
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
                inflater, R.layout.form_empresa, container, false);
        super.onCreate(savedInstanceState);
        binding.setView(this);

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        imageViewLogo = binding.imageView;
        btnTrocarLogo = binding.btnTrocarLogo;

        textViewProgramado.setVisibility(View.GONE);
        btnTrocar.setVisibility(View.GONE);

        imageViewLogo.setVisibility(View.GONE);
        btnTrocarLogo.setVisibility(View.GONE);

        viewModel = ViewModelProviders.of(getActivity()).get(EmpresasViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(empresa != null){
            viewModel.empresa = empresa;

            if(empresa.getLogo() != null && !empresa.getLogo().isEmpty()){
                File logo = new File(ctx.getFilesDir(), empresa.getLogo());

                if(logo.exists() && logo.canRead()){
                    Bitmap bmp = BitmapFactory.decodeFile(logo.getAbsolutePath());
                    viewModel.setLogo(bmp);
                    exibeLogo();
                } else{
                    ocultaLogo();
                }
            } else{
                ocultaLogo();
            }

            flagInicioEdicao = true;
        }

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        if(viewModel.empresa.getProgramadoPara() == null){
            textViewProgramado.setVisibility(View.GONE);
            btnTrocar.setVisibility(View.GONE);
        } else{
            exibeDataEscolhida();
        }

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        if(empresa != null){
            viewModel.editarEmpresa();
        } else{
            viewModel.salvarEmpresa();
        }

        viewModel.retorno.observe(this, retornoObserver);
    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickTrocar(View v){
        FormCalendario formCalendario = new FormCalendario();
        formCalendario.setParent(this);
        formCalendario.setDataAnterior(viewModel.empresa.getProgramadoPara().toCalendar(null));
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    public void onSwitchProgramadoChange(CompoundButton btn, boolean ativo){

        if(flagInicioEdicao && empresa.getProgramadoPara() != null){
            flagInicioEdicao = false;
            return;
        }

        if(ativo){
            FormCalendario formCalendario = new FormCalendario();
            formCalendario.setParent(this);

            if(viewModel.empresa.getProgramadoPara() != null){
                formCalendario.setDataAnterior(viewModel.empresa.getProgramadoPara().toCalendar(null));
            }

            formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
        } else{
            ocultaDataEscolhida();
        }

    }

    @Override
    public void setData(Calendar umaData) {
        viewModel.empresa.setProgramadoPara(new DateTime(umaData,
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))));

        if(viewModel.empresa.getProgramadoPara() == null){
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
        viewModel.empresa.setProgramadoPara(null);
    }

    private void exibeDataEscolhida(){

        textViewProgramado.setText(DateTimeFormat
                .forPattern("dd/MM/yy HH:mm").print(viewModel.empresa.getProgramadoPara()));

        textViewProgramado.setVisibility(View.VISIBLE);
        btnTrocar.setVisibility(View.VISIBLE);
    }

    private void ocultaLogo(){
        binding.switchProgramado.setChecked(false);
        imageViewLogo.setVisibility(View.GONE);
        btnTrocarLogo.setVisibility(View.GONE);
        viewModel.logo = null;
        viewModel.empresa.setLogo(null);
        binding.btnLogo.setVisibility(View.VISIBLE);
    }

    public void exibeLogo(){
        imageViewLogo.setImageBitmap(viewModel.logo);
        imageViewLogo.invalidate();
        imageViewLogo.setVisibility(View.VISIBLE);
        btnTrocarLogo.setVisibility(View.VISIBLE);
        binding.btnLogo.setVisibility(View.GONE);
    }

    public void onClickBtnLogo(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(Intent.createChooser(intent, "Escolha a imagem da logo"), PICK_IMAGE);
    }

    @BindingAdapter("srcCompat")
    public static void setImagemLogo(ImageView imageView, Bitmap bitmap){

        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }

    }

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getContext().getApplicationContext(), "Empresa cadastrada!", Toast.LENGTH_SHORT).show();
                viewModel.setEmpresa(new Empresa());
                dismiss();
            } else if(retorno == 0){
                Toast.makeText(getContext().getApplicationContext(),
                        "Dados necessários não informados. Por favor preencha " +
                                "todos os dados obrigatórios!",
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

}
