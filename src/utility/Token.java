package utility;

/**
 *
 * Token实体
 *
 */
public class Token {

    /**
     * 类型
     */
    private String type;

    /**
     * 检索到的值
     */
    private String value;

    /**
     * 错误信息
     */
    private String error;

    /**
     *  未传入error时，则没有发生错误
     */
    public Token(String type, String value) {
        this.type = type;
        this.value = value;
        this.error = "no error";
    }

    /**
     *  传入error时，type未知
     */
    public Token(String type, String value, String error) {
        this.type = type;
        this.value = value;
        this.error = error;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
