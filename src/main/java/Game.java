import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;

    private boolean isRunning = false;
    private Thread thread;
    private Handler handler;
    private Camera camera;
    private final ProceduralLevelGenerator levelGenerator = new ProceduralLevelGenerator();
    private GameState gameState = GameState.Menu;

    private final Rectangle startButtonBounds = new Rectangle(420, 260, 160, 60);
    private final Rectangle quitButtonBounds = new Rectangle(420, 350, 160, 60);
    private final Rectangle restartButtonBounds = new Rectangle(270, 390, 220, 90);
    private final Rectangle menuButtonBounds = new Rectangle(510, 390, 220, 90);

    private final Path highScoreFilePath = Path.of("highscore.txt");
    private long roundStartTimeMs;
    private int roundTimeSeconds;
    private int roundScore;
    private int roundKills;
    private int highScore;
    private boolean newHighScore;

    public Game() {
        new Window(1000, 563, "Shooter", this);
        start();

        handler = new Handler();
        camera = new Camera(0, 0);
        this.addKeyListener(new Input(handler, this));
        this.addMouseListener(new MouseInput(handler, camera, this));

        highScore = loadHighScore();

    }

    private void start() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
            }
        }
        stop();
    }

    public void tick() {
        if (gameState != GameState.Game) {
            return;
        }

        for (GameObject gameObject : handler.object) {
            if (gameObject.getId() == ID.Player) {
                camera.tick(gameObject);
                break;
            }
        }

        handler.tick();

        GameObject playerObject = handler.getPlayer();
        if (playerObject instanceof Player player && player.getHealth() <= 0) {
            finalizeRound();
            gameState = GameState.GameOver;
            resetMovementFlags();
        }
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        g.setColor(Color.white);
        g.fillRect(0, 0, 1000, 563);

        if (gameState == GameState.Game) {
            g2d.translate(-camera.getX(), -camera.getY());
            handler.render(g);
            g2d.translate(camera.getX(), camera.getY());
            renderHealthBar(g);
        } else if (gameState == GameState.GameOver) {
            renderGameOverScreen(g2d);
        } else {
            renderStartMenu(g2d);
        }

        g.dispose();
        bs.show();
    }

    private void renderStartMenu(Graphics2D g) {
        g.setColor(new Color(225, 225, 225));
        g.fillRect(8, 8, 984, 547);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(5));
        g.drawRect(6, 6, 970, 514);

        g.setFont(new Font("Arial", Font.PLAIN, 72));
        FontMetrics titleMetrics = g.getFontMetrics();
        String title = "Top-Down Shooter";
        int titleX = (1000 - titleMetrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, 200);

        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString("High Score: " + highScore, 390, 250);

        drawMenuButton(g, startButtonBounds, "Start");
        drawMenuButton(g, quitButtonBounds, "Quit");
    }

    private void renderGameOverScreen(Graphics2D g) {
        g.setColor(new Color(225, 225, 225));
        g.fillRect(8, 8, 984, 547);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(5));
        g.drawRect(6, 6, 970, 514);

        g.setFont(new Font("Arial", Font.PLAIN, 72));
        FontMetrics titleMetrics = g.getFontMetrics();
        String title = "GAME OVER";
        int titleX = (1000 - titleMetrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, 240);

        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString("Round Time: " + roundTimeSeconds + "s", 345, 290);
        g.drawString("Enemies Killed: " + roundKills, 345, 325);
        g.drawString("Score: " + roundScore, 345, 360);

        if (newHighScore) {
            g.setColor(new Color(0, 130, 0));
            g.drawString("New High Score!", 500, 360);
            g.setColor(Color.BLACK);
        } else {
            g.drawString("High Score: " + highScore, 500, 360);
        }

        drawMenuButton(g, restartButtonBounds, "Restart");
        drawMenuButton(g, menuButtonBounds, "Menu");
    }

    private void drawMenuButton(Graphics2D g, Rectangle bounds, String label) {
        g.setColor(new Color(235, 235, 235));
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(5));
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        FontMetrics metrics = g.getFontMetrics();
        int textX = bounds.x + (bounds.width - metrics.stringWidth(label)) / 2;
        int textY = bounds.y + ((bounds.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(label, textX, textY);
    }

    public void handleMenuClick(int mouseX, int mouseY) {
        if (gameState == GameState.Menu) {
            if (startButtonBounds.contains(mouseX, mouseY)) {
                startGame();
                return;
            }

            if (quitButtonBounds.contains(mouseX, mouseY)) {
                System.exit(0);
            }
            return;
        }

        if (gameState == GameState.GameOver) {
            if (restartButtonBounds.contains(mouseX, mouseY)) {
                startGame();
                return;
            }

            if (menuButtonBounds.contains(mouseX, mouseY)) {
                returnToMenu();
            }
        }
    }

    private void startGame() {
        if (gameState == GameState.Game) {
            return;
        }

        handler.object.clear();
        handler.resetRoundStats();
        roundStartTimeMs = System.currentTimeMillis();
        roundTimeSeconds = 0;
        roundScore = 0;
        roundKills = 0;
        newHighScore = false;
        resetMovementFlags();
        camera.setX(0);
        camera.setY(0);
        loadGeneratedLevel();
        gameState = GameState.Game;
    }

    private void returnToMenu() {
        handler.object.clear();
        handler.resetRoundStats();
        roundStartTimeMs = System.currentTimeMillis();
        roundTimeSeconds = 0;
        roundScore = 0;
        roundKills = 0;
        newHighScore = false;
        resetMovementFlags();
        camera.setX(0);
        camera.setY(0);
        gameState = GameState.Menu;
    }

    private void resetMovementFlags() {
        handler.setUp(false);
        handler.setDown(false);
        handler.setLeft(false);
        handler.setRight(false);
    }

    public boolean isGameRunning() {
        return gameState == GameState.Game;
    }

    private enum GameState {
        Menu,
        Game,
        GameOver
    }

    private void renderHealthBar(Graphics g) {
        GameObject gameObject = handler.getPlayer();
        if (!(gameObject instanceof Player player)) {
            return;
        }

        int barWidth = 220;
        int barHeight = 24;
        int x = 20;
        int y = 20;

        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, barWidth, barHeight);

        float healthRatio = (float) player.getHealth() / player.getMaxHealth();
        int currentWidth = (int) (barWidth * healthRatio);

        g.setColor(new Color(30, 180, 30));
        g.fillRect(x, y, currentWidth, barHeight);

        g.setColor(Color.BLACK);
        g.drawRect(x, y, barWidth, barHeight);
        g.drawString("Health: " + player.getHealth() + "/" + player.getMaxHealth(), x + 8, y + 16);
    }

    private void loadGeneratedLevel() {
        ProceduralLevelGenerator.GeneratedLevel generatedLevel = levelGenerator.generate();
        boolean[][] floorTiles = generatedLevel.getFloorTiles();
        int tileSize = generatedLevel.getTileSize();

        for (int x = 0; x < generatedLevel.getWidthTiles(); x++) {
            for (int y = 0; y < generatedLevel.getHeightTiles(); y++) {
                if (!floorTiles[x][y]) {
                    handler.addObject(new Block(x * tileSize, y * tileSize, ID.Block));
                }
            }
        }

        int playerX = generatedLevel.getPlayerSpawnTile().x * tileSize;
        int playerY = generatedLevel.getPlayerSpawnTile().y * tileSize;
        handler.addObject(new Player(playerX, playerY, ID.Player, handler));

        for (Point enemySpawnTile : generatedLevel.getEnemySpawnTiles()) {
            handler.addObject(new Enemy(enemySpawnTile.x * tileSize, enemySpawnTile.y * tileSize, ID.Enemy, handler));
        }
    }

    private void finalizeRound() {
        roundKills = handler.getEnemiesKilled();
        roundTimeSeconds = (int) ((System.currentTimeMillis() - roundStartTimeMs) / 1000L);
        roundScore = calculateRoundScore(roundTimeSeconds, roundKills);

        if (roundScore > highScore) {
            highScore = roundScore;
            newHighScore = true;
            saveHighScore(highScore);
        } else {
            newHighScore = false;
        }
    }

    private int calculateRoundScore(int survivedSeconds, int enemiesKilled) {
        return survivedSeconds * 10 + enemiesKilled * 100;
    }

    private int loadHighScore() {
        if (!Files.exists(highScoreFilePath)) {
            return 0;
        }

        try {
            String value = Files.readString(highScoreFilePath).trim();
            if (value.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(value);
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }

    private void saveHighScore(int score) {
        try {
            Files.writeString(highScoreFilePath, String.valueOf(score));
        } catch (IOException ignored) {
        }
    }

    public static void main(String args[]) {
        new Game();
    }
}
