package core.spring.springcoremoduleproject.Entities;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class User {
    private final int id;
    private static int idQuantity = 0;
    private final String login;
    private final List<Account> accountList = new ArrayList<>();

    public User(String login, double firstAccountDefaultAmount) {
        this.id = ++idQuantity;
        this.login = login;
        accountList.add(new Account(id, firstAccountDefaultAmount));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ",\naccountList=" + accountList +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccountList() {
        return accountList;
    }
}
