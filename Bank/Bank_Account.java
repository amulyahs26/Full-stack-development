package Bank;

import java.io.*;
import java.util.*;

abstract class Account {
    protected int accountNumber;
    protected String name;
    protected String pin;
    protected double amount;

    public Account(int accountNumber, String name, double amount, String pin) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.amount = amount;
        this.pin = pin;
    }

    public abstract void deposit(double amt);

    public abstract void withdraw(double amt);

    public abstract String getAccountType();

    public void display() {
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Name: " + name);
        System.out.printf("Balance: Rs. %.2f\n", amount);
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public String getPin() {
        return pin;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amt) {
        this.amount = amt;
    }

    public void setPin(String newPin) {
        this.pin = newPin;
    }
}

final class SBAccount extends Account {
    private static final double INTEREST_RATE = 0.04;
    private static final double MIN_BALANCE = 1000.0;

    public SBAccount(int accountNumber, String name, double amount, String pin) {
        super(accountNumber, name, amount, pin);
    }

    @Override
    public void deposit(double amt) {
        if (amt > 0) {
            double interest = amt * INTEREST_RATE;
            amount += amt + interest;
            System.out.printf("Deposited Rs. %.2f + Rs. %.2f interest. Total credited: Rs.%.2f\n", amt, interest,
                    amt + interest);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    @Override
    public void withdraw(double amt) {
        if (amt > 0 && (amount - amt) >= MIN_BALANCE) {
            amount -= amt;
            System.out.printf("Withdrawn Rs. %.2f\n", amt);
        } else {
            System.out.println("Cannot withdraw. Maintain minimum balance.");
        }
    }

    @Override
    public String getAccountType() {
        return "SB";
    }
}

final class CurrentAccount extends Account {
    private static final double MIN_BALANCE = 5000.0;

    public CurrentAccount(int accountNumber, String name, double amount, String pin) {
        super(accountNumber, name, amount, pin);
    }

    @Override
    public void deposit(double amt) {
        if (amt > 0) {
            amount += amt;
            System.out.printf("Deposited Rs. %.2f\n", amt);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    @Override
    public void withdraw(double amt) {
        if (amt > 0 && (amount - amt) >= MIN_BALANCE) {
            amount -= amt;
            System.out.printf("Withdrawn Rs. %.2f\n", amt);
        } else {
            System.out.println("Cannot withdraw. Maintain minimum balance.");
        }
    }

    @Override
    public String getAccountType() {
        return "Current";
    }
}

public class Bank_Account {
    private static int nextAccountNumber = 1001;
    private static final String FILE_NAME = "accounts.txt";
    private static Map<Integer, Account> accountMap = new HashMap<>();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadAccountsFromFile();
        updateNextAccountNumber();

        System.out.println("=== Welcome to Amulya HARMAN Bank ===");

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. New Customer Registration");
            System.out.println("2. Existing Customer Login");
            System.out.println("3. Search Account by Name");
            System.out.println("4. Search by Account Number");
            System.out.println("5. Sort Accounts by Balance");
            System.out.println("6. Sort Accounts by Name");
            System.out.println("7. Show Top N Richest Customers");
            System.out.println("8. Exit");

            System.out.print("Choose an option (1-8): ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    Account newAcc = registerNewAccount();
                    if (newAcc != null)
                        handleBankOperations(newAcc);
                    break;
                case "2":
                    Account existingAcc = loginExistingAccount();
                    if (existingAcc != null)
                        handleBankOperations(existingAcc);
                    break;
                case "3":
                    searchAccountByName();
                    break;
                case "4":
                    searchByAccountNumber();
                    break;
                case "5":
                    sortAccountsByBalance();
                    break;
                case "6":
                    sortAccountsByName();
                    break;
                case "7":
                    showTopNCustomers();
                    break;
                case "8":
                    System.out.println("Thank you for banking with us!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void loadAccountsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length != 5)
                    continue;

                int accNo = Integer.parseInt(parts[0]);
                String name = parts[1];
                String pin = parts[2];
                String type = parts[3];
                double amount = Double.parseDouble(parts[4]);

                Account acc = (type.equals("SB")) ? new SBAccount(accNo, name, amount, pin)
                        : new CurrentAccount(accNo, name, amount, pin);

                accountMap.put(accNo, acc);
            }
        } catch (IOException e) {
            // First time - no file yet
        }
    }

    private static void saveAccountsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Account acc : accountMap.values()) {
                String line = acc.getAccountNumber() + "|" + acc.getName() + "|" + acc.getPin() + "|" +
                        acc.getAccountType() + "|" + acc.getAmount();
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts.");
        }
    }

    private static void updateNextAccountNumber() {
        for (int accNum : accountMap.keySet()) {
            if (accNum >= nextAccountNumber) {
                nextAccountNumber = accNum + 1;
            }
        }
    }

    private static Account registerNewAccount() {
        int accNo = nextAccountNumber++;
        System.out.println("\nYour Account Number is: " + accNo);

        String name;
        while (true) {
            System.out.print("Enter Full Name: ");
            name = sc.nextLine();
            if (name.matches("[a-zA-Z ]+"))
                break;
            System.out.println("Invalid name. Only letters and spaces allowed.");
        }

        String pin;
        while (true) {
            System.out.print("Set 4-digit PIN: ");
            pin = sc.nextLine();
            if (pin.matches("\\d{4}"))
                break;
            System.out.println("Invalid PIN.");
        }

        System.out.println("1. Savings Bank (SB)");
        System.out.println("2. Current Account");
        System.out.print("Enter choice (1 or 2): ");
        int choice = Integer.parseInt(sc.nextLine());

        double amt;
        Account acc = null;

        if (choice == 1) {
            while (true) {
                System.out.print("Initial Amount (Min Rs.1000): ");
                amt = Double.parseDouble(sc.nextLine());
                if (amt >= 1000) {
                    acc = new SBAccount(accNo, name, amt, pin);
                    break;
                }
                System.out.println("Minimum Rs.1000 required.");
            }
        } else if (choice == 2) {
            while (true) {
                System.out.print("Initial Amount (Min Rs.5000): ");
                amt = Double.parseDouble(sc.nextLine());
                if (amt >= 5000) {
                    acc = new CurrentAccount(accNo, name, amt, pin);
                    break;
                }
                System.out.println("Minimum Rs.5000 required.");
            }
        } else {
            System.out.println("Invalid choice.");
            return null;
        }

        accountMap.put(accNo, acc);
        saveAccountsToFile();
        System.out.println("Account created successfully!");
        return acc;
    }

    private static Account loginExistingAccount() {
        System.out.print("Enter Account Number: ");
        int accNo = Integer.parseInt(sc.nextLine());

        Account acc = accountMap.get(accNo);
        if (acc == null) {
            System.out.println("Account not found.");
            return null;
        }

        System.out.print("Enter 4-digit PIN: ");
        String pin = sc.nextLine();

        if (!acc.getPin().equals(pin)) {
            System.out.println("Incorrect PIN.");
            return null;
        }

        System.out.println("Welcome back, " + acc.getName() + "!");
        return acc;
    }

    private static void handleBankOperations(Account acc) {
        while (true) {
            System.out.println("\n1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Show Balance");
            System.out.println("4. Change PIN");
            System.out.println("5. Logout");
            System.out.print("Choose (1-5): ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Amount to deposit: ");
                    double dAmt = Double.parseDouble(sc.nextLine());
                    acc.deposit(dAmt);
                    saveAccountsToFile();
                    break;
                case 2:
                    System.out.print("Amount to withdraw: ");
                    double wAmt = Double.parseDouble(sc.nextLine());
                    acc.withdraw(wAmt);
                    saveAccountsToFile();
                    break;
                case 3:
                    acc.display();
                    break;
                case 4:
                    changePin(acc);
                    break;
                case 5:
                    System.out.println("Logged out.\n");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void searchAccountByName() {
        System.out.print("Enter name to search: ");
        String searchName = sc.nextLine().toLowerCase();
        boolean found = false;

        for (Account acc : accountMap.values()) {
            if (acc.getName().toLowerCase().contains(searchName)) {
                acc.display();
                System.out.println("--------------------------");
                found = true;
            }
        }

        if (!found) {
            System.out.println("No accounts found.");
        }
    }

    private static void searchByAccountNumber() {
        List<Account> list = new ArrayList<>(accountMap.values());
        list.sort(Comparator.comparingInt(Account::getAccountNumber));

        System.out.print("Enter account number: ");
        int accNo = Integer.parseInt(sc.nextLine());

        int low = 0, high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            Account midAcc = list.get(mid);

            if (midAcc.getAccountNumber() == accNo) {
                midAcc.display();
                return;
            } else if (midAcc.getAccountNumber() < accNo) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        System.out.println("Account not found.");
    }

    private static void sortAccountsByBalance() {
        List<Account> list = new ArrayList<>(accountMap.values());
        list.sort(Comparator.comparingDouble(Account::getAmount));

        for (Account acc : list) {
            acc.display();
            System.out.println("--------------------------");
        }
    }

    private static void sortAccountsByName() {
        List<Account> list = new ArrayList<>(accountMap.values());
        list.sort(Comparator.comparing(Account::getName));

        for (Account acc : list) {
            acc.display();
            System.out.println("--------------------------");
        }
    }

    private static void showTopNCustomers() {
        System.out.print("Enter N: ");
        int n = Integer.parseInt(sc.nextLine());

        List<Account> list = new ArrayList<>(accountMap.values());
        list.sort((a, b) -> Double.compare(b.getAmount(), a.getAmount()));

        for (int i = 0; i < Math.min(n, list.size()); i++) {
            list.get(i).display();
            System.out.println("--------------------------");
        }
    }

    private static void changePin(Account acc) {
        System.out.print("Enter current PIN: ");
        String oldPin = sc.nextLine();

        if (!acc.getPin().equals(oldPin)) {
            System.out.println("Incorrect PIN.");
            return;
        }

        String newPin;
        while (true) {
            System.out.print("Enter new 4-digit PIN: ");
            newPin = sc.nextLine();
            if (newPin.matches("\\d{4}"))
                break;
            System.out.println("Invalid PIN format.");
        }

        acc.setPin(newPin);
        saveAccountsToFile();
        System.out.println("PIN changed successfully.");
    }
}
