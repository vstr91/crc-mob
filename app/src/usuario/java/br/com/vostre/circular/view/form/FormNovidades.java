package br.com.vostre.circular.view.form;

import android.app.Dialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import br.com.vostre.circular.R;
import br.com.vostre.circular.databinding.FormNovidadesBinding;
import br.com.vostre.circular.model.Mensagem;
import br.com.vostre.circular.utils.PreferenceUtils;
import br.com.vostre.circular.view.utils.CustomPagerAdapter;
import br.com.vostre.circular.view.utils.PagerModel;
import br.com.vostre.circular.viewModel.MensagensViewModel;
import me.relex.circleindicator.CircleIndicator;

public class FormNovidades extends FormBase {

    FormNovidadesBinding binding;

    String versao;

    Bundle bundle;
    FirebaseAnalytics mFirebaseAnalytics;

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
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
                inflater, R.layout.form_novidades, container, false);
        super.onCreate(savedInstanceState);

        binding.setView(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity().getApplicationContext());

        ViewPager viewPager = binding.viewPager;
        final CustomPagerAdapter adapter = new CustomPagerAdapter(this.getActivity());
        viewPager.setAdapter(adapter);

        if(adapter.getCount() == 1){
            binding.btnEntendi.setVisibility(View.VISIBLE);
            binding.btnFechar.setVisibility(View.INVISIBLE);
        } else{
            binding.btnEntendi.setVisibility(View.INVISIBLE);
            binding.btnFechar.setVisibility(View.VISIBLE);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position+1 == adapter.getCount()){
                    binding.btnFechar.setVisibility(View.INVISIBLE);
                    binding.btnEntendi.setVisibility(View.VISIBLE);
                } else{
                    binding.btnFechar.setVisibility(View.VISIBLE);
                    binding.btnEntendi.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.textView62.setText(versao);

        CircleIndicator indicator = binding.indicator;
        indicator.setViewPager(viewPager);


        return binding.getRoot();

    }

    public void onClickFechar(View v){
        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("clicou_pular_novidades", bundle);

        dismiss();
    }

    public void onClickEntendi(View v){
        bundle = new Bundle();
        mFirebaseAnalytics.logEvent("clicou_entendi_novidades", bundle);

        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

}
