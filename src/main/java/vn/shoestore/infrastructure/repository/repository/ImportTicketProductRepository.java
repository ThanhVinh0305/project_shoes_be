package vn.shoestore.infrastructure.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.shoestore.infrastructure.repository.entity.ImportTicketProductEntity;

import java.util.List;

@Repository
public interface ImportTicketProductRepository
    extends JpaRepository<ImportTicketProductEntity, Long> {
  List<ImportTicketProductEntity> findAllByTicketIdIn(List<Long> ticketIds);
}
