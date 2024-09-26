import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JFrame {

    private static final int TILE_SIZE = 20;
    private static final int GRID_SIZE = 20;

    private GamePanel gamePanel;
    private Timer timer;
    private Snake snake;
    private Point food;
    private int gameSpeed; // Variable to store game speed

    public SnakeGame(int speed) {
        this.gameSpeed = speed; // Initialize game speed
        setTitle("Snake Game");
        setSize(GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(100, 100, 100)); // Set background color

        gamePanel = new GamePanel();
        add(gamePanel);

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                snake.setDirection(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        initializeGame();

        timer = new Timer(gameSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!snake.move(food)) {
                    endGame();
                }
                if (snake.getHead().equals(food)) {
                    snake.grow();
                    generateFood();
                }
                gamePanel.repaint();
            }
        });
        timer.start();

        setVisible(true);
    }

    private void initializeGame() {
        snake = new Snake();
        generateFood();
    }

    private void generateFood() {
        Random random = new Random();
        int x = random.nextInt(GRID_SIZE);
        int y = random.nextInt(GRID_SIZE);
        food = new Point(x, y);

        while (snake.contains(food)) {
            x = random.nextInt(GRID_SIZE);
            y = random.nextInt(GRID_SIZE);
            food = new Point(x, y);
        }
    }

    private void endGame() {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Game Over!");
        initializeGame();
        timer.start();
    }

    private class GamePanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw snake body
            LinkedList<Point> body = snake.getBody();
            for (Point point : body) {
                drawSnakeSegment(g, point.x * TILE_SIZE, point.y * TILE_SIZE);
            }

            // Draw snake head
            Point head = snake.getHead();
            drawSnakeHead(g, head.x * TILE_SIZE, head.y * TILE_SIZE);

            // Draw food
            g.setColor(Color.GREEN);
            g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        private void drawSnakeSegment(Graphics g, int x, int y) {
            g.setColor(new Color(34, 139, 34)); // Green color for snake body
            g.fillOval(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
        }

        private void drawSnakeHead(Graphics g, int x, int y) {
            g.setColor(new Color(139, 69, 19)); // Brown color for snake head
            g.fillRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 15, 15);
        }
    }

    private class Snake {

        private LinkedList<Point> body;
        private int direction;

        public Snake() {
            body = new LinkedList<>();
            body.add(new Point(5, 5));
            direction = KeyEvent.VK_RIGHT;
        }

        public LinkedList<Point> getBody() {
            return body;
        }

        public Point getHead() {
            return body.getFirst();
        }

        public void setDirection(int newDirection) {
            if (newDirection == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) {
                direction = newDirection;
            } else if (newDirection == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT) {
                direction = newDirection;
            } else if (newDirection == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) {
                direction = newDirection;
            } else if (newDirection == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP) {
                direction = newDirection;
            }
        }

        public boolean move(Point food) {
            Point newHead;
            switch (direction) {
                case KeyEvent.VK_LEFT:
                    newHead = new Point(getHead().x - 1, getHead().y);
                    break;
                case KeyEvent.VK_RIGHT:
                    newHead = new Point(getHead().x + 1, getHead().y);
                    break;
                case KeyEvent.VK_UP:
                    newHead = new Point(getHead().x, getHead().y - 1);
                    break;
                case KeyEvent.VK_DOWN:
                    newHead = new Point(getHead().x, getHead().y + 1);
                    break;
                default:
                    return false;
            }

            // Check for boundary collision
            if (newHead.x < 0 || newHead.x >= GRID_SIZE || newHead.y < 0 || newHead.y >= GRID_SIZE) {
                return false; // Collision with wall
            }

            // Check for collision with itself
            if (body.contains(newHead)) {
                return false; // Collision with itself
            }

            // Move the snake
            body.addFirst(newHead);
            if (!newHead.equals(food)) {
                body.removeLast();
            }
            return true;
        }

        public void grow() {
            body.addLast(new Point(-1, -1)); // Placeholder, actual position will be set in the next move
        }

        public boolean contains(Point point) {
            return body.contains(point);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] options = {"Easy", "Medium", "Hard"};
            int choice = JOptionPane.showOptionDialog(null, "Select Difficulty",
                    "Difficulty", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            int speed;
            switch (choice) {
                case 0: // Easy
                    speed = 200; // 200 ms
                    break;
                case 1: // Medium
                    speed = 150; // 150 ms
                    break;
                case 2: // Hard
                    speed = 100; // 100 ms
                    break;
                default:
                    return; // Exit if no choice made
            }

            new SnakeGame(speed);
        });
    }
}

