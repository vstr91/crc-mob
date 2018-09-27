package br.com.vostre.circular.view.form;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormEstadoBinding;
import br.com.vostre.circular.databinding.FormHorarioItinerarioBinding;
import br.com.vostre.circular.model.Estado;
import br.com.vostre.circular.model.HorarioItinerario;
import br.com.vostre.circular.model.Pais;
import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;
import br.com.vostre.circular.viewModel.EstadosViewModel;
import br.com.vostre.circular.viewModel.HorariosItinerarioViewModel;

public class FormHorarioItinerario extends FormBase {

    FormHorarioItinerarioBinding binding;
    Calendar data;

    TextView textViewProgramado;
    Button btnTrocar;

    HorariosItinerarioViewModel viewModel;

    HorarioItinerarioNome horario;
    public Boolean flagInicioEdicao;

    static Application ctx;

    public Application getCtx() {
        return ctx;
    }

    public void setCtx(Application ctx) {
        this.ctx = ctx;
    }

    public HorarioItinerarioNome getHorario() {
        return horario;
    }

    public void setHorario(HorarioItinerarioNome horario) {
        this.horario = horario;
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
                inflater, R.layout.form_horario_itinerario, container, false);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this.getActivity()).get(HorariosItinerarioViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        if(horario != null){
            viewModel.horario = horario;
            flagInicioEdicao = true;
        }

        textViewProgramado = binding.textViewProgramado;
        btnTrocar = binding.btnTrocar;

        if(viewModel.horario.getHorarioItinerario() == null ||
                viewModel.horario.getHorarioItinerario().getProgramadoPara() == null){
            textViewProgramado.setVisibility(View.GONE);
            btnTrocar.setVisibility(View.GONE);
        } else{
            exibeDataEscolhida();
        }

        return binding.getRoot();

    }

    public void onClickSalvar(View v){

        HorarioItinerario hi = horario.getHorarioItinerario();

        if(hi.getDomingo() || hi.getSegunda() || hi.getTerca() || hi.getQuarta() ||
                hi.getQuinta() || hi.getSexta() || hi.getSabado()){
            horario.getHorarioItinerario().setItinerario(viewModel.iti.get().getItinerario().getId());
            horario.getHorarioItinerario().setHorario(horario.getIdHorario());

            viewModel.editarHorario();

            viewModel.retorno.observe(this, retornoObserver);

        } else{
            Toast.makeText(ctx, "Ao menos um dia deve ser selecionado!", Toast.LENGTH_SHORT).show();
        }

    }

    public void onClickFechar(View v){
        dismiss();
    }

    public void onClickTrocar(View v){
        FormCalendario formCalendario = new FormCalendario();
        formCalendario.setParent(this);
        formCalendario.setDataAnterior(viewModel.horario.getHorarioItinerario().getProgramadoPara().toCalendar(null));
        formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
    }

    public void onSwitchProgramadoChange(CompoundButton btn, boolean ativo){

        if(flagInicioEdicao && horario.getHorarioItinerario() != null
                && horario.getHorarioItinerario().getProgramadoPara() != null){
            flagInicioEdicao = false;
            return;
        }

        if(ativo){
            FormCalendario formCalendario = new FormCalendario();
            formCalendario.setParent(this);

            if(viewModel.horario.getHorarioItinerario() != null
                    && viewModel.horario.getHorarioItinerario().getProgramadoPara() != null){
                formCalendario.setDataAnterior(viewModel.horario.getHorarioItinerario()
                        .getProgramadoPara().toCalendar(null));
            }

            formCalendario.show(getActivity().getSupportFragmentManager(), "formCalendario");
        } else{
            ocultaDataEscolhida();
        }

    }

    @Override
    public void setData(Calendar umaData) {

        if(viewModel.horario.getHorarioItinerario() == null){
            viewModel.horario.setHorarioItinerario(new HorarioItinerario());
        }

        viewModel.horario.getHorarioItinerario().setProgramadoPara(new DateTime(umaData,
                DateTimeZone.forTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))));

        if(viewModel.horario.getHorarioItinerario().getProgramadoPara() == null){
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
        viewModel.horario.getHorarioItinerario().setProgramadoPara(null);
    }

    private void exibeDataEscolhida(){

        if(viewModel.horario.getHorarioItinerario() != null){
            textViewProgramado.setText(DateTimeFormat
                    .forPattern("dd/MM/yy HH:mm").print(viewModel.horario.getHorarioItinerario().getProgramadoPara()));

            textViewProgramado.setVisibility(View.VISIBLE);
            btnTrocar.setVisibility(View.VISIBLE);
        }

    }

    Observer<Integer> retornoObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer retorno) {

            if(retorno == 1){
                Toast.makeText(getContext().getApplicationContext(), "Horário cadastrado!", Toast.LENGTH_SHORT).show();
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
