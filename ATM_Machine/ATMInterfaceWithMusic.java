import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;

// Bank Account class
class BankAccount {
    private double balance;

    public BankAccount(double initialBalance) {
        this.balance = Math.max(initialBalance, 0);
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
}

// ATM GUI class
class ATMGUI extends JFrame implements ActionListener {
    private BankAccount account;
    private JLabel screenLabel;
    private JTextField amountField;
    private JButton checkBalanceBtn, depositBtn, withdrawBtn, exitBtn;

    // Music
    private Clip backgroundMusic;
    private Clip beepSound;

    public ATMGUI(BankAccount account) {
        this.account = account;

        // Frame setup
        setTitle("ATM Machine");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(20, 20, 20));

        // Card slot
        JLabel cardSlot = new JLabel("💳 Inserted Card", SwingConstants.CENTER);
        cardSlot.setOpaque(true);
        cardSlot.setBackground(new Color(60, 60, 60));
        cardSlot.setForeground(Color.WHITE);
        cardSlot.setFont(new Font("Arial", Font.BOLD, 16));
        cardSlot.setPreferredSize(new Dimension(100, 50));
        add(cardSlot, BorderLayout.NORTH);

        // ATM screen
        screenLabel = new JLabel("Welcome! Please choose an option.", SwingConstants.CENTER);
        screenLabel.setOpaque(true);
        screenLabel.setBackground(Color.BLACK);
        screenLabel.setForeground(Color.GREEN);
        screenLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        screenLabel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        add(screenLabel, BorderLayout.CENTER);

        // Amount input panel (FIXED)
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(20, 20, 20));

        JLabel amountLabel = new JLabel("Enter Amount: ");
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));

        amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));
        amountField.setHorizontalAlignment(JTextField.CENTER);
        amountField.setBackground(new Color(230, 230, 230));  // Light background for visibility
        amountField.setForeground(Color.BLACK);
        amountField.setPreferredSize(new Dimension(150, 30));

        inputPanel.add(amountLabel, BorderLayout.WEST);
        inputPanel.add(amountField, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.SOUTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBackground(new Color(20, 20, 20));

        checkBalanceBtn = createATMButton("💰 Check Balance");
        depositBtn = createATMButton("➕ Deposit");
        withdrawBtn = createATMButton("➖ Withdraw");
        exitBtn = createATMButton("❌ Exit");

        buttonPanel.add(checkBalanceBtn);
        buttonPanel.add(depositBtn);
        buttonPanel.add(withdrawBtn);
        buttonPanel.add(exitBtn);

        add(buttonPanel, BorderLayout.EAST);

        // Load sounds
        loadSounds();

        // Play background music
        playBackgroundMusic();

        setVisible(true);
    }

    private JButton createATMButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 120, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.addActionListener(this);
        return button;
    }

    private void loadSounds() {
        try {
            // Background music
            File bgMusicFile = new File("background.wav"); // put background.wav in project folder
            AudioInputStream bgStream = AudioSystem.getAudioInputStream(bgMusicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(bgStream);

            // Beep sound
            File beepFile = new File("beep.wav"); // put beep.wav in project folder
            AudioInputStream beepStream = AudioSystem.getAudioInputStream(beepFile);
            beepSound = AudioSystem.getClip();
            beepSound.open(beepStream);

        } catch (Exception e) {
            System.out.println("Error loading sounds: " + e.getMessage());
        }
    }

    private void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY); // keep looping
        }
    }

    private void playBeep() {
        if (beepSound != null) {
            if (beepSound.isRunning()) {
                beepSound.stop();
            }
            beepSound.setFramePosition(0);
            beepSound.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        playBeep(); // play beep sound for every button press

        String input = amountField.getText();
        double amount = 0;
        if (!input.isEmpty()) {
            try {
                amount = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                screenLabel.setText("⚠ Invalid amount! Enter a number.");
                return;
            }
        }

        if (e.getSource() == checkBalanceBtn) {
            screenLabel.setText("💳 Balance: ₹" + account.getBalance());
        } else if (e.getSource() == depositBtn) {
            if (amount > 0) {
                account.deposit(amount);
                screenLabel.setText("✅ Deposited ₹" + amount + " | Balance: ₹" + account.getBalance());
            } else {
                screenLabel.setText("⚠ Enter a valid deposit amount.");
            }
        } else if (e.getSource() == withdrawBtn) {
            if (amount > 0) {
                if (account.withdraw(amount)) {
                    screenLabel.setText("✅ Withdrawn ₹" + amount + " | Balance: ₹" + account.getBalance());
                } else {
                    screenLabel.setText("❌ Insufficient Balance!");
                }
            } else {
                screenLabel.setText("⚠ Enter a valid withdrawal amount.");
            }
        } else if (e.getSource() == exitBtn) {
            if (backgroundMusic != null) {
                backgroundMusic.stop(); // stop music on exit
            }
            JOptionPane.showMessageDialog(this, "Thank you for using the ATM!");
            System.exit(0);
        }

        amountField.setText("");
    }
}

// Main Class
public class ATMInterfaceWithMusic {
    public static void main(String[] args) {
        BankAccount userAccount = new BankAccount(5000);
        new ATMGUI(userAccount);
    }
}
