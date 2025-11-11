package core.spring.springcoremoduleproject.Services;

import core.spring.springcoremoduleproject.Entities.Account;
import core.spring.springcoremoduleproject.Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Scanner;
import java.util.InputMismatchException;

@Service
@PropertySource("classpath:application.properties")
public class OperationsConsoleListener {
    private final UserService userService;
    private final AccountService accountService;
    @Value("${account.transfer-commission}")
    private double transferCommission;

    @Autowired
    public OperationsConsoleListener(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("\nPlease enter one of operation type:");
                System.out.println("- USER_CREATE");
                System.out.println("- SHOW_ALL_USERS");
                System.out.println("- ACCOUNT_CREATE");
                System.out.println("- ACCOUNT_CLOSE");
                System.out.println("- ACCOUNT_DEPOSIT");
                System.out.println("- ACCOUNT_TRANSFER");
                System.out.println("- ACCOUNT_WITHDRAW");

                String command = scanner.nextLine().trim().toUpperCase();

                switch (command) {
                    case "USER_CREATE":
                        handleUserCreate(scanner);
                        break;
                    case "SHOW_ALL_USERS":
                        handleShowAllUsers();
                        break;
                    case "ACCOUNT_CREATE":
                        handleAccountCreate(scanner);
                        break;
                    case "ACCOUNT_CLOSE":
                        handleAccountClose(scanner);
                        break;
                    case "ACCOUNT_DEPOSIT":
                        handleAccountDeposit(scanner);
                        break;
                    case "ACCOUNT_TRANSFER":
                        handleAccountTransfer(scanner);
                        break;
                    case "ACCOUNT_WITHDRAW":
                        handleAccountWithdraw(scanner);
                        break;
                    default:
                        System.out.println("Unknown command. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handleUserCreate(Scanner scanner) {
        try {
            System.out.println("Enter login for new user:");
            String login = scanner.nextLine().trim();
            if (login.isEmpty()) {
                throw new IllegalArgumentException("Login cannot be empty.");
            }
            userService.createUser(login);
            User newUser = userService.getUserList().stream()
                    .filter(user -> user.getLogin().equals(login))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not created."));
            System.out.println("User created: " + newUser);
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    private void handleShowAllUsers() {
        try {
            System.out.println("List of all users:");
            if (userService.getUserList().isEmpty()) {
                System.out.println("No users found.");
            } else {
                for (User user : userService.getUserList()) {
                    System.out.println(user);
                }
            }
        } catch (Exception e) {
            System.out.println("Error showing users: " + e.getMessage());
        }
    }

    private void handleAccountCreate(Scanner scanner) {
        try {
            System.out.println("Enter the user ID for which to create an account:");
            int userId = Integer.parseInt(scanner.nextLine().trim());
            User user = userService.findUserById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User with ID " + userId + " not found.");
            }
            accountService.createAccount(user);
            Account newAccount = user.getAccountList().stream()
                    .max((a1, a2) -> Integer.compare(a1.getId(), a2.getId()))
                    .orElseThrow(() -> new RuntimeException("Account not created."));
            System.out.println("New account created with ID: " + newAccount.getId() + " for user: " + user.getLogin());
        } catch (NumberFormatException e) {
            System.out.println("Invalid user ID format.");
        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    private void handleAccountClose(Scanner scanner) {
        try {
            System.out.println("Enter account ID to close:");
            int accountId = Integer.parseInt(scanner.nextLine().trim());

            Account accountToClose = null;
            User accountOwner = null;
            for (User user : userService.getUserList()) {
                for (Account account : user.getAccountList()) {
                    if (account.getId() == accountId) {
                        accountToClose = account;
                        accountOwner = user;
                        break;
                    }
                }
                if (accountToClose != null) {
                    break;
                }
            }

            if (accountToClose == null || accountOwner == null) {
                throw new IllegalArgumentException("Account with ID " + accountId + " not found.");
            }

            if (accountOwner.getAccountList().size() <= 1) {
                throw new IllegalArgumentException("Cannot close the only account for user: " + accountOwner.getLogin());
            }

            double balance = accountToClose.getMoneyAmount();
            Account firstAccount = accountOwner.getAccountList().get(0);
            firstAccount.plusMoney(balance);

            accountOwner.getAccountList().remove(accountToClose);

            System.out.println("Account with ID " + accountId + " has been closed. Balance transferred to account ID " + firstAccount.getId());
        } catch (NumberFormatException e) {
            System.out.println("Invalid account ID format.");
        } catch (Exception e) {
            System.out.println("Error closing account: " + e.getMessage());
        }
    }


    private void handleAccountDeposit(Scanner scanner) {
        try {
            System.out.println("Enter user ID:");
            int userId = Integer.parseInt(scanner.nextLine().trim());
            User user = userService.findUserById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User with ID " + userId + " not found.");
            }
            System.out.println("Enter account ID:");
            int accountId = Integer.parseInt(scanner.nextLine().trim());
            Account account = user.getAccountList().stream()
                    .filter(acc -> acc.getId() == accountId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Account with ID " + accountId + " not found."));
            System.out.println("Enter amount to deposit:");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive.");
            }
            account.plusMoney(amount);
            System.out.println("Amount " + amount + " deposited to account ID: " + accountId);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format.");
        } catch (Exception e) {
            System.out.println("Error depositing to account: " + e.getMessage());
        }
    }

    private void handleAccountTransfer(Scanner scanner) {
        try {
            System.out.println("Enter source user ID:");
            int fromUserId = Integer.parseInt(scanner.nextLine().trim());
            User fromUser = userService.findUserById(fromUserId);
            if (fromUser == null) {
                throw new IllegalArgumentException("User with ID " + fromUserId + " not found.");
            }
            System.out.println("Enter target user ID:");
            int toUserId = Integer.parseInt(scanner.nextLine().trim());
            User toUser = userService.findUserById(toUserId);
            if (toUser == null) {
                throw new IllegalArgumentException("User with ID " + toUserId + " not found.");
            }
            System.out.println("Enter source account ID:");
            int fromAccountId = Integer.parseInt(scanner.nextLine().trim());
            Account fromAccount = fromUser.getAccountList().stream()
                    .filter(acc -> acc.getId() == fromAccountId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Source account with ID " + fromAccountId + " not found."));
            System.out.println("Enter target account ID:");
            int toAccountId = Integer.parseInt(scanner.nextLine().trim());
            Account toAccount = toUser.getAccountList().stream()
                    .filter(acc -> acc.getId() == toAccountId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Target account with ID " + toAccountId + " not found."));
            System.out.println("Enter amount to transfer:");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive.");
            }
            if (fromAccount.getMoneyAmount() < amount) {
                throw new IllegalArgumentException("Not enough money on account ID " + fromAccountId);
            }
            double amountWithCommission = amount * (1 + (transferCommission / 100));
            fromAccount.minusMoney(amountWithCommission);
            toAccount.plusMoney(amount);
            System.out.println("Amount " + amount + " transferred from account ID " + fromAccountId + " to account ID " + toAccountId);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format.");
        } catch (Exception e) {
            System.out.println("Error transferring amount: " + e.getMessage());
        }
    }


    private void handleAccountWithdraw(Scanner scanner) {
        try {
            System.out.println("Enter user ID:");
            int userId = Integer.parseInt(scanner.nextLine().trim());
            User user = userService.findUserById(userId);
            if (user == null) {
                throw new IllegalArgumentException("User with ID " + userId + " not found.");
            }
            System.out.println("Enter account ID to withdraw from:");
            int accountId = Integer.parseInt(scanner.nextLine().trim());
            Account account = user.getAccountList().stream()
                    .filter(acc -> acc.getId() == accountId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Account with ID " + accountId + " not found."));
            System.out.println("Enter amount to withdraw:");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive.");
            }
            if (account.getMoneyAmount() < amount) {
                throw new IllegalArgumentException("Not enough money on account ID " + accountId);
            }
            account.minusMoney(amount);
            System.out.println("Amount " + amount + " withdrawn from account ID: " + accountId);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format.");
        } catch (Exception e) {
            System.out.println("Error withdrawing amount: " + e.getMessage());
        }
    }
}
