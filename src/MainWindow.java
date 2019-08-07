import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class MainWindow implements MouseListener {

    private JFrame frame;
    private Board board;
    static Image[] dice;
    static JLabel dice_1, dice_2, lblTimePlaying, backgammonBackground;
    private JButton btnCompletemove,  btnNewButton, btnNewButton_2;
    private boolean done = false;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainWindow window = new MainWindow();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.addMouseListener(this);
        frame.setResizable(false);
        frame.setBounds(100, 100, 800, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        try {
            dice = new Image[]{ImageIO.read(new File("src/dice_1.jpg")).getScaledInstance(30, 30, Image.SCALE_SMOOTH),
                    ImageIO.read(new File("src/dice_2.jpg")).getScaledInstance(30, 30, Image.SCALE_SMOOTH),
                    ImageIO.read(new File("src/dice_3.jpg")).getScaledInstance(30, 30, Image.SCALE_SMOOTH),
                    ImageIO.read(new File("src/dice_4.jpg")).getScaledInstance(30, 30, Image.SCALE_SMOOTH),
                    ImageIO.read(new File("src/dice_5.jpg")).getScaledInstance(30, 30, Image.SCALE_SMOOTH),
                    ImageIO.read(new File("src/dice_6.jpg")).getScaledInstance(30, 30, Image.SCALE_SMOOTH)};
        } catch (Exception ignored) {

        }

        dice_1 = new JLabel("");
        dice_1.setBounds(580, 350, 30, 30);
        dice_1.setIcon(new ImageIcon(dice[0]));

        dice_2 = new JLabel("");
        dice_2.setBounds(580, 400, 30, 30);
        dice_2.setIcon(new ImageIcon(dice[0]));

        frame.getContentPane().add(dice_1);
        frame.getContentPane().add(dice_2);

        btnCompletemove = new JButton("Complete Move");
        btnCompletemove.addActionListener(e1 ->play());

        btnCompletemove.setBounds(213, 11, 200, 23);
        btnCompletemove.setEnabled(false);
        frame.getContentPane().add(btnCompletemove);

        btnNewButton_2 = new JButton("Cancel Move");
        btnNewButton_2.setEnabled(false);
        btnNewButton_2.setBounds(413, 11, 200, 23);
        btnNewButton_2.addActionListener(e -> revert());
        frame.getContentPane().add(btnNewButton_2);

        btnNewButton = new JButton("New Game");
        btnNewButton.addActionListener(e -> newGame());
        btnNewButton.setBounds(10, 11, 200, 23);
        frame.getContentPane().add(btnNewButton);

        lblTimePlaying = new JLabel("Now Playing:");
        lblTimePlaying.setBounds(10, 45, 130, 14);
        frame.getContentPane().add(lblTimePlaying);

        Image img;
        try {
            img = ImageIO.read(new File("src/backgamon.jpg"));
            backgammonBackground = new JLabel("");
            backgammonBackground.setBounds(10, 70, 720, 641);
            Image dimg = img.getScaledInstance(backgammonBackground.getWidth(), backgammonBackground.getHeight(), Image.SCALE_SMOOTH);
            backgammonBackground.setIcon(new ImageIcon(dimg));
            frame.getContentPane().add(backgammonBackground);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newGame() {
        if(board!=null)quit();
        done = false;
        btnCompletemove.setEnabled(true);
        btnNewButton_2.setEnabled(true);
        this.board = new Board();
        DrawInit();
        board.RollDice();
        do {
            if (Board.dice[0] > Board.dice[1]) {
                Board.currently_playing = 1;
                Board.old_state = new BoardState(Board.current_state);
                lblTimePlaying.setText("Now Playing: User");
                lblTimePlaying.setForeground(Color.decode("#262626"));
                lblTimePlaying.repaint();
                break;
            } else if (Board.dice[0] < Board.dice[1]) {
                Board.currently_playing = 2;
                lblTimePlaying.setText("Now Playing: AI");
                lblTimePlaying.setForeground(Color.decode("#990000"));
                lblTimePlaying.repaint();
                this.btnCompletemove.doClick();
                break;
            } else
                board.RollDice();
        } while (true);
    }

    /*
     * Draw the initial board
     */
    private void DrawInit() {
        frame.remove(backgammonBackground);
        LinkedList<Chip_view>[] chips = board.GetViews();
        for (LinkedList<Chip_view> list : chips) {
            if (list != null)
                for (Chip_view aChip : list) {
                    this.frame.getContentPane().add(aChip);
                }
            frame.getContentPane().add(backgammonBackground);
            frame.getContentPane().validate();
            frame.getContentPane().repaint();
        }
    }

    private void quit() {
        frame.getContentPane().removeAll();
        btnNewButton_2.setEnabled(false);
        btnCompletemove.setEnabled(false);
        btnNewButton.setEnabled(true);
        frame.getContentPane().add(btnNewButton_2);
        frame.getContentPane().add(btnNewButton);
        frame.getContentPane().add(btnCompletemove);
        frame.getContentPane().add(lblTimePlaying);
        frame.getContentPane().add(backgammonBackground);
        frame.getContentPane().validate();
        frame.getContentPane().repaint();
    }

    public void ReDraw() {
        frame.getContentPane().removeAll();
        frame.getContentPane().validate();
        frame.getContentPane().repaint();

        frame.getContentPane().add(dice_1);
        frame.getContentPane().add(dice_2);
        frame.getContentPane().add(btnCompletemove);
        frame.getContentPane().add(btnNewButton);
        frame.getContentPane().add(lblTimePlaying);
        frame.getContentPane().add(btnNewButton_2);
        board.Update_Views();
        LinkedList<Chip_view>[] chips = board.GetViews();
        for (LinkedList<Chip_view> list : chips) {
            for (Chip_view aChip : list) {
                frame.getContentPane().add(aChip);
            }
        }
        frame.getContentPane().add(backgammonBackground);
        frame.getContentPane().validate();
        frame.getContentPane().repaint();
    }

    private void play(){
        if(done)return;

        if(board.CompleteMove()){

            if(Board.current_state.GetWithdrawn(1) == 15 || Board.current_state.GetWithdrawn(2) == 15 ){
                done = true;
                if(Board.current_state.GetWithdrawn(1) == 15 ){
                    JOptionPane.showMessageDialog(null,"Congratulations you won!");
                }
                else{
                    JOptionPane.showMessageDialog(null,"You lost :(");
                }
            }

            ReDraw();
            board.RollDice();
            if(Board.currently_playing == 1) {
                Board.currently_playing = 2;
                lblTimePlaying.setText("Now Playing: AI");
                lblTimePlaying.setForeground(Color.decode("#990000"));
                lblTimePlaying.repaint();
                this.btnCompletemove.doClick();
            }
            else {
                Board.old_state = new BoardState(Board.current_state);
                Board.currently_playing = 1;
                lblTimePlaying.setText("Now Playing: User");
                lblTimePlaying.setForeground(Color.decode("#262626"));
                lblTimePlaying.repaint();
            }
        }
        else{
            ReDraw();
        }
    }

    private void revert(){
        board.revert();
        ReDraw();

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(board == null)return;
        if (Board.selected != -5) {
            board.Move(e.getX(), e.getY());
            ReDraw();
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}