package View.mainWindow;

import View.clientWindows.clientForm;
import View.productWindows.addProduct;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component("crm")
@Scope("prototype")
@PropertySource("classpath:datas.properties")
public class crmWindow extends JFrame {
    private final Environment environment;
    private JTable resultTable;
    private JScrollPane scrollPane;
    private static AnnotationConfigApplicationContext context;
    public void setContext(AnnotationConfigApplicationContext context) {
        crmWindow.context = context;
        createWindow();
    }
    public crmWindow(Environment environment) {
        this.environment = environment;
    }
    public void createWindow() {
        setLocationRelativeTo(null);
        setTitle("Главное окно");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        int leftPanelWidth = screenWidth / 2;
        int rightPanelWidth = screenWidth / 2;

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.LIGHT_GRAY);
        leftPanel.setLayout(null);
        leftPanel.setBounds(0, 0, leftPanelWidth, screenHeight);
        String[] columnNames = {"ID", "ФИО", "Статус лица", "Вероятность соответствия"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        scrollPane = new JScrollPane(resultTable);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(30); // ID
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(150); // ФИО
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Статус лица
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Вероятность соответствия

        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrollPane.setBounds(0, 0, leftPanelWidth, screenHeight);
        leftPanel.add(scrollPane);
        add(leftPanel);

// Установка ширины столбцов
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null); // Сохраняем null layout для правой панели
        rightPanel.setBounds(leftPanelWidth, 0, rightPanelWidth, screenHeight);

        JLabel idLabel = new JLabel("ID= " + environment.getProperty("user.id"));
        idLabel.setBounds(10, 10, rightPanelWidth - 20, 30); // Позиция и размер JLabel
        rightPanel.add(idLabel);

        // Определяем размеры и расположение кнопок
        JButton uploadButton = new JButton("Загрузить excel файл");
        uploadButton.setBounds((rightPanelWidth / 2) - (rightPanelWidth / 4), 30, rightPanelWidth / 2, 30); // Центрируем кнопку
        uploadButton.addActionListener(new UploadButtonManager());
        rightPanel.add(uploadButton);

        JButton addClient = new JButton("Добавить клиента");
        addClient.setBounds(10, 90, rightPanelWidth / 2 - 15, 30);
        addClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientForm clientForm=context.getBean("clientADD", clientForm.class);
                clientForm.setContext(context);
            }
        });
        rightPanel.add(addClient);

        JButton addProducts = new JButton("Добавить продукт");
        addProducts.setBounds(rightPanelWidth / 2 + 5, 70, rightPanelWidth / 2 - 15, 30);
        addProducts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                context.getBean("productForm", addProduct.class);
            }
        });
        rightPanel.add(addProducts);

        JButton productList = new JButton("Список продуктов");
        productList.setBounds(10, 110, rightPanelWidth - 20, 30);
        productList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                context.getBean("productList", View.productWindows.productList.class);
            }
        });
        rightPanel.add(productList);

        JButton getResult = new JButton("Получить результат");
        getResult.setBounds(10, 150, rightPanelWidth - 20, 30);
        getResult.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ваш код для получения результата
            }
        });
        rightPanel.add(getResult);

        styleButton(getResult);
        styleButton(uploadButton);
        styleButton(addClient);
        styleButton(addProducts);
        styleButton(productList);

        add(rightPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newWidth = getWidth() / 2;
                leftPanel.setBounds(0, 0, newWidth, getHeight());
                rightPanel.setBounds(newWidth, 0, newWidth, getHeight());
                scrollPane.setBounds(0, 0, newWidth, getHeight());

                // Обновляем размеры кнопок при изменении размера окна
                uploadButton.setBounds((newWidth / 2) - (newWidth / 4), 50, newWidth / 2, 30); // Центрируем кнопку
                addClient.setBounds(10, 90, newWidth / 2 - 15, 30);
                addProducts.setBounds(newWidth / 2 + 5, 90, newWidth / 2 - 15, 30);
                productList.setBounds(10, 130, newWidth - 20, 30);
                getResult.setBounds(10, 170, newWidth - 20, 30);

                revalidate();
                repaint();
            }
        });

        setVisible(true);
    }
    class CreatePartnerWindow implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Логика добавления партнера
        }
    }

    class UploadButtonManager implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter xlsFilter = new FileNameExtensionFilter("Excel Files (*.xls)", "xls");
            FileNameExtensionFilter xlsxFilter = new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx");
            FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
            fileChooser.addChoosableFileFilter(xlsFilter);
            fileChooser.addChoosableFileFilter(xlsxFilter);
            fileChooser.addChoosableFileFilter(csvFilter);
            fileChooser.setFileFilter(csvFilter);
            int result = fileChooser.showOpenDialog(crmWindow.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                List<String[]> data = readExcelFile(selectedFile);
                for (String[] row : data) {
                    System.out.println(String.join(", ", row)); // Печать строки
                }
            }
        }

        private List<String[]> readExcelFile(File file) {
            List<String[]> data = new ArrayList<>();
            try (Workbook workbook = new XSSFWorkbook(file)) {
                Sheet sheet = workbook.getSheetAt(0); // Получаем первый лист
                for (Row row : sheet) {
                    String[] rowData = new String[row.getPhysicalNumberOfCells()];
                    for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                        Cell cell = row.getCell(i);
                        if (cell != null) {
                            rowData[i] = cell.toString(); // Преобразуем ячейку в строку
                        } else {
                            rowData[i] = ""; // Пустая ячейка
                        }
                    }
                    data.add(rowData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                throw new RuntimeException(e);
            }
            return data;
        }
    }

    class GetResultButtonManager implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Логика обработки
        }
    }

    public static void styleButton(JButton button) {
        button.setBorder(new LineBorder(Color.BLACK, 1));
    }
}