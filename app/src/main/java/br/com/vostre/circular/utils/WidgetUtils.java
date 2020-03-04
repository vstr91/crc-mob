package br.com.vostre.circular.utils;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import br.com.vostre.circular.R;

public class WidgetUtils {

    public static void formataTabs(TabWidget tw, Context ctx, int textSize, int color){

        for (int i = 0; i < tw.getChildCount(); ++i){
            final View tabView = tw.getChildTabViewAt(i);
            final TextView tv = (TextView) tabView.findViewById(android.R.id.title);

            if(textSize > 0){
                tv.setTextSize(textSize);
            }

            if(color > 0){
                tv.setTextColor(ctx.getResources().getColor(color));
            }

        }

    }

}
