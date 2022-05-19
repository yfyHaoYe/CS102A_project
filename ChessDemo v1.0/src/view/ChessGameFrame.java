package view;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import controller.GameController;
import model.ChessColor;
import model.ChessComponent;
import model.ChessPiece;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame {
    //    public final Dimension FRAME_SIZE ;
    private int WIDTH;
    private int HEIGTH;
    public int chessboardSize;
    private GameController gameController;

    public Chessboard getChessboard() {
        return chessboard;
    }

    private Chessboard chessboard;
    private JLabel statusLabel;

    public ChessGameFrame(int width, int height) {
        setTitle("2022 CS102A Project Demo"); //设置标题
        this.WIDTH = width;
        this.HEIGTH = height;
        this.chessboardSize = HEIGTH * 4 / 5;

        setSize(WIDTH, HEIGTH);
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);


        addChessboard();
        addLabel();
        addLoadButton();
        addResetButton();  //重置按钮
        addWriteButton();   //写入存档
        addReadButton();    //读取存档
    }


    /**
     * 在游戏面板中添加棋盘
     */
    private void addChessboard() {
        chessboard = new Chessboard(this, chessboardSize, chessboardSize);
        gameController = new GameController(chessboard);
        chessboard.setLocation(HEIGTH / 10, HEIGTH / 10);
        add(chessboard);
    }

    /**
     * 在游戏面板中添加标签
     */
    private void addLabel() {
        statusLabel = new JLabel("current player: " + chessboard.getCurrentColor());
        statusLabel.setLocation(HEIGTH - 40, HEIGTH / 10);
        statusLabel.setSize(300, 60);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(statusLabel);
    }

    /**
     * 设置标签文本
     */
    public void setLabelText(String text) {
        statusLabel.setText(text);
    }

    private void addLoadButton() {
        JButton button = new JButton("Load");
        button.setLocation(HEIGTH - 40, HEIGTH / 10 + 125);
        button.setSize(220, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
        button.addActionListener(e -> {
            System.out.println("Click load");
            String path = JOptionPane.showInputDialog(this,"Input Path here");
            gameController.loadGameFromFile(path);
        });
    }

    /**
     * 重置按钮
     */
    private void addResetButton() {
        JButton button = new JButton("Reset Chessboard");
        button.setLocation(HEIGTH - 40, HEIGTH / 10 + 250);
        button.setSize(220, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        button.addActionListener((e) -> {
            chessboard.resetChessboard();
        });
        add(button);
    }

    /**
     * 写入存档按钮
     */
    private void addWriteButton() {
        JButton button = new JButton("write archive");
        button.setLocation(HEIGTH - 40, HEIGTH / 10 + 375);
        button.setSize(220, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        button.addActionListener((e) -> {
            //文件选择
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("国际象棋存档.json")); //选择的文件
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);    //保存类型
            int res = chooser.showSaveDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {   //点击了保存
                File file = chooser.getSelectedFile();  //选择的文件
                //处理数据
                ChessComponent[][] chessComponents = chessboard.getChessComponents();   //所有组件
                java.util.List<ChessPiece> chessPieceList = ChessPiece.getListByComponent(chessComponents); //封装list
                Map<String, Object> map = new HashMap<>();
                map.put("currentColor", chessboard.getCurrentColor());  //当前行棋颜色
                map.put("chessPieces", chessPieceList);  //所有棋子
                //转成json
                String json = JSONUtil.toJsonStr(map);
                //保存文件
                try {
                    IoUtil.writeUtf8(new FileOutputStream(file), true, json);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "保存存档失败，错误信息：" + ex.getMessage());
                    return;
                }
                JOptionPane.showMessageDialog(this, "保存存档成功，文件路径：" + file.getAbsolutePath());
            }
        });
        add(button);
    }

    /**
     * 读取存档
     */
    private void addReadButton() {
        JButton button = new JButton("read archive");
        button.setLocation(HEIGTH - 40, HEIGTH / 10 + 500);
        button.setSize(220, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        button.addActionListener((e) -> {
            //文件选择
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || StrUtil.equals("json", FileUtil.getSuffix(f));
                }

                @Override
                public String getDescription() {
                    return "国际象棋存档";
                }
            });
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);    //打开类型
            int res = chooser.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {   //选择了文件
                File file = chooser.getSelectedFile();
                //(4)	文件格式错误(例如规定是 txt，导入的时候是json)
                if (!StrUtil.equals("json", FileUtil.getSuffix(file))) {
                    JOptionPane.showMessageDialog(this, "文件格式错误");
                    return;
                } else {
                    try {
                        //读取解析json
                        String json = IoUtil.read(new FileReader(file));
                        JSONObject obj = JSONUtil.parseObj(json);
                        java.util.List<ChessPiece> chessPieces = ((JSONArray) obj.get("chessPieces")).toList(ChessPiece.class);
                        String currentColor = (String) obj.get("currentColor");
                        //(1)	棋盘并非 8*8
                        boolean present = chessPieces.stream()
                                .filter(item -> item.getRow() < 0 || item.getRow() >= 8 ||
                                                item.getCol() < 0 || item.getCol() >= 8)
                                .findAny()
                                .isPresent();
                        if (present) {
                            JOptionPane.showMessageDialog(this, "读取存档失败，错误信息：棋盘并非 8*8");
                            return;
                        }
                        //(2)	棋子并非六种之一，棋子并非黑白棋子
                        present = chessPieces.stream()
                                .filter(item -> (item.getKind() < 1 || item.getKind() > 6) ||
                                        (item.getColor() != ChessColor.BLACK && item.getColor() != ChessColor.WHITE))
                                .findAny()
                                .isPresent();
                        if (present) {
                            JOptionPane.showMessageDialog(this, "读取存档失败，错误信息：棋子并非六种之一，棋子并非黑白棋子");
                            return;
                        }
                        //(3)	缺少下一步行棋方
                        if (StrUtil.isBlank(currentColor)) {
                            JOptionPane.showMessageDialog(this, "读取存档失败，错误信息：缺少下一步行棋方");
                            return;
                        }
                        //渲染表盘
                        chessboard.initiateEmptyChessboard();
                        for (ChessPiece piece : chessPieces) {
                            chessboard.initComponentOnBoard(piece.getKind(), piece.getRow(), piece.getCol(), piece.getColor());
                        }
                        chessboard.repaint();
                        //设置行棋方
                        if (!StrUtil.equalsIgnoreCase(currentColor, chessboard.getCurrentColor().getName())) {
                            chessboard.swapColor();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "读取存档失败，错误信息：" + ex.getMessage());
                        return;
                    }
                }
            }
        });
        add(button);
    }

    public void resizedAfter() {
        //
        //把上面的final关键字去了
        Dimension size = getSize();
        HEIGTH = size.height;
        WIDTH = size.width;

        chessboardSize = HEIGTH * 4 / 5;

        //设置棋盘大小
        chessboard.setLocation((int) (WIDTH/2.6-chessboardSize/2),HEIGTH/2-chessboardSize/2);
        chessboard.setSize(chessboardSize,chessboardSize);
        chessboard.resizedAfter();

        //遍历设置标签和按钮位置
        Component[] components = getContentPane().getComponents();
        java.util.List<Component> componentList = Arrays.stream(components)
                .filter(item -> !(item instanceof Chessboard))  //不是棋盘的组件
                .collect(Collectors.toList());
        for (int i = 0; i < componentList.size(); i++) {
            Component c = componentList.get(i);
            c.setSize((int) (WIDTH/4.5), (int) (HEIGTH/13));
            c.setLocation((int) (WIDTH/1.4), (int) (HEIGTH/6.7*(i+1)));
        }
    }

}
