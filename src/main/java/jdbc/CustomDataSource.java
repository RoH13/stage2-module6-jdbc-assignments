package jdbc;

import javax.sql.DataSource;
import lombok.Getter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private CustomDataSource() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("app.properties")) {

            if (input == null) {
                throw new RuntimeException("app.properties not found in classpath");
            }

            Properties properties = new Properties();
            properties.load(input);

            this.driver = properties.getProperty("postgres.driver", "org.postgresql.Driver");
            this.url = properties.getProperty("postgres.url");
            this.name = properties.getProperty("postgres.name", "postgres");
            this.password = properties.getProperty("postgres.password", "password");

            // Регистрируем драйвер
            Class.forName(driver);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error initializing CustomDataSource", e);
        }
    }

    public static CustomDataSource getInstance() {
        if (instance == null) {
            synchronized (CustomDataSource.class) {
                if (instance == null) {
                    instance = new CustomDataSource();
                }
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return CustomConnector.getConnection(url, name, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return CustomConnector.getConnection(url, username, password);
    }

    // Остальные методы DataSource (правильно реализованы)
    @Override
    public PrintWriter getLogWriter() throws SQLException { return null; }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {}

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {}

    @Override
    public int getLoginTimeout() throws SQLException { return 0; }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException { return null; }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException { return null; }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
}