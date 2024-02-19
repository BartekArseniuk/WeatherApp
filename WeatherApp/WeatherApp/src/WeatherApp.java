import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

class RoundBorder implements Border {
    private int radius;

    RoundBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius, this.radius, this.radius, this.radius);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.drawRoundRect(x, y, width-1, height-1, radius, radius);
    }
}

public class WeatherApp {
    private JTextField locationField;
    private JButton celsiusButton;
    private JButton fahrenheitButton;
    private JComboBox<String> weatherDate;
    private JTextArea answer;
    private JFrame frame;

    public WeatherApp() {
        frame = new JFrame("Weather v1.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image backgroundImage = getBackgroundImage(answer.getText());
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        JPanel appPanel = new JPanel();
        appPanel.setOpaque(false);
        appPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new FlowLayout());

        JLabel locationLabel = new JLabel("Lokalizacja:");
        locationField = new JTextField(10);
        locationField.setBorder(new RoundBorder(10));

        topPanel.add(locationLabel);
        topPanel.add(locationField);

        appPanel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout());

        celsiusButton = new JButton("Skala Celsjusza  (jednostka °C)");
        fahrenheitButton = new JButton("Skala Fahrenheita (jednostka °F)");
        weatherDate = new JComboBox<>();

        celsiusButton.setBackground(Color.WHITE);
        fahrenheitButton.setBackground(Color.WHITE);
        weatherDate.setBackground(Color.WHITE);

        celsiusButton.setBorder(new RoundBorder(10));
        fahrenheitButton.setBorder(new RoundBorder(10));
        weatherDate.setBorder(new RoundBorder(7));

        buttonPanel.add(celsiusButton);
        buttonPanel.add(fahrenheitButton);
        buttonPanel.add(new JLabel("Data:"));
        buttonPanel.add(weatherDate);

        appPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new GridBagLayout());

        answer = new JTextArea(12, 30);
        answer.setText("Podaj lokalizację oraz wybierz jednostkę i datę.");
        answer.setLineWrap(true);
        answer.setWrapStyleWord(true);
        answer.setEditable(false);
        answer.setFocusable(false);
        answer.setFont(new Font("Arial", Font.BOLD, 18));
        answer.setMargin(new Insets(10, 120, 10, 10));
        answer.setOpaque(false);
        answer.setBackground(new Color(0, 0, 0, 0));

        JScrollPane scrollPane = new JScrollPane(answer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        textPanel.add(scrollPane);

        appPanel.add(textPanel, BorderLayout.SOUTH);

        backgroundPanel.add(appPanel, BorderLayout.CENTER);

        frame.setContentPane(backgroundPanel);
        frame.setSize(700, 500);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        celsiusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String location = locationField.getText();
                String selectedDate = (String) weatherDate.getSelectedItem();
                GetWeatherData weatherData = new GetWeatherData();
                String weatherInfo = weatherData.getWeatherData(location, selectedDate, "metric");
                answer.setText(weatherInfo);
                frame.repaint();
            }
        });

        fahrenheitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String location = locationField.getText();
                String selectedDate = (String) weatherDate.getSelectedItem();
                GetWeatherData weatherData = new GetWeatherData();
                String weatherInfo = weatherData.getWeatherData(location, selectedDate, "imperial");
                answer.setText(weatherInfo);
                frame.repaint();
            }
        });

        String[] dates = getFutureDates(7);
        weatherDate.setModel(new DefaultComboBoxModel<>(dates));
    }

    private String[] getFutureDates(int numberOfDays) {
        String[] dates = new String[numberOfDays];
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

        for (int i = 0; i < numberOfDays; i++) {
            LocalDate date = currentDate.plusDays(i);
            dates[i] = date.format(formatter);
        }

        return dates;
    }

    private Image getBackgroundImage(String weatherDescription) {
        if (weatherDescription.contains("Deszcz")) {
            return new ImageIcon("D:\\ABNS\\PPO\\PROJEKT\\WeatherApp\\WeatherApp\\resources\\rain.png").getImage();
        } else if (weatherDescription.contains("Słońce")) {
            return new ImageIcon("D:\\ABNS\\PPO\\PROJEKT\\WeatherApp\\WeatherApp\\resources\\sun.png").getImage();
        } else if (weatherDescription.contains("Zachmurzenie")) {
            return new ImageIcon("D:\\ABNS\\PPO\\PROJEKT\\WeatherApp\\WeatherApp\\resources\\clouds.png").getImage();
        } else if (weatherDescription.contains("Mgła")) {
            return new ImageIcon("D:\\ABNS\\PPO\\PROJEKT\\WeatherApp\\WeatherApp\\resources\\fog.png").getImage();
        } else if (weatherDescription.contains("Burza")) {
            return new ImageIcon("D:\\ABNS\\PPO\\PROJEKT\\WeatherApp\\WeatherApp\\resources\\storm.png").getImage();
        } else {
            return new ImageIcon("D:\\ABNS\\PPO\\PROJEKT\\WeatherApp\\WeatherApp\\resources\\mainbg.png").getImage();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WeatherApp app = new WeatherApp();
            }
        });
    }
}
