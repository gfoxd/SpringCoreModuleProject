package core.spring.springcoremoduleproject.Services;

import core.spring.springcoremoduleproject.Entities.Account;
import core.spring.springcoremoduleproject.Entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Value("${account.default-amount}")
    private double defaultAmount;
    @Value("${account.transfer-commission}")
    private double commission;

    public void createAccount(User user) {
        double initialAmount = user.getAccountList().isEmpty() ? defaultAmount : 0.0;
        Account newAccount = new Account(user.getId(), initialAmount);
        user.getAccountList().add(newAccount);
    }
}
