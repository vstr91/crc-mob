package br.com.vostre.circular.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Rect;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.List;

import br.com.vostre.circular.R;

public class DestaqueUtils {

    public static void geraDestaqueUnico(Activity ctx, View v, String titulo, String descricao, TapTargetView.Listener listener, boolean cancelavel, boolean transparente) {
        TapTargetView.showFor(ctx, TapTarget.forView(v, titulo, descricao).outerCircleColor(R.color.azul).cancelable(cancelavel).transparentTarget(transparente), listener);
    }

    public static void geraDestaqueUnico(Dialog ctx, View v, String titulo, String descricao, TapTargetView.Listener listener, boolean cancelavel, boolean transparente) {
        TapTargetView.showFor(ctx, TapTarget.forView(v, titulo, descricao).outerCircleColor(R.color.azul).cancelable(cancelavel).transparentTarget(transparente), listener);
    }

    public static void geraDestaqueUnico(Dialog ctx, Rect rect, String titulo, String descricao, TapTargetView.Listener listener, boolean cancelavel, boolean transparente) {
        TapTargetView.showFor(ctx, TapTarget.forBounds(rect, titulo, descricao).outerCircleColor(R.color.azul).cancelable(cancelavel).transparentTarget(transparente), listener);
    }

    public static void geraSequenciaDestaques(Activity ctx, List<TapTarget> targets, TapTargetSequence.Listener listener){
        new TapTargetSequence(ctx).targets(targets).listener(listener).start();
    }

    public static TapTarget geraTapTarget(View v, String titulo, String descricao, boolean cancelavel, boolean transparente, int id){

        return TapTarget.forView(v, titulo, descricao).outerCircleColor(R.color.azul).cancelable(cancelavel).transparentTarget(transparente).id(id).targetRadius(50);



    }

    public static TapTarget geraTapTarget(View v, String titulo, String descricao, boolean cancelavel, boolean transparente, int id, int raio){
        return TapTarget.forView(v, titulo, descricao).outerCircleColor(R.color.azul).cancelable(cancelavel).transparentTarget(transparente).id(id).targetRadius(raio);
    }

}
