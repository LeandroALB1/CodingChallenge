package com.example.library.controller;

import com.example.library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Component
public class LibraryController implements CommandLineRunner {

    @Autowired
    private LibraryService libraryService;

    @Override
    public void run(String... args) {
        printWelcomeBanner();

        Scanner scanner = new Scanner(System.in);
        String line;
        while (true) {
            System.out.print("Enter a command: ");
            if (!scanner.hasNextLine()) {
                break;
            }
            line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0].toLowerCase();

            try {
                switch (cmd) {
                    case "store" -> handleStore(parts);
                    case "search" -> handleSearch(parts);
                    case "delete" -> handleDelete(parts);
                    case "remove-tag" -> handleRemoveTag(parts);
                    case "list-tags" -> handleListTags(parts);
                    case "list-all" -> handleListAll();
                    case "stats" -> handleStats();
                    case "help" -> printHelp();
                    case "exit" -> {
                        System.out.println("Exiting... Goodbye!");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Error, unknown command. Type 'help' for a list of commands.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        scanner.close();
    }

    private void handleStore(String[] parts) {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Error, no ISBN and tags provided.");
        }
        String[] sp = parts[1].split("\\s+");
        String isbn = sp[0];
        List<String> tags = sp.length > 1 ? List.of(sp).subList(1, sp.length) : List.of();
        System.out.println(libraryService.store(isbn, tags));
    }

    private void handleSearch(String[] parts) {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Error, no tags provided.");
        }
        List<String> tags = List.of(parts[1].split("\\s+"));
        List<String> result = libraryService.search(tags);
        System.out.println(result.isEmpty() ? "No books found" : String.join(", ", result));
    }

    private void handleDelete(String[] parts) {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Error, no ISBN provided.");
        }
        String isbn = parts[1].trim();
        System.out.println(libraryService.delete(isbn));
    }

    private void handleRemoveTag(String[] parts) {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Error, no ISBN and tag provided.");
        }
        String[] sp = parts[1].split("\\s+");
        if (sp.length < 2) {
            throw new IllegalArgumentException("Error, no tag provided.");
        }
        String isbn = sp[0];
        String tag = sp[1];
        System.out.println(libraryService.removeTag(isbn, tag));
    }

    private void handleListTags(String[] parts) {
        if (parts.length < 2) {
            throw new IllegalArgumentException("Error, no ISBN provided.");
        }
        String isbn = parts[1].trim();
        List<String> tags = libraryService.listTags(isbn);
        System.out.println(tags.isEmpty() ? "No tags found" : String.join(", ", tags));
    }

    private void handleListAll() {
        Map<String, List<String>> all = libraryService.listAll();
        if (all.isEmpty()) {
            System.out.println("Library is empty");
            return;
        }
        all.forEach((isbn, tags) ->
                System.out.println(isbn + ": " + String.join(", ", tags))
        );
    }

    private void handleStats() {
        System.out.println(libraryService.stats());
    }

    private static void printWelcomeBanner() {
        System.out.println("Welcome to the Library Management App!");
        System.out.println("Scanner ready...");
        System.out.println("--------------------------------");
        printHelp();
    }

    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  store <isbn> <tags...>     - add or update a book's tags");
        System.out.println("  search <tags...>           - list books matching all tags");
        System.out.println("  delete <isbn>              - remove a book");
        System.out.println("  remove-tag <isbn> <tag>    - unlink a tag from a book");
        System.out.println("  list-tags <isbn>           - show tags for a book");
        System.out.println("  list-all                   - list all books and their tags");
        System.out.println("  stats                      - show library statistics");
        System.out.println("  help                       - display this help message");
        System.out.println("  exit                       - quit the app");
        System.out.println("--------------------------------");
    }
}
