package br.com.vostre.circular.view.form;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormUsuarioBinding;
import br.com.vostre.circular.model.Usuario;
import br.com.vostre.circular.viewModel.UsuariosViewModel;

public class FormUsuario extends FormBase {

    FormUsuarioBinding binding;
    Calendar data;

    TextView textViewProgramado;
    Button btnTrocar;

    UsuariosViewModel viewModel;

    Usuario usuario;
    public Boolean flagInicioEdicao;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
                inflater, R.layout.form_usuario, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(UsuariosViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(usuario != null){
            viewModel.usuario = usuario;
            flagInicioEdicao = true;
        }

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        if(viewModel.usuario.getProgramadoPara() == null){
            textViewProgramado.setVisibility(View.GONE);
            btnTrocar.setVisibility(View.GONE);
        } else{
            exibeDataEscolhida();
        }

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        if(usuario != null){
            viewModel.editarUsuario();
        } else{
            viewModel.salvarUsuario();
        }

        dismiss();
    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickTrocar(View v){
        FormCalendario formCalendario = new FormCalendario();
        formCalendario.setParent(this);
        formCalendario.setDataAnterior(viewModel.usuario.getProgramadoPara().toCalendar(null));
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    public void onSwitchProgramadoChange(CompoundButton btn, boolean ativo){

        if(flagInicioEdicao && usuario.getProgramadoPara() != null){
            flagInicioEdicao = false;
            return;
        }

        if(ativo){
            FormCalendario formCalendario = new FormCalendario();
            formCalendario.setParent(this);

            if(viewModel.usuario.getProgramadoPara() != null){
                formCalendario.setDataAnterior(viewModel.usuario.getProgramadoPara().toCalendar(null));
            }

            formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
        } else{
            ocultaDataEscolhida();
        }

    }

    @Override
    public void setData(Calendar umaData) {
        viewModel.usuario.setProgramadoPara(new DateTime(umaData,
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))));

        if(viewModel.usuario.getProgramadoPara() == null){
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
        viewModel.usuario.setProgramadoPara(null);
    }

    private void exibeDataEscolhida(){

        textViewProgramado.setText(DateTimeFormat
                .forPattern("dd/MM/yy HH:mm").print(viewModel.usuario.getProgramadoPara()));

        textViewProgramado.setVisibility(View.VISIBLE);
        btnTrocar.setVisibility(View.VISIBLE);
    }

}
