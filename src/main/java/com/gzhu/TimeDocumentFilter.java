package com.gzhu;

import javax.swing.*;

// 时间输入验证类（支持带冒号的时间格式）
class TimeDocumentFilter extends javax.swing.text.PlainDocument {
    private JTextField textField;

    public TimeDocumentFilter(JTextField textField) {
        this.textField = textField;
    }

    @Override
    public void insertString(int offset, String str, javax.swing.text.AttributeSet attr)
            throws javax.swing.text.BadLocationException {
        if (str == null) return;

        String currentText = getText(0, getLength());
        StringBuilder newText = new StringBuilder(currentText);
        newText.insert(offset, str);

        // 检查长度（最多允许5个字符，如10:30）
        if (newText.length() > 5) {
            return;
        }

        // 验证时间格式（允许数字和冒号）
        String testText = newText.toString();

        // 模式匹配：允许的数字和冒号组合
        if (!testText.matches("\\d{0,2}:?\\d{0,2}")) {
            return;
        }

        // 检查冒号位置（只能在第2或第3位）
        int colonIndex = testText.indexOf(':');
        if (colonIndex != -1 && colonIndex != 2) {
            return;
        }

        // 自动插入冒号逻辑
        if (testText.length() == 2 && colonIndex == -1) {
            // 输入了2位数字，自动添加冒号
            super.insertString(offset, str, attr);
            super.insertString(offset + str.length(), ":", attr);
            return;
        }

        super.insertString(offset, str, attr);
    }


}
