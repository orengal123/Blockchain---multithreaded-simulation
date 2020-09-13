package blockchain;

public class User {

    final static int INITIAL_BALANCE = 100;

    private int balance;
    private String name;

    public User(String name) {
        this.balance = INITIAL_BALANCE;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    protected void increaseBalance(int amount) {
        this.balance += amount;
    }

    protected void decreaseBalance(int amount) {
        this.balance -= amount;
    }
}
