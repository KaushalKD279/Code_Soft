import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class NumberGuessingSwingWithSound extends JFrame {
    private int numberToGuess;
    private int attemptsLeft;
    private int maxRange;
    private int score = 0;
    private int maxAttempts;

    private JLabel messageLabel;
    private JTextField guessField;
    private JButton guessButton, playAgainButton;
    private JLabel scoreLabel;
    private JProgressBar attemptsBar;

    private Clip backgroundClip; // 🎵 background music

    public NumberGuessingSwingWithSound() {
        setTitle("🎯 Number Guessing Game");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1, 10, 10));
        getContentPane().setBackground(Color.DARK_GRAY);

        // Difficulty selection
        chooseDifficulty();

        messageLabel = new JLabel("Guess a number between 1 and " + maxRange, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        messageLabel.setForeground(Color.WHITE);

        guessField = new JTextField();
        guessButton = new JButton("Guess");
        playAgainButton = new JButton("Play Again");
        scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scoreLabel.setForeground(Color.CYAN);

        attemptsBar = new JProgressBar(0, maxAttempts);
        attemptsBar.setValue(maxAttempts);
        attemptsBar.setString("Attempts left: " + attemptsLeft);
        attemptsBar.setStringPainted(true);

        add(messageLabel);
        add(guessField);
        add(guessButton);
        add(attemptsBar);
        add(scoreLabel);
        add(playAgainButton);

        playAgainButton.setVisible(false);

        // 🎮 Guess Button Logic
        guessButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (attemptsLeft > 0) {
                    try {
                        int guess = Integer.parseInt(guessField.getText());
                        attemptsLeft--;
                        attemptsBar.setValue(attemptsLeft);
                        attemptsBar.setString("Attempts left: " + attemptsLeft);

                        if (guess == numberToGuess) {
                            messageLabel.setText("✅ Correct! You guessed it!");
                            messageLabel.setForeground(Color.GREEN);
                            score++;
                            scoreLabel.setText("Score: " + score);
                            guessButton.setEnabled(false);
                            playAgainButton.setVisible(true);
                        } else if (guess < numberToGuess) {
                            messageLabel.setText("📉 Too low! Try again.");
                            messageLabel.setForeground(Color.ORANGE);
                        } else {
                            messageLabel.setText("📈 Too high! Try again.");
                            messageLabel.setForeground(Color.ORANGE);
                        }

                        if (attemptsLeft == 0 && guess != numberToGuess) {
                            messageLabel.setText("❌ Out of attempts! Number was " + numberToGuess);
                            messageLabel.setForeground(Color.RED);
                            guessButton.setEnabled(false);
                            playAgainButton.setVisible(true);
                        }

                    } catch (NumberFormatException ex) {
                        messageLabel.setText("⚠ Enter a valid number!");
                        messageLabel.setForeground(Color.YELLOW);
                    }
                }
            }
        });

        // 🔄 Restart game button
        playAgainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        // 🎵 Start background music
        playBackgroundMusic("Love_me_like_you_do.wav");

        setVisible(true);
    }

    // 🔹 Ask for difficulty level
    private void chooseDifficulty() {
        String[] options = {
            "Easy (1–50, 10 attempts)",
            "Medium (1–100, 7 attempts)",
            "Hard (1–500, 5 attempts)"
        };
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose Difficulty Level:",
                "Difficulty Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[1]
        );

        switch (choice) {
            case 0: maxRange = 50; maxAttempts = 10; break;
            case 1: maxRange = 100; maxAttempts = 7; break;
            case 2: maxRange = 500; maxAttempts = 5; break;
            default: maxRange = 100; maxAttempts = 7; break;
        }
        attemptsLeft = maxAttempts;
        numberToGuess = new Random().nextInt(maxRange) + 1;
    }

    // 🔄 Restart the game
    private void restartGame() {
        chooseDifficulty();
        messageLabel.setText("Guess a number between 1 and " + maxRange);
        messageLabel.setForeground(Color.WHITE);
        attemptsBar.setMaximum(maxAttempts);
        attemptsBar.setValue(attemptsLeft);
        attemptsBar.setString("Attempts left: " + attemptsLeft);
        guessField.setText("");
        guessButton.setEnabled(true);
        playAgainButton.setVisible(false);
    }

    // 🎵 Play background music
    private void playBackgroundMusic(String fileName) {
        try {
            File file = new File(fileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // loop forever
        } catch (Exception e) {
            System.out.println("Music error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new NumberGuessingSwingWithSound();
    }
}
