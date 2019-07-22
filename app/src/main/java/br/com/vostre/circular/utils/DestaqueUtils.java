package br.com.vostre.circular.utils;

import android.app.Activity;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.List;

import br.com.vostre.circular.R;

public class DestaqueUtils {

    public static void geraDestaqueUnico(Activity ctx, View v, String titulo, String descricao, TapTargetView.Listener listener) {
        TapTargetView.showFor(ctx, TapTarget.forView(v, titulo, descricao), listener);
    }

    public static void geraSequenciaDestaques(Activity ctx, List<TapTarget> targets, TapTargetSequence.Listener listener){
        new TapTargetSequence(ctx).targets(targets).listener(listener).start();
    }

    public static TapTarget geraTapTarget(View v, String titulo, String descricao){
        return TapTarget.forView(v, titulo, descricao).outerCircleColor(R.color.azul);
    }

}
