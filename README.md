# Coding Challenge Lufthansa

# Library Tag Management
A simple console-based Spring Boot application that allows storing, tagging, searching, and managing books using ISBNs and tags. 
---
## Features
- Add a book with multiple tags
- Search for books by tag(s)
- Remove a tag from a book
- Delete a book
- List tags of a book
- List all books with their tags
- View library statistics
- Console-based interactive interface
---
## Domain Model
- **Book**: Identified by ISBN, contains a set of tags
- **Tag**: Identified by name
- **BookTag**: Join entity representing a many-to-many relationship between books and tags using a composite key `BookTagId`
---
## Structure
- `Book` – Entity with `isbn` as primary key
- `Tag` – Entity with `name` as primary key
- `BookTag` – Join table for `Book` and `Tag`, using composite key `BookTagId`
- `BookTagId` – Embeddable key holding `isbn` and `tagName`
- `LibraryService` – Business logic
- `LibraryController` – Console-based command-line runner
---
## Setup & Run
### Prerequisites
- Java 17+
- Maven
### Run the Application
```bash
mvn spring-boot:run
