import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class User {
    String username;
    String displayName;
    String state;
    List<String> friends;

    User(String username, String displayName, String state, List<String> friends) {
        this.username = username;
        this.displayName = displayName;
        this.state = state;
        this.friends = friends;
    }
}

class Post {
    String postId;
    String userId;
    String visibility;

    Post(String postId, String userId, String visibility) {
        this.postId = postId;
        this.userId = userId;
        this.visibility = visibility;
    }
}

public class SocialMediaApp {
    private static Map<String, User> users = new HashMap<>();
    private static Map<String, Post> posts = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean dataLoaded = false;

        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Load input data.");
            System.out.println("2. Check visibility.");
            System.out.println("3. Retrieve posts.");
            System.out.println("4. Search users by location.");
            System.out.println("5. Exit.");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (option) {
                case 1:
                    loadData();
                    dataLoaded = true;
                    break;
                case 2:
                    if (dataLoaded) {
                        checkVisibility(scanner);
                    } else {
                        System.out.println("Data not loaded yet. Please load data first.");
                    }
                    break;
                case 3:
                    if (dataLoaded) {
                        retrievePosts(scanner);
                    } else {
                        System.out.println("Data not loaded yet. Please load data first.");
                    }
                    break;
                case 4:
                    if (dataLoaded) {
                        searchUsersByLocation(scanner);
                    } else {
                        System.out.println("Data not loaded yet. Please load data first.");
                    }
                    break;
                case 5:
                    System.out.println("Exiting program...");
                    return;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private static void loadData() {
        users.clear();
        posts.clear();

        try {
            BufferedReader userReader = new BufferedReader(new FileReader("user-info.txt"));
            String line;
            while ((line = userReader.readLine()) != null) {
                String[] parts = line.split(";");
                String username = parts[0];
                String displayName = parts[1];
                String state = parts[2];
                List<String> friends = parseFriends(parts[3]);
                users.put(username, new User(username, displayName, state, friends));
            }
            userReader.close();

            BufferedReader postReader = new BufferedReader(new FileReader("post-info.txt"));
            while ((line = postReader.readLine()) != null) {
                String[] parts = line.split(";");
                String postId = parts[0];
                String userId = parts[1];
                String visibility = parts[2];
                posts.put(postId, new Post(postId, userId, visibility));
            }
            postReader.close();

            System.out.println("Data loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    private static List<String> parseFriends(String friendsList) {
        List<String> friends = new ArrayList<>();
        String[] parts = friendsList.substring(1, friendsList.length() - 1).split(", ");
        for (String friend : parts) {
            friends.add(friend);
        }
        return friends;
    }

    private static void checkVisibility(Scanner scanner) {
        System.out.print("Input post ID: ");
        String postId = scanner.nextLine().trim();
        System.out.print("Input username: ");
        String username = scanner.nextLine().trim();

        if (posts.containsKey(postId) && users.containsKey(username)) {
            Post post = posts.get(postId);
            User user = users.get(username);

            if (post.visibility.equals("public") || post.userId.equals(username) || user.friends.contains(post.userId)) {
                System.out.println("Access Permitted");
            } else {
                System.out.println("Access Denied");
            }
        } else {
            System.out.println("Invalid post ID or username.");
        }
    }

    private static void retrievePosts(Scanner scanner) {
        System.out.print("Input username: ");
        String username = scanner.nextLine().trim();

        if (users.containsKey(username)) {
            User user = users.get(username);
            for (Map.Entry<String, Post> entry : posts.entrySet()) {
                Post post = entry.getValue();
                if (!post.userId.equals(username) && (post.visibility.equals("public") || user.friends.contains(post.userId))) {
                    System.out.println(post.postId);
                }
            }
        } else {
            System.out.println("Invalid username.");
        }
    }

    private static void searchUsersByLocation(Scanner scanner) {
        System.out.print("Input state location: ");
        String state = scanner.nextLine().trim();

        for (User user : users.values()) {
            if (user.state.equals(state)) {
                System.out.println(user.displayName);
            }
        }
    }
}
