package lt.viko.eif;

import lt.viko.eif.algorithm.AesAlgorithm;
import lt.viko.eif.enc.ShaEncryptor;
import lt.viko.eif.entity.User;
import lt.viko.eif.entity.UserPassword;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AesAlgorithm aes = new AesAlgorithm();

    public static void main(String[] args) {
        mainMenu();
    }

    public static void mainMenu() {
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            register();
        } else if (choice.equals("2")) {
            login();
        } else if (choice.equals("3")) {
            System.exit(0);
        } else {
            System.out.println("Error!");
            mainMenu();
        }
    }

    public static void register() {
        System.out.println("Write the username: ");
        String username = scanner.nextLine();
        System.out.println("Write the password (the title of the first password will be 'first'): ");
        String password = scanner.nextLine();

        try {
            File file = new File("src/main/resources/users/" + username.toLowerCase() + ".csv");
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());

                User user = new User(username, new UserPassword("first", password));
                writeFirstData(file, user);
                mainMenu();
            } else {
                System.out.println("User is already exists.");
                System.out.println();
                mainMenu();
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void userMenu(String username) {
        System.out.println("1. Search password by title.");
        System.out.println("2. Save new password.");
        System.out.println("3. Update password by title.");
        System.out.println("4. Delete password by title.");
        System.out.println("5. Log out");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.println("Enter the title: ");
            String title = scanner.nextLine();
            searchPasswordByTitle(username, title);
        } else if (choice.equals("2")) {
            System.out.println("Enter the new title: ");
            String title = scanner.nextLine();
            System.out.println("Enter the new password: ");
            String password = scanner.nextLine();
            saveNewPassword(username, title, password);
        } else if (choice.equals("3")) {
            System.out.println("Enter the title: ");
            String title = scanner.nextLine();
            System.out.println("Enter the updated password: ");
            String password = scanner.nextLine();
            updatePassword(username, title, password);
        } else if (choice.equals("4")) {
            System.out.println("Enter the title: ");
            String title = scanner.nextLine();
            deletePasswordByTitle(username, title);
        } else if (choice.equals("5")) {
            aes.encryptFile("src/main/resources/users/" + username + ".csv");
            mainMenu();
        } else {
            System.out.println("Error!");
            userMenu(username);
        }
    }

    private static void deletePasswordByTitle(String username, String title) {
        try {
            File file = new File("src/main/resources/users/" + username + ".csv");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            boolean found = false;

            String line;
            sb.append(br.readLine() + "\n");
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(title)) {
                    found = true;
                    continue;
                }
                sb.append(line);
                sb.append("\n");
            }

            if (found) {
                writeNewDataToFile(file, sb.toString());
                System.out.println("Password has been deleted.");
            } else {
                System.out.println("Title is not found.");
            }
            userMenu(username);
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void updatePassword(String username, String title, String newPassword) {
        try {
            File file = new File("src/main/resources/users/" + username + ".csv");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            boolean found = false;

            String line;
            sb.append(br.readLine() + "\n");
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(title)) {
                    values[1] = ShaEncryptor.toSHA1(newPassword);
                    String newPasswordLine = values[0] + "," + values[1];
                    sb.append(newPasswordLine);
                    found = true;
                    continue;
                }
                sb.append(line);
                sb.append("\n");
            }

            if (found) {
                writeNewDataToFile(file, sb.toString());
                System.out.println("Password has been updated.");
            } else {
                System.out.println("Title is not found.");
            }
            userMenu(username);
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void saveNewPassword(String username, String title, String password) {
        File file = new File("src/main/resources/users/" + username.toLowerCase() + ".csv");
        User user = new User(username, new UserPassword(title, password));
        writeData(file, user);
        userMenu(username);
    }

    private static void searchPasswordByTitle(String username, String title) {
        try {
            File file = new File("src/main/resources/users/" + username + ".csv");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(title)) {
                    StringBuilder stringBuilder = new StringBuilder(values[1].length());
                    for (int i = 0; i < values[1].length(); i++) {
                        stringBuilder.append("*");
                    }
                    System.out.println("Found: " + stringBuilder);
                    userMenu(username);
                }
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Not found.");
        userMenu(username);
    }

    private static void writeFirstData(File file, User user) {
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            UserPassword userPassword = user.getPassword();

            String password = ShaEncryptor.toSHA1(userPassword.getPassword());
            out.println("password_title,password");
            out.println(userPassword.getTitle() + "," + password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        aes.encryptFile(String.valueOf(file));
    }

    private static void writeData(File file, User user) {
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            UserPassword userPassword = user.getPassword();
            String password = ShaEncryptor.toSHA1(userPassword.getPassword());
            out.println(userPassword.getTitle() + "," + password);
            System.out.println("New password has been created.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeNewDataToFile(File file, String text) {
        try (FileWriter fw = new FileWriter(file, false);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            String[] data = text.split("\n");
            Arrays.stream(data).forEach(out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void login() {
        System.out.println("Write the username: ");
        String username = scanner.nextLine();
        System.out.println("Write the password: ");
        String password = scanner.nextLine();

        File dir = new File("src/main/resources/users");

        File[] listOfFiles = dir.listFiles();

        for (File file : listOfFiles) {
            if (file.getName().equalsIgnoreCase(username + ".csv")) {
                String decryptedFileText = aes.decryptFile(String.valueOf(file));
                boolean isCorrectPassword = scanPasswords(decryptedFileText, ShaEncryptor.toSHA1(password));
                if (isCorrectPassword) {
                    aes.writeData(String.valueOf(file), decryptedFileText);
                    userMenu(username);
                }
                break;
            }
        }

        System.out.println("The username or password is incorrect.");
        mainMenu();
    }

    private static boolean scanPasswords(String text, String correctPassword) {
        String[] data = text.split("\n");
        for (String datum : data) {
            String[] password = datum.split(",");
            if (password[1].equals(correctPassword)) {
                return true;
            }
        }
        return false;
    }

}
