import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * Chip class that extends the Jpanel
 * it will draw the chip, by the constructor argument
 *
 */
public class Chip_view extends JPanel implements MouseListener{

    Color c;
    Graphics g;
    int r = 80/2;
    int bound1 = 55, bound2=55;
    public byte column;
    public byte player;

    /**
     * Primary Constructor
     * The parameter defines the color
     * Red or Green
     * and the position on the x,y axis
     */
    public Chip_view(byte column, byte player, int x, int y){
    	setOpaque(false);
        this.column = column;
        this.player = player;
        this.addMouseListener(this);

        setBounds(x,y,bound1,bound2);

        if(player==2)
            c = Color.decode("#990000");
        else
            c= Color.decode("#262626");

    }


	public void Move(int x ,int y){

        this.setBounds(x, y, bound1, bound2);
    }

    protected void paintComponent(Graphics g){
    	super.paintComponent(g);
    	Graphics2D g2 = (Graphics2D) g;
    	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    	    RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c);
        g2.fillOval(0, 10, r,r);
        g2.setColor(Color.BLACK);
        if(Board.selected_view == this) g2.setColor(Color.GREEN);
        else g2.setColor(Color.BLACK);
        g2.drawOval(0, 10, r, r);
        g2.drawOval(1, 10, r-1, r+1);
        g2.drawOval(1, 11, r-2, r-2);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    	if(this.contains(e.getX(), e.getY()))
    	{
    		if(Board.currently_playing != this.player) return;
    		if(Board.selected_view != null) Board.selected_view.repaint();
    		Board.SetSelected(this);
    		repaint();
    	}
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }
}
