package core.spring.springcoremoduleproject.Entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Account {

    private final int id;
    private static int idQuantity = 0;

    private final int userId;

    private double moneyAmount;

    public Account(int userId) {
        this.id = ++idQuantity;
        this.userId = userId;
        this.moneyAmount = 0;
    }

    public Account(int userId, double moneyAmount) {
        this.id = ++idQuantity;
        this.userId = userId;
        this.moneyAmount = moneyAmount;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", moneyAmount=" + moneyAmount +
                '}';
    }

    public void plusMoney (double plusMoney){
        moneyAmount += plusMoney;
    }

    public void minusMoney (double minusMoney){
        moneyAmount -= minusMoney;
    }

    public int getId() {
        return id;
    }

    public double getMoneyAmount() {
        return moneyAmount;
    }

    public int getUserId() {
        return userId;
    }
}
