package vn.shoestore.infrastructure.repository.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.BillEntity;
import vn.shoestore.shared.query_object.BillStatusObject;
import vn.shoestore.shared.query_object.ImportProduct;

@Repository
public interface BillRepository extends JpaRepository<BillEntity, Long> {
  @Query(
      value =
          """
        select * from bills where
            (:userId is null or user_id = :userId)
            order by created_date desc
        """,
      nativeQuery = true)
  Page<BillEntity> findAllByConditions(Long userId, Pageable pageable);

  @Query(
      value =
          """
        SELECT b.* from bills b
        JOIN product_bills pb on b.id = pb.bill_id
        JOIN product_properties pp on pp.id = pb.product_properties_id
        JOIN products p on p.id = pp.product_id = p.id
        where b.user_id = :userId and p.id = :productId;
  """,
      nativeQuery = true)
  List<BillEntity> findByProductIdAndUserId(Long userId, Long productId);

  @Query(
      value =
          """
                    select MONTH(created_date) as month, YEAR(created_date) as year, sum(total) as total
                    from bills
                    where bills.status = 1
                    GROUP BY month, year
                  """,
      nativeQuery = true)
  List<ImportProduct> getBillStatistic();

  @Query(
      value =
          """
                  select MONTH(created_date) as month, YEAR(created_date) as year, status as status , count(*) as total
                    from bills
                    where status in (1 , 2)
                    GROUP BY month, year , status;
         """,
      nativeQuery = true)
  List<BillStatusObject> findBillStatusStatistic();
}
