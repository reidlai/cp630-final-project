package cp630oc.paymentrequeststore.repository;

import cp630oc.paymentrequeststore.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Find by email (unique identifier)
    Optional<Customer> findByEmail(String email);
    
    // Find by name combinations
    List<Customer> findByFirstName(String firstName);
    List<Customer> findByLastName(String lastName);
    List<Customer> findByFirstNameAndLastName(String firstName, String lastName);
    
    // Find by age range
    List<Customer> findByCurrentAgeBetween(int minAge, int maxAge);
    
    // Find by credit score range
    List<Customer> findByCreditScoreGreaterThanEqual(int minCreditScore);
    List<Customer> findByCreditScoreBetween(int minScore, int maxScore);
    
    // Find by income criteria
    List<Customer> findByYearlyIncomeGreaterThan(float minIncome);
    List<Customer> findByTotalDebtLessThan(float maxDebt);
    
    // Find by location (using custom query)
    @Query("SELECT c FROM Customer c WHERE " +
           "(:radius >= " +
           "FUNCTION('acos', FUNCTION('cos', FUNCTION('radians', :lat)) * " +
           "FUNCTION('cos', FUNCTION('radians', c.latitude)) * " +
           "FUNCTION('cos', FUNCTION('radians', c.longitude) - FUNCTION('radians', :lon)) + " +
           "FUNCTION('sin', FUNCTION('radians', :lat)) * " +
           "FUNCTION('sin', FUNCTION('radians', c.latitude))) * 6371)")
    List<Customer> findCustomersWithinRadius(
            @Param("lat") float latitude,
            @Param("lon") float longitude,
            @Param("radius") float radiusInKm);
    
    // Find by gender
    List<Customer> findByGender(String gender);
    
    // Find customers with specific card count
    List<Customer> findByCreditCardCountGreaterThan(int cardCount);
    
    // Find customers by birth year
    List<Customer> findByBirthYear(int birthYear);
    
    // Custom queries for financial analysis
    @Query("SELECT c FROM Customer c WHERE (c.yearlyIncome / c.totalDebt) >= :ratio")
    List<Customer> findCustomersWithHealthyDebtToIncomeRatio(@Param("ratio") float ratio);
    
    // Check if email exists
    boolean existsByEmail(String email);
}