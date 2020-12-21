package br.com.vostre.circular.model.log;

public enum TiposLog {
    ITINERARIO_TRADICIONAL(0),
    ITINERARIO_POR_LINHA(1),
    ITINERARIO_POR_DESTINO(2),
    ITINERARIO_INVERSAO(3),
    PARADA(4),
    PARADA_DETALHE(5),
    QUADRO_DE_HORARIOS(6);

    private int valor;

    private TiposLog(int valor){
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }
}
