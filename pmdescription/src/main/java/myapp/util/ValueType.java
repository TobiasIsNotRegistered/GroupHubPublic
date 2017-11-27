package myapp.util;

/**
 * @author Dieter Holz
 */
public enum ValueType {
    //todo: add your application specific value types
    ID("[\\d]+", "%d"),
    STRING,
    FLOAT("[+-]?[\\d']*[\\.]?[\\d]*", "%,.2f"),
    DOUBLE("[+-]?[\\d']*[\\.]?[\\d]*", "%,.2f"),
    INT("[+-]?[\\d']{0,14}", "%,d"),
    LONG("[+-]?[\\d']+", "%,2d"),
    BOOLEAN("((?i)" + "true" + "){1}|((?i)" + "false" + "){1}", "%s"),
    FOREIGN_KEY("[\\d]+", "%d"),
    YEAR("[-]|[\\d]{4}", "%d"),
    ISO2_COUNTRY_CODE,
    IMAGE_URL;

    private final String formatPattern;
    private final String syntaxPattern;

    ValueType(){
        this(".*", "%s");
    }

    ValueType(String syntaxPattern, String formatPattern){
        this.syntaxPattern = syntaxPattern;
        this.formatPattern = formatPattern;
    }

    public boolean isNumber(){
        return isWholeNumber() || DOUBLE.equals(this) || FLOAT.equals(this);
    }

    public Object convert(String data){
        switch (this) {
            case FLOAT:
                return data == null ? 0.0 : Float.valueOf(data);
            case DOUBLE:
                return data == null ? 0.0 : Double.valueOf(data);
            case BOOLEAN:
                return data == null ? false : Boolean.valueOf(data);
            case INT: case YEAR:
                return data == null ? 0 : Integer.valueOf(data);
            case LONG: case ID: case FOREIGN_KEY:
                return data == null ? 0 : Long.valueOf(data);
            case STRING : case IMAGE_URL: case ISO2_COUNTRY_CODE:
                return data == null ? "" : data;
            default:
                return data == null ? "" : data;
        }
    }

    public boolean isWholeNumber(){
        return ID.equals(this) || INT.equals(this) || LONG.equals(this) || YEAR.equals(this);
    }

    public String formatPattern() {
        return formatPattern;
    }

    public String syntaxPattern() {
        return syntaxPattern;
    }
}
