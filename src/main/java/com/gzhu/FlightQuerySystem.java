package com.gzhu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// 主界面类
public class FlightQuerySystem extends JFrame {
    private FlightList flightList;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> searchTypeComboBox;
    private JTextField searchField;
    private JButton searchButton;
    private JButton showAllButton;
    private JButton helpButton;
    private JLabel statusLabel;

    public FlightQuerySystem() {
        flightList = new FlightList();
        initializeFlights();
        initializeUI();
        showWelcomeDialog();
    }

    // 初始化航班数据
    private void initializeFlights() {
        // 添加题目中的航班数据（使用带冒号的时间格式）
        flightList.add(new FlightNode("CA1544", "合肥", "北京", "1, 2, 4, 5", "10:55", "12:40", "733", "960"));
        flightList.add(new FlightNode("MU5341", "上海", "广州", "1, 2, 3, 4, 5, 6, 7", "14:20", "16:15", "M90", "1280"));
        flightList.add(new FlightNode("CZ3869", "重庆", "深圳", "2, 4, 6", "08:55", "10:35", "733", "1010"));
        flightList.add(new FlightNode("MU3682", "桂林", "南京", "2, 3, 4, 6, 7", "20:50", "22:15", "M90", "1380"));
        flightList.add(new FlightNode("HU1836", "上海", "北京", "1, 2, 3, 4, 5, 6, 7", "09:40", "11:20", "738", "1250"));
        flightList.add(new FlightNode("CZ3828", "成都", "厦门", "1, 3, 4, 5, 7", "15:10", "16:50", "CRJ", "1060"));
        flightList.add(new FlightNode("MU4596", "昆明", "西安", "1, 3, 5, 6", "10:15", "11:40", "328", "1160"));
        flightList.add(new FlightNode("SC7425", "青岛", "海口", "1, 3, 6", "19:20", "21:20", "DH4", "1630"));
    }

    private void initializeUI() {
        setTitle("航班信息查询系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLayout(new BorderLayout());

        // 创建顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 第一行：标题
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel(" 航班信息查询系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        titlePanel.add(titleLabel);
        topPanel.add(titlePanel, BorderLayout.NORTH);

        // 第二行：查询面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel searchTypeLabel = new JLabel("查询条件:");
        searchTypeLabel.setFont(new Font("宋体", Font.BOLD, 14));
        String[] searchTypes = {"请选择查询条件", "航班号", "起点站", "终点站", "航班期(星期)", "起飞时间(晚于)", "到达时间(早于)"};
        searchTypeComboBox = new JComboBox<>(searchTypes);
        searchTypeComboBox.setFont(new Font("宋体", Font.PLAIN, 14));

        // 添加查询条件监听器
        searchTypeComboBox.addActionListener(e -> {
            String selected = (String) searchTypeComboBox.getSelectedItem();
            if (selected != null) {
                if (selected.contains("时间")) {
                    // 设置时间输入限制（带冒号）
                    searchField.setDocument(new TimeDocumentFilter(searchField));
                    searchField.setToolTipText("请输入时间格式(如：10:55、14:20)，自动添加冒号");
                    searchField.setText(""); // 清空输入框
                } else {
                    // 移除时间输入限制
                    searchField.setDocument(new javax.swing.text.PlainDocument());
                    searchField.setToolTipText("在此输入查询内容");
                }
            }
        });

        JLabel searchContentLabel = new JLabel("查询内容:");
        searchContentLabel.setFont(new Font("宋体", Font.BOLD, 14));
        searchField = new JTextField(15);
        searchField.setFont(new Font("宋体", Font.PLAIN, 14));
        searchField.setToolTipText("在此输入查询内容");

        // 创建按钮 - 使用黑色字体
        searchButton = new JButton("查询");
        searchButton.setFont(new Font("宋体", Font.BOLD, 14));
        searchButton.setBackground(new Color(0, 153, 255));
        searchButton.setForeground(Color.BLACK);

        showAllButton = new JButton("显示所有航班");
        showAllButton.setFont(new Font("宋体", Font.BOLD, 14));
        showAllButton.setBackground(new Color(76, 175, 80));
        showAllButton.setForeground(Color.BLACK);

        helpButton = new JButton("功能介绍");
        helpButton.setFont(new Font("宋体", Font.BOLD, 14));
        helpButton.setBackground(new Color(255, 152, 0));
        helpButton.setForeground(Color.BLACK);

        searchButton.addActionListener(e -> performSearch());
        showAllButton.addActionListener(e -> showAllFlights());
        helpButton.addActionListener(e -> showFunctionIntroduction());

        searchPanel.add(searchTypeLabel);
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchContentLabel);
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(showAllButton);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(helpButton);

        topPanel.add(searchPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // 创建表格（时间列显示带冒号）
        String[] columnNames = {"航班号", "起点站", "终点站", "航班期", "起飞时间", "到达时间", "机型", "票价"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("宋体", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));

        // 设置列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);
        table.getColumnModel().getColumn(7).setPreferredWidth(60);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);

        // 创建底部状态栏
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));

