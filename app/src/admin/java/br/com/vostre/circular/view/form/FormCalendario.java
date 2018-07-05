package br.com.vostre.circular.view.form;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.TabHost;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormCalendarioBinding;

public class FormCalendario extends DialogFragment {

    FormCalendarioBinding binding;
    TabHost tabHost;
    Calendar data;
    Calendar dataAnterior;
    FormBase parent;

    public DialogFragment getParent() {
        return parent;
    }

    public void setParent(FormBase parent) {
        this.parent = parent;
    }

    public Calendar getDataAnterior() {
        return dataAnterior;
    }

    public void setDataAnterior(Calendar dataAnterior) {
        this.dataAnterior = dataAnterior;
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

        if(dataAnterior != null){
            binding.timePicker.setCurrentHour(dataAnterior.get(Calendar.HOUR_OF_DAY));
            binding.timePicker.setCurrentMinute(dataAnterior.get(Calendar.MINUTE));

            binding.calendarView.setDate(dataAnterior.getTimeInMillis());
            setDataEscolhida(dataAnterior.get(Calendar.DAY_OF_MONTH),
                    dataAnterior.get(Calendar.MONTH), dataAnterior.get(Calendar.YEAR));
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

        if(data == null){
            data = Calendar.getInstance();
        }

        System.out.println(binding.timePicker.getCurrentHour());

        data.set(Calendar.HOUR_OF_DAY, binding.timePicker.getCurrentHour());
        data.set(Calendar.MINUTE, binding.timePicker.getCurrentMinute());

        parent.setData(data);

    }

    private void setDataEscolhida(Integer dia, Integer mes, Integer ano){

        if(data == null){
            data = Calendar.getInstance(Locale.getDefault());
        }

        data.set(Calendar.DAY_OF_MONTH, dia);
        data.set(Calendar.MONTH, mes);
        data.set(Calendar.YEAR, ano);

        data.set(Calendar.HOUR_OF_DAY, binding.timePicker.getCurrentHour());
        data.set(Calendar.MINUTE, binding.timePicker.getCurrentMinute());

        parent.setData(data);
    }

}
