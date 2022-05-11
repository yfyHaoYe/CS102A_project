package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 这个类表示国际象棋里面的主教
 */
public class BishopChessComponent extends ChessComponent {
    /**
     * 黑主教和白主教的图片，static使得其可以被所有主教对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image BISHOP_WHITE;
    private static Image BISHOP_BLACK;

    /**
     * 主教棋子对象自身的图片，是上面两种中的一种
     */
    private Image bishopImage;

    /**
     * 读取加载主教棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (BISHOP_WHITE == null) {
            BISHOP_WHITE = ImageIO.read(new File("./images/bishop-white.png"));
        }

        if (BISHOP_BLACK == null) {
            BISHOP_BLACK = ImageIO.read(new File("./images/bishop-black.png"));
        }
    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定bishopImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateRookImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                bishopImage = BISHOP_WHITE;
            } else if (color == ChessColor.BLACK) {
                bishopImage = BISHOP_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BishopChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateRookImage(color);
    }

    /**
     * 主教棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 主教棋子移动的合法性
     */

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();
        //判断是否在同一左上-右下对角线
        if (destination.getY()-source.getY() == destination.getX()-source.getX()) {
            for (int i=1;i<Math.abs(destination.getY()-source.getY());i++) {
                if(destination.getY()>source.getY()){
                    if (!(chessComponents[source.getX()+i][source.getY()+i] instanceof EmptySlotComponent)) {
                        return false;
                    }
                } else {
                    if (!(chessComponents[source.getX()-i][source.getY()-i] instanceof EmptySlotComponent)) {
                        return false;
                    }
                }
            }
        }
        //判断是否在同一右上-左下对角线
        else if (destination.getY()-source.getY() == -destination.getX()+source.getX()) {
            for (int i=1;i<Math.abs(destination.getY()-source.getY());i++) {
                if(destination.getY()>source.getY()){
                    if (!(chessComponents[source.getX()-i][source.getY()+i] instanceof EmptySlotComponent)) {
                        return false;
                    }
                } else {
                    if (!(chessComponents[source.getX()+i][source.getY()-i] instanceof EmptySlotComponent)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 注意这个方法，每当窗体受到了形状的变化，或者是通知要进行绘图的时候，就会调用这个方法进行画图。
     *
     * @param g 可以类比于画笔
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(bishopImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(bishopImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
    }
}
