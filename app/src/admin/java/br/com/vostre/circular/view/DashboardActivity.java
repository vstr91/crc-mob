package br.com.vostre.circular.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.joda.time.format.DateTimeFormat;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.ActivityDashboardBinding;
import br.com.vostre.circular.databinding.ActivitySobreBinding;
import br.com.vostre.circular.model.pojo.AcessoDia;
import br.com.vostre.circular.model.pojo.ItinerarioPartidaDestino;
import br.com.vostre.circular.viewModel.DashboardViewModel;

public class DashboardActivity extends BaseActivity {

    ActivityDashboardBinding binding;
    DashboardViewModel viewModel;
    LineChart chart;
    PieChart chartItinerarios;

    int qtdMunicipais = 0;
    int qtdIntermunicipais = 0;
    boolean triggerGrafico = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        super.onCreate(savedInstanceState);
        binding.setView(this);
        setTitle("Dashboard");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        chart = binding.chart;
        chartItinerarios = binding.chartItinerarios;

        chart.setNoDataText("Carregando dados...");
        chart.setNoDataTextColor(R.color.azul);
        chart.invalidate();

        chartItinerarios.setNoDataText("Carregando dados...");
        chartItinerarios.setNoDataTextColor(R.color.azul);
        chartItinerarios.invalidate();

        viewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        viewModel.acessos.observe(this, acessosObserver);
        viewModel.itinerarios.observe(this, itinerariosObserver);
        viewModel.paradas.observe(this, paradasObserver);
        viewModel.cidades.observe(this, cidadesObserver);
        viewModel.horarios.observe(this, horariosObserver);

        viewModel.itinerariosMunicipais.observe(this, municipaisObserver);
        viewModel.itinerariosIntermunicipais.observe(this, intermunicipaisObserver);

    }

    public void onClickCardAcesso(View v){
        Intent i = new Intent(getApplicationContext(), AcessoDiaActivity.class);
        startActivity(i);
    }

    public void onClickCardItinerario(View v){

    }

    public void onClickCardParada(View v){

    }

    public void onClickCardCidade(View v){

    }

    public void onClickCardHorario(View v){

    }

    Observer<List<AcessoDia>> acessosObserver = new Observer<List<AcessoDia>>() {
        @Override
        public void onChanged(final List<AcessoDia> acessos) {
            geraGraficoAcessos(acessos);
        }
    };

    private void geraGraficoAcessos(final List<AcessoDia> acessos) {
        List<Entry> entries = new ArrayList<>();
        int d = 0;

        Collections.reverse(acessos);

        Description descr = new Description();
        descr.setText("");

        chart.setDescription(descr);

        for (AcessoDia dia : acessos) {

            // turn your data into Entry objects
            entries.add(new Entry(d, dia.getTotalAcessos()));
            d++;
        }

        LineDataSet setComp1 = new LineDataSet(entries, "Acessos - Usuários Únicos");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DateTimeFormat.forPattern("dd/MM").print(DateTimeFormat.forPattern("YYYY-MM-dd").parseDateTime(acessos.get((int) value).getDia()));
            }

        };

        IAxisValueFormatter formatterY = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.getNumberInstance().setMaximumFractionDigits(0);
                nf.setMinimumFractionDigits(0);
                return nf.format(value);
            }



        };

        IValueFormatter valueFormatterY = new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.getNumberInstance().setMaximumFractionDigits(0);
                nf.setMinimumFractionDigits(0);
                return nf.format(value);
            }
        };

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        yAxis.setValueFormatter(formatterY);

        LineData data = new LineData(setComp1);
        data.setValueFormatter(valueFormatterY);
        chart.setData(data);
        chart.setClickable(false);
        chart.invalidate(); // refresh
    }

    Observer<Integer> itinerariosObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer itinerarios) {
            //binding.textViewItinerarios.setText(String.valueOf(itinerarios));
        }
    };

    Observer<Integer> paradasObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer paradas) {
            binding.textViewParadas.setText(String.valueOf(paradas));
        }
    };

    Observer<Integer> cidadesObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer cidades) {
            binding.textViewCidades.setText(String.valueOf(cidades));
        }
    };

    Observer<Integer> horariosObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer horarios) {
            binding.textViewHorarios.setText(String.valueOf(horarios));
        }
    };

    Observer<List<ItinerarioPartidaDestino>> municipaisObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
            qtdMunicipais = itinerarios.size();

            if(!triggerGrafico){
                geraGraficoItinerarios();
            }

        }
    };

    Observer<List<ItinerarioPartidaDestino>> intermunicipaisObserver = new Observer<List<ItinerarioPartidaDestino>>() {
        @Override
        public void onChanged(List<ItinerarioPartidaDestino> itinerarios) {
            qtdIntermunicipais = itinerarios.size();

            if(!triggerGrafico){
                geraGraficoItinerarios();
            }


        }
    };

    private void geraGraficoItinerarios(){

        if(qtdMunicipais > 0 && qtdIntermunicipais > 0){
            triggerGrafico = true;

            Description descr = new Description();
            descr.setText("");

            chartItinerarios.setDescription(descr);

            List<Integer> colors = new ArrayList<>();
            colors.add(ContextCompat.getColor(getApplicationContext(), R.color.ciano));
            colors.add(ContextCompat.getColor(getApplicationContext(), R.color.azul));

            List<PieEntry> entries = new ArrayList<>();

            entries.add(new PieEntry(qtdMunicipais, "Municipais - "+qtdMunicipais));
            entries.add(new PieEntry(qtdIntermunicipais, "Intermunicipais - "+qtdIntermunicipais));

            PieDataSet set = new PieDataSet(entries, "");

            set.setColors(colors);

            PieData data = new PieData(set);
            chartItinerarios.setData(data);
            chartItinerarios.setUsePercentValues(true);
            chartItinerarios.setEntryLabelColor(Color.WHITE);
            chartItinerarios.setClickable(false);
            chartItinerarios.setDrawCenterText(false);
            chartItinerarios.setDrawEntryLabels(false);
            chartItinerarios.invalidate(); // refresh
        }

    }

}
