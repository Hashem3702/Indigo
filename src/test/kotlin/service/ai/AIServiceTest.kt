package service.ai

import entity.*
import service.*
import kotlin.test.*

/**
 * The `AIServiceTest` class contains unit tests for the AI service functionalities.
 * It tests the behavior of both random and smart AI moves, validates possible positions on the game board,
 * sets and retrieves the current game state, checks all possible rotations of a tile, and examines possible next states.
 *
 * @property gameService An instance of the GameService used for setting up and managing game-related operations.
 * @property playerService An instance of the PlayerService responsible for player-related functionalities.
 * @property rootService An instance of the RootService managing the root-level game data and services.
 * @property aiServices An instance of the AIServices providing AI-related functionalities.
 */
class AIServiceTest {

    private lateinit var gameService: GameService
    private lateinit var playerService: PlayerService
    private lateinit var rootService: RootService
    private lateinit var aiServices: AIServices

    /**
     * Sets up the necessary services and initializes them before each test case.
     */
    @BeforeTest
    fun setUp() {
        rootService = RootService()
        playerService = PlayerService(rootService)
        gameService = GameService(rootService)
        aiServices = AIServices(rootService)
    }

    /**
     * Tests the behavior of the random AI and the chosen move.
     * Expects an IllegalStateException to be thrown.
     */
    @Test
    fun testRandomAIMoves() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.BLUE, heldTile = RouteTile(TileType.TILE1), isAI = true, smartAI = false),
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val game = rootService.currentGame
        checkNotNull(game)

        var move = aiServices.playRandomly()

        assertTrue { rootService.playerService.checkPlacement(move.first) }

        rootService.playerService.placeTile(AxialPos(1, -1))

        rootService.playerService.placeTile(AxialPos(-1, 4))

        move = aiServices.playRandomly()

        assertTrue { rootService.playerService.checkPlacement(move.first) }
    }

    /**
     * Tests the behavior of the smart AI and the chosen move.
     * Expects an IllegalStateException to be thrown.
     */
    @Test
    fun testSmartAIMoves() {
        assertFails { playerService.placeTile(AxialPos(1, -3)) }

        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P3", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = true, smartAI = true)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val game = rootService.currentGame
        checkNotNull(game)
    }

    /**
     * Test the retrieval of valid positions on the game board.
     * It checks if the valid positions are calculated correctly, considering existing tiles on the board.
     */
    @Test
    fun testValidPositions() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val game = rootService.currentGame
        checkNotNull(game)

        val axialPosCoordinates = mutableListOf(
            //-4
            AxialPos(-4,1),AxialPos(-4,2),AxialPos(-4,3),
            //-3
            AxialPos(-3,-1),AxialPos(-3,0),AxialPos(-3,1),
            AxialPos(-3,2),AxialPos(-3,3),AxialPos(-3,4),
            //-2
            AxialPos(-2,-2),AxialPos(-2,-1),AxialPos(-2,0),AxialPos(-2,1),
            AxialPos(-2,2),AxialPos(-2,3),AxialPos(-2,4),
            //-1
            AxialPos(-1,-3),AxialPos(-1,-2),AxialPos(-1,-1),AxialPos(-1,0),
            AxialPos(-1,1),AxialPos(-1,2),AxialPos(-1,3),AxialPos(-1,4),
            //0
            AxialPos(0,-3),AxialPos(0,-2),AxialPos(0,-1),
            AxialPos(0,1),AxialPos(0,2),AxialPos(0,3),
            //1
            AxialPos(1,-4),AxialPos(1,-3),AxialPos(1,-2),AxialPos(1,-1),
            AxialPos(1,0),AxialPos(1,1),AxialPos(1,2),AxialPos(1,3),
            //2
            AxialPos(2,-4),AxialPos(2,-3),AxialPos(2,-2),AxialPos(2,-1),
            AxialPos(2,0),AxialPos(2,1),AxialPos(2,2),
            //3
            AxialPos(3,-4),AxialPos(3,-3),AxialPos(3,-2),AxialPos(3,-1),
            AxialPos(3,0),AxialPos(3,1),
            //4
            AxialPos(4,-3),AxialPos(4,-2),AxialPos(4,-1)
        )

        //the valid coordinates are all the coordinates of the board minus the TreasureTiles and GateWayTiles.
        val coordinates = aiServices.findAllValidPositions()

        assertEquals(axialPosCoordinates, coordinates)

        //check if the placedTile coordinates got removed.
        rootService.playerService.placeTile(AxialPos(1, -1))

        axialPosCoordinates.remove(AxialPos(1,-1))
        val newCoordinates = aiServices.findAllValidPositions()

        println(aiServices.findAllValidPositions().size)
        assertEquals(axialPosCoordinates, newCoordinates)
    }

    /**
     * Test the setting and getting of the current game state in the AI service.
     * It ensures that the state can be successfully set and retrieved.
     */

    @Test
    fun testCurrentState() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )
        val newBoard: MutableMap<AxialPos, Tile> = mutableMapOf()
        val newDrawStack: MutableList<RouteTile> = mutableListOf(RouteTile(TileType.TILE0))
        val newPlayers: List<Player> =
            listOf(Player("one", Color.BLUE, heldTile = RouteTile(TileType.TILE4), isAI = false, smartAI = false))
        val newGems: MutableList<Gem> =
            mutableListOf(Gem.AMBER, Gem.EMERALD, Gem.EMERALD)

        val newGameState = GameState(newBoard, newDrawStack, newPlayers, newGems)

        aiServices.setCurrentState(newGameState)
        val game = rootService.currentGame
        checkNotNull(game)

        val settedGameSate = GameState(
            game.currentBoard,
            game.currentDrawStack,
            game.currentPlayers,
            game.currentGems)

        //assert setting the Game state
        assertEquals(newGameState, settedGameSate)

        //assert getting the Game state
        val gottenState = aiServices.getCurrentState()
        assertEquals(newGameState, gottenState)
    }

    /**
     * Test the retrieval of all possible rotations for a given tile.
     * It ensures that the AI service correctly calculates the possible rotations of a tile.
     */
    @Test
    fun testAllTilePossibleRotations() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val game = rootService.currentGame
        checkNotNull(game)
        val tile = game.playerAtTurn.heldTile
        checkNotNull(tile)
        val possibleRotations = aiServices.getAllTilePossibleRotations(tile)
        assertEquals(6, possibleRotations.size)

        assertEquals(0, possibleRotations[0].rotation) //0->5
        assertEquals(1, possibleRotations[1].rotation) //1->5
        assertEquals(2, possibleRotations[2].rotation) //2->5
        assertEquals(3, possibleRotations[3].rotation) //3->5
        assertEquals(4, possibleRotations[4].rotation) //4->5
        assertEquals(5, possibleRotations[5].rotation) //5->5

    }

    /**
     * Tests obtaining all possible next states.
     */
    @Test
    fun testGetAllPossibleNextStates() {
        gameService.startGame(
            players = listOf(
                Player("P1", Color.RED, heldTile = RouteTile(TileType.TILE0), isAI = false, smartAI = false),
                Player("P2", Color.PURPLE, heldTile = RouteTile(TileType.TILE2), isAI = false, smartAI = false)
            ),
            aiSpeed = 10,
            sharedGates = false
        )

        val size = aiServices.getAllPossibleNextStates().size

        println(size)
    }
}