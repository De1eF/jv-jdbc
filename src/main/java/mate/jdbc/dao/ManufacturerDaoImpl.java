package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.DataProcessingException;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class ManufacturerDaoImpl implements ManufacturerDao {

    @Override
    public Manufacturer create(Manufacturer manufaturer) {
        String createManufacturerRequest =
                "INSERT INTO manufacturers(name, country, is_deleted) VALUES(?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createManufacturer =
                        connection.prepareStatement(createManufacturerRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            createManufacturer.setString(1, manufaturer.getName());
            createManufacturer.setString(2, manufaturer.getCountry());
            createManufacturer.setString(3, "0");
            createManufacturer.executeUpdate();
            ResultSet generatedKeys = createManufacturer.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                manufaturer.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create manufacturer: " + manufaturer, e);
        }
        return manufaturer;
    }

    @Override
    public Optional<Manufacturer> get(Long id) {
        String getManufacturerRequest =
                "SELECT * FROM manufacturers WHERE id = ? AND is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getManufacturer =
                        connection.prepareStatement(getManufacturerRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            getManufacturer.setString(1, id.toString());
            ResultSet resultSet = getManufacturer.executeQuery();
            Optional<Manufacturer> returnedManufacturer = Optional.empty();
            if (resultSet.next()) {
                returnedManufacturer = Optional.of(
                         new Manufacturer(resultSet.getObject("id", Long.class),
                                 resultSet.getString("name"),
                                 resultSet.getString("country")));
            }
            return returnedManufacturer;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't fetch manufacturer with id: " + id, e);
        }
    }

    @Override
    public List<Manufacturer> getAll() {
        List<Manufacturer> result = new ArrayList<>();
        String getAllRequest =
                "SELECT * FROM manufacturers WHERE is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllManufacturers =
                        connection.createStatement()) {
            ResultSet resultSet = getAllManufacturers.executeQuery(getAllRequest);
            while (resultSet.next()) {
                result.add(new Manufacturer(resultSet.getObject("id", Long.class),
                        resultSet.getString("name"),
                        resultSet.getString("country")));
            }
            return result;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't fetch all manufacturers ", e);
        }
    }

    @Override
    public Manufacturer update(Manufacturer manufaturer) {
        String updateManufacturerRequest =
                "UPDATE manufacturers SET name = ?, country = ? WHERE id = ? AND is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateManufacturer =
                        connection.prepareStatement(updateManufacturerRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            updateManufacturer.setString(1, manufaturer.getName());
            updateManufacturer.setString(2, manufaturer.getCountry());
            updateManufacturer.setString(3, manufaturer.getId().toString());
            updateManufacturer.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update manufacturer: " + manufaturer, e);
        }
        return manufaturer;
    }

    @Override
    public boolean delete(Long id) {
        String deleteManufacturerRequest =
                "UPDATE manufacturers SET is_deleted = 1 WHERE id = ? AND is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteManufacturer =
                        connection.prepareStatement(deleteManufacturerRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            deleteManufacturer.setString(1, id.toString());
            return deleteManufacturer.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete manufacturer with id: " + id, e);
        }
    }
}