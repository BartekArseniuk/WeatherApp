import javax.swing.*; // Importuje pakiet zawierający klasy do tworzenia interfejsu graficznego użytkownika (GUI) w Swing
import javax.swing.border.Border; // Importuje klasę Border z pakietu javax.swing.border do tworzenia obramowania komponentów
import java.awt.*; // Importuje pakiet zawierający klasy i interfejsy dla tworzenia i zarządzania GUI w AWT
import java.awt.event.ActionEvent; // Importuje klasę ActionEvent z pakietu java.awt.event do obsługi zdarzeń akcji
import java.awt.event.ActionListener; // Importuje interfejs ActionListener z pakietu java.awt.event do obsługi słuchaczy zdarzeń
import java.time.LocalDate; // Importuje klasę LocalDate z pakietu java.time do obsługi daty
import java.time.format.DateTimeFormatter; // Importuje klasę DateTimeFormatter z pakietu java.time.format do formatowania daty
import java.util.Locale; // Importuje klasę Locale z pakietu java.util do obsługi lokalizacji


// Klasa implementująca interfejs Border do tworzenia obramowania z zaokrąglonymi rogami
class RoundBorder implements Border {
    private int radius; // Promień obramowania

    RoundBorder(int radius) {
        this.radius = radius; // Inicjalizacja promienia obramowania
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius, this.radius, this.radius, this.radius); // Zwraca marginesy obramowania
    }

    @Override
    public boolean isBorderOpaque() {
        return true; // Obramowanie jest pełne, nieprzezroczyste
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.drawRoundRect(x, y, width-1, height-1, radius, radius); // Rysuje obramowanie z zaokrąglonymi rogami
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
        frame = new JFrame("Weather v1.0"); // Tworzy nowe okno JFrame z tytułem "Weather v1.0"
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ustawia operację zamknięcia aplikacji po zamknięciu okna
        frame.setLayout(new BorderLayout()); // Ustawia menedżer rozkładu BorderLayout dla okna

        JPanel backgroundPanel = new JPanel() { // Tworzy nowy panel JPanel, dziedziczący po JPanel, który będzie tłem aplikacji
            @Override
            protected void paintComponent(Graphics g) { // Przesłania metodę paintComponent, aby narysować tło panelu
                super.paintComponent(g);
                Image backgroundImage = getBackgroundImage(answer.getText()); // Pobiera obraz tła na podstawie opisu pogody w polu tekstowym
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Rysuje obraz tła na panelu
            }
        };
        backgroundPanel.setLayout(new BorderLayout()); // Ustawia menedżer rozkładu BorderLayout dla tła panelu

        JPanel appPanel = new JPanel(); // Tworzy nowy panel JPanel, który będzie zawierał elementy interfejsu aplikacji
        appPanel.setOpaque(false); // Ustawia przezroczystość panelu
        appPanel.setLayout(new BorderLayout()); // Ustawia menedżer rozkładu BorderLayout dla panelu aplikacji

        JPanel topPanel = new JPanel(); // Tworzy nowy panel JPanel, który będzie zawierał elementy górnej części aplikacji
        topPanel.setOpaque(false); // Ustawia przezroczystość panelu
        topPanel.setLayout(new FlowLayout()); // Ustawia menedżer rozkładu FlowLayout dla górnego panelu

        JLabel locationLabel = new JLabel("Lokalizacja:"); // Tworzy etykietę "Lokalizacja"
        locationField = new JTextField(10); // Tworzy pole tekstowe o szerokości 10 znaków
        locationField.setBorder(new RoundBorder(10)); // Ustawia obramowanie pola tekstowego z zaokrąglonymi rogami

        topPanel.add(locationLabel); // Dodaje etykietę do górnego panelu
        topPanel.add(locationField); // Dodaje pole tekstowe do górnego panelu

        appPanel.add(topPanel, BorderLayout.NORTH); // Dodaje górny panel do panelu aplikacji w obszarze północnym

        JPanel buttonPanel = new JPanel(); // Tworzy nowy panel JPanel, który będzie zawierał przyciski i listę rozwijaną
        buttonPanel.setOpaque(false); // Ustawia przezroczystość panelu
        buttonPanel.setLayout(new FlowLayout()); // Ustawia menedżer rozkładu FlowLayout dla panelu przycisków

        celsiusButton = new JButton("Skala Celsjusza  (jednostka °C)"); // Tworzy przycisk "Skala Celsjusza"
        fahrenheitButton = new JButton("Skala Fahrenheita (jednostka °F)"); // Tworzy przycisk "Skala Fahrenheita"
        weatherDate = new JComboBox<>(); // Tworzy listę rozwijaną dla daty

        celsiusButton.setBackground(Color.WHITE); // Ustawia kolor tła przycisku na biały
        fahrenheitButton.setBackground(Color.WHITE); // Ustawia kolor tła przycisku na biały
        weatherDate.setBackground(Color.WHITE); // Ustawia kolor tła listy rozwijanej na biały

        celsiusButton.setBorder(new RoundBorder(10)); // Ustawia obramowanie przycisku z zaokrąglonymi rogami
        fahrenheitButton.setBorder(new RoundBorder(10)); // Ustawia obramowanie przycisku z zaokrąglonymi rogami
        weatherDate.setBorder(new RoundBorder(7)); // Ustawia obramowanie listy rozwijanej z zaokrąglonymi rogami

        buttonPanel.add(celsiusButton); // Dodaje przycisk "Skala Celsjusza" do panelu przycisków
        buttonPanel.add(fahrenheitButton); // Dodaje przycisk "Skala Fahrenheita" do panelu przycisków
        buttonPanel.add(new JLabel("Data:")); // Dodaje etykietę "Data" do panelu przycisków
        buttonPanel.add(weatherDate); // Dodaje listę rozwijaną dla daty do panelu przycisków

        appPanel.add(buttonPanel, BorderLayout.CENTER); // Dodaje panel przycisków do panelu aplikacji w obszarze centralnym

        JPanel textPanel = new JPanel(); // Tworzy nowy panel JPanel, który będzie zawierał pole tekstowe z odpowiedzią
        textPanel.setOpaque(false); // Ustawia przezroczystość panelu
        textPanel.setLayout(new GridBagLayout()); // Ustawia menedżer rozkładu GridBagLayout dla panelu tekstu

        answer = new JTextArea(12, 30); // Tworzy pole tekstowe o rozmiarze 12 wierszy na 30 kolumn
        answer.setText("Podaj lokalizację oraz wybierz jednostkę i datę."); // Ustawia domyślny tekst w polu tekstowym
        answer.setLineWrap(true); // Ustawia zawijanie wierszy
        answer.setWrapStyleWord(true); // Ustawia zawijanie słów
        answer.setEditable(false); // Ustawia pole tekstowe jako nieedytowalne
        answer.setFocusable(false); // Ustawia pole tekstowe jako niemające focusu
        answer.setFont(new Font("Arial", Font.BOLD, 18)); // Ustawia czcionkę i rozmiar tekstu
        answer.setMargin(new Insets(10, 120, 10, 10)); // Ustawia marginesy wewnątrz pola tekstowego
        answer.setOpaque(false); // Ustawia przezroczystość pola tekstowego
        answer.setBackground(new Color(0, 0, 0, 0)); // Ustawia przezroczyste tło pola tekstowego

        JScrollPane scrollPane = new JScrollPane(answer); // Tworzy panel przewijania dla pola tekstowego
        scrollPane.setOpaque(false); // Ustawia przezroczystość panelu przewijania
        scrollPane.getViewport().setOpaque(false); // Ustawia przezroczystość obszaru przewijanego
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Ustawia pusty obramowanie dla panelu przewijania

        textPanel.add(scrollPane); // Dodaje panel przewijania do panelu tekstu

        appPanel.add(textPanel, BorderLayout.SOUTH); // Dodaje panel tekstu do panelu aplikacji w obszarze południowym

        backgroundPanel.add(appPanel, BorderLayout.CENTER); // Dodaje panel aplikacji do tła panelu

        frame.setContentPane(backgroundPanel); // Ustawia tło panelu jako zawartość okna
        frame.setSize(700, 500); // Ustawia rozmiar okna
        frame.setResizable(false); // Blokuje zmianę rozmiaru okna
        frame.setLocationRelativeTo(null); // Ustawia położenie okna na środku ekranu
        frame.setVisible(true); // Ustawia widoczność okna na true

        celsiusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kod obsługi zdarzenia po kliknięciu przycisku "Skala Celsjusza"
                String location = locationField.getText(); // Pobranie tekstu z pola lokalizacji
                String selectedDate = (String) weatherDate.getSelectedItem(); // Pobranie wybranej daty z pola wyboru daty
                GetWeatherData weatherData = new GetWeatherData(); // Tworzenie obiektu do pobierania danych pogodowych
                String weatherInfo = weatherData.getWeatherData(location, selectedDate, "metric"); // Pobranie informacji o pogodzie dla danej lokalizacji i daty w skali Celsjusza
                answer.setText(weatherInfo); // Ustawienie pobranych informacji o pogodzie w odpowiednim komponencie (np. etykiecie, polu tekstowym)
                frame.repaint(); // Odświeżenie tła po zmianie opisu pogody
            }
        });

        fahrenheitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kod obsługi zdarzenia po kliknięciu przycisku "Skala Fahrenheita"
                String location = locationField.getText(); // Pobranie tekstu z pola lokalizacji
                String selectedDate = (String) weatherDate.getSelectedItem(); // Pobranie wybranej daty z pola wyboru daty
                GetWeatherData weatherData = new GetWeatherData(); // Tworzenie obiektu do pobierania danych pogodowych
                String weatherInfo = weatherData.getWeatherData(location, selectedDate, "imperial"); // Pobranie informacji o pogodzie dla danej lokalizacji i daty w skali Fahrenheita
                answer.setText(weatherInfo); // Ustawienie pobranych informacji o pogodzie w odpowiednim komponencie (np. etykiecie, polu tekstowym)
                frame.repaint(); // Odświeżenie tła po zmianie opisu pogody
            }
        });


        String[] dates = getFutureDates(7); // Tworzenie tablicy zawierającej przyszłe daty dla 7 dni
        weatherDate.setModel(new DefaultComboBoxModel<>(dates)); // Ustawienie modelu pola wyboru `weatherDate` na podstawie utworzonej tablicy dat

    }
    private String[] getFutureDates(int numberOfDays) {
        String[] dates = new String[numberOfDays]; // Tworzenie tablicy na przechowywanie dat
        LocalDate currentDate = LocalDate.now(); // Pobranie bieżącej daty
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH); // Utworzenie formatera daty

        for (int i = 0; i < numberOfDays; i++) { // Pętla iterująca przez wszystkie dni
            LocalDate date = currentDate.plusDays(i); // Obliczenie kolejnej daty
            dates[i] = date.format(formatter); // Przetworzenie daty na format tekstowy i zapisanie jej w tablicy
        }

        return dates; // Zwrócenie tablicy zawierającej przyszłe daty
    }


    private Image getBackgroundImage(String weatherDescription) {
        // Wybieranie odpowiedniego obrazu tła w zależności od opisu pogody
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
                WeatherApp app = new WeatherApp(); // Tworzy nową instancję klasy WeatherApp
            }
        });
    }
}
