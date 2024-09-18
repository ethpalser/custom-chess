# custom-chess
The classic game of Chess built with (limited but expanding) support for piece and board customization.

This is the core library of custom chess responsible for the game's engine. Within are all resources to create, play and resume a game of custom chess.
This project has been divided into mutliple repositories for different avenues of playing:
* [custom-chess-cli](https://github.com/ethpalser/custom-chess-cli)
* [custom-chess-api](https://github.com/ethpalser/custom-chess-api)
* custom-chess-web (Coming Soon)

## Purpose
Custom Chess has been developed to provide users the ability to create custom pieces and boards to create new experiences for Chess, or even invent a new game built upon this engine. As configurations for customization expand it will be possible to create any type of piece a user can image. The goal is to give people the power to create a 2-dimensional grid-based game with a built-in computer opponent to play against. This has been created as a library to allow integration into different interfaces, such as a CLI for single-player experiences and an API for global multi-player matchmaking.

### Custom Chess CLI
Custom Chess CLI runs on its bespoke "command line inteface" built upon [Game CLI](https://github.com/ethpalser/game-cli). Created in Java, this allows execution on any platform as it runs in the Java Virtual Machine (JVM). This exists for players to play a text-based version of Custom Chess on their machine against a bot opponent, which works as a playground for testing new pieces and boards.

Current Version:
- Only supports classic Chess
- Limited save and load capabilities
- Creating custom pieces and boards is work-in-progress
- Piece inspection is work-in-progress
- Menu help is work-in-progress

### Custom Chess API
Custom Chess API is the backend of the Custom Chess website. This allows authenticated and authorized users to create games using custom boards and pieces, and matchmake against other users. Everyone will be able to read existing games and user statistics.

Current Version (Unavailable):
- Endpoints, user detials and database implemented
- Hosting service on cloud machine in-progress
- Plugging-in Custom Chess for each endpoint is in-progress
- Creating and searching custom pieces coming soon
- Websockets is under investigation
- Caching active games for faster fetch-update-publish cycles is under investigation

### Custom Chess Web
Custom Chess Web is the frontend interface of the Custom Chess website. This website provides a user-friendly experience interfacing with Custom Chess' API to create games, pieces and matchmake. Additionally, users can play local games against another player (on the same machine) or bot.

Current Version (Unavailable):
- Website using websockets and https to be implemented
- Saving user access token / refresh token to be implemented
- Local games using WASM for Java is under investigation

## Contributing

### Clone the repo
```
gh clone https://github.com/ethpalser/custom-chess
cd custom-chess
```

### Build the project
Using Gradle:
```
./gradlew build
```

### Run tests
```
gradle test
```

### Add local jar to project
Example:
In "custom-chess-cli", copy the jar into its libs folder
```
cp ./build/libs/customizable-chess.jar ../custom-chess-cli/libs
```
In "custom-chess-cli" build.gradle, add the local dependency
```
dependencies {
    implementation files('libs/customizable-chess.jar')
}
```

### Create a pull request
If you'd like to contribute, please fork the repository and open a pull request to the `main` branch.
