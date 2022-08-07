import java.util.Scanner;

public class ATM {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Bank theBank = new Bank("Ваш Банк");

        User aUser = theBank.addUser("John", "Doe", "1234");

        Account newAccount = new Account("Checking", aUser, theBank);

        aUser.addAccount(newAccount);
        theBank.addAccount(newAccount);

        User curUser;
        while (true){
            curUser = ATM.mainMenuPrompt(theBank, sc);

            ATM.printUserMenu(curUser, sc);
        }
    }
    public static User mainMenuPrompt(Bank theBank, Scanner sc){
        String userID;
        String pin;
        User authUser;

        do{
            System.out.printf("\n\n Добро пожаловать в %s\n\n", theBank.getName());
            System.out.print("Введите ID пользователя: ");
            userID = sc.nextLine();
            System.out.print("Введите пароль: ");
            pin = sc.nextLine();

            authUser = theBank.userLogin(userID, pin);

            if(authUser == null){
                System.out.println("Неправильно введен логин и/или пароль");
            }

        }while(authUser == null);

        return authUser;
    }

    public static void printUserMenu(User theUser, Scanner sc){
        theUser.printAccountsSummary();

        int choice;

        do{
           System.out.printf("Добро пожаловать %s, что вы хотите сделать?\n", theUser.getFirstName());
           System.out.println(" 1) Посмотреть историю переводов");
           System.out.println(" 2) Вывести средства");
           System.out.println(" 3) Внести средства");
           System.out.println(" 4) Перевести средства");
           System.out.println(" 5) Выход");
           System.out.println();
           System.out.print("Сделайте выбор: ");
           choice = sc.nextInt();

           if(choice < 1 || choice > 5){
               System.out.println("Такого действия нет! Попробуйте еще раз.");
           }
        }while (choice < 1 || choice > 5);

        switch (choice){
            case 1:
                ATM.showTransHistory(theUser, sc);
                break;
            case 2:
                ATM.withdrawFunds(theUser, sc);
                break;
            case 3:
                ATM.depositFunds(theUser, sc);
                break;
            case 4:
                ATM.transferFunds(theUser, sc);
                break;
            case 5:
                sc.nextLine();
                break;
        }

        if(choice != 5){
            ATM.printUserMenu(theUser, sc);
        }
    }

    public static void showTransHistory(User theUser, Scanner sc){
        int theAcct;

        do{
            System.out.printf("Введите номер аккаунта (1-%d), историю которого вы хотели бы посмотреть",
                    theUser.numAccounts());

            theAcct = sc.nextInt() - 1;

            if(theAcct < 0 || theAcct >= theUser.numAccounts()){
                System.out.println("Неверный номер аккаунта.");
            }
        }while (theAcct < 0 || theAcct >= theUser.numAccounts());

        theUser.printAcctTransHistory(theAcct);
    }
    public static void transferFunds(User theUser, Scanner sc){
        int fromAcct;
        int toAcct;
        double amount;
        double acctBal;

        do{
            System.out.printf("Введите номер аккаунта (1-%d), с которого вы хотите перевести средства:" +
                    " ", theUser.numAccounts());
            fromAcct = sc.nextInt() - 1;

            if(fromAcct < 0 || fromAcct >= theUser.numAccounts()){
                System.out.println("Неверный номер аккаунта.");
            }

        }while(fromAcct < 0 || fromAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBal(fromAcct);

        do{
            System.out.printf("Введите номер аккаунта (1-%d), на который вы хотите перевести средства:" +
                    " ", theUser.numAccounts());
            toAcct = sc.nextInt() - 1;

            if(toAcct < 0 || toAcct >= theUser.numAccounts()){
                System.out.println("Неверный номер аккаунта.");
            }
        }while(toAcct < 0 || toAcct >= theUser.numAccounts());

        do{
            System.out.printf("Введите сумму для перевода: (макс. $%.02f): $", acctBal);
            amount = sc.nextDouble();

            if(amount < 0){
                System.out.println("Сумма должна быть больше 0. ");
            }else if(amount > acctBal){
                System.out.println("Сумма не должна превышать доступный баланс");
            }
        }while (amount < 0 || amount > acctBal);

        theUser.addAcctTransactoin(fromAcct, -1*amount, String.format("Перевод на аккаунт %s",
                theUser.getAcctUUID(toAcct)));

        theUser.addAcctTransactoin(toAcct, amount, String.format("Перевод с аккаунта %s",
                theUser.getAcctUUID(fromAcct)));
    }

    public static void withdrawFunds(User theUser, Scanner sc){
        int fromAcct;
        double amount;
        double acctBal;
        String memo;

        do{
            System.out.printf("Введите номер аккаунта (1-%d), с которого вы хотите вывести средства:" +
                    " ", theUser.numAccounts());
            fromAcct = sc.nextInt() - 1;

            if(fromAcct < 0 || fromAcct >= theUser.numAccounts()){
                System.out.println("Неверный номер аккаунта.");
            }

        }while(fromAcct < 0 || fromAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBal(fromAcct);

        do{
            System.out.printf("Введите сумму для вывода: (макс. $%.02f): $", acctBal);
            amount = sc.nextDouble();

            if(amount < 0){
                System.out.println("Сумма должна быть больше 0. ");
            }else if(amount > acctBal){
                System.out.println("Сумма не должна превышать доступный баланс");
            }
        }while (amount < 0 || amount > acctBal);

        sc.nextLine();

        System.out.println("Обозначьте трату: ");
        memo = sc.nextLine();

        theUser.addAcctTransactoin(fromAcct, -1*amount, memo);
    }

    public static void depositFunds(User theUser, Scanner sc) {
        int toAcct;
        double amount;
        double acctBal;
        String memo;

        do {
            System.out.printf("Введите номер аккаунта (1-%d), на который вы хотите зачислить средства:" +
                    " ", theUser.numAccounts());
            toAcct = sc.nextInt() - 1;

            if (toAcct < 0 || toAcct >= theUser.numAccounts()) {
                System.out.println("Неверный номер аккаунта.");
            }

        } while (toAcct < 0 || toAcct >= theUser.numAccounts());
        acctBal = theUser.getAcctBal(toAcct);

        do {
            System.out.println("Введите сумму: ");
            amount = sc.nextDouble();

            if (amount < 0) {
                System.out.println("Сумма должна быть больше 0. ");

            }
        } while (amount < 0) ;

            sc.nextLine();

            System.out.println("Обозначьте взнос: ");
            memo = sc.nextLine();

            theUser.addAcctTransactoin(toAcct, amount, memo);

    }
}
