package br.com.vostre.circular.listener;

import java.util.List;

import br.com.vostre.circular.model.pojo.HorarioItinerarioNome;

public interface HorarioCarregadoListener {

    public void onLoaded(List<HorarioItinerarioNome> horarios);

}
