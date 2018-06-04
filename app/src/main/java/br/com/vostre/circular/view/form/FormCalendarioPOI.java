package br.com.vostre.circular.view.form;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TabHost;

import java.util.Calendar;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormCalendarioBinding;

public class FormCalendarioPOI extends FormCalendario {

    FormCalendarioBinding binding;
    TabHost tabHost;
    Calendar dataInicio;
    Calendar dataInicioAnterior;
    Calendar dataFim;
    Calendar dataFimAnterior;
    FormPOIBase parent;
    Integer qual;

    public DialogFragment getParent() {
        return parent;
    }

    public void setParent(FormPOIBase parent) {
        this.parent = parent;
    }

    public Calendar getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Calendar dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Calendar getDataInicioAnterior() {
        return dataInicioAnterior;
    }

    public void setDataInicioAnterior(Calendar dataInicioAnterior) {
        this.dataInicioAnterior = dataInicioAnterior;
    }

    public Calendar getDataFim() {
        return dataFim;
    }

    public void setDataFim(Calendar dataFim) {
        this.dataFim = dataFim;
    }

    public Calendar getDataFimAnterior() {
        return dataFimAnterior;
    }

    public void setDataFimAnterior(Calendar dataFimAnterior) {
        this.dataFimAnterior = dataFimAnterior;
    }

    public Integer getQual() {
        return qual;
    }

    public void setQual(Integer qual) {
        this.qual = qual;
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
                inflater, R.layout.form_calendario, container, false);
        super.onCreate(savedInstanceState);
        binding.setView(this);

        binding.timePicker.setIs24HourView(true);

        if(qual == 0 && dataInicioAnterior != null){
            binding.timePicker.setCurrentHour(dataInicioAnterior.get(Calendar.HOUR_OF_DAY));
            binding.timePicker.setCurrentMinute(dataInicioAnterior.get(Calendar.MINUTE));

            binding.calendarView.setDate(dataInicioAnterior.getTimeInMillis());
            setDataEscolhida(dataInicioAnterior.get(Calendar.DAY_OF_MONTH),
                    dataInicioAnterior.get(Calendar.MONTH), dataInicioAnterior.get(Calendar.YEAR));
        }

        if(qual == 1 && dataFimAnterior != null){
            binding.timePicker.setCurrentHour(dataFimAnterior.get(Calendar.HOUR_OF_DAY));
            binding.timePicker.setCurrentMinute(dataFimAnterior.get(Calendar.MINUTE));

            binding.calendarView.setDate(dataFimAnterior.getTimeInMillis());
            setDataEscolhida(dataFimAnterior.get(Calendar.DAY_OF_MONTH),
                    dataFimAnterior.get(Calendar.MONTH), dataFimAnterior.get(Calendar.YEAR));
        }

        tabHost = binding.tabs;
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec("Dia");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Dia");
        tabHost.addTab(spec);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("Hora");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Hora");
        tabHost.addTab(spec2);

        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                setDataEscolhida(dayOfMonth, month, year);
            }
        });

        return binding.getRoot();

    }

    public void onClickEscolher(View v){
//        Toast.makeText(getContext(), "DATA >>>> "+data+" | "+"HORA >>>> "
//                +binding.timePicker.getCurrentHour()+" : "+binding.timePicker.getCurrentMinute(),
//                Toast.LENGTH_SHORT).show();

        setDataEscolhida();

        dismiss();
    }

    public void onClickFechar(View v){
        dismiss();
        parent.setData(dataAnterior);
    }

    private void setDataEscolhida(){

        if(qual == 0){
            if(dataInicio == null){
                dataInicio = Calendar.getInstance();
            }

            dataInicio.set(Calendar.HOUR_OF_DAY, binding.timePicker.getCurrentHour());
            dataInicio.set(Calendar.MINUTE, binding.timePicker.getCurrentMinute());

            parent.setDataInicio(dataInicio);
        } else{
            if(dataFim == null){
                dataFim = Calendar.getInstance();
            }

            dataFim.set(Calendar.HOUR_OF_DAY, binding.timePicker.getCurrentHour());
            dataFim.set(Calendar.MINUTE, binding.timePicker.getCurrentMinute());

            parent.setDataFim(dataFim);
        }



    }

    private void setDataEscolhida(Integer dia, Integer mes, Integer ano){

        if(qual == 0){
            if(dataInicio == null){
                dataInicio = Calendar.getInstance();
            }

            dataInicio.set(Calendar.DAY_OF_MONTH, dia);
            dataInicio.set(Calendar.MONTH, mes);
            dataInicio.set(Calendar.YEAR, ano);

            dataInicio.set(Calendar.HOUR_OF_DAY, binding.timePicker.getCurrentHour());
            dataInicio.set(Calendar.MINUTE, binding.timePicker.getCurrentMinute());

            parent.setDataInicio(dataInicio);
        } else{
            if(dataFim == null){
                dataFim = Calendar.getInstance();
            }

            dataFim.set(Calendar.DAY_OF_MONTH, dia);
            dataFim.set(Calendar.MONTH, mes);
            dataFim.set(Calendar.YEAR, ano);

            dataFim.set(Calendar.HOUR_OF_DAY, binding.timePicker.getCurrentHour());
            dataFim.set(Calendar.MINUTE, binding.timePicker.getCurrentMinute());

            parent.setDataFim(dataFim);
        }

    }

}
