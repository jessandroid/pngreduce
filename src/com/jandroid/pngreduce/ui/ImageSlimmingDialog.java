package com.jandroid.pngreduce.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jandroid.pngreduce.bean.PngReduceBean;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import kotlin.Pair;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.jandroid.pngreduce.extension.GUIDialogExtKt.showDialog;

public class ImageSlimmingDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<String> mCBoxInputPath;
    private JButton mBtnInputDir;
    private JComboBox<String> mCBoxOutputPath;
    private JButton mBtnOutputDir;
    private JCheckBox mCbShowPrefix;
    private JLabel mLabelPrefix;
    private JComboBox<String> mCBoxPrefix;
    private JLabel mLabelRename;
    private JTextField mTvRename;


    public ImageSlimmingDialog(Pair<List<String>, List<String>> usedDirs, List<String> filePrefixList, DialogCallback callback) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        mLabelPrefix.setVisible(false);
        mCBoxPrefix.setVisible(false);
        mLabelRename.setVisible(false);
        mTvRename.setVisible(false);


        mCBoxInputPath.setEditable(true);
        mCBoxOutputPath.setEditable(true);
        mCBoxPrefix.setEditable(true);

        mCbShowPrefix.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (mCbShowPrefix.isSelected()) {
                    mLabelPrefix.setVisible(true);
                    mCBoxPrefix.setVisible(true);
                    mLabelRename.setVisible(true);
                    mTvRename.setVisible(true);
                } else {
                    mLabelPrefix.setVisible(false);
                    mCBoxPrefix.setVisible(false);
                    mLabelRename.setVisible(false);
                    mTvRename.setVisible(false);
                }
            }
        });

        //render cbox
        for (String inputDir : usedDirs.getFirst()) {
            mCBoxInputPath.addItem(inputDir.trim());
        }

        for (String outputDir : usedDirs.getSecond()) {
            mCBoxOutputPath.addItem(outputDir.trim());
        }

        for (String filePrefix : filePrefixList) {
            if (mCBoxPrefix != null) {
                mCBoxPrefix.addItem(filePrefix.trim());
            }
        }

        //选择输入文件
        mBtnInputDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileAndSetPath(mCBoxInputPath, JFileChooser.FILES_AND_DIRECTORIES, true);
            }
        });

        //选择输出文件
        mBtnOutputDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileAndSetPath(mCBoxOutputPath, JFileChooser.DIRECTORIES_ONLY, false);
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(callback);
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel(callback);
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel(callback);
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel(callback);
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void openFileAndSetPath(JComboBox<String> cBoxPath, int selectedMode, Boolean isSupportMultiSelect) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(selectedMode);
        fileChooser.setMultiSelectionEnabled(isSupportMultiSelect);
        //设置文件扩展过滤器
        if (selectedMode != JFileChooser.DIRECTORIES_ONLY) {
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".png", "png"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".jpg", "jpg"));
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(".jpeg", "jpeg"));
        }

        fileChooser.showOpenDialog(null);


        if (selectedMode == JFileChooser.DIRECTORIES_ONLY) {//仅仅选择目录情况，不存在多文件选中
            File selectedDir = fileChooser.getSelectedFile();
            if (selectedDir != null) {
                cBoxPath.insertItemAt(selectedDir.getAbsolutePath(), 0);
                cBoxPath.setSelectedIndex(0);
            }
        } else {//选择含有文件情况，包括仅仅 选择文件 和 同时选择文件和目录，
            File[] selectedFiles = fileChooser.getSelectedFiles();
            if (selectedFiles != null && selectedFiles.length > 0) {
                cBoxPath.insertItemAt(getSelectedFilePath(selectedFiles), 0);
                cBoxPath.setSelectedIndex(0);
            }
        }

    }

    private String getSelectedFilePath(File[] selectedFiles) {
        if (selectedFiles.length == 1) {//单个文件或者目录
            return selectedFiles[0].getAbsolutePath();
        }
        //多个文件选中情况,使用逗号分隔将选中多个文件的路径连接为一个长串
        StringBuilder builder = new StringBuilder();
        builder.append(selectedFiles[0]);
        for (int i = 1; i < selectedFiles.length; i++) {
            builder.append(",");
            builder.append(selectedFiles[i].getAbsolutePath());
        }
        return builder.toString();
    }

    private void onOK(DialogCallback callback) {
        String filePrefix = "";
        if (mCBoxPrefix.getSelectedItem() != null) {
            filePrefix = mCBoxPrefix.getSelectedItem().toString();
        }

        if (callback != null && mCBoxInputPath.getSelectedItem() != null && mCBoxOutputPath.getSelectedItem() != null) {
            callback.onOkClicked(new PngReduceBean(mCBoxInputPath.getSelectedItem().toString(), mCBoxOutputPath.getSelectedItem().toString(), filePrefix));
        }
        dispose();
    }

    private void onCancel(DialogCallback callback) {
        // add your code here if necessary
        if (callback != null) {
            callback.onCancelClicked();
        }
        dispose();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new FormLayout("fill:56px:grow", "center:40px:noGrow,center:p:grow,top:3dlu:noGrow,center:max(d;4px):noGrow,center:32px:noGrow"));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        CellConstraints cc = new CellConstraints();
        contentPane.add(panel1, cc.xy(1, 5));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("OK");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:29px:grow,fill:28px:noGrow", "center:d:grow,top:3dlu:noGrow,center:d:grow"));
        contentPane.add(panel3, new CellConstraints(1, 2, 1, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(10, 10, 10, 10)));
        final JLabel label1 = new JLabel();
        label1.setText("源目录");
        panel3.add(label1, cc.xy(1, 1));
        mCBoxInputPath = new JComboBox();
        panel3.add(mCBoxInputPath, cc.xy(3, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JLabel label2 = new JLabel();
        label2.setText("输出目录");
        panel3.add(label2, cc.xy(1, 3));
        mCBoxOutputPath = new JComboBox();
        panel3.add(mCBoxOutputPath, cc.xy(3, 3));
        mBtnInputDir = new JButton();
        mBtnInputDir.setHorizontalAlignment(0);
        mBtnInputDir.setHorizontalTextPosition(11);
        mBtnInputDir.setText("...");
        mBtnInputDir.setVerticalAlignment(1);
        mBtnInputDir.setVerticalTextPosition(1);
        panel3.add(mBtnInputDir, cc.xy(4, 1));
        mBtnOutputDir = new JButton();
        mBtnOutputDir.setText("...");
        mBtnOutputDir.setVerticalAlignment(1);
        panel3.add(mBtnOutputDir, cc.xy(4, 3));
        final JLabel label3 = new JLabel();
        label3.setText("图片压缩框架");
        contentPane.add(label3, new CellConstraints(1, 1, 1, 1, CellConstraints.CENTER, CellConstraints.DEFAULT, new Insets(8, 0, 0, 0)));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FormLayout("left:4dlu:noGrow,fill:d:grow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:max(d;4px):noGrow,top:3dlu:noGrow,center:d:grow"));
        contentPane.add(panel4, cc.xy(1, 4));
        mCbShowPrefix = new JCheckBox();
        mCbShowPrefix.setHideActionText(true);
        mCbShowPrefix.setMargin(new Insets(10, 10, 0, 1));
        mCbShowPrefix.setText("指定输出文件前缀名");
        panel4.add(mCbShowPrefix, cc.xy(2, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        mLabelPrefix = new JLabel();
        mLabelPrefix.setText("前缀名");
        panel4.add(mLabelPrefix, cc.xy(3, 1));
        mCBoxPrefix = new JComboBox();
        mCBoxPrefix.setEditable(false);
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        mCBoxPrefix.setModel(defaultComboBoxModel1);
        mCBoxPrefix.setPreferredSize(new Dimension(140, 27));
        panel4.add(mCBoxPrefix, cc.xy(5, 1));
        mLabelRename = new JLabel();
        mLabelRename.setText("重命名");
        panel4.add(mLabelRename, cc.xy(7, 1));
        mTvRename = new JTextField();
        mTvRename.setMargin(new Insets(0, 0, 0, 0));
        mTvRename.setPreferredSize(new Dimension(130, 26));
        mTvRename.setText("");
        panel4.add(mTvRename, cc.xy(9, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    public interface DialogCallback {
        void onOkClicked(PngReduceBean imageSlimmingModel);

        void onCancelClicked();
    }

    public static void main(String[] args) {
        ImageSlimmingDialog dialog = new ImageSlimmingDialog(new Pair<>(new ArrayList<>(), new ArrayList<>()), new ArrayList<>(), new DialogCallback() {
            @Override
            public void onOkClicked(PngReduceBean compressModel) {
                System.out.println(compressModel.toString());
            }

            @Override
            public void onCancelClicked() {

            }
        });
        showDialog(dialog, 580, 200, true, false);
    }
}