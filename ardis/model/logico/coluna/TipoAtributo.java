package ardis.model.logico.coluna;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;


public enum TipoAtributo implements Serializable{
    
    BIGINT,
    BOOLEAN,
    CHAR,
    DATE,
    DATETIME,
    DECIMAL,
    DOUBLE,
    FLOAT,
    INT,
    NUMERIC,
    SMALLINT, 
    TEXT,
    TIME,
    TIMESTAMP,
    TINYINT,
    VARCHAR;
    
  
    public static TipoAtributo[] getSortedVaules() {
        TipoAtributo[] statures = values();
        Arrays.sort(statures, EnumByNameComparator.INSTANCE);
        return statures;
    }

    private static class EnumByNameComparator implements Comparator<Enum<?>> {

        public static final Comparator<Enum<?>> INSTANCE = new EnumByNameComparator();

        public int compare(Enum<?> enum1, Enum<?> enum2) {
            return enum1.name().compareTo(enum2.name());
        }

    }
   
    
}
