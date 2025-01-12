package android.security.checking;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static List<User> userList = new ArrayList<>();
    private static CryptoUtils cryptoUtils = new CryptoUtils();
    public static void addUser(User user) {
        userList.add(user);
    }

    public static List<User> getUsers() {
        return userList;
    }

    public static boolean authenticateUser(String username, String password, Context context) {
        User user = findUserByUsername(username);
        if (user != null) {
            String decryptedPassword = cryptoUtils.decryptData(user.getPassword(), context);

            return password.equals(decryptedPassword);
        }
        return false;
    }

    public static User findUserByUsername(String username) {
        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    private static User currentUser;  // Variable statique pour l'utilisateur connecté

    // Méthode pour définir l'utilisateur actuellement connecté
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    // Méthode pour obtenir l'utilisateur actuellement connecté
    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isUserAdmin() {
        User currentUser = getCurrentUser();
        return currentUser != null && "admin".equals(currentUser.getUsername()) && "admin".equals(currentUser.getPassword());
    }
}