        statusLabel = new JLabel("就绪 - 共有8条航班记录");
        statusLabel.setFont(new Font("宋体", Font.PLAIN, 12));

        JLabel systemLabel = new JLabel("航班信息查询系统 v1.0");
        systemLabel.setFont(new Font("宋体", Font.PLAIN, 12));
        systemLabel.setForeground(Color.GRAY);
        systemLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(systemLabel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        // 显示所有航班
        showAllFlights();

        setLocationRelativeTo(null);
    }

    // 时间验证方法（支持带冒号的格式）
    private boolean isValidTime(String timeStr) {
        // 移除冒号进行验证
        String timeWithoutColon = timeStr.replace(":", "");

        if (timeWithoutColon.length() != 4) {
            JOptionPane.showMessageDialog(this,
                    "时间格式错误！\n必须输入4位数字，如：10:55",
                    "时间输入错误",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int hours = Integer.parseInt(timeWithoutColon.substring(0, 2));
            int minutes = Integer.parseInt(timeWithoutColon.substring(2, 4));

            // 验证小时和分钟是否在有效范围内
            if (hours < 0 || hours > 23) {
                JOptionPane.showMessageDialog(this,
                        "小时必须在00-23之间\n当前输入：" + (hours < 10 ? "0" + hours : hours),
                        "时间输入错误",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (minutes < 0 || minutes > 59) {
                JOptionPane.showMessageDialog(this,
                        "分钟必须在00-59之间\n当前输入：" + (minutes < 10 ? "0" + minutes : minutes),
                        "时间输入错误",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "时间格式错误！\n请输入有效的数字时间",
                    "时间输入错误",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // 格式化时间查询输入
    private String formatTimeQuery(String timeInput) {
        // 如果已经有冒号，直接返回
        if (timeInput.contains(":")) {
            return timeInput;
        }

        // 如果没有冒号且是4位数字，添加冒号
        if (timeInput.length() == 4 && timeInput.matches("\\d{4}")) {
            return timeInput.substring(0, 2) + ":" + timeInput.substring(2);
        }

        // 其他情况返回原输入
        return timeInput;
    }

    private void showWelcomeDialog() {
        JDialog welcomeDialog = new JDialog(this, "欢迎使用", true);
        welcomeDialog.setSize(500, 450);
        welcomeDialog.setLayout(new BorderLayout());
        welcomeDialog.setLocationRelativeTo(this);

        JLabel titleLabel = new JLabel("️ 欢迎使用航班信息查询系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        welcomeDialog.add(titleLabel, BorderLayout.NORTH);

        JTextArea introArea = new JTextArea();
        introArea.setEditable(false);
        introArea.setFont(new Font("宋体", Font.PLAIN, 14));
        introArea.setLineWrap(true);
        introArea.setWrapStyleWord(true);
        introArea.setMargin(new Insets(10, 20, 10, 20));

        String introduction = """
             系统功能介绍：
            
            1. 查询功能：
               • 按航班号查询：输入完整航班号
               • 按起点站查询：输入出发城市
               • 按终点站查询：输入到达城市
               • 按航班期查询：可查询多个星期，支持格式：
                 - 单个：1 或 3
                 - 多个：1,3,5
                 - 范围：1-5
                 - 混合：1,3-5,7
               • 按起飞时间查询：查询晚于指定时间（24小时制）
               • 按到达时间查询：查询早于指定时间（24小时制）
            
            2. 数据显示：
               • 显示所有航班信息
               • 实时显示查询结果数量
            
            点击"功能介绍"按钮可随时查看详细说明。
            """;

        introArea.setText(introduction);
        welcomeDialog.add(new JScrollPane(introArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton confirmButton = new JButton("开始使用");
        confirmButton.setFont(new Font("宋体", Font.BOLD, 14));
        confirmButton.setBackground(new Color(0, 153, 255));
        confirmButton.setForeground(Color.BLACK);
        confirmButton.addActionListener(e -> welcomeDialog.dispose());

        buttonPanel.add(confirmButton);
        welcomeDialog.add(buttonPanel, BorderLayout.SOUTH);

        welcomeDialog.setVisible(true);
    }

    private void showFunctionIntroduction() {
        JDialog introDialog = new JDialog(this, "功能介绍", true);
        introDialog.setSize(500, 500);
        introDialog.setLayout(new BorderLayout());
        introDialog.setLocationRelativeTo(this);

        JLabel titleLabel = new JLabel("系统功能详细说明", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        introDialog.add(titleLabel, BorderLayout.NORTH);

        JTextArea functionArea = new JTextArea();
        functionArea.setEditable(false);
        functionArea.setFont(new Font("宋体", Font.PLAIN, 14));
        functionArea.setLineWrap(true);
        functionArea.setWrapStyleWord(true);
        functionArea.setMargin(new Insets(10, 20, 10, 20));

        String functions = """
             查询功能详细说明：
            
            1. 航班号查询：
               • 功能：通过航班号精确查询航班信息
               • 示例：输入 "CA1544" 查询该航班
               • 特点：支持大小写不敏感匹配
            
            2. 起点站查询：
               • 功能：查询从指定城市出发的所有航班
               • 示例：输入 "上海" 查询所有从上海出发的航班
            
            3. 终点站查询：
               • 功能：查询到达指定城市的所有航班
               • 示例：输入 "北京" 查询所有到达北京的航班
            
            4. 航班期（星期）查询：
               • 功能：查询在指定星期运行的航班
               • 数字 1-7 分别代表星期一至星期日
               • 格式说明：
                 - 逗号分隔多个星期：1,3,5
                 - 连字符表示范围：1-5（星期一到星期五）
                 - 可混合使用：1,3-5,7
            
            5. 起飞时间查询：
               • 功能：查询起飞时间晚于指定时间的航班
               • 智能输入：输入两位数字后自动添加冒号
            
            6. 到达时间查询：
               • 功能：查询到达时间早于指定时间的航班
               • 时间格式与起飞时间查询相同
            
             时间输入规则：
            1. 输入两位数字后自动添加冒号
            2. 小时范围：00-23
            3. 分钟范围：00-59
            4. 有效示例：08:55、14:20、20:50
            5. 也支持：0855、1420、2050
            
             数据显示：
            • 航班时间以带冒号格式显示
            • 所有查询结果以表格形式显示
            • 点击表头可按该列排序
            • 状态栏显示查询结果数量
            
             注意事项：
            1. 查询前请先选择查询条件
            3. 航班期查询请按指定格式输入
            4. 所有查询内容输入后点击"查询"按钮
            """;

        functionArea.setText(functions);
        introDialog.add(new JScrollPane(functionArea), BorderLayout.CENTER);

        JPanel examplePanel = new JPanel(new GridLayout(2, 3, 5, 5));
        examplePanel.setBorder(BorderFactory.createTitledBorder("快速查询示例"));

        String[] examples = {
                "航班号: CA1544",
                "起点站: 上海",
                "终点站: 北京",
                "航班期: 1,3,5",
                "起飞时间: 10:55",
                "到达时间: 12:40"
        };

        for (String example : examples) {
            JButton exampleButton = new JButton(example);
            exampleButton.setForeground(Color.BLACK);
            exampleButton.addActionListener(e -> {
                String text = exampleButton.getText();
                String[] parts = text.split(": ");
                if (parts.length == 2) {
                    String condition = parts[0];
                    String value = parts[1];

                    for (int i = 0; i < searchTypeComboBox.getItemCount(); i++) {
                        if (searchTypeComboBox.getItemAt(i).contains(condition)) {
                            searchTypeComboBox.setSelectedIndex(i);
                            break;
                        }
                    }

                    searchField.setText(value);
                    introDialog.dispose();

                    SwingUtilities.invokeLater(() -> performSearch());
                }
            });
            examplePanel.add(exampleButton);
        }

        introDialog.add(examplePanel, BorderLayout.SOUTH);
        introDialog.setVisible(true);
    }

    private void performSearch() {
        String searchType = (String) searchTypeComboBox.getSelectedItem();
        String searchText = searchField.getText().trim();

        if (searchType.equals("请选择查询条件")) {
            JOptionPane.showMessageDialog(this, "请先选择查询条件", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入查询内容", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<FlightNode> results = new ArrayList<>();

        switch (searchType) {
            case "航班号":
                results = flightList.searchByFlightNumber(searchText);
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "未找到航班号为 " + searchText + " 的航班", "查询结果", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                break;
            case "起点站":
                results = flightList.searchByDeparture(searchText);
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "未找到从 " + searchText + " 出发的航班", "查询结果", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                break;
            case "终点站":
                results = flightList.searchByDestination(searchText);
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "未找到到达 " + searchText + " 的航班", "查询结果", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                break;
            case "航班期(星期)":
                if (!searchText.matches("^[1-7]([,-][1-7])*$")) {
                    JOptionPane.showMessageDialog(this,
                            "航班期格式错误！\n正确格式示例：\n• 1,3,5\n• 1-5\n• 1,3-5,7",
                            "输入错误",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                results = flightList.searchByDays(searchText);
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "未找到在星期 " + searchText + " 运行的航班", "查询结果", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                break;
            case "起飞时间(晚于)":
                // 格式化时间输入（确保有冒号）
                String formattedTime = formatTimeQuery(searchText);

                if (!isValidTime(formattedTime)) {
                    return;
                }

                results = flightList.searchByDepartureTime(formattedTime);
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "未找到起飞时间晚于 " + formattedTime + " 的航班", "查询结果", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                break;
            case "到达时间(早于)":
                // 格式化时间输入（确保有冒号）
                String formattedArrivalTime = formatTimeQuery(searchText);

                if (!isValidTime(formattedArrivalTime)) {
                    return;
                }

                results = flightList.searchByArrivalTime(formattedArrivalTime);
                if (results.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "未找到到达时间早于 " + formattedArrivalTime + " 的航班", "查询结果", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                break;
        }

        updateTable(results);
    }

    private void showAllFlights() {
        updateTable(flightList.getAllFlights());
        statusLabel.setText("显示所有航班 - 共 " + flightList.getAllFlights().size() + " 条记录");
    }

    private void updateTable(ArrayList<FlightNode> flights) {
        // 清空表格
        tableModel.setRowCount(0);

        // 添加新的数据（时间显示带冒号格式）
        for (FlightNode flight : flights) {
            tableModel.addRow(new Object[]{
                    flight.flightNumber,
                    flight.departure,
                    flight.destination,
                    flight.getDaysString(),
                    flight.getDepartureTimeFormatted(),
                    flight.getArrivalTimeFormatted(),
                    flight.aircraftType,
                    flight.price
            });
        }

        // 更新状态栏
        statusLabel.setText("查询完成 - 找到 " + flights.size() + " 条航班记录");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            FlightQuerySystem system = new FlightQuerySystem();
            system.setVisible(true);
        });
    }
}