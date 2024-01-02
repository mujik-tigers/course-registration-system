package site.courseregistrationsystem.basket.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import site.courseregistrationsystem.basket.Basket;

public interface BasketRepository extends JpaRepository<Basket, Long> {

}
