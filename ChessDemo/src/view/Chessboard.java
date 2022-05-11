package view;


import model.*;
import controller.ClickController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 这个类表示面板上的棋盘组件对象
 */
public class Chessboard extends JComponent {
    /**
     * CHESSBOARD_SIZE： 棋盘是8 * 8的
     * <br>
     * BACKGROUND_COLORS: 棋盘的两种背景颜色
     * <br>
     * chessListener：棋盘监听棋子的行动
     * <br>
     * chessboard: 表示8 * 8的棋盘
     * <br>
     * currentColor: 当前行棋方
     */
    private static final int CHESSBOARD_SIZE = 8;

    private final ChessComponent[][] chessComponents = new ChessComponent[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
    private ChessColor currentColor = ChessColor.BLACK;
    //all chessComponents in this chessboard are shared only one model controller
    private final ClickController clickController = new ClickController(this);
    private final int CHESS_SIZE;

    private boolean end=false;
    private ChessColor winner;

    public Chessboard(int width, int height) {
        setLayout(null); // Use absolute layout.
        setSize(width, height);
        CHESS_SIZE = width / 8;
        System.out.printf("chessboard size = %d, chess size = %d\n", width, CHESS_SIZE);

        initiateEmptyChessboard();

        // FIXME: Initialize chessboard for testing only.
        initComponentOnBoard(1,0, 0, ChessColor.BLACK);
        initComponentOnBoard(1,0, 7, ChessColor.BLACK);
        initComponentOnBoard(1,7, 0, ChessColor.WHITE);
        initComponentOnBoard(1,7, 7, ChessColor.WHITE);
        initComponentOnBoard(2,0, 3, ChessColor.BLACK);
        initComponentOnBoard(2,7, 3, ChessColor.WHITE);
        initComponentOnBoard(3,0, 4, ChessColor.BLACK);
        initComponentOnBoard(3,7, 4, ChessColor.WHITE);
        initComponentOnBoard(4,0, 2, ChessColor.BLACK);
        initComponentOnBoard(4,0, 5, ChessColor.BLACK);
        initComponentOnBoard(4,7, 2, ChessColor.WHITE);
        initComponentOnBoard(4,7, 5, ChessColor.WHITE);
        initComponentOnBoard(5,0, 1, ChessColor.BLACK);
        initComponentOnBoard(5,0, 6, ChessColor.BLACK);
        initComponentOnBoard(5,7, 1, ChessColor.WHITE);
        initComponentOnBoard(5,7, 6, ChessColor.WHITE);
        for (int i=0;i<8;i++){
            initComponentOnBoard(6,1, i, ChessColor.BLACK);
            initComponentOnBoard(6,6, i, ChessColor.WHITE);
        }
    }

    public ChessComponent[][] getChessComponents() {
        return chessComponents;
    }

    public ChessColor getCurrentColor() {
        return currentColor;
    }

    public void putChessOnBoard(ChessComponent chessComponent) {
        int row = chessComponent.getChessboardPoint().getX(), col = chessComponent.getChessboardPoint().getY();

        if (chessComponents[row][col] != null) {
            remove(chessComponents[row][col]);
        }
        add(chessComponents[row][col] = chessComponent);
    }

    public void swapChessComponents(ChessComponent chess1, ChessComponent chess2) {
        // Note that chess1 has higher priority, 'destroys' chess2 if exists.

        //判断兵是否可以走两格
        if (chess1 instanceof PawnChessComponent){
            ((PawnChessComponent) chess1).move();
        }

        //吃棋
        if (!(chess2 instanceof EmptySlotComponent)) {
            //获胜条件：吃掉对方的王
            if(chess2 instanceof KingChessComponent){
                end=true;
                winner=currentColor;
            }
            remove(chess2);
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController, CHESS_SIZE));
        }
        chess1.swapLocation(chess2);
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        chessComponents[row1][col1] = chess1;
        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
        chessComponents[row2][col2] = chess2;

        chess1.repaint();
        chess2.repaint();
    }

    public void initiateEmptyChessboard() {
        for (int i = 0; i < chessComponents.length; i++) {
            for (int j = 0; j < chessComponents[i].length; j++) {
                putChessOnBoard(new EmptySlotComponent(new ChessboardPoint(i, j), calculatePoint(i, j), clickController, CHESS_SIZE));
            }
        }
    }

    public void swapColor() {
        currentColor = currentColor == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }

    private void initComponentOnBoard(int kind,int row, int col, ChessColor color) {
        ChessComponent chessComponent = switch (kind) {
            case 1 ->
                    new RookChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
            case 2 ->
                    new KingChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
            case 3 ->
                    new QueenChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
            case 4 ->
                    new BishopChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
            case 5 ->
                    new KnightChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
            case 6 ->
                    new PawnChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
            default ->
                    new EmptySlotComponent(new ChessboardPoint(row,col), calculatePoint(row, col), clickController, CHESS_SIZE);
        };
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }


    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE, row * CHESS_SIZE);
    }

    public void loadGame(List<String> chessData) {
        chessData.forEach(System.out::println);
    }
}
