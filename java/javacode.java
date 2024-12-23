import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Custom exception for insufficient funds
class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

// BankAccount class representing a bank account
class BankAccount {
    private String accountNumber;
    private String accountHolder;
    private double balance;

    public BankAccount(String accountNumber, String accountHolder) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = 0.0;
    }

    public synchronized void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: " + amount + " to account: " + accountNumber);
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }

    public synchronized void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal.");
        }
        balance -= amount;
        System.out.println("Withdrew: " + amount + " from account: " + accountNumber);
    }

    public synchronized double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }
}

// Bank class to manage accounts
class Bank {
    private Map<String, BankAccount> accounts = new HashMap<>();

    public void createAccount(String accountNumber, String accountHolder) {
        accounts.put(accountNumber, new BankAccount(accountNumber, accountHolder));
        System.out.println("Account created for: " + accountHolder);
    }

    public BankAccount getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
}

// Thread class for handling transactions
class TransactionThread extends Thread {
    private Bank bank;
    private String accountNumber;
    private double amount;
    private String transactionType;

    public TransactionThread(Bank bank, String accountNumber, double amount, String transactionType) {
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.transactionType = transactionType;
    }

    @Override
    public void run() {
        BankAccount account = bank.getAccount(accountNumber);
        if (account != null) {
            try {
                if (transactionType.equals("deposit")) {
                    account.deposit(amount);
                } else if (transactionType.equals("withdraw")) {
                    account.withdraw(amount);
                }
                System.out.println("Current balance for account " + accountNumber + ": " + account.getBalance());
            } catch (InsufficientFundsException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Account not found: " + accountNumber);
        }
    }
}

// Main class to run the banking system
public class BankingSystem {
    public static void main(String[] args) {
        Bank bank = new Bank();
        Scanner scanner = new Scanner(System.in);

        // Create accounts
        bank.createAccount("12345", "Alice");
        bank.createAccount("67890", "Bob");

        // Start transactions
        TransactionThread t1 = new TransactionThread(bank, "12345", 500, "deposit");
        TransactionThread t2 = new TransactionThread(bank, "67890", 300, "deposit");
        TransactionThread t3 = new TransactionThread(bank, "12345", 200, "withdraw");
        TransactionThread t4 = new TransactionThread(bank, "67890", 400, "withdraw"); // This should fail

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        // Wait for all threads to finish
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            System.out.println("Transaction interrupted.");
        }

        scanner.close();
    }
}
