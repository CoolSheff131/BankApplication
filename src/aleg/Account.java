package aleg;

public class Account {

    private int number;
    private String type;
    private float sum;

    public Account(int num, String kind, float s){
        number = num;
        type = kind;
        sum = s;
    }

    public Account(){}

    public int getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public float getSum() {
        return sum;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }
}
