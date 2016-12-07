package ru.sbrf.dao.impl.h2db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.sbrf.dao.IngredientDao;
import ru.sbrf.domain.Ingredient;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Ivan on 07/12/2016.
 */
@Component
public class H2DbIngredientDaoImpl implements IngredientDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Ingredient> ingredientRowMapper = new IngredientRowMapper();

    @Autowired
    public H2DbIngredientDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Optional<Ingredient> getIngredientByName(String name) {
        final String sql = "SELECT id, name FROM INGREDIENTS " +
                "WHERE name=?";
        List<Ingredient> result = jdbcTemplate.query(sql, ingredientRowMapper, name);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Ingredient getOrCreateIngredient(String name, int amount) {
        Optional<Ingredient> select = getIngredientByName(name);
        Ingredient result;

        if (!select.isPresent()) {
            final String sql = "INSERT INTO INGREDIENTS " +
                    "(name) VALUES (?)";

            final KeyHolder keyHolder = new GeneratedKeyHolder();

            int insertionResult = jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, name);
                return ps;
            }, keyHolder);

            assert insertionResult == 1;

            return new Ingredient(keyHolder.getKey().longValue(), name, amount);
        } else
            result = select.get();

        return result;
    }


    private static class IngredientRowMapper implements RowMapper<Ingredient> {
        @Override
        public Ingredient mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Ingredient(
                    rs.getLong("id"),
                    rs.getString("name"),
                    0
            );
        }
    }
}
