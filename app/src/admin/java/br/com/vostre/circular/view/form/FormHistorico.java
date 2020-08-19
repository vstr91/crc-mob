package br.com.vostre.circular.view.form;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingAdapter;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormHistoricoBinding;
import br.com.vostre.circular.model.HistoricoItinerario;
import br.com.vostre.circular.model.Itinerario;
import br.com.vostre.circular.viewModel.DetalhesItinerarioViewModel;

public class FormHistorico extends FormBase {

    FormHistoricoBinding binding;
    Calendar data;

    LineChart chart;
    Button btnTrocar;

    DetalhesItinerarioViewModel viewModel;
    Itinerario itinerario;
    List<HistoricoItinerario> historico;

    static Application ctx;

    public static Application getCtx() {
        return ctx;
    }

    public static void setCtx(Application ctx) {
        FormHistorico.ctx = ctx;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public List<HistoricoItinerario> getHistorico() {
        return historico;
    }

    public void setHistorico(List<HistoricoItinerario> historico) {
        this.historico = historico;
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
                inflater, R.layout.form_historico, container, false);
        super.onCreate(savedInstanceState);

        chart = binding.chart;

        List<Entry> valores = new ArrayList<>();

        for(HistoricoItinerario h : historico){
            valores.add(new Entry((float) historico.indexOf(h), Float.valueOf(String.valueOf(h.getTarifa()))));
        }

        LineDataSet setComp1 = new LineDataSet(valores, "Company 1");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        // the labels that should be drawn on the XAxis
        final String[] quarters = new String[] {"Q1"};

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }

        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();

        viewModel = ViewModelProviders.of(this.getActivity()).get(DetalhesItinerarioViewModel.class);

        binding.setView(this);
        binding.setViewModel(viewModel);

        return binding.getRoot();

    }


    public void onClickFechar(View v){
        dismiss();
    }

}
