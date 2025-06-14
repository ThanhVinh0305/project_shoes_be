package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ImportTicketEntity;
import vn.shoestore.shared.query_object.ImportProduct;

import java.util.List;

@Repository
public interface ImportTicketRepository extends JpaRepository<ImportTicketEntity, Long> {
  @Query(value = """
    select * from import_tickets where id > 0
    """, nativeQuery = true)
  Page<ImportTicketEntity> getAllByConditions(Pageable pageable);

  @Query(
      value =
          """
          select MONTH(it.created_date) as month, YEAR(it.created_date) as year, SUM(itp.amount) as total
          from import_tickets it
                   JOIN import_ticket_products itp on it.id = itp.ticket_id
          GROUP BY month, year
                  """,
      nativeQuery = true)
  List<ImportProduct> getStatisticImport();
}
