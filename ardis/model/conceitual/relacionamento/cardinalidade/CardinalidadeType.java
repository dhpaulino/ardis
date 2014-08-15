package ardis.model.conceitual.relacionamento.cardinalidade;

public enum CardinalidadeType implements Cloneable {

    ZEROPARAUM("0:1"), UMPARAUM("1:1"), ZEROPARAMUITOS("0:N"), UMPARAMUITOS("1:N");

    private CardinalidadeType(String cardinalidade) {
        this.cardinalidade = cardinalidade;
    }
    private final String cardinalidade;

    @Override
    public String toString() {
        return cardinalidade;
    }

    public boolean isMuitos() {
        return (this.equals(CardinalidadeType.UMPARAMUITOS)
                || this.equals(CardinalidadeType.ZEROPARAMUITOS));



    }

    public boolean isMaxUm() {
        return (this.equals(CardinalidadeType.ZEROPARAUM)
                || this.equals(CardinalidadeType.UMPARAUM));


    }

}
