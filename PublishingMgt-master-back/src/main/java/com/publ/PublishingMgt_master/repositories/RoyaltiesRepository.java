package com.publ.PublishingMgt_master.repositories;

import com.publ.PublishingMgt_master.dtos.MonthlyRoyaltyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoyaltiesRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<MonthlyRoyaltyDTO> getMonthlyDetails(String title, int year, Long authorId) {

        String sql = """
            SELECT 
                b.title AS title,
                SUM(s.quantity_sold) AS quantitySold,
                SUM(s.quantity_return) AS quantityReturn,
                SUM(s.quantity_sold - s.quantity_return) AS quantityNet,
                SUM(s.amount) AS montant,
                TO_CHAR(s.sale_date, 'Month') AS month,
                TO_CHAR(s.sale_date, 'YYYY') AS year
            FROM sales s
            JOIN book b ON b.book_id = s.book_id
            JOIN author_participation ap ON ap.book_id = b.book_id
            WHERE b.title = ?
              AND EXTRACT(YEAR FROM s.sale_date) = ?
              AND ap.author_id = ?
            GROUP BY b.title, month, year
            ORDER BY MIN(s.sale_date)
        """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new MonthlyRoyaltyDTO(
                        rs.getString("title"),
                        rs.getInt("quantitySold"),
                        rs.getInt("quantityReturn"),
                        rs.getInt("quantityNet"),
                        rs.getDouble("montant"),
                        rs.getString("month").trim(),
                        rs.getString("year")
                ),
                title,
                year,
                authorId
        );
    }
}