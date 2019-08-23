package br.com.vostre.circular.view.utils;


import br.com.vostre.circular.R;

public enum PagerModel {

//    PRIMEIRA("Primeira", R.layout.novidade_210_login),
//    SEGUNDA("Segunda", R.layout.novidade_210_sugestao),
//    TERCEIRA("Terceira", R.layout.novidade_210_horario);

    PRIMEIRA("Primeira", R.layout.novidade_220_itinerarios);

    private String mTitleResId;
    private int mLayoutResId;

    PagerModel(String titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public String getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }

}
